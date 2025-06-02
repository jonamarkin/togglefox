package com.markin.togglefox.dataaccess.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.markin.togglefox.dataaccess.entity.FeatureFlagEntity;
import com.markin.togglefox.dataaccess.entity.StrategyEntity;
import com.markin.togglefox.domain.model.Environment;
import com.markin.togglefox.domain.model.FeatureFlag;
import com.markin.togglefox.domain.model.FeatureFlagId;
import com.markin.togglefox.domain.strategy.AttributeBasedStrategy;
import com.markin.togglefox.domain.strategy.PercentageRolloutStrategy;
import com.markin.togglefox.domain.strategy.RolloutStrategy;
import com.markin.togglefox.domain.strategy.UserTargetingStrategy;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class FeatureFlagMapper {

    private final ObjectMapper objectMapper;

    public FeatureFlagMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    /**
     * Convert domain FeatureFlag to JPA entity
     */
    public FeatureFlagEntity toEntity(FeatureFlag domain) {
        FeatureFlagEntity entity = new FeatureFlagEntity(
                domain.getId().getValue(),
                domain.getName(),
                domain.getDescription(),
                domain.isEnabled(),
                domain.getEnvironment().getName(),
                domain.getCreatedAt(),
                domain.getUpdatedAt()
        );

        // Map strategy
        StrategyEntity strategyEntity = toStrategyEntity(domain.getRolloutStrategy());
        entity.setStrategy(strategyEntity);

        return entity;
    }

    /**
     * Convert JPA entity to domain FeatureFlag
     */
    public FeatureFlag toDomain(FeatureFlagEntity entity) {
        FeatureFlagId flagId = FeatureFlagId.of(entity.getId());
        Environment environment = Environment.of(entity.getEnvironment());
        RolloutStrategy strategy = toDomainStrategy(flagId, entity.getStrategy());

        return FeatureFlag.reconstruct(
                flagId,
                entity.getName(),
                entity.getDescription(),
                entity.getEnabled(),
                environment,
                strategy,
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

    /**
     * Convert domain strategy to JPA entity
     */
    private StrategyEntity toStrategyEntity(RolloutStrategy strategy) {
        String configJson = serializeStrategyConfig(strategy);

        return new StrategyEntity(
                UUID.randomUUID().toString(),
                strategy.getStrategyType(),
                configJson
        );
    }

    /**
     * Convert JPA strategy entity to domain strategy
     */
    private RolloutStrategy toDomainStrategy(FeatureFlagId flagId, StrategyEntity entity) {
        if (entity == null) {
            // Default strategy if none found
            return new PercentageRolloutStrategy(flagId, 0);
        }

        Map<String, Object> config = deserializeStrategyConfig(entity.getConfiguration());

        switch (entity.getStrategyType()) {
            case "PERCENTAGE":
                Integer percentage = (Integer) config.get("percentage");
                return new PercentageRolloutStrategy(flagId, percentage != null ? percentage : 0);

            case "USER_TARGETING":
                @SuppressWarnings("unchecked")
                List<String> users = (List<String>) config.get("users");
                Set<String> userSet = users != null ? new HashSet<>(users) : new HashSet<>();
                return new UserTargetingStrategy(flagId, userSet);

            case "ATTRIBUTE_BASED":
                @SuppressWarnings("unchecked")
                Map<String, List<Object>> rules = (Map<String, List<Object>>) config.get("rules");
                Map<String, Set<Object>> attributeRules = new HashMap<>();
                if (rules != null) {
                    rules.forEach((key, values) -> attributeRules.put(key, new HashSet<>(values)));
                }
                return new AttributeBasedStrategy(flagId, attributeRules);

            default:
                // Fallback to percentage strategy
                return new PercentageRolloutStrategy(flagId, 0);
        }
    }

    /**
     * Serialize strategy configuration to JSON
     */
    private String serializeStrategyConfig(RolloutStrategy strategy) {
        Map<String, Object> config = new HashMap<>();

        switch (strategy.getStrategyType()) {
            case "PERCENTAGE":
                PercentageRolloutStrategy percentageStrategy = (PercentageRolloutStrategy) strategy;
                config.put("percentage", percentageStrategy.getPercentage());
                break;

            case "USER_TARGETING":
                UserTargetingStrategy userStrategy = (UserTargetingStrategy) strategy;
                config.put("users", new ArrayList<>(userStrategy.getTargetedUsers()));
                break;

            case "ATTRIBUTE_BASED":
                AttributeBasedStrategy attributeStrategy = (AttributeBasedStrategy) strategy;
                Map<String, List<Object>> rules = new HashMap<>();
                attributeStrategy.getAttributeRules().forEach((key, values) ->
                        rules.put(key, new ArrayList<>(values)));
                config.put("rules", rules);
                break;
        }

        try {
            return objectMapper.writeValueAsString(config);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize strategy configuration", e);
        }
    }

    /**
     * Deserialize strategy configuration from JSON
     */
    private Map<String, Object> deserializeStrategyConfig(String configJson) {
        if (configJson == null || configJson.trim().isEmpty()) {
            return new HashMap<>();
        }

        try {
            return objectMapper.readValue(configJson, new TypeReference<Map<String, Object>>() {});
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to deserialize strategy configuration", e);
        }
    }
}

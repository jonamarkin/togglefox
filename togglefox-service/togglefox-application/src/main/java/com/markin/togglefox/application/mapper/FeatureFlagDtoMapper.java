package com.markin.togglefox.application.mapper;

import com.markin.togglefox.application.dto.request.CreateFlagRequestDto;
import com.markin.togglefox.application.dto.request.EvaluationRequestDto;
import com.markin.togglefox.application.dto.request.UpdateStrategyRequestDto;
import com.markin.togglefox.application.dto.response.FeatureFlagResponseDto;
import com.markin.togglefox.application.dto.response.FlagEvaluationResponseDto;
import com.markin.togglefox.domain.model.FeatureFlag;
import com.markin.togglefox.domain.model.FlagEvaluationResult;
import com.markin.togglefox.domain.strategy.AttributeBasedStrategy;
import com.markin.togglefox.domain.strategy.PercentageRolloutStrategy;
import com.markin.togglefox.domain.strategy.RolloutStrategy;
import com.markin.togglefox.domain.strategy.UserTargetingStrategy;
import com.markin.togglefox.dto.command.CreateFlagCommand;
import com.markin.togglefox.dto.command.EnableFlagCommand;
import com.markin.togglefox.dto.command.UpdateStrategyCommand;
import com.markin.togglefox.dto.query.EvaluateFlagQuery;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class FeatureFlagDtoMapper {

    /**
     * Map CreateFlagRequestDto to CreateFlagCommand
     */
    public CreateFlagCommand toCreateCommand(CreateFlagRequestDto dto) {
        return new CreateFlagCommand(
                dto.getName(),
                dto.getDescription(),
                dto.getEnvironment(),
                dto.getStrategyType(),
                dto.getStrategyConfig()
        );
    }

    /**
     * Map flagId to EnableFlagCommand
     */
    public EnableFlagCommand toEnableCommand(String flagId) {
        return new EnableFlagCommand(flagId);
    }

    /**
     * Map UpdateStrategyRequestDto to UpdateStrategyCommand
     */
    public UpdateStrategyCommand toUpdateStrategyCommand(String flagId, UpdateStrategyRequestDto dto) {
        return new UpdateStrategyCommand(
                flagId,
                dto.getStrategyType(),
                dto.getStrategyConfig()
        );
    }

    /**
     * Map evaluation parameters to EvaluateFlagQuery
     */
    public EvaluateFlagQuery toEvaluationQuery(String flagName, String environment, EvaluationRequestDto dto) {
        return new EvaluateFlagQuery(
                flagName,
                environment,
                dto.getUserId(),
                dto.getAttributes()
        );
    }

    /**
     * Map evaluation parameters to EvaluateFlagQuery (simple version)
     */
    public EvaluateFlagQuery toEvaluationQuery(String flagName, String environment, String userId) {
        return new EvaluateFlagQuery(
                flagName,
                environment,
                userId,
                new HashMap<>()
        );
    }

    /**
     * Map FeatureFlag domain object to FeatureFlagResponseDto
     */
    public FeatureFlagResponseDto toResponse(FeatureFlag flag) {
        Map<String, Object> strategyConfig = extractStrategyConfig(flag.getRolloutStrategy());

        return new FeatureFlagResponseDto(
                flag.getId().getValue(),
                flag.getName(),
                flag.getDescription(),
                flag.isEnabled(),
                flag.getEnvironment().getName(),
                flag.getRolloutStrategy().getStrategyType(),
                strategyConfig,
                flag.getCreatedAt(),
                flag.getUpdatedAt()
        );
    }

    /**
     * Map FlagEvaluationResult to FlagEvaluationResponseDto
     */
    public FlagEvaluationResponseDto toEvaluationResponse(FlagEvaluationResult result) {
        return new FlagEvaluationResponseDto(
                result.getFlagId().getValue(),
                null, // Flag name not available in result, could be added if needed
                result.isEnabled(),
                result.getReason(),
                result.getVariation()
        );
    }

    /**
     * Extract configuration from rollout strategy for response DTO
     */
    private Map<String, Object> extractStrategyConfig(RolloutStrategy strategy) {
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

        return config;
    }

    /**
     * Map list of FeatureFlag to list of FeatureFlagResponseDto
     */
    public List<FeatureFlagResponseDto> toResponseList(List<FeatureFlag> flags) {
        List<FeatureFlagResponseDto> responseList = new ArrayList<>();
        for (FeatureFlag flag : flags) {
            responseList.add(toResponse(flag));
        }
        return responseList;

    }
}

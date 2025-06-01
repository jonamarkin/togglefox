package com.markin.togglefox.service;

import com.markin.togglefox.model.Environment;
import com.markin.togglefox.model.FeatureFlag;
import com.markin.togglefox.model.FeatureFlagId;
import com.markin.togglefox.port.in.QueryFlagUseCase;
import com.markin.togglefox.port.out.CacheRepository;
import com.markin.togglefox.port.out.FeatureFlagRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
@Transactional(readOnly = true)
public class QueryFlagService implements QueryFlagUseCase {

    private static final Logger logger = LoggerFactory.getLogger(QueryFlagService.class);

    private final FeatureFlagRepository repository;
    private final CacheRepository cacheRepository;

    public QueryFlagService(FeatureFlagRepository repository, CacheRepository cacheRepository) {
        this.repository = Objects.requireNonNull(repository, "Repository cannot be null");
        this.cacheRepository = Objects.requireNonNull(cacheRepository, "Cache repository cannot be null");
    }

    @Override
    public Optional<FeatureFlag> getFlag(String flagName, Environment environment) {
        Objects.requireNonNull(flagName, "Flag name cannot be null");
        Objects.requireNonNull(environment, "Environment cannot be null");

        logger.debug("Querying flag: {} in environment: {}", flagName, environment);

        try {
            // Try cache first
            Optional<FeatureFlag> cachedFlag = cacheRepository.getFlag(flagName, environment);
            if (cachedFlag.isPresent()) {
                logger.debug("Cache hit for flag: {} in environment: {}", flagName, environment);
                return cachedFlag;
            }

            // Cache miss - load from database
            logger.debug("Cache miss for flag: {} in environment: {}, loading from database",
                    flagName, environment);

            Optional<FeatureFlag> flag = repository.findByNameAndEnvironment(flagName, environment);

            if (flag.isPresent()) {
                // Cache the loaded flag for future requests
                cacheRepository.cacheFlag(flag.get());
                logger.debug("Cached flag after database load: {} in environment: {}",
                        flagName, environment);
            } else {
                logger.debug("Flag not found: {} in environment: {}", flagName, environment);
            }

            return flag;

        } catch(Exception e) {
            logger.error("Failed to query flag: {} in environment: {}", flagName, environment, e);

            // Fallback to database only if cache fails
            try {
                return repository.findByNameAndEnvironment(flagName, environment);
            } catch (Exception dbException) {
                logger.error("Database fallback also failed for flag: {} in environment: {}",
                        flagName, environment, dbException);
                return Optional.empty();
            }
        }
    }

    @Override
    public Optional<FeatureFlag> getFlagById(FeatureFlagId flagId) {
        Objects.requireNonNull(flagId, "Flag ID cannot be null");

        logger.debug("Querying flag by ID: {}", flagId.getValue());

        try {
            // For ID-based queries, go directly to database as cache is name+environment based
            Optional<FeatureFlag> flag = repository.findById(flagId);

            if (flag.isPresent()) {
                // Cache the flag for future name+environment queries
                cacheRepository.cacheFlag(flag.get());
                logger.debug("Found and cached flag by ID: {}", flagId.getValue());
            } else {
                logger.debug("Flag not found by ID: {}", flagId.getValue());
            }

            return flag;

        } catch (Exception e) {
            logger.error("Failed to query flag by ID: {}", flagId.getValue(), e);
            return Optional.empty();
        }
    }

    @Override
    public List<FeatureFlag> getAllFlags(Environment environment) {
        Objects.requireNonNull(environment, "Environment cannot be null");

        logger.debug("Querying all flags in environment: {}", environment);

        try {
            // For bulk queries, use database directly as caching all flags might be memory intensive
            List<FeatureFlag> flags = repository.findByEnvironment(environment);

            logger.debug("Found {} flags in environment: {}", flags.size(), environment);

            // Optionally cache frequently accessed flags
            cacheHotFlags(flags);

            return flags;

        } catch (Exception e) {
            logger.error("Failed to query all flags in environment: {}", environment, e);
            return List.of(); // Return empty list on error
        }
    }

    @Override
    public List<FeatureFlag> searchFlags(String namePattern, Environment environment) {
        Objects.requireNonNull(namePattern, "Name pattern cannot be null");
        Objects.requireNonNull(environment, "Environment cannot be null");

        logger.debug("Searching flags with pattern: '{}' in environment: {}", namePattern, environment);

        try {
            // Search operations typically go to database for flexibility
            List<FeatureFlag> matchingFlags = repository.findByNamePatternAndEnvironment(namePattern, environment);

            logger.debug("Found {} flags matching pattern: '{}' in environment: {}",
                    matchingFlags.size(), namePattern, environment);

            // Cache the found flags for potential future individual lookups
            matchingFlags.forEach(cacheRepository::cacheFlag);

            return matchingFlags;

        } catch (Exception e) {
            logger.error("Failed to search flags with pattern: '{}' in environment: {}",
                    namePattern, environment, e);
            return List.of(); // Return empty list on error
        }
    }

    public List<FeatureFlag> getEnabledFlags(Environment environment) {
        Objects.requireNonNull(environment, "Environment cannot be null");

        logger.debug("Querying enabled flags in environment: {}", environment);

        try {
            List<FeatureFlag> enabledFlags = repository.findByEnvironmentAndEnabledTrue(environment);

            logger.debug("Found {} enabled flags in environment: {}", enabledFlags.size(), environment);

            // Cache enabled flags as they're likely to be queried frequently
            enabledFlags.forEach(cacheRepository::cacheFlag);

            return enabledFlags;

        } catch (Exception e) {
            logger.error("Failed to query enabled flags in environment: {}", environment, e);
            return List.of();
        }
    }

    public List<FeatureFlag> getFlagsByIds(List<FeatureFlagId> flagIds) {
        Objects.requireNonNull(flagIds, "Flag IDs cannot be null");

        if (flagIds.isEmpty()) {
            return List.of();
        }

        logger.debug("Querying {} flags by IDs", flagIds.size());

        try {
            List<FeatureFlag> flags = repository.findByIdIn(flagIds);

            logger.debug("Found {} flags out of {} requested IDs", flags.size(), flagIds.size());

            // Cache all found flags
            flags.forEach(cacheRepository::cacheFlag);

            return flags;

        } catch (Exception e) {
            logger.error("Failed to query flags by IDs", e);
            return List.of();
        }
    }

    public boolean flagExists(String flagName, Environment environment) {
        Objects.requireNonNull(flagName, "Flag name cannot be null");
        Objects.requireNonNull(environment, "Environment cannot be null");

        logger.debug("Checking existence of flag: {} in environment: {}", flagName, environment);

        try {
            // Check cache first
            if (cacheRepository.exists(flagName, environment)) {
                logger.debug("Flag exists in cache: {} in environment: {}", flagName, environment);
                return true;
            }

            // Check database
            boolean exists = repository.existsByNameAndEnvironment(flagName, environment);
            logger.debug("Flag existence check in database: {} in environment: {} = {}",
                    flagName, environment, exists);

            return exists;

        } catch (Exception e) {
            logger.error("Failed to check flag existence: {} in environment: {}",
                    flagName, environment, e);
            return false;
        }
    }

    public List<FeatureFlag> getFlagsByStrategyType(String strategyType, Environment environment) {
        Objects.requireNonNull(strategyType, "Strategy type cannot be null");
        Objects.requireNonNull(environment, "Environment cannot be null");

        logger.debug("Querying flags with strategy type: '{}' in environment: {}",
                strategyType, environment);

        try {
            List<FeatureFlag> flags = repository.findByStrategyTypeAndEnvironment(strategyType, environment);

            logger.debug("Found {} flags with strategy type: '{}' in environment: {}",
                    flags.size(), strategyType, environment);

            return flags;

        } catch (Exception e) {
            logger.error("Failed to query flags by strategy type: '{}' in environment: {}",
                    strategyType, environment, e);
            return List.of();
        }
    }

    public List<FeatureFlag> getFlagsByCreator(String createdBy, Environment environment) {
        Objects.requireNonNull(createdBy, "Created by cannot be null");
        Objects.requireNonNull(environment, "Environment cannot be null");

        logger.debug("Querying flags created by: '{}' in environment: {}", createdBy, environment);

        try {
            List<FeatureFlag> flags = repository.findByCreatedByAndEnvironment(createdBy, environment);

            logger.debug("Found {} flags created by: '{}' in environment: {}",
                    flags.size(), createdBy, environment);

            return flags;

        } catch (Exception e) {
            logger.error("Failed to query flags by creator: '{}' in environment: {}",
                    createdBy, environment, e);
            return List.of();
        }
    }

    private void cacheHotFlags(List<FeatureFlag> flags) {
        try {
            // Cache enabled flags as they're more likely to be queried for evaluation
            List<FeatureFlag> enabledFlags = flags.stream()
                    .filter(FeatureFlag::isEnabled)
                    .collect(Collectors.toList());

            enabledFlags.forEach(flag -> {
                try {
                    cacheRepository.cacheFlag(flag);
                } catch (Exception e) {
                    logger.warn("Failed to cache hot flag: {}", flag.getName(), e);
                }
            });

            logger.debug("Cached {} hot flags out of {} total flags", enabledFlags.size(), flags.size());

        } catch (Exception e) {
            logger.warn("Failed to cache hot flags", e);
        }
    }
}

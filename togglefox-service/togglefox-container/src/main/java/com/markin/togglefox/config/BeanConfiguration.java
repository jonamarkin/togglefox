package com.markin.togglefox.config;

import com.markin.togglefox.dataaccess.messaging.DomainEventPublisher;
import com.markin.togglefox.port.in.CreateFlagUseCase;
import com.markin.togglefox.port.in.FlagEvaluationUseCase;
import com.markin.togglefox.port.in.ManageFlagUseCase;
import com.markin.togglefox.port.out.CacheRepository;
import com.markin.togglefox.port.out.EventPublisher;
import com.markin.togglefox.port.out.FeatureFlagRepository;
import com.markin.togglefox.service.CreateFlagService;
import com.markin.togglefox.service.FlagEvaluationService;
import com.markin.togglefox.service.ManageFlagService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanConfiguration {

    /**
     * Flag evaluation use case
     */
    @Bean
    public FlagEvaluationUseCase flagEvaluationUseCase(
            FeatureFlagRepository repository,
            CacheRepository cache) {
        return new FlagEvaluationService(repository, cache);
    }

    /**
     * Create flag use case
     */
    @Bean
    public CreateFlagUseCase createFlagUseCase(
            FeatureFlagRepository repository,
            EventPublisher eventPublisher) {
        return new CreateFlagService(repository, eventPublisher);
    }

    /**
     * Manage flag use case
     */
    @Bean
    public ManageFlagUseCase manageFlagUseCase(
            FeatureFlagRepository repository,
            CacheRepository cache,
            EventPublisher eventPublisher) {
        return new ManageFlagService(repository, cache, eventPublisher);
    }

    /**
     * Event publisher
     */
    @Bean
    public EventPublisher eventPublisher() {
        return new DomainEventPublisher();
    }
}

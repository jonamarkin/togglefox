package com.markin.togglefox.domain;

import com.markin.togglefox.port.out.CacheRepository;
import com.markin.togglefox.port.out.EventPublisher;
import com.markin.togglefox.port.out.FeatureFlagRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

import static org.mockito.Mockito.mock;

public class ToggleFoxTestConfiguration {
    @Bean
    @Primary
    public FeatureFlagRepository mockFeatureFlagRepository() {
        return mock(FeatureFlagRepository.class);
    }

    @Bean
    @Primary
    public CacheRepository mockCacheRepository() {
        return mock(CacheRepository.class);
    }

    @Bean
    @Primary
    public EventPublisher mockEventPublisher() {
        return mock(EventPublisher.class);
    }


}

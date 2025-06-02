package com.markin.togglefox.dataaccess.repository;

import com.markin.togglefox.domain.model.Environment;
import com.markin.togglefox.domain.model.FeatureFlag;
import com.markin.togglefox.domain.model.FeatureFlagId;
import com.markin.togglefox.domain.strategy.PercentageRolloutStrategy;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Testcontainers
@Transactional
public class RepositoryIntegrationTest {
    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine")
            .withDatabaseName("togglemate_test")
            .withUsername("test")
            .withPassword("test");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    private FeatureFlagRepositoryImpl repository;

    @Test
    @DisplayName("Should save and retrieve feature flag")
    void shouldSaveAndRetrieveFeatureFlag() {
        // Given
        FeatureFlagId id = FeatureFlagId.generate();
        Environment environment = Environment.development();
        PercentageRolloutStrategy strategy = new PercentageRolloutStrategy(id, 25);

        FeatureFlag flag = FeatureFlag.create(
                id,
                "test-repository-flag",
                "Test flag for repository",
                environment,
                strategy
        );

        // When
        FeatureFlag savedFlag = repository.save(flag);
        Optional<FeatureFlag> retrievedFlag = repository.findById(id);

        // Then
        assertThat(savedFlag).isNotNull();
        assertThat(retrievedFlag).isPresent();
        assertThat(retrievedFlag.get().getId()).isEqualTo(id);
        assertThat(retrievedFlag.get().getName()).isEqualTo("test-repository-flag");
        assertThat(retrievedFlag.get().getEnvironment()).isEqualTo(environment);
    }

    @Test
    @DisplayName("Should find flag by name and environment")
    void shouldFindFlagByNameAndEnvironment() {
        // Given
        FeatureFlagId id = FeatureFlagId.generate();
        Environment environment = Environment.production();
        PercentageRolloutStrategy strategy = new PercentageRolloutStrategy(id, 50);

        FeatureFlag flag = FeatureFlag.create(
                id,
                "prod-flag",
                "Production flag",
                environment,
                strategy
        );
        repository.save(flag);

        // When
        Optional<FeatureFlag> found = repository.findByNameAndEnvironment("prod-flag", environment);

        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("prod-flag");
        assertThat(found.get().getEnvironment()).isEqualTo(environment);
    }

    @Test
    @DisplayName("Should check if flag exists by name and environment")
    void shouldCheckIfFlagExistsByNameAndEnvironment() {
        // Given
        FeatureFlagId id = FeatureFlagId.generate();
        Environment environment = Environment.staging();
        PercentageRolloutStrategy strategy = new PercentageRolloutStrategy(id, 75);

        FeatureFlag flag = FeatureFlag.create(
                id,
                "exists-test-flag",
                "Exists test flag",
                environment,
                strategy
        );
        repository.save(flag);

        // When & Then
        assertThat(repository.existsByNameAndEnvironment("exists-test-flag", environment)).isTrue();
        assertThat(repository.existsByNameAndEnvironment("non-existent", environment)).isFalse();
        assertThat(repository.existsByNameAndEnvironment("exists-test-flag", Environment.development())).isFalse();
    }
}

package com.markin.togglefox.domain;

import com.markin.togglefox.domain.model.*;
import com.markin.togglefox.domain.strategy.PercentageRolloutStrategy;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class FeatureFlagTest {
    @Nested
    @DisplayName("Feature Flag Creation")
    class Creation {

        @Test
        @DisplayName("Should create feature flag with valid parameters")
        void shouldCreateFeatureFlagWithValidParameters() {
            // Given
            FeatureFlagId id = FeatureFlagId.generate();
            String name = "test-flag";
            String description = "Test flag description";
            Environment environment = Environment.production();
            PercentageRolloutStrategy strategy = new PercentageRolloutStrategy(id, 50);

            // When
            FeatureFlag flag = FeatureFlag.create(id, name, description, environment, strategy);

            // Then
            assertThat(flag.getId()).isEqualTo(id);
            assertThat(flag.getName()).isEqualTo(name);
            assertThat(flag.getDescription()).isEqualTo(description);
            assertThat(flag.getEnvironment()).isEqualTo(environment);
            assertThat(flag.isEnabled()).isFalse(); // New flags are disabled by default
            assertThat(flag.getRolloutStrategy()).isEqualTo(strategy);
            assertThat(flag.getCreatedAt()).isNotNull();
            assertThat(flag.getUpdatedAt()).isNotNull();
        }

        @Test
        @DisplayName("Should raise domain event when creating feature flag")
        void shouldRaiseDomainEventWhenCreatingFeatureFlag() {
            // Given
            FeatureFlagId id = FeatureFlagId.generate();
            Environment environment = Environment.development();
            PercentageRolloutStrategy strategy = new PercentageRolloutStrategy(id, 25);

            // When
            FeatureFlag flag = FeatureFlag.create(id, "test-flag", "description", environment, strategy);

            // Then
            var events = flag.getAndClearDomainEvents();
            assertThat(events).hasSize(1);
            assertThat(events.get(0).getEventType()).isEqualTo("FeatureFlagCreated");
        }

        @Test
        @DisplayName("Should reject null name")
        void shouldRejectNullName() {
            // Given
            FeatureFlagId id = FeatureFlagId.generate();
            Environment environment = Environment.development();
            PercentageRolloutStrategy strategy = new PercentageRolloutStrategy(id, 25);

            // When & Then
            assertThatThrownBy(() ->
                    FeatureFlag.create(id, null, "description", environment, strategy))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("name cannot be null or empty");
        }

        @Test
        @DisplayName("Should reject invalid name characters")
        void shouldRejectInvalidNameCharacters() {
            // Given
            FeatureFlagId id = FeatureFlagId.generate();
            Environment environment = Environment.development();
            PercentageRolloutStrategy strategy = new PercentageRolloutStrategy(id, 25);

            // When & Then
            assertThatThrownBy(() ->
                    FeatureFlag.create(id, "invalid name with spaces", "description", environment, strategy))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("can only contain alphanumeric characters");
        }
    }

    @Nested
    @DisplayName("Feature Flag State Management")
    class StateManagement {

        @Test
        @DisplayName("Should enable flag and raise event")
        void shouldEnableFlagAndRaiseEvent() {
            // Given
            FeatureFlag flag = createTestFlag();
            assertThat(flag.isEnabled()).isFalse();

            // When
            flag.enable();

            // Then
            assertThat(flag.isEnabled()).isTrue();
            var events = flag.getAndClearDomainEvents();
            assertThat(events).hasSize(1);
            assertThat(events.get(0).getEventType()).isEqualTo("FeatureFlagEnabled");
        }

        @Test
        @DisplayName("Should disable flag and raise event")
        void shouldDisableFlagAndRaiseEvent() {
            // Given
            FeatureFlag flag = createTestFlag();
            flag.enable();
            flag.getAndClearDomainEvents(); // Clear creation events

            // When
            flag.disable();

            // Then
            assertThat(flag.isEnabled()).isFalse();
            var events = flag.getAndClearDomainEvents();
            assertThat(events).hasSize(1);
            assertThat(events.get(0).getEventType()).isEqualTo("FeatureFlagDisabled");
        }

        @Test
        @DisplayName("Should not raise event when enabling already enabled flag")
        void shouldNotRaiseEventWhenEnablingAlreadyEnabledFlag() {
            // Given
            FeatureFlag flag = createTestFlag();
            flag.enable();
            flag.getAndClearDomainEvents(); // Clear events

            // When
            flag.enable(); // Enable again

            // Then
            var events = flag.getAndClearDomainEvents();
            assertThat(events).isEmpty();
        }
    }

    @Nested
    @DisplayName("Feature Flag Evaluation")
    class Evaluation {

        @Test
        @DisplayName("Should return disabled when flag is disabled")
        void shouldReturnDisabledWhenFlagIsDisabled() {
            // Given
            FeatureFlag flag = createTestFlag();
            EvaluationContext context = EvaluationContext.forUser("user123");

            // When
            FlagEvaluationResult result = flag.evaluate(context);

            // Then
            assertThat(result.isEnabled()).isFalse();
            assertThat(result.getReason()).contains("disabled");
        }

        @Test
        @DisplayName("Should delegate to strategy when flag is enabled")
        void shouldDelegateToStrategyWhenFlagIsEnabled() {
            // Given
            FeatureFlag flag = createTestFlag();
            flag.enable();
            EvaluationContext context = EvaluationContext.forUser("user123");

            // When
            FlagEvaluationResult result = flag.evaluate(context);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.getFlagId()).isEqualTo(flag.getId());
        }
    }

    private FeatureFlag createTestFlag() {
        FeatureFlagId id = FeatureFlagId.generate();
        PercentageRolloutStrategy strategy = new PercentageRolloutStrategy(id, 50);
        FeatureFlag flag = FeatureFlag.create(
                id,
                "test-flag",
                "Test flag",
                Environment.development(),
                strategy
        );
        flag.getAndClearDomainEvents(); // Clear creation events
        return flag;
    }

}

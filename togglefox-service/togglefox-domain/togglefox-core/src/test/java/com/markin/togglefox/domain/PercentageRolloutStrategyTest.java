package com.markin.togglefox.domain;

import com.markin.togglefox.domain.model.EvaluationContext;
import com.markin.togglefox.domain.model.FeatureFlagId;
import com.markin.togglefox.domain.model.FlagEvaluationResult;
import com.markin.togglefox.domain.strategy.PercentageRolloutStrategy;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class PercentageRolloutStrategyTest {
    @Nested
    @DisplayName("Strategy Creation")
    class Creation {

        @Test
        @DisplayName("Should create strategy with valid percentage")
        void shouldCreateStrategyWithValidPercentage() {
            // Given
            FeatureFlagId flagId = FeatureFlagId.generate();

            // When
            PercentageRolloutStrategy strategy = new PercentageRolloutStrategy(flagId, 25);

            // Then
            assertThat(strategy.getPercentage()).isEqualTo(25);
            assertThat(strategy.getStrategyType()).isEqualTo("PERCENTAGE");
            assertThat(strategy.isValid()).isTrue();
        }

        @Test
        @DisplayName("Should reject negative percentage")
        void shouldRejectNegativePercentage() {
            // Given
            FeatureFlagId flagId = FeatureFlagId.generate();

            // When & Then
            assertThatThrownBy(() -> new PercentageRolloutStrategy(flagId, -1))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Percentage must be between 0 and 100");
        }

        @Test
        @DisplayName("Should reject percentage over 100")
        void shouldRejectPercentageOver100() {
            // Given
            FeatureFlagId flagId = FeatureFlagId.generate();

            // When & Then
            assertThatThrownBy(() -> new PercentageRolloutStrategy(flagId, 101))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Percentage must be between 0 and 100");
        }
    }

    @Nested
    @DisplayName("Strategy Evaluation")
    class Evaluation {

        @Test
        @DisplayName("Should return disabled for anonymous users")
        void shouldReturnDisabledForAnonymousUsers() {
            // Given
            FeatureFlagId flagId = FeatureFlagId.generate();
            PercentageRolloutStrategy strategy = new PercentageRolloutStrategy(flagId, 50);
            EvaluationContext context = EvaluationContext.anonymous();

            // When
            FlagEvaluationResult result = strategy.evaluate(context);

            // Then
            assertThat(result.isEnabled()).isFalse();
            assertThat(result.getReason()).contains("Anonymous users not supported");
        }

        @Test
        @DisplayName("Should return consistent results for same user")
        void shouldReturnConsistentResultsForSameUser() {
            // Given
            FeatureFlagId flagId = FeatureFlagId.generate();
            PercentageRolloutStrategy strategy = new PercentageRolloutStrategy(flagId, 50);
            EvaluationContext context = EvaluationContext.forUser("user123");

            // When
            FlagEvaluationResult result1 = strategy.evaluate(context);
            FlagEvaluationResult result2 = strategy.evaluate(context);

            // Then
            assertThat(result1.isEnabled()).isEqualTo(result2.isEnabled());
            assertThat(result1.getReason()).isEqualTo(result2.getReason());
        }

        @Test
        @DisplayName("Should distribute users according to percentage")
        void shouldDistributeUsersAccordingToPercentage() {
            // Given
            FeatureFlagId flagId = FeatureFlagId.generate();
            PercentageRolloutStrategy strategy = new PercentageRolloutStrategy(flagId, 25);

            // When
            int enabledCount = 0;
            int totalUsers = 1000;

            for (int i = 0; i < totalUsers; i++) {
                EvaluationContext context = EvaluationContext.forUser("user" + i);
                FlagEvaluationResult result = strategy.evaluate(context);
                if (result.isEnabled()) {
                    enabledCount++;
                }
            }

            // Then
            double actualPercentage = (double) enabledCount / totalUsers * 100;
            // Allow 5% tolerance for hash distribution variance
            assertThat(actualPercentage).isBetween(20.0, 30.0);
        }
    }
}

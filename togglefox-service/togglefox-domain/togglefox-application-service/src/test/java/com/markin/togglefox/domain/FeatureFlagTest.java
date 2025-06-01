package com.markin.togglefox.domain;

import com.markin.togglefox.model.*;
import com.markin.togglefox.strategy.PercentageRolloutStrategy;
import com.markin.togglefox.strategy.UserTargetingStrategy;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

public class FeatureFlagTest {
    @Test
    @DisplayName("Should create feature flag with default disabled state")
    public void shouldCreateFeatureFlagWithDefaultDisabledState() {
        // Given
        //FeatureFlagId flagId = FeatureFlagId.generate();
        String name = "test-feature";
        String description = "Test feature flag";
        Environment environment = Environment.DEVELOPMENT;
        String createdBy = "test-user";

        // When
        FeatureFlag featureFlag = FeatureFlag.create(name, description, environment, createdBy);

        // Then
        //assertThat(featureFlag.getId()).isEqualTo(flagId);
        assertThat(featureFlag.getName()).isEqualTo(name);
        assertThat(featureFlag.getEnvironment()).isEqualTo(environment);
        assertThat(featureFlag.isEnabled()).isFalse();
    }

    @Test
    @DisplayName("Should reconstitute feature flag with all properties")

    public void shouldReconstituteFeatureFlagWithAllProperties() {
        // Given
        FeatureFlagId flagId = FeatureFlagId.generate();
        String name = "test-feature";
        String description = "Test feature flag";
        Environment environment = Environment.DEVELOPMENT;
        String createdBy = "test-user";
        boolean enabled = true;
        PercentageRolloutStrategy rolloutStrategy = new PercentageRolloutStrategy(50); // 50% rollout
        var createdAt = java.time.LocalDateTime.now();
        var updatedAt = java.time.LocalDateTime.now();
        var tags = Set.of("tag1", "tag2");

        // When
        FeatureFlag featureFlag = FeatureFlag.reconstitute(
                flagId, name, description, enabled, environment,
                rolloutStrategy, createdAt, updatedAt, createdBy, tags);

        // Then
        assertThat(featureFlag.getId()).isEqualTo(flagId);
        assertThat(featureFlag.getName()).isEqualTo(name);
        assertThat(featureFlag.getDescription()).isEqualTo(description);
        assertThat(featureFlag.getEnvironment()).isEqualTo(environment);
        assertThat(featureFlag.isEnabled()).isTrue();
        assertThat(featureFlag.getCreatedBy()).isEqualTo(createdBy);
        assertThat(featureFlag.getTags()).containsExactlyInAnyOrder("tag1", "tag2");
    }

    @Test
    @DisplayName("Should enable feature flag")
    public void shouldEnableFeatureFlag() {
        // Given
        FeatureFlagId flagId = FeatureFlagId.generate();
        String name = "test-feature";
        String description = "Test feature flag";
        Environment environment = Environment.DEVELOPMENT;
        String createdBy = "test-user";

        FeatureFlag featureFlag = FeatureFlag.create(name, description, environment, createdBy);

        // When
        featureFlag.enable(createdBy, "Enabling feature for testing");

        // Then
        assertThat(featureFlag.isEnabled()).isTrue();
    }

    @Test
    @DisplayName("Should disable feature flag")
    public void shouldDisableFeatureFlag() {
        // Given
        FeatureFlagId flagId = FeatureFlagId.generate();
        String name = "test-feature";
        String description = "Test feature flag";
        Environment environment = Environment.DEVELOPMENT;
        String createdBy = "test-user";

        FeatureFlag featureFlag = FeatureFlag.create(name, description, environment, createdBy);
        featureFlag.enable(createdBy, "Enabling feature for testing");

        // When
        featureFlag.disable(createdBy, "Disabling feature for testing");

        // Then
        assertThat(featureFlag.isEnabled()).isFalse();
    }

    @Test
    @DisplayName("Should evaluate using percentage rollout strategy")
    public void shouldEvaluateUsingPercentageRolloutStrategy() {
        // Given
        FeatureFlag flag = FeatureFlag.create("test", "test", Environment.DEVELOPMENT, "user");
        flag.enable("user", "Enabling for testing");
        flag.updateRolloutStrategy(new PercentageRolloutStrategy(100), "user"); // 100% rollout
        EvaluationContext context = EvaluationContext.of("user123", Environment.DEVELOPMENT);

        // When
        FlagEvaluationResult result = flag.evaluate(context);

        // Then
        assertTrue(result.enabled());
        assertTrue(result.reason().contains("rollout"));
    }

    @Test
    @DisplayName("Should evaluate using user targeting strategy")
    void shouldEvaluateUsingUserTargetingStrategy() {
        // Given
        FeatureFlag flag = FeatureFlag.create("test", "test", Environment.DEVELOPMENT, "user");
        flag.enable("user", "Enabling for testing");
        flag.updateRolloutStrategy(UserTargetingStrategy.whitelist(Set.of("user123", "user456")), "user"); // Whitelist strategy
        EvaluationContext context = EvaluationContext.of("user123", Environment.DEVELOPMENT);

        // When
        FlagEvaluationResult result = flag.evaluate(context);

        // Then
        assertTrue(result.enabled());
        assertEquals("User is in whitelist", result.reason());
    }

    @Test
    @DisplayName("Should enable and disable flag")
    void shouldEnableAndDisableFlag() {
        // Given
        FeatureFlag flag = FeatureFlag.create("test", "test", Environment.DEVELOPMENT, "user");
        assertFalse(flag.isEnabled());

        // When - Enable
        flag.enable("user", "Enabling for testing");

        // Then
        assertTrue(flag.isEnabled());
        assertEquals(2, flag.getDomainEvents().size()); // Create + Enable events

        // When - Disable
        flag.disable("user", "Disabling for testing");

        // Then
        assertFalse(flag.isEnabled());
        assertEquals(3, flag.getDomainEvents().size()); // Create + Enable + Disable events
    }

    @Test
    @DisplayName("Should add and remove tags")
    void shouldAddAndRemoveTags() {
        // Given
        FeatureFlag flag = FeatureFlag.create("test", "test", Environment.DEVELOPMENT, "user");

        // When
        flag.addTag("ui");
        flag.addTag("experimental");

        // Then
        assertEquals(2, flag.getTags().size());
        assertTrue(flag.getTags().contains("ui"));
        assertTrue(flag.getTags().contains("experimental"));

        // When
        flag.removeTag("ui");

        // Then
        assertEquals(1, flag.getTags().size());
        assertFalse(flag.getTags().contains("ui"));
        assertTrue(flag.getTags().contains("experimental"));
    }

    @Test
    @DisplayName("Should validate flag name")
    void shouldValidateFlagName() {
        // Given
        Environment environment = Environment.DEVELOPMENT;
        String createdBy = "user";

        // When & Then
        assertThrows(IllegalArgumentException.class, () ->
                FeatureFlag.create(null, "description", environment, createdBy));

        assertThrows(IllegalArgumentException.class, () ->
                FeatureFlag.create("", "description", environment, createdBy));

        assertThrows(IllegalArgumentException.class, () ->
                FeatureFlag.create("a".repeat(101), "description", environment, createdBy));
    }

}

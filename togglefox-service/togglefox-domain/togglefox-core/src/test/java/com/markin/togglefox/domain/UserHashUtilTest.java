package com.markin.togglefox.domain;

import com.markin.togglefox.domain.util.UserHashUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class UserHashUtilTest {
    @Nested
    @DisplayName("Hash Consistency")
    class HashConsistency {

        @Test
        @DisplayName("Should return same hash for same user and flag")
        void shouldReturnSameHashForSameUserAndFlag() {
            // Given
            String userId = "user123";
            String flagId = "flag456";

            // When
            int hash1 = UserHashUtil.hashUser(userId, flagId);
            int hash2 = UserHashUtil.hashUser(userId, flagId);

            // Then
            assertThat(hash1).isEqualTo(hash2);
        }

        @Test
        @DisplayName("Should return different hashes for different flags")
        void shouldReturnDifferentHashesForDifferentFlags() {
            // Given
            String userId = "user123";
            String flagId1 = "flag1";
            String flagId2 = "flag2";

            // When
            int hash1 = UserHashUtil.hashUser(userId, flagId1);
            int hash2 = UserHashUtil.hashUser(userId, flagId2);

            // Then
            assertThat(hash1).isNotEqualTo(hash2);
        }
    }

    @Nested
    @DisplayName("Hash Distribution")
    class HashDistribution {

        @Test
        @DisplayName("Should return hash in valid range")
        void shouldReturnHashInValidRange() {
            // Given
            String userId = "user123";
            String flagId = "flag456";

            // When
            int hash = UserHashUtil.hashUser(userId, flagId);

            // Then
            assertThat(hash).isBetween(0, 99);
        }

        @Test
        @DisplayName("Should distribute hashes uniformly")
        void shouldDistributeHashesUniformly() {
            // Given
            String flagId = "test-flag";
            String[] userIds = new String[1000];
            for (int i = 0; i < 1000; i++) {
                userIds[i] = "user" + i;
            }

            // When & Then
            boolean isUniform = UserHashUtil.isUniformDistribution(userIds, flagId, 0.2); // 20% tolerance
            assertThat(isUniform).isTrue();
        }

        @Test
        @DisplayName("Should generate diverse hash values")
        void shouldGenerateDiverseHashValues() {
            // Given
            String flagId = "test-flag";
            Set<Integer> uniqueHashes = new HashSet<>();

            // When
            for (int i = 0; i < 100; i++) {
                int hash = UserHashUtil.hashUser("user" + i, flagId);
                uniqueHashes.add(hash);
            }

            // Then
            // Should have a good spread of unique values
            assertThat(uniqueHashes.size()).isGreaterThan(50);
        }
    }

    @Nested
    @DisplayName("Input Validation")
    class InputValidation {

        @Test
        @DisplayName("Should reject null user ID")
        void shouldRejectNullUserId() {
            // When & Then
            assertThatThrownBy(() -> UserHashUtil.hashUser(null, "flag123"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("User ID and Flag ID cannot be null");
        }

        @Test
        @DisplayName("Should reject null flag ID")
        void shouldRejectNullFlagId() {
            // When & Then
            assertThatThrownBy(() -> UserHashUtil.hashUser("user123", null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("User ID and Flag ID cannot be null");
        }
    }
}

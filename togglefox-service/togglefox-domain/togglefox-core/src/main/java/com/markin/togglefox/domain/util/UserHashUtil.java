package com.markin.togglefox.domain.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public final class UserHashUtil {

    private UserHashUtil() {
    }

    /**
     * Hash a user ID and flag ID combination to produce a consistent
     * percentage value (0-99) for rollout decisions.
     *
     * @param userId The user identifier
     * @param flagId The feature flag identifier
     * @return A consistent hash value between 0-99
     */
    public static int hashUser(String userId, String flagId) {
        if (userId == null || flagId == null) {
            throw new IllegalArgumentException("User ID and Flag ID cannot be null");
        }

        // Combine user ID and flag ID to ensure different flags
        // can have different rollout results for the same user
        String combined = userId + ":" + flagId;

        try {
            // Use SHA-256 for consistent, uniform distribution
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(combined.getBytes(StandardCharsets.UTF_8));

            // Convert first 4 bytes to a positive integer
            int hash = Math.abs(
                    ((hashBytes[0] & 0xFF) << 24) |
                            ((hashBytes[1] & 0xFF) << 16) |
                            ((hashBytes[2] & 0xFF) << 8) |
                            (hashBytes[3] & 0xFF)
            );

            // Map to 0-99 range
            return hash % 100;

        } catch (NoSuchAlgorithmException e) {
            // SHA-256 should always be available, but fallback to simpler hash
            return Math.abs(combined.hashCode()) % 100;
        }
    }

    /**
     * Validate that the hash function produces uniform distribution
     * (useful for testing)
     */
    public static boolean isUniformDistribution(String[] userIds, String flagId,
                                                double tolerance) {
        if (userIds.length < 1000) {
            throw new IllegalArgumentException("Need at least 1000 users for meaningful distribution test");
        }

        int[] buckets = new int[10]; // 0-9, 10-19, ..., 90-99

        for (String userId : userIds) {
            int hash = hashUser(userId, flagId);
            buckets[hash / 10]++;
        }

        double expectedPerBucket = userIds.length / 10.0;

        for (int count : buckets) {
            double deviation = Math.abs(count - expectedPerBucket) / expectedPerBucket;
            if (deviation > tolerance) {
                return false;
            }
        }

        return true;
    }
}
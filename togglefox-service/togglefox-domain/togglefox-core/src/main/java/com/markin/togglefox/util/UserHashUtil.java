package com.markin.togglefox.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class UserHashUtil {
    private static final String HASH_ALGORITHM = "SHA-256";
    private static final String SALT = "togglemate-domain-salt";

    private UserHashUtil() {
    }

    public static int hashUser(String userId) {
        if (userId == null) {
            throw new IllegalArgumentException("User ID cannot be null");
        }

        try {
            MessageDigest digest = MessageDigest.getInstance(HASH_ALGORITHM);
            String saltedUserId = SALT + userId;
            byte[] hash = digest.digest(saltedUserId.getBytes(StandardCharsets.UTF_8));

            int result = 0;
            for (int i = 0; i < 4 && i < hash.length; i++) {
                result = (result << 8) + (hash[i] & 0xFF);
            }

            return Math.abs(result);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Hash algorithm not available", e);
        }
    }

    public static boolean isUserInPercentage(String userId, int percentage) {
        if (percentage < 0 || percentage > 100) {
            throw new IllegalArgumentException("Percentage must be between 0 and 100");
        }

        int hash = hashUser(userId);
        return (hash % 100) < percentage;
    }
}

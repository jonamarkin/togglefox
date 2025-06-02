package com.markin.togglefox.common;

import java.util.regex.Pattern;

public final class ValidationUtils {

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Za-z0-9+_.-]+@([A-Za-z0-9.-]+\\.[A-Za-z]{2,})$"
    );

    private static final Pattern FLAG_NAME_PATTERN = Pattern.compile(
            "^[a-zA-Z0-9._-]+$"
    );

    private ValidationUtils() {
        // Utility class - prevent instantiation
    }

    /**
     * Check if a string is null or empty (after trimming)
     */
    public static boolean isNullOrEmpty(String value) {
        return value == null || value.trim().isEmpty();
    }

    /**
     * Check if a string is not null and not empty
     */
    public static boolean isNotEmpty(String value) {
        return !isNullOrEmpty(value);
    }

    /**
     * Validate email format
     */
    public static boolean isValidEmail(String email) {
        return isNotEmpty(email) && EMAIL_PATTERN.matcher(email).matches();
    }

    /**
     * Validate feature flag name format
     */
    public static boolean isValidFlagName(String name) {
        return isNotEmpty(name) &&
                name.length() <= 100 &&
                FLAG_NAME_PATTERN.matcher(name).matches();
    }

    /**
     * Validate that a value is within a range (inclusive)
     */
    public static boolean isInRange(int value, int min, int max) {
        return value >= min && value <= max;
    }

    /**
     * Validate that a percentage is valid (0-100)
     */
    public static boolean isValidPercentage(int percentage) {
        return isInRange(percentage, 0, 100);
    }

    /**
     * Sanitize a string by trimming and handling null
     */
    public static String sanitize(String value) {
        return value == null ? null : value.trim();
    }
}

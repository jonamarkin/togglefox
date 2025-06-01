package com.markin.togglefox.port.out;

public record CacheStats(
        long hitCount,
        long missCount,
        long totalRequests,
        double hitRatio,
        long evictionCount,
        long totalCachedItems
) {
    public static CacheStats empty() {
        return new CacheStats(0, 0, 0, 0.0, 0, 0);
    }

    public CacheStats withHit() {
        return new CacheStats(
                hitCount + 1,
                missCount,
                totalRequests + 1,
                calculateHitRatio(hitCount + 1, totalRequests + 1),
                evictionCount,
                totalCachedItems
        );
    }

    public CacheStats withMiss() {
        return new CacheStats(
                hitCount,
                missCount + 1,
                totalRequests + 1,
                calculateHitRatio(hitCount, totalRequests + 1),
                evictionCount,
                totalCachedItems
        );
    }

    private static double calculateHitRatio(long hits, long total) {
        return total > 0 ? (double) hits / total : 0.0;
    }
}

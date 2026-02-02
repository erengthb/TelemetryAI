package com.telemetryai.backend.common;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RateLimiterService {
    private final double ratePerSecond;
    private final double burst;
    private final Map<String, TokenBucket> buckets = new ConcurrentHashMap<>();

    public RateLimiterService(double ratePerSecond, double burst) {
        this.ratePerSecond = ratePerSecond;
        this.burst = burst;
    }

    public boolean tryConsume(String key) {
        TokenBucket bucket = buckets.computeIfAbsent(key, k -> new TokenBucket(burst));
        synchronized (bucket) {
            bucket.refill(ratePerSecond, burst);
            if (bucket.tokens < 1.0d) {
                return false;
            }
            bucket.tokens -= 1.0d;
            return true;
        }
    }

    private static final class TokenBucket {
        private double tokens;
        private long lastRefillNanos;

        private TokenBucket(double tokens) {
            this.tokens = tokens;
            this.lastRefillNanos = System.nanoTime();
        }

        private void refill(double ratePerSecond, double burst) {
            long now = System.nanoTime();
            double elapsedSeconds = (now - lastRefillNanos) / 1_000_000_000.0d;
            if (elapsedSeconds > 0) {
                tokens = Math.min(burst, tokens + elapsedSeconds * ratePerSecond);
                lastRefillNanos = now;
            }
        }
    }
}

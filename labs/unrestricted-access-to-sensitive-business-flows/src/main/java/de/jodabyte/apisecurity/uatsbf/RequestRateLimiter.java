package de.jodabyte.apisecurity.uatsbf;

import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;


/**
 * Simple request rate limiter that allows only a certain number of requests per hour for each principal.
 */
public class RequestRateLimiter {

    private static final Integer MAX_REQUESTS_PER_HOUR = 1;

    private final Cache cache;

    public RequestRateLimiter(CacheManager cacheManager) {
        this.cache = cacheManager.getCache("request-rate-limit");
    }

    public boolean isAllowed(String principal) {
        if (principal == null || principal.isBlank()) {
            return false;
        }

        Integer count = cache.get(principal, Integer.class);
        if (count != null && count >= MAX_REQUESTS_PER_HOUR) {
            return false;
        }

        cache.put(principal, 1);
        return true;
    }
}

package de.jodabyte.apisecurity.uatsbf;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Filter that restricts access based on request frequency for authenticated users.
 * It checks if the authenticated user has made a request within the last hour and denies access if so.
 */
public class RestrictedFlowFilter extends OncePerRequestFilter {

    private final RequestRateLimiter limiter;

    public RestrictedFlowFilter(RequestRateLimiter limiter) {
        this.limiter = limiter;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated()) {
            this.logger.warn("Authentication required");
            return;
        }

        String principal = auth.getName();
        if (!limiter.isAllowed(principal)) {
            this.logger.warn("Access denied: rate limit exceeded");
            response.sendError(HttpStatus.TOO_MANY_REQUESTS.value(), "Rate limit: one request per hour");
            return;
        }

        filterChain.doFilter(request, response);
    }
}

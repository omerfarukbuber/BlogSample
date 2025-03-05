package com.omerfbuber.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.omerfbuber.extension.CustomResults;
import com.omerfbuber.result.Error;
import com.omerfbuber.result.Result;
import io.github.bucket4j.Bucket;
import jakarta.servlet.*;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Duration;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class RateLimitingFilter implements Filter {

    private final ConcurrentHashMap<String, Bucket> buckets = new ConcurrentHashMap<>();
    private static final Duration DURATION_OF_MINUTES = Duration.ofMinutes(1);
    private static final long REFILL_TOKEN_COUNT = 10;
    private static final long BUCKET_CAPACITY = 15;

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {
        String ipAddress = servletRequest.getRemoteAddr();
        Bucket bucket = buckets.computeIfAbsent(ipAddress, k -> createBucket());

        if (bucket.tryConsume(1)) {
            filterChain.doFilter(servletRequest, servletResponse);
        }
        else {
            var objectMapper = new ObjectMapper();
            var error = Error.tooManyRequests(REFILL_TOKEN_COUNT / DURATION_OF_MINUTES.toMinutes());
            var problemDetail = CustomResults.toProblemDetail(Result.failure(error));
            servletResponse.setContentType("application/json");
            servletResponse.getWriter().write(objectMapper.writeValueAsString(problemDetail));
        }
    }

    private Bucket createBucket() {
        return Bucket.builder()
            .addLimit(
                limit -> limit.capacity(BUCKET_CAPACITY).refillGreedy(REFILL_TOKEN_COUNT, DURATION_OF_MINUTES))
            .build();
    }
}

package com.omerfbuber.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.time.Duration;
import java.time.Instant;

@Component
public class LoggingInterceptor implements HandlerInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(LoggingInterceptor.class);
    private static final ThreadLocal<Instant> requestStartTime = new ThreadLocal<>();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        requestStartTime.set(Instant.now());

        String clientIp = request.getRemoteAddr();
        String userAgent = request.getHeader("User-Agent");
        String queryString = request.getQueryString();
        String fullUrl = request.getRequestURI() + (queryString != null ? "?" + queryString : "");

        logger.info("Request received | Method: {} | Path: {} | Client IP: {} | User-Agent: {}",
                request.getMethod(), fullUrl, clientIp, userAgent);

        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        Instant start = requestStartTime.get();
        if (start != null) {
            long elapsedTime = Duration.between(start, Instant.now()).toMillis();
            requestStartTime.remove();

            String queryString = request.getQueryString();
            String fullUrl = request.getRequestURI() + (queryString != null ? "?" + queryString : "");

            logger.info("Response sent | Status: {} | Path: {} | Duration: {}ms",
                    response.getStatus(), fullUrl, elapsedTime);
        }
    }
}

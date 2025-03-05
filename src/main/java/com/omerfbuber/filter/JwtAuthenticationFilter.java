package com.omerfbuber.filter;

import com.omerfbuber.service.user.CustomUserDetailsService;
import com.omerfbuber.util.TokenProvider;
import jakarta.annotation.Nonnull;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Set;

@Component
public class JwtAuthenticationFilter  extends OncePerRequestFilter {

    private final TokenProvider tokenProvider;
    private final CustomUserDetailsService userDetailsService;

    public JwtAuthenticationFilter(TokenProvider tokenProvider, CustomUserDetailsService userDetailsService) {
        this.tokenProvider = tokenProvider;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, @Nonnull HttpServletResponse response, @Nonnull FilterChain filterChain)
            throws ServletException, IOException {

        final String bearer = "Bearer ";
        final String authHeader = request.getHeader("Authorization");

        final Set<String> permittedUrls = Set.of("/api/auth/", "/swagger-ui/", "/v3/api-docs/");

        //For the register endpoint with POST method
        final String userPostEndpoint = "/api/users";

        if (authHeader == null || !authHeader.startsWith(bearer)) {
            filterChain.doFilter(request, response);
            return;
        }

        if (request.getRequestURI().startsWith(userPostEndpoint) && request.getMethod().equals("POST")) {
            filterChain.doFilter(request, response);
            return;
        }

        if (permittedUrls.stream().anyMatch(permittedUrl -> request.getRequestURI().startsWith(permittedUrl))) {
            filterChain.doFilter(request, response);
            return;
        }

        String authToken = authHeader.substring(bearer.length());
        String email = tokenProvider.extractEmail(authToken);

        if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(email);
            if (tokenProvider.isTokenValid(authToken, userDetails.getUsername())){
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }

        filterChain.doFilter(request, response);
    }
}

package com.omerfbuber.configurations;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.omerfbuber.extensions.CustomResults;
import com.omerfbuber.extensions.JwtAuthenticationFilter;
import com.omerfbuber.results.Error;
import com.omerfbuber.results.Result;
import com.omerfbuber.services.shared.PasswordHasher;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final PasswordHasher passwordHasher;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(PasswordHasher passwordHasher, JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.passwordHasher = passwordHasher;
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth ->
                        auth
                            .requestMatchers("/swagger-ui/**").permitAll()
                            .requestMatchers("/v3/api-docs/**").permitAll()
                            .requestMatchers(HttpMethod.GET, "/api/users/**").hasAuthority("User.Read")
                            .requestMatchers(HttpMethod.PUT, "/api/users/**").hasAuthority("User.Update")
                            .requestMatchers(HttpMethod.DELETE, "/api/users/**").hasAuthority("User.Delete.Self")
                            .requestMatchers(HttpMethod.POST, "/api/users").permitAll()
                            .requestMatchers("api/auth/**").permitAll()
                            .anyRequest().authenticated())
                .exceptionHandling(exceptionHandler ->
                        exceptionHandler.authenticationEntryPoint(
                            (request, response, authException) -> {
                                handleError(response, authException, HttpServletResponse.SC_UNAUTHORIZED);
                            })
                        .accessDeniedHandler(
                            (request, response, accessDeniedException) -> {
                                handleError(response, accessDeniedException, HttpServletResponse.SC_FORBIDDEN);
                            }
                        ))
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    private void handleError(HttpServletResponse response, RuntimeException ex, int statusCode) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        response.setContentType("application/json");
        Error error = switch (statusCode) {
            case 401 -> Error.unauthorized(ex.getClass().getSimpleName(), ex.getMessage());
            case 403 -> Error.forbidden(ex.getClass().getSimpleName(), ex.getMessage());
            default -> Error.NONE;
        };
        response.setStatus(statusCode);
        var problemDetail = CustomResults.toProblemDetail(Result.failure(error));
        response.getWriter().write(objectMapper.writeValueAsString(problemDetail));
    }

    @Bean
    public AuthenticationManager authenticationManager(UserDetailsService userDetailsService) {
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
        daoAuthenticationProvider.setUserDetailsService(userDetailsService);
        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder());
        return new ProviderManager(daoAuthenticationProvider);
    }

    @Bean
    public PasswordEncoder passwordEncoder()
    {
        return passwordHasher.getPasswordEncoder();
    }

}

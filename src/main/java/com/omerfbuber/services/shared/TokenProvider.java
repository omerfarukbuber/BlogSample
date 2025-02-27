package com.omerfbuber.services.shared;

import com.omerfbuber.dtos.auth.response.TokenResponse;

public interface TokenProvider {
    TokenResponse generateToken(String email);
    boolean isTokenValid(String token, String email);
    String extractEmail(String token);
}

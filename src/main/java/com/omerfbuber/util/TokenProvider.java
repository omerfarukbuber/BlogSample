package com.omerfbuber.util;

import com.omerfbuber.dto.auth.TokenResponse;

public interface TokenProvider {
    TokenResponse generateToken(String email);
    boolean isTokenValid(String token, String email);
    String extractEmail(String token);
}

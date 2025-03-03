package com.omerfbuber.services.auth;

import com.omerfbuber.dto.auth.LoginRequest;
import com.omerfbuber.dto.auth.RefreshTokenRequest;
import com.omerfbuber.dto.auth.TokenResponse;
import com.omerfbuber.result.Result;

public interface AuthenticationService {
    Result<TokenResponse> login(LoginRequest request);
    Result<TokenResponse> refreshToken(RefreshTokenRequest request);
    Result<Void> logout();
}

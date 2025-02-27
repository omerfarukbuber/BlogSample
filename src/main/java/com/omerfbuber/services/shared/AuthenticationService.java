package com.omerfbuber.services.shared;

import com.omerfbuber.dtos.auth.request.LoginRequest;
import com.omerfbuber.dtos.auth.request.RefreshTokenRequest;
import com.omerfbuber.dtos.auth.response.TokenResponse;
import com.omerfbuber.results.Result;

public interface AuthenticationService {
    Result<TokenResponse> login(LoginRequest request);
    Result<TokenResponse> refreshToken(RefreshTokenRequest request);
    Result<Void> logout();
}

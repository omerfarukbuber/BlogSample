package com.omerfbuber.services.shared;

import com.omerfbuber.dtos.auth.response.TokenResponse;
import com.omerfbuber.results.Result;

public interface AuthenticationService {
    Result<TokenResponse> login(String email, String password);
}

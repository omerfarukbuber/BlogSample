package com.omerfbuber.controllers;

import com.omerfbuber.dtos.auth.request.LoginRequest;
import com.omerfbuber.dtos.auth.response.TokenResponse;
import com.omerfbuber.extensions.ResponseEntityExtension;
import com.omerfbuber.results.Result;
import com.omerfbuber.services.shared.AuthenticationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthenticationService authenticationService;

    public AuthController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@RequestBody LoginRequest loginRequest) {
        Result<TokenResponse> result = authenticationService.login(loginRequest.email(), loginRequest.password());
        return ResponseEntityExtension.okOrProblem(result);
    }
}

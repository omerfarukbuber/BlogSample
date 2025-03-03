package com.omerfbuber.service.auth;

import com.omerfbuber.dto.auth.LoginRequest;
import com.omerfbuber.dto.auth.RefreshTokenRequest;
import com.omerfbuber.dto.auth.TokenResponse;
import com.omerfbuber.entity.RefreshToken;
import com.omerfbuber.entity.User;
import com.omerfbuber.repository.RefreshTokenRepository;
import com.omerfbuber.repository.UserRepository;
import com.omerfbuber.result.Error;
import com.omerfbuber.result.Result;
import com.omerfbuber.util.TokenProvider;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class AuthenticationServiceImpl implements AuthenticationService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final TokenProvider tokenProvider;
    private final AuthenticationManager authenticationManager;
    private final HttpServletRequest httpRequest;

    public AuthenticationServiceImpl(UserRepository userRepository, RefreshTokenRepository refreshTokenRepository,
                                     TokenProvider tokenProvider,AuthenticationManager authenticationManager,
                                     HttpServletRequest httpRequest) {
        this.userRepository = userRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.tokenProvider = tokenProvider;
        this.authenticationManager = authenticationManager;
        this.httpRequest = httpRequest;
    }

    @Override
    public Result<TokenResponse> login(LoginRequest request) {
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password()));

        return createAndSaveToken(user);
    }

    @Override
    public Result<TokenResponse> refreshToken(RefreshTokenRequest request) {
        var entity = refreshTokenRepository.findByToken(request.refreshToken()).orElse(null);
        if (entity == null) {
            return Result.failure(Error.notFound("RefreshToken.NotFound", "RefreshToken not found"));
        }

        if (entity.getExpiresAt().before(new Date())) {
            refreshTokenRepository.delete(entity);
            return Result.failure(Error.notFound("RefreshToken.Expired", "RefreshToken expired"));
        }

        return createAndSaveToken(entity.getUser());
    }

    @Override
    public Result<Void> logout() {
        var token = getTokenFromHeader();
        if (token.isEmpty()) {
            return Result.failure(Error.notFound("AccessToken.NotFound", "Access token could not be found"));
        }
        var email = tokenProvider.extractEmail(token);
        var user = userRepository.findByEmail(email).orElse(null);
        if (user == null) {
            return Result.failure(Error.notFound("User.NotFound", "User not found"));
        }

        var refreshToken = refreshTokenRepository.findByUserAndClientInfo(
                user, httpRequest.getRemoteAddr()).orElse(null);

        SecurityContextHolder.clearContext();

        if (refreshToken == null) {
            return Result.success();
        }

        refreshTokenRepository.delete(refreshToken);
        return Result.success();
    }

    private String getTokenFromHeader() {
        var authHeader = httpRequest.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return "";
    }

    private Result<TokenResponse> createAndSaveToken(User user) {
        var tokenResponse = tokenProvider.generateToken(user.getEmail());

        saveRefreshToken(user, tokenResponse.refreshToken(), tokenResponse.refreshTokenExpire());

        return Result.success(tokenResponse);
    }

    private void saveRefreshToken(User user, String token, Date expires) {
        var clientInfo = httpRequest.getRemoteAddr();
        var entity = refreshTokenRepository.findByUserAndClientInfo(user, clientInfo);

        entity.ifPresent(refreshTokenRepository::delete);

        var refreshToken = new RefreshToken(
                null,
                user,
                token,
                expires,
                new Date(),
                clientInfo
        );

        refreshTokenRepository.save(refreshToken);
    }


}

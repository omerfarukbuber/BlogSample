package com.omerfbuber.services.shared;

import com.omerfbuber.dtos.auth.response.TokenResponse;
import com.omerfbuber.entities.User;
import com.omerfbuber.repositories.users.UserRepository;
import com.omerfbuber.results.Result;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationServiceImpl implements AuthenticationService {

    private final UserRepository userRepository;
    private final TokenProvider tokenProvider;
    private final AuthenticationManager authenticationManager;

    public AuthenticationServiceImpl(UserRepository userRepository, TokenProvider tokenProvider,
                                     AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.tokenProvider = tokenProvider;
        this.authenticationManager = authenticationManager;
    }

    @Override
    public Result<TokenResponse> login(String email, String password) {

        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, password));

        User user = userRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return Result.success(tokenProvider.generateToken(user.getEmail()));
    }
}

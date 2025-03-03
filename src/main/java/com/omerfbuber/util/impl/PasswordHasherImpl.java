package com.omerfbuber.util.impl;

import com.omerfbuber.util.PasswordHasher;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class PasswordHasherImpl implements PasswordHasher {
    private final PasswordEncoder passwordEncoder;

    public PasswordHasherImpl() {
        this.passwordEncoder = getPasswordEncoder();
    }

    @Override
    public String hash(String password) {
        return passwordEncoder.encode(password);
    }

    @Override
    public boolean verify(String password, String hashedPassword) {
        return passwordEncoder.matches(password, hashedPassword);
    }

    @Override
    public PasswordEncoder getPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }
}

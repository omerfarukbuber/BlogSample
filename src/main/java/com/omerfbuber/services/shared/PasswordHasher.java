package com.omerfbuber.services.shared;

import org.springframework.security.crypto.password.PasswordEncoder;

public interface PasswordHasher {
    String hash(String password);
    boolean verify(String password, String hashedPassword);
    PasswordEncoder getPasswordEncoder();
}

package com.omerfbuber.util;

import org.springframework.security.crypto.password.PasswordEncoder;

public interface PasswordHasher {
    String hash(String password);
    boolean verify(String password, String hashedPassword);
    PasswordEncoder getPasswordEncoder();
}

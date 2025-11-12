package com.example.AIResumeAnalyzer.security;

import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;

@Component
public class JwtKeyHolder {

    @Getter
    private static SecretKey key;

    @Value("${jwt.secret:}")
    private String secret;

    @PostConstruct
    public void init() {
        if (secret == null || secret.isEmpty()) {
            throw new IllegalStateException("JWT_SECRET not found! Make sure it's set in environment variables.");
        }

        key = Keys.hmacShaKeyFor(secret.getBytes());
    }
}


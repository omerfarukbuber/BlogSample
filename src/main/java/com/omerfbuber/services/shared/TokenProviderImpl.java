package com.omerfbuber.services.shared;

import com.omerfbuber.dtos.auth.response.TokenResponse;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.function.Function;

@Component
public class TokenProviderImpl implements TokenProvider {
    private final Key key;

    @Value("${security.jwt.expiration}")
    private long expirationInMinutes;

    public TokenProviderImpl(@Value("${security.jwt.secret}") String secret) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes());
    }

    @Override
    public TokenResponse generateToken(String email) {
        long expirationInMillis = (expirationInMinutes * 60 * 1000L);
        Date now = new Date();
        Date expirationDate = new Date(System.currentTimeMillis() + expirationInMillis);
        String token = Jwts.builder()
                .setSubject(email)
                .setIssuedAt(now)
                .setExpiration(expirationDate)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

        return new TokenResponse(token, expirationDate);
    }

    @Override
    public boolean isTokenValid(String token, String email) {
        return extractEmail(token).equals(email) && !isTokenExpired(token);
    }

    @Override
    public String extractEmail(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    private boolean isTokenExpired(String token) {
        return extractClaim(token, Claims::getExpiration).before(new Date());
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claimsResolver.apply(claims);
    }
}

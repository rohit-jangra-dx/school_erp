package org.example.schoolerp.security.auth;

import java.util.Date;

import javax.crypto.SecretKey;

import org.example.schoolerp.identity.entity.AuthAccount;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {

    private static final String ORGANIZATION_ID_CLAIM = "organization_id";

    private final SecretKey key;
    private final long expirationMs;
    

    public JwtService(
            @Value("${school-erp.security.secret-key}") String secretKey,
            @Value("${school-erp.security.access-token-expiration}") long accessTokenExpiration) {
        this.key = Keys.hmacShaKeyFor(secretKey.getBytes());
        this.expirationMs = accessTokenExpiration;
    }

    public String generateToken(AuthAccount authAccount) {
        return Jwts.builder()
                .subject(authAccount.getUser().getUsername())
                .claim(
                        ORGANIZATION_ID_CLAIM,
                        authAccount.getUser().getOrganization().getId())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expirationMs))
                .signWith(key)
                .compact();
    }

    public String extractUsername(String token) {
        return parseClaims(token).getSubject();
    }

    public Long extractOrganizationId(String token) {
        return parseClaims(token).get(ORGANIZATION_ID_CLAIM, Long.class);
    }
    
    public boolean isValid(String token, String expectedUsername) {
        try {
            Claims claims = parseClaims(token);
            return claims.getSubject().equals(expectedUsername)
                && claims.getExpiration().after(new Date());

        } catch (Exception e) {
            return false;
        }
    }

    public Claims parseClaims(String token) {
        return Jwts.parser()
            .verifyWith(key)
            .build()
            .parseSignedClaims(token)
            .getPayload();
    }
}

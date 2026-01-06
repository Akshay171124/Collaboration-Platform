package com.akshay.notionlite.security;


import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.UUID;

@Component
public class JwtTokenProvider {

    // Move to application.yml and load securely (AWS Secrets Manager later)
    private final Key key = Keys.secretKeyFor(SignatureAlgorithm.HS256);
    private final long ttlMs = 1000L * 60 * 60 * 24; // 24h

    public String generate(UUID userId, String email) {
        Date now = new Date();
        Date exp = new Date(now.getTime() + ttlMs);

        return Jwts.builder()
                .setSubject(userId.toString())
                .claim("email", email)
                .setIssuedAt(now)
                .setExpiration(exp)
                .signWith(key)
                .compact();
    }

    public JwtPrincipal parse(String token) {
        var claims = Jwts.parserBuilder().setSigningKey(key).build()
                .parseClaimsJws(token).getBody();

        UUID userId = UUID.fromString(claims.getSubject());
        String email = (String) claims.get("email");
        return new JwtPrincipal(userId, email);
    }
}

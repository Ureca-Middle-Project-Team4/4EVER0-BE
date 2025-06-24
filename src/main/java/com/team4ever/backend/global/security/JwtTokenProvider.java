package com.team4ever.backend.global.security;

import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtTokenProvider {
    @Value("${app.auth.tokenSecret}")
    private String secretKey;
    @Value("${app.auth.accessTokenExpirationMsec}")
    private long accessTokenValidity;
    @Value("${app.auth.refreshTokenExpirationMsec}")
    private long refreshTokenValidity;

    public String createAccessToken(String userId) {
        Date now = new Date();
        return Jwts.builder()
                .setSubject(userId)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + accessTokenValidity))
                .signWith(SignatureAlgorithm.HS512, secretKey)
                .compact();
    }

    public String createRefreshToken(String userId) {
        Date now = new Date();
        return Jwts.builder()
                .setSubject(userId)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + refreshTokenValidity))
                .signWith(SignatureAlgorithm.HS512, secretKey)
                .compact();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    public String getUserId(String token) {
        return Jwts.parser()
                .setSigningKey(secretKey)
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }
}
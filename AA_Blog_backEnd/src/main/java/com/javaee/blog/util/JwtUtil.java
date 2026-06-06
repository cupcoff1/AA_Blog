package com.javaee.blog.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtUtil {

    private final SecretKey key;
    private final long ttl;

    public JwtUtil(@Value("${jwt.secret}") String secret,
                   @Value("${jwt.ttl}") long ttl) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.ttl = ttl;
    }

    public String generateToken(String username) {
        Date now = new Date();
        return Jwts.builder()
                .subject(username)
                .issuedAt(now)
                .expiration(new Date(now.getTime() + ttl * 1000))
                .signWith(key)
                .compact();
    }

    /** 生成评论者 Token，含 avatar 信息 */
    public String generateCommenterToken(String username, String avatar) {
        Date now = new Date();
        return Jwts.builder()
                .subject(username)
                .claim("avatar", avatar)
                .issuedAt(now)
                .expiration(new Date(now.getTime() + ttl * 1000))
                .signWith(key)
                .compact();
    }

    public Claims parseToken(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}

package com.laksh.ott_enterprise.auth.service;

import com.laksh.ott_enterprise.auth.entity.UserEntity;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Duration;
import java.util.Date;
import java.util.concurrent.TimeUnit;

@Service
public class JwtService {

    @Value("${jwt.secret:404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970}")
    private String secretKey;

    @Value("${jwt.access-token-expiration:900000}") // 15 mins
    private long accessTokenExpiration;

    private final StringRedisTemplate redisTemplate;

    public JwtService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public String generateAccessToken(UserEntity userDetails) {
        return buildToken(userDetails, accessTokenExpiration);
    }

    private String buildToken(UserEntity userDetails, long expiration) {
        return Jwts.builder()
                .subject(userDetails.getEmail())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSignInKey()) // Algorithm HS256 is automatically inferred from SecretKey length
                .compact();
    }

    public boolean isTokenValid(String token, String userEmail) {
        final String username = extractUsername(token);
        boolean isBlacklisted = Boolean.TRUE.equals(redisTemplate.hasKey("blackList:" + token));
        return (username.equals(userEmail)) && !isTokenExpired(token) && !isBlacklisted;
    }

    public void invalidateToken(String token) {
        long expiration = getExpirationDate(token).getTime() - System.currentTimeMillis();
        if (expiration > 0) {
            redisTemplate.opsForValue().set(
                    "blackList:" + token,
                    "true",
                    Duration.ofMillis(expiration)
            );
        }
    }

    public String extractUsername(String token) {
        return extractAllClaims(token).getSubject();
    }

    private boolean isTokenExpired(String token) {
        return getExpirationDate(token).before(new Date());
    }

    private Date getExpirationDate(String token) {
        return extractAllClaims(token).getExpiration();
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSignInKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private SecretKey getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
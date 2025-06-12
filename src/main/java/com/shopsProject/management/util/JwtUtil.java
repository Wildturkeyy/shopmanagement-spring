package com.shopsProject.management.util;

import com.shopsProject.management.config.JwtProperties;
import java.util.Date;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtUtil {

    private final JwtProperties jwtProperties;

    /**
     * Acess Token 발급 메서드
     * @param uuid
     * @param userId
     * @param role "WHOLESALER', 'RETAILER', 'AGENT'
     * @return
     */
    public String generateAccessToken(String uuid, String userId, String role) {

        return Jwts.builder()
            .setSubject(uuid)
            .claim("userId", userId)
            .claim("role", role)
            .setIssuedAt(new Date())
            .setExpiration(new Date(System.currentTimeMillis() + jwtProperties.getAccessTokenExpireMs()))
            .signWith(Keys.hmacShaKeyFor(jwtProperties.getSecret().getBytes()))
            .compact();
    }

    /**
     * Refresh Token 발급 메서드
     * @param uuid
     * @return
     */
    public String generateRefreshToken(String uuid) {
        return Jwts.builder()
            .setSubject(uuid)
            .setIssuedAt(new Date())
            .setExpiration(new Date(System.currentTimeMillis() + jwtProperties.getRefreshTokenExpireMs()))
            .signWith(Keys.hmacShaKeyFor(jwtProperties.getSecret().getBytes()))
            .compact();
    }

    /**
     * 토큰 안에 든 userId 정보 꺼내기
     * @param token
     * @return
     */
    public String extractUserId(String token) {
        return (String)Jwts.parserBuilder()
            .setSigningKey(jwtProperties.getSecret().getBytes())
            .build()
            .parseClaimsJws(token)
            .getBody()
            .get("userId");
    }

    /**
     * 토큰 유효성 검증 메서드
     * @param token
     * @return
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                .setSigningKey(jwtProperties.getSecret().getBytes())
                .build()
                .parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            log.warn("[토큰 유효성 검증] 실패");
            return false;
        }
    }

    /**
     * 토큰 안에 든 uuid 정보 꺼내기
     * @param token
     * @return String uuid
     */
    public String extractUuid(String token) {
        return Jwts.parserBuilder()
            .setSigningKey(jwtProperties.getSecret().getBytes())
            .build()
            .parseClaimsJws(token)
            .getBody()
            .getSubject();
    }

    /**
     * 토큰 안에 든 Role 정보 꺼내기
     * @param token
     * @return String role
     */
    public String extractRole(String token) {
        return (String) Jwts.parserBuilder()
            .setSigningKey(jwtProperties.getSecret().getBytes())
            .build()
            .parseClaimsJws(token)
            .getBody()
            .get("role");
    }
}

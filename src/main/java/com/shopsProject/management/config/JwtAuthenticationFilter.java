package com.shopsProject.management.config;

import com.shopsProject.management.security.CustomUserDetails;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * JWT 인증 필터
 * - HTTP 요청의 Authorization 헤더에서 accessToken 추출
 * - 유효한 값이면 user인증 정보(CustomUserDetails)를 생성해 SecurityContext에 등록
 * - 인증이 필요한 모든 API, 요청마다 동작 (SpringFilterChain 내장)
 */
@Slf4j
@AllArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtProperties jwtProperties;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
        FilterChain filterChain) throws ServletException, IOException {

        String header = request.getHeader("Authorization");

        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);

            try {
                var claims = Jwts.parserBuilder().setSigningKey(jwtProperties.getSecret().getBytes()).build().parseClaimsJws(token).getBody();

                String uuid = claims.getSubject();
                String userId = (String)claims.get("userId");
                String role = (String)claims.get("role");

                log.debug("[JWT] 토큰 인증 성공 / uuid: {}", uuid);

                CustomUserDetails userDetails = new CustomUserDetails(uuid, userId, null, role);
                List<SimpleGrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_" + role));

                Authentication authentication =
                    new UsernamePasswordAuthenticationToken(userDetails, null, authorities);
                SecurityContextHolder.getContext().setAuthentication(authentication);

            } catch (Exception e) {
                log.warn("[JWT] 인증 또는 파싱 실패: {}", e.getMessage());
            }

        }
        filterChain.doFilter(request, response);
    }
}

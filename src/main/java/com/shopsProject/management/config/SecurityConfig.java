package com.shopsProject.management.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Spring Security 보안 설정 클래스
 * - JWT 인증 기반 REST API 환경에서 필요한 필터, 인증 인가 정책
 * - Swagger, 로그인/회원가입 등 비인증 허용, 나머지는 토큰 인증 필요
 */
@EnableMethodSecurity(prePostEnabled = true, securedEnabled = true)
@Configuration
@RequiredArgsConstructor
public class SecurityConfig {
    private final JwtProperties jwtProperties;
    private static final String[] AUTH_WHITELIST = {
        "/graphiql", "/graphql",
        "/swagger-ui/**", "/api-docs", "/swagger-ui-custom.html", "/swagger-custom-ui.html",
        "/v3/api-docs/**", "/api-docs/**", "/swagger-ui.html"
    };

    /**
     * 스프링 시큐리티 필터체인 빈 설정
     * @param http HttpSecurity 객체
     * @return SecurityFilterChain
     * @throws Exception
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            // JWT 환경이므로 폼 로그인/베이직 인증 비활성화
            .formLogin(form -> form.disable())
            .httpBasic(basic -> basic.disable())

            // 경로별 인가 정책
            .authorizeHttpRequests(request -> request
                .requestMatchers("/api/v1/auth/login", "/api/v1/auth/signup", "/api/v1/auth/refresh").permitAll()
                .requestMatchers(AUTH_WHITELIST).permitAll()
                .requestMatchers("/api/v1/**").authenticated()
                .anyRequest().denyAll()
            )

            // JWT 필터 (토큰 인증)
            .addFilterBefore(new JwtAuthenticationFilter(jwtProperties), UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    /**
     * BCrypt 패스워드 인코더 빈 등록
     * @return
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}

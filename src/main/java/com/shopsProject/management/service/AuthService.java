package com.shopsProject.management.service;

import com.shopsProject.management.Repository.RefreshTokenRepository;
import com.shopsProject.management.Repository.UserRepository;
import com.shopsProject.management.domain.RefreshToken;
import com.shopsProject.management.domain.User;
import com.shopsProject.management.dto.LoginRequest;
import com.shopsProject.management.dto.LoginResponse;
import com.shopsProject.management.dto.TokenRefreshRequest;
import com.shopsProject.management.dto.TokenRefreshResponse;
import com.shopsProject.management.exception.CustomException;
import com.shopsProject.management.util.JwtUtil;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    /**
     * 사용자 로그인
     * @param req 회원 정보 : userId, password
     * @return LoginResponseDto 회원 정보를 포함한 AccessToken, RefreshToken
     */
    @Transactional
    public LoginResponse login(LoginRequest req) {
        log.info("[회원 관리] 로그인 / 회원 userId: {}", req.userId());
        // 1. 유저 찾기
        User user = userRepository.findByUserId(req.userId())
            .orElseThrow(() -> {
                log.warn("[회원 관리] 로그인 - 아이디 오류 / 회원 userId(masking): {}", mask(req.userId()));
                return new CustomException("USER_NOT_FOUND", "아이디 오류", HttpStatus.UNAUTHORIZED);
            });

        // 2. 비밀번호 대조
        if (!passwordEncoder.matches(req.password(), user.getPassword())) {
            log.warn("[회원 관리] 로그인 - 비밀번호 오류 / userId(masking): {}", mask(req.userId()));
            throw new CustomException("INVALID_PASSWORD", "비밀번호 오류", HttpStatus.UNAUTHORIZED);
        }

        // 3. 토큰 생성
        String accessToken = jwtUtil.generateAccessToken(user.getUuid(), user.getUserId(), user.getRole().name());
        String refreshToken = jwtUtil.generateRefreshToken(user.getUuid());

        // 4. refreshToken 저장
        refreshTokenRepository.deleteByUser(user);
        refreshTokenRepository.save(RefreshToken.builder()
            .refreshToken(refreshToken)
            .user(user)
            .createdAt(LocalDateTime.now())
            .expiresAt(LocalDateTime.now().plusDays(7))
            .build());

        log.info("[회원 관리] 로그인 - 로그인 완료 / uuid: {}, userId(masking): {}",user.getUuid(), mask(req.userId()));
        return new LoginResponse(accessToken, refreshToken);
    }

    /**
     * 토큰 재발급
     * @param req refreshToken
     * @return AccessToken, refreshToken
     */
    public TokenRefreshResponse refreshToken(TokenRefreshRequest req) {
        String userUuid = jwtUtil.extractUuid(req.refreshToken());
        log.info("[회원 관리] 토큰 재발급 요청 / uuid: {}", userUuid);

        String refreshToken = req.refreshToken();
        if (!jwtUtil.validateToken(refreshToken)) {
            log.warn("[회원 관리] 토큰 재발급 - 유효하지 않은 토큰 오류 / uuid: {}", mask(userUuid));
            throw new CustomException("INVALID_TOKEN", "유효하지 않은 토큰", HttpStatus.UNAUTHORIZED);
        }

        RefreshToken tokenEntity = refreshTokenRepository.findByRefreshToken(refreshToken)
            .orElseThrow(() -> {
                log.warn("[회원 관리] 토큰 재발급 - 유효하지 않은 리프레시 토큰 오류 / uuid: {}", userUuid);
                return new CustomException("INVALID_REFRESH_TOKEN", "유요하지 않은 리프레시 토큰", HttpStatus.UNAUTHORIZED);
            });

        if (tokenEntity.getExpiresAt().isBefore(LocalDateTime.now())) {
            log.warn("[회원 관리] 토큰 재발급 - 만료된 리프레시 토큰 오류 / uuid: {}", userUuid);
            throw new CustomException("EXPIRES_TOKEN", "만료된 토큰", HttpStatus.UNAUTHORIZED);
        }

        log.debug("[회원 관리] 토큰 재발급 - 토큰 유효성 검사 완료 / uuid: {}", userUuid);

        User user = userRepository.findById(userUuid)
            .orElseThrow(() -> {
                log.error("[회원 관리] 토큰 재발급 실패 - 인증되지 않은 사용자 오류 / uuid: {}", userUuid);
                return new CustomException("USER_NOT_FOUND", "인증되지 않은 사용자", HttpStatus.UNAUTHORIZED);
            });

        // 새 AccessToken 발급
        String newAccessToken = jwtUtil.generateAccessToken(user.getUuid(), user.getUserId(), user.getRole().name());
        log.info("[회원 관리] 토큰 재발급 완료 / uuid: {}", userUuid);
        return new TokenRefreshResponse(newAccessToken, refreshToken);
    }

    public void logout(String refreshToken) {
        String userUuid = jwtUtil.extractUuid(refreshToken);
        log.info("[회원 관리] 로그아웃 요청 / uuid: {}", userUuid);

        refreshTokenRepository.findByRefreshToken(refreshToken)
            .ifPresentOrElse(
                t -> {
                    refreshTokenRepository.delete(t);
                    log.info("[회원 관리] 로그아웃 완료 / uuid: {}", userUuid);
                },
                () -> log.warn("[회원 관리] 로그아웃 - 이미 소멸된 토큰 / uuid: {}", userUuid)
            );
    }

    private String mask(String id) {
        if (id == null || id.length() < 3) return "***";
        int n = id.length();
        return id.substring(0, 1) + "*".repeat(n-2) +id.substring(n-1);
    }
}

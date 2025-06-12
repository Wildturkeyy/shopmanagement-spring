package com.shopsProject.management.controller;

import com.shopsProject.management.dto.ErrorResponse;
import com.shopsProject.management.dto.LoginRequest;
import com.shopsProject.management.dto.LoginResponse;
import com.shopsProject.management.dto.TokenRefreshRequest;
import com.shopsProject.management.dto.TokenRefreshResponse;
import com.shopsProject.management.service.AuthService;
import com.shopsProject.management.util.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 인증/회원 관련 API 컨트롤러
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final JwtUtil jwtUtil;

    /**
     * 로그인 API
     * @param req 로그인 요청 정보 (userId, password)
     * @return JWT accessToken, refreshToken, role 등이 포함된 응답값
     */
    @Operation(
        summary = "로그인",
        description = "userId, password로 인증하면, uuid, userId, role 등을 포함한 AccessToken, refreshToken 반환",
        responses = {
            @ApiResponse(responseCode = "200", description = "성공",
                content = @Content(schema = @Schema(implementation = LoginResponse.class))),
            @ApiResponse(responseCode = "401", description = "인증 실패",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest req) {
        log.info("[회원 관리 API 요청] 로그인 / userId: {}", req.userId());
        return ResponseEntity.ok(authService.login(req));
    }

    /**
     * 토큰 재발급 API. 만료된 accessToken을 refreshToken으로 재발행
     * @param req refreshToken 정보
     * @return 신규 accessToken
     */
    @Operation(
        summary = "토큰 재발급",
        description = "refreshToken으로 accessToken을 새로 발급",
        responses = {
            @ApiResponse(responseCode = "200", description = "성공",
                content = @Content(schema = @Schema(implementation = TokenRefreshResponse.class))),
            @ApiResponse(responseCode = "401", description = "인증 실패",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/refresh")
    public ResponseEntity<TokenRefreshResponse> refresh(@RequestBody TokenRefreshRequest req) {
        String uuid = jwtUtil.extractUuid(req.refreshToken());
        log.info("[회원 관리 API 요청] 토큰 재발급 요청 / uuid: {}", uuid);
        return ResponseEntity.ok(authService.refreshToken(req));
    }

    /**
     * 로그아웃 처리
     * @param accessToken 헤더에서 받은 AccessToken (Authorization)
     * @param req body에서 받은 refreshToken
     * @return 성공/실패 메시지
     */
    @Operation(
        summary = "로그아웃",
        description = "AccessToken(헤더) + refreshToken(바디)로 로그아웃 처리",
        responses = {
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "401", description = "인증 실패",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/logout")
    public ResponseEntity<?> logout(
        @Parameter(name = "Authorization", description = "Access Token, Bearer {token}", in = ParameterIn.HEADER, required = true)
        @RequestHeader("Authorization") String accessToken,
        @RequestBody TokenRefreshRequest req) {
        String uuid = jwtUtil.extractUuid(accessToken.substring(7));
        log.info("[회원 관리 API 요청] 로그아웃 / uuid: {}", uuid);
        authService.logout(req.refreshToken());
        return ResponseEntity.ok("로그아웃 완료");
    }
}
package com.shopsProject.management.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "토큰 재발급 응답 dto")
public record TokenRefreshResponse(String accessToken, String refreshToken) {
}

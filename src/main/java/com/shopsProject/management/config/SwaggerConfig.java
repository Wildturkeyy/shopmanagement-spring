package com.shopsProject.management.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * Swagger/OpenAPI 구성
 * - API info(제목/설명/버전) 정의
 * - JWT Bearer 인증 스키마를 전체 API에 적용 (Swagger UI에서 Authorize 사용 가능)
 * - @Profile("!prod")로 운영 서버에서는 문서 자동 비활성화
 */
@OpenAPIDefinition(
    info = @Info(
        title = "쇼핑몰 통합 관리 프로젝트 API 명세서",
        description = "쇼핑몰 통합 관리 프로젝트에 사용되는 API 명세서",
        version = "v1"
    )
)
@Configuration
public class SwaggerConfig {

    private static final String SECURITY_SCHEME_NAME = "Authorization"; // 또는 "BearerAuth"

    @Bean
    @Profile("!prod") // 소문자 profile 적용(운영에서 비활성화)
    public OpenAPI openAPI() {
        // Security설정: 모든 API에 Bearer 토큰 적용
        SecurityRequirement securityRequirement = new SecurityRequirement().addList(SECURITY_SCHEME_NAME);
        Components components = new Components()
            .addSecuritySchemes(SECURITY_SCHEME_NAME,
                new SecurityScheme()
                    .name(SECURITY_SCHEME_NAME)
                    .type(SecurityScheme.Type.HTTP)
                    .scheme("bearer")
                    .bearerFormat("JWT")
            );

        return new OpenAPI()
            .addSecurityItem(securityRequirement)
            .components(components);
    }

    // grouping 기능을 쓸 경우 예시(생략 가능)
    // @Bean
    // public GroupedOpenApi publicApi() {
    //     return GroupedOpenApi.builder()
    //         .group("api")
    //         .pathsToMatch("/api/**")
    //         .build();
    // }
}

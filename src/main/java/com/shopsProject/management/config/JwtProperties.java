package com.shopsProject.management.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * application에 저장된 jwt값을 가져와 사용할 수 있게 하는 class
 * jwt-secret
 * jwt-acess-token-expires-ms
 * jwt-refres-token-expires-ms
 */
@Getter @Setter
@Component
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {
    private String secret;
    private long accessTokenExpireMs;
    private long refreshTokenExpireMs;
}

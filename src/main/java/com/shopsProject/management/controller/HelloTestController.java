package com.shopsProject.management.controller;

import com.shopsProject.management.dto.LoginRequest;
import com.shopsProject.management.security.CustomUserDetails;
import jakarta.validation.Valid;
import java.util.Locale;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class HelloTestController {

    public record AuthTestResponse(String userID, String uuid, String userId, String role) {}

    @PreAuthorize("hasRole('WHOLESALER')")
    @PostMapping("/api/v1/auth-test")
    public ResponseEntity<?> authTest(
        @RequestBody @Valid LoginRequest req,
        @AuthenticationPrincipal CustomUserDetails principal) {
        log.info("[auth test] / principal: {}", principal);

        if (principal == null) {
            return ResponseEntity.status(401).build();
        }
        return ResponseEntity.ok(
            new AuthTestResponse(
                req.userId(),
                principal.getUuid(),
                principal.getUserId(),
                principal.getRole()
            )
        );
    }

}

package com.shopsProject.management.controller;

import com.shopsProject.management.dto.ErrorResponse;
import com.shopsProject.management.dto.WholesaleProdDto;
import com.shopsProject.management.security.CustomUserDetails;
import com.shopsProject.management.service.WholesaleProdService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 도매상품 관련 API 컨트롤러
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/wholesaler/products")
@RequiredArgsConstructor
public class WholesaleProdController {

    private final WholesaleProdService wholesaleProdService;

    /**
     * 도매업체 상품 등록
     * @param req 상품 등록 요청 DTO
     * @param principal 로그인된 도매업체(WHOLESALER 사용자) 정보
     * @return 생성된 상품 ID
     */
    @Operation(
        summary = "도매업체 상품 등록",
        description = "도매업체 전용 상품 등록",
        responses = {
            @ApiResponse(responseCode = "200", description = "성공",
                content = @Content(schema = @Schema(implementation = WholesaleProdDto.CreateResponse.class))),
            @ApiResponse(responseCode = "400", description = "등록 오류",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PreAuthorize("hasRole('WHOLESALER')")
    @PostMapping
    public ResponseEntity<WholesaleProdDto.CreateResponse> createProd(
        @RequestBody @Valid WholesaleProdDto.CreateRequest req,
        @AuthenticationPrincipal CustomUserDetails principal) {
        log.info("[도매업체 상품등록] API 호출 / userId: {}, 상품명: {}", principal.getUserId(), req.getProdValue().getProductName());
        return ResponseEntity.ok(wholesaleProdService.createProduct(req, principal.getUuid()));
    }
}

package com.shopsProject.management.controller;

import com.shopsProject.management.dto.WholesaleProdVariantDto;
import com.shopsProject.management.dto.WholesaleProdVariantDto.ProdVariantDto;
import com.shopsProject.management.security.CustomUserDetails;
import com.shopsProject.management.service.WholesaleProdVariantService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/v1/wholesaler/products")
@RequiredArgsConstructor
public class WholesaleProdVariantController {

    private final WholesaleProdVariantService prodVariantService;

    @Operation(
        summary = "도매 업체 상품 전체 재고 목록 반환"
    )
    @PreAuthorize("hasRole('WHOLESALER')")
    @GetMapping("/variants")
    public Page<ProdVariantDto> getProdVariantPage(
        @RequestParam(required = false) List<Integer> categoryIds,
        @RequestParam(required = false) Boolean isActive,
        @RequestParam(required = false) String keyword,
        Pageable pageable,
        @AuthenticationPrincipal CustomUserDetails principal) {
        log.info("[도매 상품 전체 재고] 요청 Controller / 요청자: {}", principal.getUuid());
        return prodVariantService.GetProdVariantPage(principal.getUuid(), categoryIds, isActive, keyword, pageable);
    }

    /**
     * 도매업체 특정 상품의 전체 재고 목록 조회
     * @param productId
     * @return
     */
    @Operation(
        summary = "도매업체의 특정 상품의 (by productId) 전체 재고 옵션 목록 조회",
        description = "도매업체 특정 상품의 옵션 productId, (id, size, color stock) 목록 반환",
        responses = {
            @ApiResponse(responseCode = "200", description = "성공",
            content = @Content(schema = @Schema(implementation = WholesaleProdVariantDto.GetProdVariantListResponse.class)))
        }
    )
    @PreAuthorize("hasRole('WHOLESALER')")
    @GetMapping("/{productId}/variants")
    public ResponseEntity<WholesaleProdVariantDto.GetProdVariantListResponse> getProdVariantsByProdId(
        @PathVariable Long productId,
        @AuthenticationPrincipal CustomUserDetails principal) {
        log.info("[도매 상품 옵션] 상품 id로 조회 Controller / productId: {}", productId);

        return ResponseEntity.ok(prodVariantService.GetProdVariantsByProdId(productId, principal.getUuid()));
    }

    /**
     *
     * @param productId
     * @param req
     * @param principal
     * @return
     */
    @Operation(
        summary = "도매업체의 특정 상품의 (by productId) 전체 재고 옵션 수정 요청",
        description = "수정된 특정 상품의 재고 옵션 productId, (id, size, color, stock) 목록 반환",
        responses = {
            @ApiResponse(responseCode = "200", description = "수정 성공"),
            @ApiResponse(responseCode = "401", description = "사용자 인증 실패")
        }
    )
    @PreAuthorize("hasRole('WHOLESALER')")
    @PatchMapping("/{productId}/variants")
    public ResponseEntity<WholesaleProdVariantDto.GetProdVariantListResponse> updateProdVariantsByProdId(
        @PathVariable Long productId,
        @RequestBody @Valid WholesaleProdVariantDto.UpdateProdVariantListRequest req,
        @AuthenticationPrincipal CustomUserDetails principal) {
        log.info("[도매 상품 옵션 수정] 상품 id로 조회한 상품 재고 수정 요청 Controller / productId: {}", productId);

        return ResponseEntity.ok(prodVariantService.updateProdVariantsByProdId(productId,
            principal.getUuid(), req));
    }

    /**
     * 도매 업체 상품 중 특정 상품의 특정 재고 옵션 수정 by variantId
     * @param productId
     * @param variantId
     * @param req Integer stock
     * @param principal
     * @return productId, variantId, size, color, stock
     */
    @Operation(
        summary = "도매상품 재고 수정 by variantId",
        description = "도매 업체 상품 중 특정 상품의 특정 재고 옵션의 재고 수정 요청 by variantId",
        responses = {
            @ApiResponse(responseCode = "200", description = "성공",
                content = @Content(schema = @Schema(implementation = WholesaleProdVariantDto.updateProdVariantResponse.class)))
        }
    )
    @PreAuthorize("hasRole('WHOLESALER')")
    @PatchMapping("/{productId}/variants/{variantId}")
    public ResponseEntity<WholesaleProdVariantDto.updateProdVariantResponse> updateProdVariantByVariantId(
        @PathVariable Long productId,
        @PathVariable Long variantId,
        @RequestBody @Valid WholesaleProdVariantDto.updateProdVariantRequest req,
        @AuthenticationPrincipal CustomUserDetails principal ) {

        log.info("[도매 상품 옵션 수정 by Variant] 수정 요청 Controller / 요청자: {}, variantId: {}", principal.getUuid(), variantId);

        return ResponseEntity.ok(prodVariantService.updateProdVariantByVariantId(productId,variantId, req.getStock(), principal.getUuid()));
    }
}
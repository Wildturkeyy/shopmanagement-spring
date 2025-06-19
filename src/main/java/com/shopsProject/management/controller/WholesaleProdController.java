package com.shopsProject.management.controller;

import com.shopsProject.management.dto.ErrorResponse;
import com.shopsProject.management.dto.WholesaleProdDto;
import com.shopsProject.management.security.CustomUserDetails;
import com.shopsProject.management.service.CategoryService;
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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
    private final CategoryService categoryService;

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
        log.info("[도매업체 상품등록] 상품 등록 Controller / 요청자: {}, 상품명: {}", principal.getUuid(), req.getProdValue().getProductName());
        return ResponseEntity.ok(wholesaleProdService.createProduct(req, principal.getUuid()));
    }

    /**
     * 도매업체 상품 상세 페이지
     * @param productId
     * @param principal 로그인된 도매업체(WHOLESALER 사용자) 정보
     * @return 상품 정보 DTO
     */
    @Operation(
        summary = "도매업체 상품 디테일 요청",
        description = "productId로 도매업체의 상품 디테일 요청 / 상품 상세 페이지",
        responses = {
            @ApiResponse(responseCode = "200", description = "성공",
                content = @Content(schema = @Schema(implementation = WholesaleProdDto.ProdResponse.class)))
        }
    )
    @PreAuthorize("hasRole('WHOLESALER')")
    @GetMapping("/{productId}")
    public ResponseEntity<?> getProductItemByProductId(
        @PathVariable Long productId,
        @AuthenticationPrincipal CustomUserDetails principal) {

        log.info("[도매상품 상품 디테일] 요청 Controller / 요청자: {}, productId: {}", principal.getUuid(), productId);
        return ResponseEntity.ok(wholesaleProdService.getProductDetails(productId, principal.getUuid(), "details"));
    }

    /**
     * 도매 업체 상품 수정 페이지
     * @param productId
     * @param principal 로그인된 도매업체(WHOLESALER 사용자) 정보
     * @return 상품 정보 DTO + 카테고리 DTO
     */
    @Operation(
        summary = "도매업체 상품 정보 수정을 위한 디테일 요청",
        description = "productId로 도매업체의 상품 디테일 요청 / 상품 수정 페이지",
        responses = {
            @ApiResponse(responseCode = "200", description = "성공",
                content = @Content(schema = @Schema(implementation = WholesaleProdDto.ProdDetailsForUpdateResponse.class)))
        }
    )
    @PreAuthorize("hasRole('WHOLESALER')")
    @GetMapping("/{productId}/edit")
    public ResponseEntity<WholesaleProdDto.ProdDetailsForUpdateResponse> getProductItemByProductIdForUpdate(
        @PathVariable Long productId,
        @AuthenticationPrincipal CustomUserDetails principal) {

        log.info("[도매상품 상품 디테일] 수정을 위한 정보 요청 Controller / 요청자: {}, productId: {}", principal.getUuid(), productId);
        return ResponseEntity.ok(wholesaleProdService.getProductDetailsForEdit(productId, principal.getUuid()));
    }

    /**
     * 도매 업체 상품 상태 변경 (판매중 / 판매중단)
     * @param productId
     * @param req 변경하고자 하는 상태 true/false , true: 판매중으로 변경 요청, false: 판매중단으로 변경 요청
     * @param principal 로그인된 도매업체(WHOLESALER 사용자) 정보
     * @return
     */

    @Operation(
        summary = "도매 업체 상품의 상태 (판매중/판매중단) 변경",
        description = "변경하고 싶은 상태를 요청하면 유효성 검증 후 변경 / 판매중으로 변경을 원하는 경우 true, 판매 중단으로 변경을 원하는 경우 false",
        responses = {
            @ApiResponse(responseCode = "200", description = "성공",
                content = @Content(schema = @Schema(implementation  = WholesaleProdDto.UpdateIsActiveResponse.class))),
            @ApiResponse(responseCode = "400", description = "이미 요청 값과 동일",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
        }
    )
    @PreAuthorize("hasRole('WHOLESALER')")
    @PatchMapping("/{productId}/is-active")
    public ResponseEntity<WholesaleProdDto.UpdateIsActiveResponse> updateIsActive(
        @PathVariable Long productId,
        @RequestBody @Valid WholesaleProdDto.UpdateIsActiveRequest req,
        @AuthenticationPrincipal CustomUserDetails principal) {

        log.info("[도매업체 상품 활동 변경] is Active 변경 Controller / 요청자: {}, productId: {}", principal.getUuid(), productId);
        return ResponseEntity.ok(wholesaleProdService.updateIsActive(productId, principal.getUuid(), req.isActive()));
    }

    /**
     * 도배업체 상품 삭제 요청
     * @param productId
     * @param principal
     * @return
     */
    @Operation(
        summary = "도매업체 상품 삭제 요청",
        description = "판매내역이 없는 상품은 삭제, 있는 상품은 판매 중단 유도 필요",
        responses = {
            @ApiResponse(responseCode = "200", description = "성공")
        }
    )
    @PreAuthorize("hasRole('WHOLESALER')")
    @DeleteMapping("/{productId}")
    public ResponseEntity<?> deleteProduct(
        @PathVariable Long productId,
        @AuthenticationPrincipal CustomUserDetails principal) {

        log.info("[도매업체 상품 삭제] 요청 Controller / 요청자: {}, productId: {}", principal.getUuid(), productId);

        record deletedProdResponse(Long productId, String message) {};
        wholesaleProdService.deleteProduct(productId, principal.getUuid());

        return ResponseEntity.ok(new deletedProdResponse(productId, "요청한 상품 정보가 삭제되었습니다."));
    }
}

package com.shopsProject.management.dto;

import com.shopsProject.management.domain.WholesaleProdVariant;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * 도매업체 상품 재고 옵션 DTO
 */
public class WholesaleProdVariantDto {

    // 도매업체 전체 상품 재고 목록
    @Getter @Setter @Builder
    public static class ProdVariantDto {
        private Long id;
        private Long productId;
        private String productName;
        private String size;
        private String color;
        private Integer stock;
        private Integer price;
        private Boolean isActive;
        private String categoryName;

        public static ProdVariantDto from(WholesaleProdVariant v) {
            return ProdVariantDto.builder()
                .id(v.getId())
                .productId(v.getProduct().getId())
                .productName(v.getProduct().getProductName())
                .size(v.getSize())
                .color(v.getColor())
                .stock(v.getStock())
                .price(v.getProduct().getPrice())
                .isActive(v.getProduct().isActive())
                .categoryName(v.getProduct().getCategory().getCategoryName())
                .build();
        }
    }

    @Getter @Setter @Builder
    public static class ProdVariants {
        private Long productId;
        private String productName;
        private List<ProdVariantItem> prodVariantItems;
    }

    // 특정 상품(by productId)의 재고 목록 요청
    @Getter @Setter @Builder
    public static class GetProdVariantListResponse {
        private Long productId;
        private List<ProdVariantItem> prodVariants;
    }

    // 특정 상품(by productId)의 전체 재고 수정 요청
    @Getter @Setter @Builder
    public static class UpdateProdVariantListRequest {
        @NotNull(message = "재고는 필수 입력값입니다.")
        private List<@Valid ProdStock> prodVariants;
    }

    @Getter @Setter
    public static class ProdStock {
        @NotNull
        private Long id;
        @Min(value = 0, message = "0이상의 숫자를 입력해주세요.")
        @NotNull
        private Integer stock;
    }

    @Getter @Setter @Builder
    public static class ProdVariantItem {
        private Long id;
        private String size;
        private String color;
        @Min(value = 0, message = "0이상의 숫자를 입력해주세요.")
        @NotNull(message = "재고를 입력해주세요.")
        private Integer stock;
    }

}

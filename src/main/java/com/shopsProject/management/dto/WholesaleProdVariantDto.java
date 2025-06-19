package com.shopsProject.management.dto;

import com.shopsProject.management.domain.WholesaleProd;
import com.shopsProject.management.domain.WholesaleProdVariant;
import com.shopsProject.management.dto.WholesaleProdDto.ProdValue;
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

    // 도매업체 전체 상품 재고 목록 : Page로 나갈 것
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

    // 특정 상품(by productId)의 재고 목록 요청
    @Getter @Setter @Builder
    public static class GetProdVariantListResponse {
        private Long productId;
        private List<ProdVariantItem> prodVariants;
    }

    // 특정 상품(by productId)의 전체 재고 수정 요청
    @Getter @Setter
    public static class UpdateProdVariantListRequest {
        @NotNull(message = "재고는 필수 입력값입니다.")
        private List<@Valid ProdStock> prodStocks;
    }

    // 특정 상품의 재고 옵션(by variantId)의 재고 수정 요청
    @Getter @Setter
    public static class updateProdVariantRequest {
        @NotNull(message = "재고는 필수 입력입니다.")
        @Min(value = 0, message = "0이상의 숫자를 입력해주세요.")
        private Integer stock;
    }

    @Getter @Setter @Builder
    public static class updateProdVariantResponse {
        private Long productId;
        private ProdVariantItem prodVariant;
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

        public static ProdVariantItem from(WholesaleProdVariant p) {
            return ProdVariantItem.builder()
                .id(p.getId())
                .size(p.getSize())
                .color(p.getColor())
                .stock(p.getStock())
                .build();
        }
    }

}

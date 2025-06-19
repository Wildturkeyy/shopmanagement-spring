package com.shopsProject.management.dto;

import com.shopsProject.management.validation.StrictCommaSeparated;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import java.util.List;
import java.util.PrimitiveIterator;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * 도매업체 상품 관련 DTO
 */
public class WholesaleProdDto {

    // 상품 등록 Dto
    @Getter @Setter
    public static class CreateRequest {
        private ProdValue prodValue;

        @StrictCommaSeparated(message = "사이즈/색상 구분은 쉼표(,)만 사용해 주세요")
        private String sizes; // "s, m, l"

        @StrictCommaSeparated(message = "사이즈/색상 구분은 쉼표(,)만 사용해 주세요")
        private String colors; // "블랙, 아이보리, 핑크"

    }

    @Getter @Setter
    public static class CreateResponse {
        private Long productId;
    }

    // 상품 수정 Dto
    @Getter @Setter
    public static class ModifyRequest {
        private Long productId;

        private ProdValue prodValue;

        private List<@Valid StockOption> stockOptions;
    }

    // 상품 dto
    @Getter @Setter @Builder
    public static class ProdResponse {
        private Long productId;

        private ProdValue prodValue;

        private List<@Valid StockOption> stockOptions;
    }

    // 상품 수정 페이지 open dto
    @Getter @Setter @Builder
    public static class ProdDetailsForUpdateResponse {
        private Long productId;
        private ProdValue prodValue;
        private List<@Valid StockOption> stockOptions;
        private List<CategoryDto> categories;
    }

    // 상품 정보 dto
    @Getter @Setter @Builder
    public static class ProdValue {
        @NotBlank(message = "상품명을 작성해주세요.")
        @Size(max = 100, message = "상품명은 100자 이내로 작성해 주세요.")
        private String productName;

        @NotNull(message = "카테고리를 선택해주세요.")
        private Category category;

        @Positive(message = "가격은 0보다 큰 수로 작성해 주세요.")
        private Integer price; //null이면 0처리
        private Boolean isSmplAva; //null이면 true 처리

        @Schema(description = "도매업체 상품 등록자만 볼 수 있는 메모칸 / 샘플실이나 재고 예정 등을 작성할 수 있음")
        private String memo;

        @Schema(description = "블로그 형식의 상품 디테일을 작성하는 디테일 블록 / 쇼핑몰처럼 작성할 수 있음.")
        private String description;

        private List<@Valid Image> images;

        private List<@Valid DetailBlock> detailBlocks;
    }

    // 카테고리 dto
    @Getter @Setter @Builder
    public static class Category {
        private int id;
        private String name;
    }

    // 이미지 dto
    @Getter @Setter @Builder
    public static class Image {
        private Long id;
        @NotBlank
        private String imgUrl;
        private int sortOrder;
    }

    // 상세 정보 (블로그 블록) dto
    @Getter @Setter @Builder
    public static class DetailBlock {
        private Long id;
        @Schema(description = "해당 블록 타임" ,  example = "TEXT, IMAGE")
        @NotBlank(message = "blockType은 필수입니다.") // TEXT, IMAGE, VIDEO
        private String blockType;
        private String content;
        private String imgUrl;
        private int blockOrder;
    }

    // 재고 옵션 dto
    @Getter @Setter @Builder
    public static class StockOption {
        private Long id;
        private String color;
        private String size;
        private int stock;
    }

    // /////////////////////////////////////////////////////////////
    // 상품 "판매중" "판매중단" 상태변경 dto
    @Getter @Setter
    public static class UpdateIsActiveRequest {
        private boolean isActive;
    }

    @Getter @Setter @Builder
    public static class UpdateIsActiveResponse {
        private Long productId;
        private boolean isActive;
    }

}

package com.shopsProject.management.dto;

import com.shopsProject.management.domain.Category;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Schema(description = "카테고리 리스트 전달 dto")

@Builder
public  class CategoryDto {
    private Integer id;
    private String name;

    public static CategoryDto from(Category c) {
        return CategoryDto.builder()
            .id(c.getId())
            .name(c.getCategoryName())
            .build();
    }
}

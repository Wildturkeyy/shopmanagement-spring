package com.shopsProject.management.service;

import com.shopsProject.management.Repository.CategoryRepository;
import com.shopsProject.management.dto.CategoryDto;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public List<CategoryDto> getCategory() {
        return categoryRepository.findAll().stream()
            .map(CategoryDto::from)
            .toList();
    }
}

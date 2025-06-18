package com.shopsProject.management.Repository;

import com.shopsProject.management.domain.WholesaleProdVariant;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface WholesaleProdVariantRepositoryCustom {
    Page<WholesaleProdVariant> findByUserAndFilter(
        String userUuid, List<Integer> categoryIds, Boolean isActive, String keyword, Pageable pageable
    );
}

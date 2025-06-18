package com.shopsProject.management.Repository;

import com.shopsProject.management.domain.WholesaleProdVariant;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WholesaleProdVariantRepository extends JpaRepository<WholesaleProdVariant, Long>, WholesaleProdVariantRepositoryCustom {
    List<WholesaleProdVariant> findByProductId(Long product_id);
}

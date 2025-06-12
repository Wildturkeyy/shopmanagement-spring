package com.shopsProject.management.Repository;

import com.shopsProject.management.domain.WholesaleProd;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * 도매업체 상품 repository
 */
@Repository
public interface WholesaleProdRepository extends JpaRepository<WholesaleProd, Long> {

}

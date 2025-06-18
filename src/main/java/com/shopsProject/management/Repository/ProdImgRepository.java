package com.shopsProject.management.Repository;

import com.shopsProject.management.domain.ProdImg;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProdImgRepository extends JpaRepository<ProdImg, Long> {

}

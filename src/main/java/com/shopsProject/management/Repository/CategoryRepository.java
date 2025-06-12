package com.shopsProject.management.Repository;

import com.shopsProject.management.domain.Category;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Integer>{
    Optional<Category> findById(int id);
}

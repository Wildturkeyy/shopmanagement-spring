package com.shopsProject.management.config;

import com.shopsProject.management.Repository.CategoryRepository;
import com.shopsProject.management.Repository.UserRepository;
import com.shopsProject.management.domain.Category;
import com.shopsProject.management.domain.Role;
import com.shopsProject.management.domain.User;
import java.util.UUID;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * 프로그램 실행 시 우선 생성되어 DB에 저장되는 값
 * - tester 유저
 * - 카테고리
 */
@Component
public class DataInitializer implements ApplicationRunner {
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserRepository userRepository, CategoryRepository categoryRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
        this.passwordEncoder = passwordEncoder;
    }


    @Override
    public void run(ApplicationArguments args) throws Exception {
        if (userRepository.count() == 0) {
            String encodedPw = passwordEncoder.encode("test1234");
            userRepository.save(User.builder()
                .uuid(UUID.randomUUID().toString())
                .userId("tester")
                .password(encodedPw)
                .role(Role.WHOLESALER)
                .build());
        }

        if (categoryRepository.count() == 0) {
            categoryRepository.save(Category.builder()
                .categoryName("상의")
                .build());

            categoryRepository.save(Category.builder()
                .categoryName("아우터")
                .build());

            categoryRepository.save(Category.builder()
                .categoryName("바지")
                .build());

            categoryRepository.save(Category.builder()
                .categoryName("치마")
                .build());

            categoryRepository.save(Category.builder()
                .categoryName("원피스")
                .build());

            categoryRepository.save(Category.builder()
                .categoryName("세트")
                .build());

            categoryRepository.save(Category.builder()
                .categoryName("액세서리")
                .build());

            categoryRepository.save(Category.builder()
                .categoryName("신발")
                .build());
        }
    }
}

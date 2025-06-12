package com.shopsProject.management.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

/**
 * 입력값이 쉼표(,)로만 구분되도록 검증하는 커스텀 Validator Annotaion
 * ex.) "M, L, XL", "블랙, 화이트, 핑크"
 *
 * @Target(FIELD): DTO, 엔티티
 * @Constraint(validatedBy = StrictCommaSeparatedValidator.class): 실제 검증 클래스
 * message: 검증 실패시 클라이언트에 반환될 에러 메시지
 *
 * 사용 예:
 * @StrictCommaSeparated
 * private String sizes;
 */
@Documented
@Constraint(validatedBy = StrictCommaSeparatedValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface StrictCommaSeparated {
    String message() default "쉼표(,)만 구분자로 사용해 주세요.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}

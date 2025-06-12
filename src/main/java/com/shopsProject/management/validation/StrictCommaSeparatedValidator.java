package com.shopsProject.management.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * @StrictCommaSeparated 어노테이션이 붙은 필드에 대해
 * 빈 값이 아니면서, 세미콜론(;)이나 슬래시(/), 파이프(|), 탭 등의
 * 구분자가 포함되어 있으면 검증 실패 반환
 *
 * 사용 예시:
 * @StrictCommaSeparated
 * private String sizes;
 *
 * 검증 규칙
 * - null, 빈 문자열을 Ok
 * - 쉼표(,)만 허용, ;/|\t 등이 포함되면 실패
 * - 두 개 이상의 값이 쉼표로 구분되는 경우 OK
 */
public class StrictCommaSeparatedValidator implements ConstraintValidator<StrictCommaSeparated, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        // 빈 값
        if (value == null || value.trim().isEmpty()) return true;

        if (value.matches(".*[;|/\\\\t].*")) return false;

        return true;
    }
}

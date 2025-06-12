package com.shopsProject.management.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * 비즈니스/인증/검증 커스텀 예외 클래스
 *
 * - eroorCode: "USER_NOT_FOUND"
 * - httpStatus:  HTTP 상태 코드 / 400, 401, 403, 500
 * - message: 상세 설명 "존재하지 않는 사용자"
 *
 * ex.)
 * throw new CustomException("USER_NOT_FOUND", "존재하지 않는 사용자", HttpStatus.NOT_FOUND);
 */
@Getter
public class CustomException extends RuntimeException{
    private final String errorCode;
    private final HttpStatus httpStatus;

    /**
     * 예외 생성자
     * @param errorCode 에러코드
     * @param message 에러 메시지
     * @param status 반환할 HTTP 상태코드
     */
    public CustomException(String errorCode, String message, HttpStatus status) {
        super(message);
        this.errorCode = errorCode;
        this.httpStatus = status;
    }
}

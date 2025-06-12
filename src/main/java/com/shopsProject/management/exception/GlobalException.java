package com.shopsProject.management.exception;

import com.shopsProject.management.dto.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 글로벌 예외 처리기(Global Exception Handler
 * - 프로젝트 전역(@RestControllerAdvice)에서 발생하는 모든 예외를 Handling
 * - CustomException(비즈니스/권한 등)과 그 외 모든 예외를 분리 처리
 * - HttpStatus, errorCode, message 구조의 일관된 JSON 응답 반환
 * - 예외 발생시 응담
 * {
 *     "errorCode": "USER_NOT_FOUND",
 *     "message": "아이디 오류"
 * }
 */
@RestControllerAdvice
public class GlobalException {

    /**
     * 비즈니스/인증 관련 CustomException처치
     * @param e
     * @return 에러코드가 담긴 JSON 응답
     */
    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ErrorResponse> handleCustomException(CustomException e) {
        return ResponseEntity
            .status(e.getHttpStatus())
            .body(new ErrorResponse(e.getErrorCode(), e.getMessage()));
    }
//    @ExceptionHandler(CustomException.class)
//    public ResponseEntity<ErrorResponse> handleAuthException(CustomException e) {
//        // 권한 인증 401
//        return ResponseEntity
//            .status(HttpStatus.UNAUTHORIZED)
//            .body(new ErrorResponse(e.getErrorCode(), e.getMessage()));
//    }
//
//    @ExceptionHandler(Exception.class)
//    public ResponseEntity<ErrorResponse> handleBad(CustomException e) {
//        // 잘못된 입력 400 (Bad Request)
//        return ResponseEntity
//            .status(HttpStatus.BAD_REQUEST)
//            .body(new ErrorResponse(e.getErrorCode(), e.getMessage()));
//    }
//
//    @ExceptionHandler(Exception.class)
//    public ResponseEntity<ErrorResponse> handleNotFound(CustomException e) {
//        // 해당 데이터 없음 404
//        return ResponseEntity
//            .status(HttpStatus.NOT_FOUND)
//            .body(new ErrorResponse(e.getErrorCode(), e.getMessage()));
//    }

    /**
     * 그 외 모든 예외 (예상치 못한 시스템 에러)
     * @param e
     * @return errorCode: "INTERNAL_ERROR, HttpStatus 500
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleAll(Exception e) {
        // 기타 모든 예외 500
        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(new ErrorResponse("INTERNAL_ERROR", e.getMessage()));
    }
}

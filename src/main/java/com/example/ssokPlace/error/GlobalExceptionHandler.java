package com.example.ssokPlace.error;

import com.example.ssokPlace.common.CommonResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 요청 값 검증 실패 (DTO @Valid)
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<CommonResponse<Void>> handleValidation(MethodArgumentNotValidException e) {
        log.warn("Validation failed: {}", e.getMessage());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(CommonResponse.error(HttpStatus.BAD_REQUEST, "요청 값이 올바르지 않습니다."));
    }

    /**
     * 우리 도메인에서 정의한 ReportableError 처리
     */
    @ExceptionHandler(ReportableError.class)
    public ResponseEntity<CommonResponse<Void>> handleReportable(ReportableError e) {
        return ResponseEntity
                .status(e.getStatus())
                .body(CommonResponse.error(e.getStatus(), e.getClientMessage()));
    }

    /**
     * 인증/인가 관련 에러
     */
    @ExceptionHandler({UsernameNotFoundException.class, BadCredentialsException.class})
    public ResponseEntity<CommonResponse<Void>> authErrors(Exception e) {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(CommonResponse.error(HttpStatus.UNAUTHORIZED, e.getMessage()));
    }

    /**
     * 잘못된 상태로 인한 충돌
     */
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<CommonResponse<Void>> conflict(IllegalStateException e) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(CommonResponse.error(HttpStatus.CONFLICT, e.getMessage()));
    }

    /**
     * 알 수 없는 서버 에러 (최후 포괄)
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<CommonResponse<Void>> handleAny(Exception e, HttpServletRequest request) {
        log.error("Unhandled exception at {} {}", request.getMethod(), request.getRequestURI(), e);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(CommonResponse.error(HttpStatus.INTERNAL_SERVER_ERROR, "서버 오류가 발생했습니다"));
    }
}

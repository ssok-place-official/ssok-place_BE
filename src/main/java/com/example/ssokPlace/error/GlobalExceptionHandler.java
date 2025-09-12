package com.example.ssokPlace.error;

import com.example.ssokPlace.common.CommonResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<CommonResponse<Void>> handleValidation(MethodArgumentNotValidException e){
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(CommonResponse.error(HttpStatus.BAD_REQUEST, "요청 값이 올바르지 않습니다."));
    }

    @ExceptionHandler(ReportableError.class)
    public ResponseEntity<CommonResponse<Object>> handleReportable(ReportableError e, HttpServletRequest req) {
        log.warn("ReportableError {} - {}", e.getStatus(), e.getClientMessage());
        var body = CommonResponse.of(e.getStatus(), e.getClientMessage(), e.getData());
        return ResponseEntity.status(e.getStatus()).body(body);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<CommonResponse<Void>> handleAny(Exception e) {
        log.error("Unhandled exception", e);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(CommonResponse.error(HttpStatus.INTERNAL_SERVER_ERROR, "서버 오류가 발생했습니다"));
    }
}

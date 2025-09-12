package com.example.ssokPlace.attributes;

import com.example.ssokPlace.error.ReportableError;
import jakarta.servlet.RequestDispatcher;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.servlet.error.DefaultErrorAttributes;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 에러 바디 정의, 컨트롤러 밖(404, 필터단)에서 터진 오류의 포맷 제공
 * 컨트롤러 안에서 터진 예외는 GlobalExceptionHandler가 처리
 */
@Component
public class GlobalErrorAttributes extends DefaultErrorAttributes {

    @Override
    public Map<String ,Object> getErrorAttributes(WebRequest webRequest, ErrorAttributeOptions options) {
        Throwable error = getError(webRequest);

        int code = HttpStatus.INTERNAL_SERVER_ERROR.value();
        String message = "서버 오류가 발생했습니다.";
        Object data = null;

        if (error instanceof ReportableError re) {
            code = re.getStatus().value();
            message = re.getClientMessage();
            data = re.getData();
        } else if (error instanceof MethodArgumentNotValidException) {
            code = HttpStatus.BAD_REQUEST.value();
            message = "요청 값이 올바르지 않습니다.";
        } else if (error instanceof NoHandlerFoundException) {
            code = HttpStatus.NOT_FOUND.value();
            message = "요청하신 경로를 찾을 수 없습니다.";
        }

        webRequest.setAttribute(RequestDispatcher.ERROR_STATUS_CODE, code, RequestAttributes.SCOPE_REQUEST);

        Map<String ,Object> body = new LinkedHashMap<>();
        body.put("code", code);
        body.put("message", message);
        body.put("data", data);
        return body;
    }
}

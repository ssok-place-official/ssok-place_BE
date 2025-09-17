package com.example.ssokPlace.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CommonResponse<T> {
    @Builder.Default private final int code = 200;
    @Builder.Default private final String message = "";
    @Builder.Default private final T data = null;

    public static <T> CommonResponse<T> of(HttpStatus status, String message, T data) {
        return CommonResponse.<T>builder()
                .code(status.value())
                .message(message)
                .data(data)
                .build();
    }
    // 200
    public static <T> CommonResponse<T> ok(T data, String message) {
        return of(HttpStatus.OK, message, data);
    }
    // 201
    public static <T> CommonResponse<T> created(T data, String message) {
        return of(HttpStatus.CREATED, message, data);
    }
    // error
    public static <T> CommonResponse<T> error(HttpStatus status, String message) {
        return of(status, message, null);
    }
}

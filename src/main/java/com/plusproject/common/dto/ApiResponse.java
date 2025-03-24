package com.plusproject.common.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public class ApiResponse<T> {

    private final int code;
    private final String status;
    private final String message;
    private final T data;

    // ✅ 성공 응답 (데이터 있음)
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(HttpStatus.OK.value(), HttpStatus.OK.name(), "요청이 성공적으로 처리되었습니다.", data);
    }

    // ✅ 성공 응답 (메시지 + 데이터)
    public static <T> ApiResponse<T> success(T data, String message) {
        return new ApiResponse<>(HttpStatus.OK.value(), HttpStatus.OK.name(), message, data);
    }

    // ✅ 성공 응답 (메시지만 반환)
    public static <T> ApiResponse<T> success(String message) {
        return new ApiResponse<>(HttpStatus.OK.value(), HttpStatus.OK.name(), message, null);
    }

}

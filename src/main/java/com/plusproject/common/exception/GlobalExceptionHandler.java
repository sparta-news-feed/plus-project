package com.plusproject.common.exception;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.plusproject.common.dto.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ApplicationException.class)
    public ResponseEntity<ErrorResponse> handleBaseException(ApplicationException ex) {
        return getErrorResponse(ex.getStatus(), ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        String firstErrorMessage = ex.getBindingResult()
            .getFieldErrors()
            .stream()
            .findFirst()
            .map(DefaultMessageSourceResolvable::getDefaultMessage)
            .orElseThrow(() -> new IllegalStateException("검증 에러가 반드시 존재해야 합니다."));
        System.out.println("Hello!!! MethodArgumentNotValidException");
        return getErrorResponse(HttpStatus.BAD_REQUEST, firstErrorMessage);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {
        if (ex.getCause() instanceof InvalidFormatException invalidFormatException) {
            String fieldName = invalidFormatException.getPath().stream()
                .findFirst()
                .map(JsonMappingException.Reference::getFieldName)
                .orElse("알 수 없는 필드");

            String invalidValue = String.valueOf(invalidFormatException.getValue());
            String errorMessage = String.format("필드 '%s'에 대한 값 '%s'이(가) 올바르지 않습니다.", fieldName, invalidValue);


            return getErrorResponse(HttpStatus.BAD_REQUEST, errorMessage);
        }
        return getErrorResponse(HttpStatus.BAD_REQUEST, "잘못된 요청입니다.");
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleSqlException(DataIntegrityViolationException ex) {
        return getErrorResponse(HttpStatus.BAD_REQUEST, "Unique 제약으로 중복된 데이터 생성이 불가능합니다.");
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleRunTimeException(RuntimeException ex) {
        log.error("RuntimeException: " + ex.getMessage());
        return getErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "서버에 오류가 발생했습니다. 관리자에게 문의하세요.");
    }

    private ResponseEntity<ErrorResponse> getErrorResponse(HttpStatus status, String message) {
        ErrorResponse response = new ErrorResponse(
            status.name(),
            status.value(),
            message,
            LocalDateTime.now()
        );
        return new ResponseEntity<>(response, status);
    }
}

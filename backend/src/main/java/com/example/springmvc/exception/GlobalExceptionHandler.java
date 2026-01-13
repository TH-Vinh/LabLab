package com.example.springmvc.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(
            MethodArgumentNotValidException ex) {

        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<Map<String, String>> handleBusinessException(BusinessException ex) {
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("error", ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, String>> handleRuntimeException(RuntimeException ex) {
        // Ghi log chi tiết ở server
        logger.error("Lỗi runtime: ", ex);
        
        Map<String, String> errorResponse = new HashMap<>();
        String errorMessage = ex.getMessage();
        if (errorMessage == null || errorMessage.isEmpty()) {
            errorMessage = "Đã xảy ra lỗi hệ thống. Vui lòng thử lại sau hoặc liên hệ quản trị viên.";
        }
        errorResponse.put("error", errorMessage);
        
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleGeneralException(Exception ex) {
        // Ghi log chi tiết ở server
        logger.error("Lỗi hệ thống nghiêm trọng: ", ex);
        
        Map<String, String> errorResponse = new HashMap<>();
        String errorMessage = ex.getMessage();
        if (errorMessage == null || errorMessage.isEmpty()) {
            errorMessage = "Đã xảy ra lỗi hệ thống. Vui lòng thử lại sau hoặc liên hệ quản trị viên.";
        } else {
            // Trả về thông báo lỗi chi tiết nếu có
            errorMessage = "Lỗi: " + errorMessage;
        }
        errorResponse.put("error", errorMessage);

        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
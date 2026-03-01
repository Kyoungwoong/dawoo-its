package com.example.spring.common.exception;


import com.example.spring.dto.ErrorResponse;
import com.example.spring.domain.ErrorCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;

@RestControllerAdvice
public class ExceptionController {
    private static final Logger log = LoggerFactory.getLogger(ExceptionController.class);

    @ExceptionHandler(value = DawooException.class)
    public ResponseEntity<ErrorResponse<Map<String, Object>>> makeGeneralException(DawooException e) {
        log.error("handled DawooException code={} status={} message={}",
                e.getErrorCode().name(),
                e.getErrorCode().getStatus().value(),
                e.getMessage());
        return ResponseEntity
                .status(e.getErrorCode().getStatus())
                .body(
                        ErrorResponse.createErrorResponse(e.getErrorCode(), e.getErrorMap())
                );
    }

    @ExceptionHandler(value = Exception.class)
    public ResponseEntity<ErrorResponse<Map<String, Object>>> makeUnhandledException(Exception e) {
        log.error("unhandled exception", e);
        return ResponseEntity
                .status(ErrorCode.INTERNAL_SERVER_ERROR.getStatus())
                .body(ErrorResponse.createErrorResponse(ErrorCode.INTERNAL_SERVER_ERROR, Collections.emptyMap()));
    }

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse<Map<String, Object>>> handleMethodArgumentNotValid(MethodArgumentNotValidException e) {
        List<FieldError> fieldErrors = e.getBindingResult().getFieldErrors();

        Map<String, Object> data = new LinkedHashMap<>();
        data.put("errorCount", fieldErrors.size());
        data.put("errors", fieldErrors.stream()
                .map(fe -> Map.of(
                        "field", fe.getField(),
                        "rejectedValue", fe.getRejectedValue(),
                        "message", fe.getDefaultMessage()
                ))
                .collect(Collectors.toList()));

        log.warn("validation failed: fieldErrors={}", fieldErrors.size());
        return ResponseEntity
                .status(ErrorCode.BAD_REQUEST.getStatus())
                .body(ErrorResponse.createErrorResponse(ErrorCode.BAD_REQUEST, data));
    }

    @ExceptionHandler(value = ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse<Map<String, Object>>> handleConstraintViolation(ConstraintViolationException e) {
        List<ConstraintViolation<?>> violations = e.getConstraintViolations().stream().toList();

        Map<String, Object> data = new LinkedHashMap<>();
        data.put("errorCount", violations.size());
        data.put("errors", violations.stream()
                .map(v -> Map.of(
                        "path", String.valueOf(v.getPropertyPath()),
                        "invalidValue", v.getInvalidValue(),
                        "message", v.getMessage()
                ))
                .collect(Collectors.toList()));

        log.warn("validation failed: constraintViolations={}", violations.size());
        return ResponseEntity
                .status(ErrorCode.BAD_REQUEST.getStatus())
                .body(ErrorResponse.createErrorResponse(ErrorCode.BAD_REQUEST, data));
    }
}

package com.example.spring.common.exception;


import com.example.spring.dto.ErrorResponse;
import com.example.spring.domain.ErrorCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Collections;
import java.util.Map;

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
}

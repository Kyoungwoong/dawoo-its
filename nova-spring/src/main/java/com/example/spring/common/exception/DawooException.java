package com.example.spring.common.exception;

import com.example.spring.domain.ErrorCode;

import java.util.Collections;
import java.util.Map;

public class DawooException extends RuntimeException {
    private final ErrorCode errorCode;
    private final Map<String, Object> errorMap;

    public DawooException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
        this.errorMap = Collections.emptyMap();
    }

    public DawooException(ErrorCode errorCode, String message) {
        super(message == null || message.isBlank() ? errorCode.getMessage() : message);
        this.errorCode = errorCode;
        this.errorMap = Collections.emptyMap();
    }

    public DawooException(ErrorCode errorCode, Map<String, Object> errorMap) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
        this.errorMap = errorMap == null ? Collections.emptyMap() : errorMap;
    }

    public DawooException(ErrorCode errorCode, String message, Map<String, Object> errorMap) {
        super(message == null || message.isBlank() ? errorCode.getMessage() : message);
        this.errorCode = errorCode;
        this.errorMap = errorMap == null ? Collections.emptyMap() : errorMap;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }

    public Map<String, Object> getErrorMap() {
        return errorMap;
    }
}

package com.example.spring.domain;

import org.springframework.http.HttpStatus;

public enum ErrorCode {

    // 500
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "API Server Error"),

    // 503
    FILE_CONTENTS_NOT_AVAILABLE(HttpStatus.SERVICE_UNAVAILABLE, "File Contents not available");

    private final HttpStatus status;
    private final String message;

    ErrorCode(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }

    public String getMessage() {
        return this.message;
    }

    public HttpStatus getStatus() {
        return this.status;
    }
}

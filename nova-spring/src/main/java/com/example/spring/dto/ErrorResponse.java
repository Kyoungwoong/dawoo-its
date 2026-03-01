package com.example.spring.dto;

import com.example.spring.domain.ErrorCode;

import java.time.LocalDateTime;

public class ErrorResponse<T> {
    private final String timeStamp = String.valueOf(LocalDateTime.now());
    private final int status;
    private final String message;
    private final T data;

    public static <T> ErrorResponse<T> createErrorResponse(ErrorCode errorCode, T data) {
        return new ErrorResponse<>(
                errorCode.getStatus().value(),
                errorCode.getMessage(),
                data
        );
    }

    public ErrorResponse(int status, String message, T data) {
        this.status = status;
        this.message = message;
        this.data = data;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public int getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public T getData() {
        return data;
    }
}

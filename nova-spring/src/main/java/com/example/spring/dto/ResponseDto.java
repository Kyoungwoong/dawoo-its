package com.example.spring.dto;

import org.springframework.http.HttpStatus;

public class ResponseDto<T> {
    private final int status;
    private final String message;
    private final T data;

    public ResponseDto(int status, String message, T data) {
        this.status = status;
        this.message = message;
        this.data = data;
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

    public static <T> ResponseDto<T> ok(String message, T data) {
        return new ResponseDto<>(HttpStatus.OK.value(), message, data);
    }

    public static <T> ResponseDto<T> created(String message, T data) {
        return new ResponseDto<>(HttpStatus.CREATED.value(), message, data);
    }
}

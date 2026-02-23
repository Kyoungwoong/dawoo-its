package com.example.common.dto;

public enum ErrorCode {
    METHOD_NOT_ALLOWED(405, "METHOD_NOT_ALLOWED", "Method Not Allowed"),
    READ_USERS_FAILED(500, "READ_USERS_FAILED", "Failed to read users"),
    INTERNAL_SERVER_ERROR(500, "INTERNAL_SERVER_ERROR", "Internal Server Error");

    private final int status;
    private final String code;
    private final String message;

    ErrorCode(int status, String code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }

    public int status() {
        return status;
    }

    public String code() {
        return code;
    }

    public String message() {
        return message;
    }
}

package com.example.common.dto;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class ResponseDto {
    private static final Logger log = LoggerFactory.getLogger(ResponseDto.class);
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    public static void sendJson(HttpExchange exchange, int statusCode, String body) throws IOException {
        byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
        Headers headers = exchange.getResponseHeaders();
        headers.set("Content-Type", "application/json; charset=utf-8");
        exchange.sendResponseHeaders(statusCode, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }

    public static void sendJson(HttpExchange exchange, int statusCode, Object body) throws IOException {
        String json = OBJECT_MAPPER.writeValueAsString(body);
        sendJson(exchange, statusCode, json);
    }

    public static void sendError(HttpExchange exchange, ErrorCode errorCode) throws IOException {
        log.warn("sendError status={} code={}", errorCode.status(), errorCode.code());
        sendJson(exchange, errorCode.status(), ErrorResponse.from(errorCode));
    }
}

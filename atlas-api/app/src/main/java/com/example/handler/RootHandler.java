package com.example.handler;

import com.example.common.UserCount;
import com.example.common.dto.ErrorCode;
import com.example.common.dto.ResponseDto;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.util.List;

import com.example.common.util.ResourceConfig;

public class RootHandler {
    private static String USER_FILE_PATH = "user.json";
    private static final String USER_COUNT_FORMAT = "{\"sum\":%d,\"users\":%d}";

    public static void handle(HttpExchange exchange) throws IOException {
        // - method/uri: 어떤 요청이 들어왔는지
        // - protocol: HTTP 버전
        // - local/remote: 서버 주소 / 클라이언트 주소
        // - headers: 주요 헤더
        System.out.println("path: / \tprotocol=" + exchange.getProtocol()); // HTTP 버전 (예: HTTP/1.1)
        System.out.println("path: / \tlocal=" + exchange.getLocalAddress()); // 서버가 바인딩된 주소/포트
        System.out.println("path: / \tmethod=" + exchange.getRequestMethod()); // 요청 메서드
        System.out.println("path: / \turi=" + exchange.getRequestURI()); // 요청 경로 + 쿼리
        System.out.println("path: / \tremote=" + exchange.getRemoteAddress()); // 클라이언트 주소/포트
        System.out.println("path: / \tuser-agent=" + exchange.getRequestHeaders().getFirst("User-Agent")); // 클라이언트/브라우저 정보
        System.out.println("path: / \tcontent-type=" + exchange.getRequestHeaders().getFirst("Content-Type")); // 요청 바디 타입
        System.out.println("path: / \taccept=" + exchange.getRequestHeaders().getFirst("Accept")); // 클라이언트가 원하는 응답 타입
        System.out.println("path: / \tx-forwarded-for=" + exchange.getRequestHeaders().getFirst("X-Forwarded-For")); // 프록시 뒤 실제 클라이언트 IP

        if (!"GET".equalsIgnoreCase(exchange.getRequestMethod())) {
            ResponseDto.sendError(exchange, ErrorCode.METHOD_NOT_ALLOWED);
            return;
        }
        ResponseDto.sendJson(exchange, 200, "{\"message\":\"server check\"}");
    }

    public static void sumUserCount(HttpExchange ex) throws IOException {
        if (!"GET".equalsIgnoreCase(ex.getRequestMethod())) {
            ResponseDto.sendError(ex, ErrorCode.METHOD_NOT_ALLOWED);
            return;
        }
        try {
            List<UserCount> userCountList = ResourceConfig.readConfig(USER_FILE_PATH, ResourceConfig::readUsers);
            long totalCount = userCountList.stream()
                    .mapToLong(UserCount::getCount)
                    .sum();
            ResponseDto.sendJson(ex, 200, String.format(USER_COUNT_FORMAT, totalCount, userCountList.size()));
        } catch (Exception e) {
            ResponseDto.sendError(ex, ErrorCode.READ_USERS_FAILED);
        }
    }
}

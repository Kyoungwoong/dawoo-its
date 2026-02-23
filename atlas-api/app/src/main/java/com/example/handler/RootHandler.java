package com.example.handler;

import com.example.common.dto.ResponseDto;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;

public class RootHandler {
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
            ResponseDto.sendJson(exchange, 405, "{\"error\":\"Method Not Allowed\"}");
            return;
        }
        ResponseDto.sendJson(exchange, 200, "{\"message\":\"server check\"}");
    }
}

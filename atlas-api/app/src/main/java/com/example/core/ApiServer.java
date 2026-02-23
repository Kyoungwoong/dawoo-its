package com.example.core;

import com.example.common.ServerConfig;
import com.example.common.dto.ResponseDto;
import com.example.common.util.ResourceConfig;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.Executors;

public class ApiServer {
    private static final String SERVER_CONFIG_RESOURCE = "config.json";

    public static void main(String[] args) throws IOException {
        ServerConfig serverConfig =
                ResourceConfig.readConfig(SERVER_CONFIG_RESOURCE, ResourceConfig::parseServerConfigJson);
        HttpServer server = HttpServer
                .create(new InetSocketAddress(serverConfig.host(), serverConfig.port()), 0);

        // HttpExchange 핵심 학습 포인트를 하나의 엔드포인트로 정리
        server.createContext("/", exchange -> {
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
        });

        int threadCnt = Math.max(Math.max(4, Runtime.getRuntime().availableProcessors()), serverConfig.threadCount());

        server.setExecutor(Executors.newFixedThreadPool(threadCnt));
        server.start();
        System.out.println("ApiServer running at http://localhost:" + serverConfig.port());
    }

    private static void handleRoot(HttpExchange exchange) throws IOException {
        // 1) 요청 메서드/URI/프로토콜 같은 기본 정보
        String method = exchange.getRequestMethod();
        String uri = exchange.getRequestURI().toString();
        String protocol = exchange.getProtocol();

        // 2) 로컬/리모트 주소 확인
        String local = exchange.getLocalAddress().toString();
        String remote = exchange.getRemoteAddress().toString();

        // 학습용 출력: API 서버에서 자주 확인하는 정보들
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

        // 3) 요청 바디 읽기 (대부분 POST/PUT 등에서 사용)
        String bodyText = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);

        // 4) 응답 헤더 설정 후 응답 보내기 (ResponseDto 사용)
        if (!"GET".equalsIgnoreCase(method) && bodyText.isEmpty()) {
            ResponseDto.sendJson(exchange, 405, "{\"error\":\"Method Not Allowed\"}");
            return;
        }

        String body = "{"
                + "\"message\":\"server check\","
                + "\"method\":\"" + escape(method) + "\","
                + "\"uri\":\"" + escape(uri) + "\","
                + "\"protocol\":\"" + escape(protocol) + "\","
                + "\"local\":\"" + escape(local) + "\","
                + "\"remote\":\"" + escape(remote) + "\","
                + "\"body\":\"" + escape(bodyText) + "\""
                + "}";
        ResponseDto.sendJson(exchange, 200, body);
    }

    private static String escape(String s) {
        return s
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }
}

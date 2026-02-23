package com.example.handler;

import com.example.common.UserCount;
import com.example.common.dto.ErrorCode;
import com.example.common.dto.ResponseDto;
import com.sun.net.httpserver.HttpExchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

import com.example.common.util.ResourceConfig;

public class RootHandler {
    private static final Logger log = LoggerFactory.getLogger(RootHandler.class);
    private static String USER_FILE_PATH = "user.json";
    private static final String USER_COUNT_FORMAT = "{\"sum\":%d,\"users\":%d}";

    public static void handle(HttpExchange exchange) throws IOException {
        // - method/uri: 어떤 요청이 들어왔는지
        // - protocol: HTTP 버전
        // - local/remote: 서버 주소 / 클라이언트 주소
        // - headers: 주요 헤더
        log.info("path=/ protocol={}", exchange.getProtocol());
        log.info("path=/ local={}", exchange.getLocalAddress());
        log.info("path=/ method={}", exchange.getRequestMethod());
        log.info("path=/ uri={}", exchange.getRequestURI());
        log.info("path=/ remote={}", exchange.getRemoteAddress());
        log.info("path=/ user-agent={}", exchange.getRequestHeaders().getFirst("User-Agent"));
        log.info("path=/ content-type={}", exchange.getRequestHeaders().getFirst("Content-Type"));
        log.info("path=/ accept={}", exchange.getRequestHeaders().getFirst("Accept"));
        log.info("path=/ x-forwarded-for={}", exchange.getRequestHeaders().getFirst("X-Forwarded-For"));

        if (!"GET".equalsIgnoreCase(exchange.getRequestMethod())) {
            log.warn("path=/ methodNotAllowed method={}", exchange.getRequestMethod());
            ResponseDto.sendError(exchange, ErrorCode.METHOD_NOT_ALLOWED);
            return;
        }
        ResponseDto.sendJson(exchange, 200, "{\"message\":\"server check\"}");
    }

    public static void sumUserCount(HttpExchange ex) throws IOException {
        if (!"GET".equalsIgnoreCase(ex.getRequestMethod())) {
            log.warn("path=/sum methodNotAllowed method={}", ex.getRequestMethod());
            ResponseDto.sendError(ex, ErrorCode.METHOD_NOT_ALLOWED);
            return;
        }
        try {
            List<UserCount> userCountList = ResourceConfig.readConfig(USER_FILE_PATH, ResourceConfig::readUsers);
            long totalCount = userCountList.stream()
                    .mapToLong(UserCount::getCount)
                    .sum();
            log.info("path=/sum users={} sum={}", userCountList.size(), totalCount);
            ResponseDto.sendJson(ex, 200, String.format(USER_COUNT_FORMAT, totalCount, userCountList.size()));
        } catch (Exception e) {
            log.error("path=/sum failed to read users", e);
            ResponseDto.sendError(ex, ErrorCode.READ_USERS_FAILED);
        }
    }
}

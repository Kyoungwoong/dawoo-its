package com.example.core;

import com.example.common.ServerConfig;
import com.example.common.util.ResourceConfig;
import com.example.handler.RootHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

public class ApiServer {
    private static final String SERVER_CONFIG_RESOURCE = "config.json";

    public static void main(String[] args) throws IOException {
        ServerConfig serverConfig =
                ResourceConfig.readConfig(SERVER_CONFIG_RESOURCE, ResourceConfig::parseServerConfigJson);
        HttpServer server = HttpServer
                .create(new InetSocketAddress(serverConfig.host(), serverConfig.port()), 0);
        int threadCnt = Math.max(Math.max(4, Runtime.getRuntime().availableProcessors()), serverConfig.threadCount());

        server.setExecutor(Executors.newFixedThreadPool(threadCnt));

        // HttpExchange 핵심 학습 포인트를 하나의 엔드포인트로 정리
        server.createContext("/", RootHandler::handle);

        server.start();
        System.out.println("ApiServer running at http://localhost:" + serverConfig.port());
    }
}

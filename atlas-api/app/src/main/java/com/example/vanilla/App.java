package com.example.vanilla;

import com.sun.net.httpserver.HttpServer;
import java.io.OutputStream;
import java.net.InetSocketAddress;

public class App {
    public static void main(String[] args) throws Exception {
        HttpServer server = HttpServer.create(new InetSocketAddress(8081), 0);
        server.createContext("/health", exchange -> {
            byte[] res = "OK".getBytes();
            exchange.sendResponseHeaders(200, res.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(res);
            }
        });
        server.start();
        System.out.println("Vanilla API running on http://localhost:8081");
    }
}

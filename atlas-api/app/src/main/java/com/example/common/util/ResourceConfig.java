package com.example.common.util;

import app.src.main.java.com.example.common.util.TypeUtil;
import com.example.common.UserCount;
import com.example.common.ServerConfig;
import com.example.common.dto.UsersConfig;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ResourceConfig {
    private static final String DEFAULT_HOST = "0.0.0.0";
    private static final int PORT = 5239;
    private static final int THREAD_COUNT = 4;
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @FunctionalInterface
    public interface ConfigParser<T> {
        T parse(String content) throws IOException;
    }

    // 파일 내용을 그대로 읽어서 문자열로 반환 (텍스트/JSON/CSV 등 범용)
    public static String readConfig(String filePath) throws IOException {
        return Files.readString(Path.of(filePath), StandardCharsets.UTF_8);
    }

    // classpath 리소스 읽기 (src/main/resources 기준)
    public static String readFile(String pathOrResource) throws IOException {
        System.out.println("[ResourceConfig] read: " + pathOrResource);
        try (InputStream in = ResourceConfig.class.getClassLoader().getResourceAsStream(pathOrResource)) {
            if (in != null) {
                System.out.println("[ResourceConfig] source=classpath");
                return new String(in.readAllBytes(), StandardCharsets.UTF_8);
            }
        }

        Path path = Path.of(pathOrResource);
        if (Files.exists(path)) {
            System.out.println("[ResourceConfig] source=filesystem path=" + path.toAbsolutePath());
            return Files.readString(path, StandardCharsets.UTF_8);
        }

        // 실행 위치가 프로젝트 루트일 때를 대비한 fallback
        Path cwd = Path.of("").toAbsolutePath();
        Path appResources = cwd.resolve("atlas-api/app/src/main/resources").resolve(pathOrResource);
        if (Files.exists(appResources)) {
            System.out.println("[ResourceConfig] source=fallback path=" + appResources);
            return Files.readString(appResources, StandardCharsets.UTF_8);
        }

        throw new IOException("Resource or file not found: " + pathOrResource + " (cwd=" + cwd + ")");
    }

    // 파일 내용을 읽고, 원하는 타입으로 변환
    // 예: readConfig("server.properties", ResourceConfig::parseServerConfig)
    public static <T> T readConfig(String pathOrResource, ConfigParser<T> parser) throws IOException {
        String content = readFile(pathOrResource);
        return parser.parse(content);
    }

    // 서버 환경설정 예시: key=value 형식(Properties)에서 ServerConfig 생성
    // 파일 예:
    // host=0.0.0.0
    // port=5864
    // threadCount=8
    // config.json 파싱 전용
    public static ServerConfig parseServerConfigJson(String content) throws IOException {
        @SuppressWarnings("unchecked")
        Map<String, Object> map = OBJECT_MAPPER.readValue(content, Map.class);
        String host = map.getOrDefault("host", DEFAULT_HOST).toString();
        int port = TypeUtil.toInt(map.get("port"), PORT);
        int threadCount = TypeUtil.toInt(map.get("threadCount"), THREAD_COUNT);
        return new ServerConfig(host, port, threadCount);
    }

    public static List<UserCount> readUsers(String content) throws IOException {
        System.out.println("readUsers: " + content);
        UsersConfig config = OBJECT_MAPPER.readValue(content, UsersConfig.class);
        System.out.println("readUers: " + config);
        if (config.users() == null) {
            return new ArrayList<>();
        }
        return config.users();
    }

}

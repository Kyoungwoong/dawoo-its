package com.example.common.util;

import com.example.common.ServerConfig;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ResourceConfig {
    private static final String DEFAULT_HOST = "0.0.0.0";
    private static final int PORT = 5239;
    private static final int THREAD_COUNT = 4;

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
        try (InputStream in = ResourceConfig.class.getClassLoader().getResourceAsStream(pathOrResource)) {
            if (in != null) {
                return new String(in.readAllBytes(), StandardCharsets.UTF_8);
            }
        }

        Path path = Path.of(pathOrResource);
        if (Files.exists(path)) {
            return Files.readString(path, StandardCharsets.UTF_8);
        }

        // 실행 위치가 프로젝트 루트일 때를 대비한 fallback
        Path cwd = Path.of("").toAbsolutePath();
        Path appResources = cwd.resolve("atlas-api/app/src/main/resources").resolve(pathOrResource);
        if (Files.exists(appResources)) {
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
    public static ServerConfig parseServerConfigJson(String content) {
        String host = extractJsonString(content, "host");
        int port = extractJsonInt(content, "port", PORT);
        int threadCount = extractJsonInt(content, "threadCount", THREAD_COUNT);
        return new ServerConfig(host, port, threadCount);
    }

    private static String extractJsonString(String content, String key) {
        Pattern p = Pattern.compile("\"" + Pattern.quote(key) + "\"\\s*:\\s*\"([^\"]*)\"");
        Matcher m = p.matcher(content);
        return m.find() ? m.group(1) : ResourceConfig.DEFAULT_HOST;
    }

    private static int extractJsonInt(String content, String key, int defaultValue) {
        Pattern p = Pattern.compile("\"" + Pattern.quote(key) + "\"\\s*:\\s*(\\d+)");
        Matcher m = p.matcher(content);
        return m.find() ? Integer.parseInt(m.group(1)) : defaultValue;
    }
}

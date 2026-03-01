package com.example.spring.common;

import com.example.spring.domain.Transit;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class FileReader {
    private static final Logger log = LoggerFactory.getLogger(FileReader.class);

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static List<Transit> readFile(String filePath) throws IOException {
        log.info("read File Path: {}", filePath);

        // resources 일 때
        try (InputStream in = FileReader.class.getClassLoader().getResourceAsStream(filePath)) {
            if (in != null) {
                log.debug("Reading transit logs from classpath: {}", filePath);
                return objectMapper.readValue(in.readAllBytes(), new TypeReference<List<Transit>>() {});
            }
        }

        // 다른 위치일 때
        Path path = Paths.get(filePath);
        if (Files.exists(path)) {
            log.debug("Reading transit logs from filesystem: {}", path.toAbsolutePath());
            return objectMapper.readValue(Files.readAllBytes(path),
                    new TypeReference<List<Transit>>(){});
        }

        log.error("Transit log file not found: {}", filePath);
        throw new FileNotFoundException(filePath);
    }
}

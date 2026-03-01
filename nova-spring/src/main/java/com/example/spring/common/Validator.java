package com.example.spring.common;

import com.example.spring.dto.LogRequest;
import com.example.spring.dto.Request;

import java.time.Instant;
import java.time.format.DateTimeParseException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Validator {

    private static Set<String> statusMap = new HashSet<>();

    static {
        statusMap.add("COMPLETED");
        statusMap.add("IN_PROGRESS");
        statusMap.add("CANCELED");
    }

    public static boolean validate(Request request) {
        if (request.getClass().equals(LogRequest.class)) {
            return validateLogRequest(request);
        }
        return true;
    }

    private static boolean validateLogRequest(Request request) {
        LogRequest r = (LogRequest) request;

        if (r.getStatus() != null && !statusMap.contains(r.getStatus())) {
            return false;
        }

        try {
            if (r.getFrom() != null) {
                Instant.parse(r.getFrom());
            }
            if (r.getTo() != null) {
                Instant.parse(r.getTo());
            }
        } catch (DateTimeParseException e) {
            return false;
        }

        if (1 > r.getLimit() || 100 < r.getLimit()) {
            return false;
        }

        return true;
    }
}

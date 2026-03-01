package com.example.spring.service;

import com.example.spring.common.Validator;
import com.example.spring.common.exception.DawooException;
import com.example.spring.common.FileReader;
import com.example.spring.domain.ErrorCode;
import com.example.spring.domain.Transit;
import com.example.spring.dto.CardDto;
import com.example.spring.dto.LogRequest;
import com.example.spring.dto.LogResponse;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.Instant;
import java.util.Map;
import java.util.*;

@Service
public class TransitService {
    private final Logger log = LoggerFactory.getLogger(TransitService.class);

    private static final String TRANSIT_ARRAY_PATH = "transit_logs_array.json";
    private static final String TRANSIT_JSON_PATH = "transit_logs_json.json";
    private static final String TRANSIT_NOT_RESOURCE_PATH = "data/transit_logs_array.json";

    private List<Transit> transitList;

    @PostConstruct
    public void setUp() {
        try {
            log.info("Loading transit logs path={}", TRANSIT_ARRAY_PATH);
            transitList = FileReader.readFile(TRANSIT_ARRAY_PATH);
            log.info("Loaded transit logs count={}", transitList.size());
        } catch (IOException e) {
            log.error("Failed to load transit logs path={}", TRANSIT_ARRAY_PATH, e);
        }
    }

    public List<CardDto> getCardTripCounts() {
        if (transitList == null) {
            throw new DawooException(
                    ErrorCode.FILE_CONTENTS_NOT_AVAILABLE,
                    "Transit logs are not loaded",
                    Map.of("path", TRANSIT_ARRAY_PATH)
            );
        }

        Map<String, Integer> cardMap = new HashMap<>();
        List<CardDto> cardList = new ArrayList<>();

        for (Transit t : transitList) {
            String cardId = t.getCardId();
            if (cardId == null || cardId.isBlank()) {
                continue;
            }
            cardMap.put(cardId, cardMap.getOrDefault(cardId, 0) + 1);
        }

        for (String id : cardMap.keySet()) {
            cardList.add(new CardDto(id, cardMap.get(id)));
        }

        Collections.sort(cardList);

        log.debug("Built card list: transitCount={} cardCount={}",
                transitList.size(), cardList.size());
        return cardList;
    }

    public List<LogResponse> getLogs(LogRequest logRequest) {
        if (transitList == null) {
            throw new DawooException(
                    ErrorCode.FILE_CONTENTS_NOT_AVAILABLE,
                    "Transit logs are not loaded",
                    Map.of("path", TRANSIT_ARRAY_PATH)
            );
        }

        if (!Validator.validate(logRequest)) {
            throw new DawooException(ErrorCode.BAD_REQUEST);
        }

        Instant from = logRequest.getFrom() != null ? Instant.parse(logRequest.getFrom()) : null;
        Instant to = logRequest.getTo() != null ? Instant.parse(logRequest.getTo()) : null;

        List<LogResponse> result = findLogs(logRequest, from, to);

        Collections.sort(result, (a, b) -> {
            Instant at = Instant.parse(a.getTimestamp());
            Instant bt = Instant.parse(b.getTimestamp());
            return bt.compareTo(at); // timestamp desc
        });

        // limit은 정렬 이후 적용해야 최신 로그가 보장됨
        if (result.size() > logRequest.getLimit()) {
            return new ArrayList<>(result.subList(0, logRequest.getLimit()));
        }
        return result;
    }

    private List<LogResponse> findLogs(LogRequest request, Instant from, Instant to) {
        List<LogResponse> result = new ArrayList<>();

        for (Transit t : transitList) {
            if (request.getCardId() != null
                    && !request.getCardId().equals(t.getCardId())) {
                continue;
            }

            if (request.getStatus() != null
                    && !request.getStatus().equals(t.getStatus())) {
                continue;
            }

            if (from != null || to != null) {
                Instant ts = Instant.parse(t.getTimestamp());
                if (from != null && ts.isBefore(from)) {
                    continue; // from inclusive
                }
                if (to != null && !ts.isBefore(to)) {
                    continue; // to exclusive
                }
            }

            result.add(LogResponse.createLogResponse(t));
        }

        return result;
    }
}

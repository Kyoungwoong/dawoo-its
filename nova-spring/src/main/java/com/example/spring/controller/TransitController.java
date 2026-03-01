package com.example.spring.controller;

import com.example.spring.dto.CardDto;
import com.example.spring.dto.LogRequest;
import com.example.spring.dto.LogResponse;
import com.example.spring.dto.ResponseDto;
import com.example.spring.service.TransitService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transit")
public class TransitController {
    private static final Logger log = LoggerFactory.getLogger(TransitController.class);

    public final TransitService transitService;

    public TransitController(TransitService transitService) {
        this.transitService = transitService;
    }

    @GetMapping("/cards")
    public ResponseEntity<ResponseDto<List<CardDto>>> getCardList() {
        List<CardDto> result = transitService.getCardTripCounts();

        return ResponseEntity.ok(ResponseDto.ok("SUCCESS", result));

    }

    @GetMapping("/logs")
    public ResponseEntity<ResponseDto<List<LogResponse>>> getLogs(@RequestParam(required = false) String cardId,
                                                                  @RequestParam(required = false) String status,
                                                                  @RequestParam(required = false) String from,
                                                                  @RequestParam(required = false) String to,
                                                                  @RequestParam(required = false, defaultValue = "20") Integer limit) {

        LogRequest logRequest = new LogRequest(cardId, status, from, to, limit);

        return ResponseEntity.ok(
                ResponseDto.ok(
                        "Successfully query logs",
                        transitService.getLogs(logRequest)
                )
        );
    }
}

package com.example.spring.controller;

import com.example.spring.dto.CardDto;
import com.example.spring.dto.ResponseDto;
import com.example.spring.service.TransitService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
        long startNs = System.currentTimeMillis();

        List<CardDto> result = transitService.getCardTripCounts();

        long tookMs = System.currentTimeMillis() - startNs;
        log.info("GET /api/transit/cards resultSize={} tookMs={}",
                result.size(), tookMs);

        return ResponseEntity.ok(ResponseDto.ok("SUCCESS", result));

    }
}

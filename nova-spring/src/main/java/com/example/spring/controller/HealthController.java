package com.example.spring.controller;


import com.example.spring.dto.ResponseDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthController {

    @GetMapping("/health")
    public ResponseEntity<ResponseDto<String>> health() {
        return ResponseEntity.ok(ResponseDto.ok("ok", null));
    }
}

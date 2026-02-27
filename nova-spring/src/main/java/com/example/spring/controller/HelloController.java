package com.example.spring.controller;


import com.example.spring.dto.ResponseDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
public class HelloController {

    @GetMapping()
    public ResponseEntity<ResponseDto<String>> hello() {
        return ResponseEntity.ok(ResponseDto.ok("server Check", "hello"));
    }
}

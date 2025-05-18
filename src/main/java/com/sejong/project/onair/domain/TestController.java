package com.sejong.project.onair.domain;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class TestController {
    @GetMapping("/test")
    public ResponseEntity<String> test(){
        return ResponseEntity.ok("OK");
    }

    @GetMapping("/health")
    public ResponseEntity<String> health(){
        return ResponseEntity.ok("OK");
    }
}

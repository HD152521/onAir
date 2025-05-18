package com.sejong.project.onair.domain;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor

public class TestController {
    @RequestMapping("/test")
    public String test(){
        return "test";
    }
}

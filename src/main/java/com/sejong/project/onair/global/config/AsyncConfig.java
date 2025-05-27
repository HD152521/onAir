package com.sejong.project.onair.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@Configuration
public class AsyncConfig {
    @Bean("apiExecutor")
    public Executor apiExecutor() {
        // 스레드 풀 크기는 API 한도·서버 사양에 맞춰 조정필요
        return Executors.newFixedThreadPool(20);
    }
}
package com.sejong.project.onair.domain.compWeather.controller;

import com.sejong.project.onair.domain.compWeather.dto.CompWeatherResponseDto;
import com.sejong.project.onair.domain.compWeather.model.CompWeather;
import com.sejong.project.onair.domain.compWeather.service.CompWeatherService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/compWeather")
@Tag(name = "compWeather", description = "날씨 관측소 가져오는 API")
public class CompWeatherController {

    private final CompWeatherService compWeatherService;

    @GetMapping("/get/all")
    public List<CompWeather> getAll(){
        return compWeatherService.getAll();
    }

    @GetMapping("/get")
    public List<CompWeatherResponseDto> getLoca(){
        return compWeatherService.getLoca();
    }
}

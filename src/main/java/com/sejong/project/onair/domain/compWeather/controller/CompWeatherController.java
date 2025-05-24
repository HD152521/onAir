package com.sejong.project.onair.domain.compWeather.controller;

import com.sejong.project.onair.domain.compWeather.dto.CompWeatherResponseDto;
import com.sejong.project.onair.domain.compWeather.model.CompWeather;
import com.sejong.project.onair.domain.compWeather.service.CompWeatherService;
import com.sejong.project.onair.global.exception.BaseResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
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
    public BaseResponse<?> getLoca(
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateTime
    ){
        return BaseResponse.onSuccess(compWeatherService.getLoca(dateTime));
    }
}

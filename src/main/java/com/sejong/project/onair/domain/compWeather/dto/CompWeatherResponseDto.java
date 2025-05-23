package com.sejong.project.onair.domain.compWeather.dto;

import com.sejong.project.onair.domain.compWeather.model.CompWeather;

public record CompWeatherResponseDto(
    double dmX,
    double dmY,
    String stationName
) {
    public static CompWeatherResponseDto from(CompWeather compWeather){
        return new CompWeatherResponseDto(
                compWeather.getDmX(),
                compWeather.getDmY(),
                compWeather.getStationName()
        );
    }
}

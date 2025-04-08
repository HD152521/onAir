package com.sejong.project.onair.domain.file.dto;

import java.time.LocalDateTime;

public record DataDto(
        LocalDateTime time,
        double co2,
        double ch4_ppb,
        double ch4_ppm,
        String type,
        String province,
        String city,
        String district,
        String code,
        double so2_ppm,
        double no2_ppm,
        double o3_ppm,
        double co_ppm,
        double pm10,
        double pm2_5,
        double nox_ppm,
        double no_ppm,
        int windDirection,  //todo 일단은 다 정수형 같은데 혹시모름
        double windSpeed,
        double temperature,
        double humidity
        ) {}



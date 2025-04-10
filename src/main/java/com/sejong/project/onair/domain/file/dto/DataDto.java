package com.sejong.project.onair.domain.file.dto;

import com.sejong.project.onair.domain.file.model.FileData;

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
        String observatoryName,
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
        ){
        public static DataDto from(FileData fileData) {
                return new DataDto(
                        fileData.getTime(),
                        fileData.getCo2(),
                        fileData.getCh4_ppb(),
                        fileData.getCh4_ppm(),
                        fileData.getType(),
                        fileData.getProvince(),
                        fileData.getCity(),
                        fileData.getDistrict(),
                        fileData.getObservatoryName(),
                        fileData.getCode(),
                        fileData.getSo2_ppm(),
                        fileData.getNo2_ppm(),
                        fileData.getO3_ppm(),
                        fileData.getCo_ppm(),
                        fileData.getPm10(),
                        fileData.getPm2_5(),
                        fileData.getNox_ppm(),
                        fileData.getNo_ppm(),
                        fileData.getWindDirection(),
                        fileData.getWindSpeed(),
                        fileData.getTemperature(),
                        fileData.getHumidity()
                );
        }

}



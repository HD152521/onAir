package com.sejong.project.onair.domain.observatory.dto;

import com.sejong.project.onair.domain.observatory.model.ObservatoryData;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ObservatoryDataResponse {
    public record FlagFilterDto(
            Integer so2Grade,
             Integer khaiValue,
             Double  so2Value,
             Double  coValue,
             Integer pm10Value,
             Integer o3Grade,
             Integer khaiGrade,
             Integer pm25Value,
             Integer no2Grade,
             Integer pm25Grade,
             Integer coGrade,
             Double  no2Value,
             Integer pm10Grade,
             Double  o3Value,
             LocalDateTime dataTime,
             String stationName
    ){
        public static FlagFilterDto to(ObservatoryData data){
            return new FlagFilterDto(
                    data.getSo2Grade(),
                    data.getKhaiValue(),
                    data.getSo2Value(),
                    data.getCoValue(),
                    data.getPm10Value(),
                    data.getO3Grade(),
                    data.getKhaiGrade(),
                    data.getPm25Value(),
                    data.getNo2Grade(),
                    data.getPm25Grade(),
                    data.getCoGrade(),
                    data.getNo2Value(),
                    data.getPm10Grade(),
                    data.getO3Value(),
                    data.getDataTime(),
                    data.getStationName()
            );
        }

        public static List<FlagFilterDto> toAll(List<ObservatoryData> datas) {
            List<FlagFilterDto> dtos = new ArrayList<>();
            for(ObservatoryData data:datas){
                dtos.add(
                        new FlagFilterDto(
                        data.getSo2Grade(),
                        data.getKhaiValue(),
                        data.getSo2Value(),
                        data.getCoValue(),
                        data.getPm10Value(),
                        data.getO3Grade(),
                        data.getKhaiGrade(),
                        data.getPm25Value(),
                        data.getNo2Grade(),
                        data.getPm25Grade(),
                        data.getCoGrade(),
                        data.getNo2Value(),
                        data.getPm10Grade(),
                        data.getO3Value(),
                        data.getDataTime(),
                        data.getStationName()
                ));
            }
            return dtos;
        }
    }
}

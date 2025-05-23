package com.sejong.project.onair.domain.preddata.dto;

import com.sejong.project.onair.domain.preddata.model.Preddata;

import java.time.LocalDateTime;

public record PreddataResponseDto(
        LocalDateTime measurementTime,
        String nearestObservatoryName,
        Double so2,
        Double pm25,
        Double pm10,
        Double o3,
        Double no2,
        Double noAvg,
        Double co,
        Double co2,
        Double ch4,
        Long id
) {
    public static PreddataResponseDto from(Preddata preddata){
        return new PreddataResponseDto(
                preddata.getMeasurementTime(),
                preddata.getNearestObservatoryName(),
                preddata.getSo2(),
                preddata.getPm25(),
                preddata.getPm10(),
                preddata.getO3(),
                preddata.getNo2(),
                preddata.getNoAvg(),
                preddata.getCo(),
                (preddata.getCo2PredFlag()==0)? preddata.getCo2():preddata.getCo2Pred(),
                (preddata.getCh4PredFlag()==0)? preddata.getCh4():preddata.getCh4Pred(),
                preddata.getId()
        );
    }
}

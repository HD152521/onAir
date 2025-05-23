package com.sejong.project.onair.domain.preddata.dto;

import com.sejong.project.onair.domain.preddata.model.Preddata;

import java.time.LocalDateTime;

public class PreddataResponse{
    public record ResponseDto(
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
        public static ResponseDto from(Preddata preddata) {
            return new ResponseDto(
                    preddata.getMeasurementTime(),
                    preddata.getNearestObservatoryName(),
                    preddata.getSo2(),
                    preddata.getPm25(),
                    preddata.getPm10(),
                    preddata.getO3(),
                    preddata.getNo2(),
                    preddata.getNoAvg(),
                    preddata.getCo(),
                    (preddata.getCo2PredFlag() == 0) ? preddata.getCo2() : preddata.getCo2Pred(),
                    (preddata.getCh4PredFlag() == 0) ? preddata.getCh4() : preddata.getCh4Pred(),
                    preddata.getId()
            );
        }
    }

    public record SpecificDataDto(
            Double vlaue,
            double dmX,
            double dmY
    ){
        public static SpecificDataDto from(Preddata preddata, String airType){
            Double value;
            if(airType.equals("so2")) value = preddata.getSo2();
            else if(airType.equals("pm25")) value = preddata.getPm25();
            else if(airType.equals("pm10")) value = preddata.getPm10();
            else if(airType.equals("o3")) value = preddata.getO3();
            else if(airType.equals("no2")) value = preddata.getNo2();
            else if(airType.equals("noAvg")) value= preddata.getNoAvg();
            else if(airType.equals("co")) value = preddata.getCo();
            else if(airType.equals("co2")) value = preddata.getCo2();
            else if(airType.equals("ch4")) value = preddata.getCh4();
            else value = null;
            return new SpecificDataDto(value, preddata.getDmX(), preddata.getDmY());
        }
    }
}

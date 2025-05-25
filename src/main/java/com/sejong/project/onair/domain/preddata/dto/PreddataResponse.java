package com.sejong.project.onair.domain.preddata.dto;

import com.sejong.project.onair.domain.preddata.model.Preddata;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.function.Function;

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
            Double ch4
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
                    (preddata.getCh4PredFlag() == 0) ? preddata.getCh4() : preddata.getCh4Pred()
            );
        }
    }

    public record SpecificDataDto(
            Double value,
            double dmX,
            double dmY
    ){
        private static final Map<String, Function<Preddata, Double>> AIR_TYPE_GETTERS = Map.of(
                "so2", Preddata::getSo2,
                "pm25", Preddata::getPm25,
                "pm10", Preddata::getPm10,
                "o3", Preddata::getO3,
                "no2", Preddata::getNo2,
                "noAvg", Preddata::getNoAvg,
                "co", Preddata::getCo,
                "co2", Preddata::getCo2,
                "ch4", Preddata::getCh4
        );

        public static SpecificDataDto from(Preddata preddata, String airType){
            Function<Preddata, Double> getter = AIR_TYPE_GETTERS.get(airType);
            Double value = (getter != null) ? getter.apply(preddata) : null;
            return new SpecificDataDto(value, preddata.getDmX(), preddata.getDmY());
        }
    }
}

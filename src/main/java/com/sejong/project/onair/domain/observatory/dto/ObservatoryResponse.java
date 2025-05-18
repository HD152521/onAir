package com.sejong.project.onair.domain.observatory.dto;

import com.sejong.project.onair.domain.observatory.model.Observatory;

import java.util.ArrayList;
import java.util.List;

public class ObservatoryResponse {
    public record UpdateDto(
            List<Observatory> newObservatory,
            List<Observatory> deletedObservatory
    ){}

    public record LocationDto(
            double dmX,
            double dmY,
            String stationName
    ){
        public static LocationDto from(Observatory observatory){
            return new LocationDto(
                    observatory.getDmX(),
                    observatory.getDmY(),
                    observatory.getStationName()
            );
        }

        public static List<LocationDto> fromAll(List<Observatory> observatories){
            List<LocationDto> dtos = new ArrayList<>();
            for(Observatory ob : observatories){
                dtos.add(new LocationDto(
                        ob.getDmX(),
                        ob.getDmY(),
                        ob.getStationName()
                ));
            }
            return dtos;
        }

    }
}

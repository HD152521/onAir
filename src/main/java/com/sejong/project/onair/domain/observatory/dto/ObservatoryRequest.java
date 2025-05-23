package com.sejong.project.onair.domain.observatory.dto;

import com.sejong.project.onair.domain.observatory.model.Observatory;

public class ObservatoryRequest{
    public record addDto(
        String centerName,
        String address,
        double latitue,
        double longitude,
        String manageName,
        int year
    ){
        public static Observatory to(addDto request){
            return Observatory.builder()
                    .stationName(request.centerName)
                    .addr(request.address)
                    .dmX(request.latitue)
                    .dmY(request.longitude)
                    .mangName(request.manageName)
                    .year(request.year)
                    .build();
        }
    }
}

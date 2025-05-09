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
                    .centerName(request.centerName)
                    .address(request.address)
                    .latitue(request.latitue)
                    .longitude(request.longitude)
                    .manageName(request.manageName)
                    .year(request.year)
                    .build();
        }
    }
}

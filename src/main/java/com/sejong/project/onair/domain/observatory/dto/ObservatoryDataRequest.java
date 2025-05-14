package com.sejong.project.onair.domain.observatory.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class ObservatoryDataRequest {
    public record rangeDto(
            LocalDate startDate,
            LocalDate endDate,
            String nation
    ){}

    public record nationDto(
            String nation
    ){}
}

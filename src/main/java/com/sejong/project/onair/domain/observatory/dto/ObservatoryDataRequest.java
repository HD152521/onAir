package com.sejong.project.onair.domain.observatory.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class ObservatoryDataRequest {
    public record HourRangeDto(
            LocalDateTime startDate,
            LocalDateTime endDate,
            String nation
    ){}

    public record DayRangeDto(
            LocalDate startDate,
            LocalDate endDate,
            String nation
    ){}

    public record nationDto(
            String nation
    ){}
}

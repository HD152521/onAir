package com.sejong.project.onair.domain.preddata.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class PreddataRequest {
    public record DayRangeDto(
            LocalDate start,
            LocalDate end,
            String stationName
    ){}

    public record HourRangeDto(
            LocalDateTime start,
            LocalDateTime end,
            String stationName
    ){}
}

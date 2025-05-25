package com.sejong.project.onair.domain.preddata.controller;

import com.sejong.project.onair.domain.observatory.dto.ObservatoryDataRequest;
import com.sejong.project.onair.domain.preddata.dto.PreddataRequest;
import com.sejong.project.onair.domain.preddata.dto.PreddataResponse;
import com.sejong.project.onair.domain.preddata.model.Preddata;
import com.sejong.project.onair.domain.preddata.service.PreddataService;
import com.sejong.project.onair.global.exception.BaseResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/pred")
@Tag(name = "pred", description = "과거 데이터 가져오는 API")
public class PreddataController {

    private static final Logger log = LoggerFactory.getLogger(PreddataController.class);
    private final PreddataService preddataService;

    @GetMapping("/get/all")
    public List<Preddata> getAllPreddata() {
        return preddataService.getAllPreddata();
    }

    @GetMapping("/get")
    public BaseResponse<?> getSpecificData(
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateTime,
            @RequestParam String airType
    ){
        return BaseResponse.onSuccess(preddataService.getSpecificData(dateTime, airType));
    }

    @GetMapping("/get/day/range")
    public BaseResponse<?> getDataFromDayRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam String nation
    ) {
        log.info("[Controller] {}부터 {}까지 {} 지역", startDate, endDate, nation);
        // DTO 로 묶어서 서비스 호출
        var dto = new PreddataRequest.DayRangeDto(startDate, endDate, nation);
        return BaseResponse.onSuccess(preddataService.getDataFormDate(dto));
    }

    @GetMapping("/get/hour/range")
    public BaseResponse<?> getDataFromHourRange(
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDateTime,
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDateTime,
            @RequestParam String nation
    ) {
        log.info("[Controller] {}부터 {}까지 {} 지역", startDateTime, endDateTime, nation);
        var dto = new PreddataRequest.HourRangeDto(startDateTime, endDateTime, nation);
        return BaseResponse.onSuccess(preddataService.getDataFormDate(dto));
    }

    @GetMapping("/get/file")
    public List<String> getHeader(){
        return preddataService.readHeader();
    }

}

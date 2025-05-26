package com.sejong.project.onair.domain.observatory.controller;

import com.sejong.project.onair.domain.member.dto.MemberDetails;
import com.sejong.project.onair.domain.observatory.dto.ObservatoryDataRequest;
import com.sejong.project.onair.domain.observatory.model.ObservatoryData;
import com.sejong.project.onair.domain.observatory.service.ObservatoryDataService;
import com.sejong.project.onair.domain.observatory.service.ObservatoryService;
import com.sejong.project.onair.global.exception.BaseResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;


@RestController
@RequiredArgsConstructor
@RequestMapping("/observatory")
@Tag(name = "ObservatoryData", description = "관측소 측정 데이터 관련 API")
public class ObservatoryDataController {
    private static final Logger log = LoggerFactory.getLogger(ObservatoryController.class);

    private final ObservatoryDataService observatoryDataService;

    @GetMapping("/data/get/day/range")
    public BaseResponse<?> getDataFromDayRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam String nation
    ) {
        log.info("[Controller] {}부터 {}까지 {} 지역", startDate, endDate, nation);
        // DTO 로 묶어서 서비스 호출
        var dto = new ObservatoryDataRequest.DayRangeDto(startDate, endDate, nation);
        return BaseResponse.onSuccess(observatoryDataService.getObjectDatasFromDBDate(dto));
    }

    @GetMapping("/data/get/hour/range")
    public BaseResponse<?> getDataFromHourRange(
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDateTime,
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDateTime,
            @RequestParam String nation
    ) {
        log.info("[Controller] {}부터 {}까지 {} 지역", startDateTime, endDateTime, nation);
        var dto = new ObservatoryDataRequest.HourRangeDto(startDateTime, endDateTime, nation);
        return BaseResponse.onSuccess(observatoryDataService.getObjectDatasFromDBDate(dto));
    }


    @GetMapping("/data/get/all")
    public BaseResponse<?> getDataAllOfObservatory(){
        return BaseResponse.onSuccess(observatoryDataService.getNowDataAllFromDB());
    }

    @PostMapping("/data/update/all")
    public BaseResponse<?> updateAllofObservatory() {
        log.info("[Controller] upadte AllofData...");
        observatoryDataService.updateObservatoryData();
        return BaseResponse.onSuccess("Success");
    }

    @PostMapping("/data/save/json")
    public BaseResponse<?> saveDataAsJson(@RequestBody String json,String nation) {
        return BaseResponse.onSuccess(observatoryDataService.saveObjectDataFromJson(json,nation));
    }

    @PostMapping("/data/dummy")
    public BaseResponse<?> saveDummyData(
            @RequestParam
            @DateTimeFormat(pattern="yyyy-MM-dd'T'HH:mm:ss") LocalDateTime startDateTime,
            @RequestParam
            @DateTimeFormat(pattern="yyyy-MM-dd'T'HH:mm:ss") LocalDateTime endDateTime,
            @AuthenticationPrincipal MemberDetails memberDetails
            ){
        return BaseResponse.onSuccess(observatoryDataService.saveDummyData(startDateTime,endDateTime));
    }

}

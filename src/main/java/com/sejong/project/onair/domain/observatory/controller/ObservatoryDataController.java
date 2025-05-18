package com.sejong.project.onair.domain.observatory.controller;

import com.sejong.project.onair.domain.observatory.dto.ObservatoryDataRequest;
import com.sejong.project.onair.domain.observatory.model.ObservatoryData;
import com.sejong.project.onair.domain.observatory.service.ObservatoryDataService;
import com.sejong.project.onair.domain.observatory.service.ObservatoryService;
import com.sejong.project.onair.global.exception.BaseResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
@RequestMapping("/observatory")
@Tag(name = "ObservatoryData", description = "관측소 측정 데이터 관련 API")
public class ObservatoryDataController {
    private static final Logger log = LoggerFactory.getLogger(ObservatoryController.class);

    private final ObservatoryDataService observatoryDataService;


    //Note 관측데이터 가져오기
    @GetMapping("/data/get/day/range")
    public BaseResponse<?> getDatafromRangeDate(@RequestBody ObservatoryDataRequest.DayRangeDto request){
        log.info("[Controller] {}부터{}까지 {}지역",request.startDate(),request.endDate(),request.nation());
        return BaseResponse.onSuccess(observatoryDataService.getObjectDatasFromDBDate(request));
    }

    @GetMapping("/data/get/hour/range")
    public BaseResponse<?> getDatafromRangeDate(@RequestBody ObservatoryDataRequest.HourRangeDto request){
        log.info("[Controller] {}부터{}까지 {}지역",request.startDate(),request.endDate(),request.nation());
        return BaseResponse.onSuccess(observatoryDataService.getObjectDatasFromDBDate(request));
    }

    @GetMapping("/data/get/all")
    public BaseResponse<?> getDataAllOfObservatory(){
        //todo 모든거 하나씩 가져오기 from repo
        return BaseResponse.onSuccess(observatoryDataService.getNowDataAllFromDB());
    }

    @PostMapping("/data/update/all")
    public BaseResponse<?> updateAllofObservatory(){
        log.info("[Controller] upadte AllofData...");
        observatoryDataService.updateObservatoryData();
        return BaseResponse.onSuccess("Success");
    }

    @PostMapping("/data/save")
    public BaseResponse<?> saveDataAsJson(@RequestBody String json,String nation){
        return BaseResponse.onSuccess(observatoryDataService.saveObjectDataFromJson(json,nation));
    }

}

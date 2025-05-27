package com.sejong.project.onair.domain.observatory.controller;

import com.sejong.project.onair.domain.observatory.dto.ObservatoryDataRequest;
import com.sejong.project.onair.domain.observatory.dto.ObservatoryResponse;
import com.sejong.project.onair.domain.observatory.model.Observatory;
import com.sejong.project.onair.domain.observatory.model.ObservatoryData;
import com.sejong.project.onair.domain.observatory.service.AirKoreaApiService;
import com.sejong.project.onair.domain.observatory.service.ObservatoryDataService;
import com.sejong.project.onair.domain.observatory.service.ObservatoryService;
import com.sejong.project.onair.global.exception.BaseResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/observatory/airkorea")
@Tag(name = "ObservatoryAirkorea", description = "에어 코리아에서 데이터 받아오는 API")
public class ObservatoryAirkoreaController {

    private static final Logger log = LoggerFactory.getLogger(ObservatoryAirkoreaController.class);
    private final ObservatoryService observatoryService;
    private final ObservatoryDataService observatoryDataService;
    private final AirKoreaApiService airKoreaApiService;

    //Note AirKoreaAPI임
    @GetMapping("/getData/String")
    public String getData(@RequestBody ObservatoryDataRequest.nationDto request){
        log.info("[controller] data/get");
        return observatoryDataService.getStringDatasFromAirkorea(request);
    }

    @GetMapping("/getData/Object")
    public List<ObservatoryData> getDataObject(@RequestBody ObservatoryDataRequest.nationDto request) throws IOException {
        log.info("[controller] data/get");
        return observatoryDataService.getObjectDatasFromAirkorea(request);
    }

    @GetMapping("/getData/last")
    //그냥 에어코리아에서 하나씩 가져오는거
    public List<ObservatoryData> getLastDataFromAllObservatory(){
        log.info("[Controller] getAllofData...");
        return observatoryDataService.getLastObjectsFromAirKorea();
    }

    @GetMapping("/get/String")
    public String getObservatoryData(){
        log.info("[controller] getObservatoryData 진입");
        return observatoryService.getAirkoreaToString();
    }

    @GetMapping("/get/Object")
    public List<Observatory> getObservatoryDataObject(){
        log.info("[controller] getObservatoryData 진입");
        return observatoryService.getAirkoreaToObject();
    }

    @PostMapping("/update")
    public ObservatoryResponse.UpdateDto updateObservatoryFromAirkorea(){
        log.info("[controller] /update/airkorea 진입 관측소 정보 업데이트");
        return observatoryService.updateObservatoryFromAirkorea();
    }

    @PostMapping("/save/today/date")
    public List<ObservatoryData> saveTodayDate(){
        log.info("[Contorller] save today airkorea data");
        return observatoryDataService.saveTodayData();
    }

    @GetMapping("/getData/String/all")
    public String getAllData(){
        return airKoreaApiService.getData();
    }
}

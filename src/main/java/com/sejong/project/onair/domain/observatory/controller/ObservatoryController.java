package com.sejong.project.onair.domain.observatory.controller;

import com.sejong.project.onair.domain.observatory.dto.ObservatoryRequest;
import com.sejong.project.onair.domain.observatory.model.Observatory;
import com.sejong.project.onair.domain.observatory.model.ObservatoryData;
import com.sejong.project.onair.domain.observatory.service.ObservatoryDataService;
import com.sejong.project.onair.domain.observatory.service.ObservatoryService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/observatory")
public class ObservatoryController {
    private static final Logger log = LoggerFactory.getLogger(ObservatoryController.class);
    /*
    todo (관측소)
       #관측소 저장
       #관측소 삭제
       #관측소 목록 보기
       #관측소별 데이터 가져오기
    */

    private final ObservatoryDataService observatoryDataService;
    private final ObservatoryService observatoryService;

    //Note AirKoreaAPI임
    @GetMapping("/getData/allJson")
    public String getData(){
        log.info("[controller] data/get");
        return observatoryDataService.getAirkoreaDataToString();
    }

    @GetMapping("/getData/allObject")
    public List<ObservatoryData> getDataObject(){
        log.info("[controller] data/get");
        return observatoryDataService.getAirkoreaDataToObject();
    }


    @GetMapping("/get/airKorea")
    public String getObservatoryData(){
        log.info("[controller] getObservatoryData 진입");
        return observatoryService.getObservatoryData();
    }


    //Note CSV파일 입력해서 관측소 데이터 저장하기
    @GetMapping("/get/csv")
    public List<Observatory> readObservatoryByCsv(@RequestParam("file") MultipartFile file){
        log.info("[controller] getObservatory/csv 진입");
        return observatoryService.readObserbatoryDataByCsv(file);
    }
    @PostMapping("/add/csv")
    public List<Observatory> addObservatoryByCsv(@RequestParam("file") MultipartFile file){
        log.info("[controller] addObservatory/csv 진입");
        return observatoryService.addObserbatoryDataByCsv(file);
    }
    @GetMapping("/getAll")
    public List<Observatory> getAllObservatory(){
        log.info("[controller] getAllObservatory 진입");
        return observatoryService.getAllObservatory();
    }
    @PostMapping("/add")
    public Observatory addObservatory(@RequestBody ObservatoryRequest.addDto request){
        return observatoryService.addObservatory(request);
    }

}

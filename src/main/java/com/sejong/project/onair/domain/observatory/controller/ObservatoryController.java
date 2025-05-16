package com.sejong.project.onair.domain.observatory.controller;

import com.sejong.project.onair.domain.observatory.dto.ObservatoryDataRequest;
import com.sejong.project.onair.domain.observatory.dto.ObservatoryRequest;
import com.sejong.project.onair.domain.observatory.dto.ObservatoryResponse;
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

    private final ObservatoryDataService observatoryDataService;
    private final ObservatoryService observatoryService;

    //Note AirKoreaAPI임
    @GetMapping("/airkorea/getData/String")
    public String getData(@RequestBody ObservatoryDataRequest.nationDto request){
        log.info("[controller] data/get");
        return observatoryDataService.getStringDatasFromAirkorea(request);
    }

    @GetMapping("/airkorea/getData/Object")
    public List<ObservatoryData> getDataObject(@RequestBody ObservatoryDataRequest.nationDto request){
        log.info("[controller] data/get");
        return observatoryDataService.getObjectDatasFromAirkorea(request);
    }

    @GetMapping("/airkorea/getData/last")
    //그냥 에어코리아에서 하나씩 가져오는거
    public List<ObservatoryData> getLastDataFromAllObservatory(){
        log.info("[Controller] getAllofData...");
        return observatoryDataService.getLastObjectDatasFromAirkorea();
    }

    @GetMapping("/airkorea/get/String")
    public String getObservatoryData(){
        log.info("[controller] getObservatoryData 진입");
        return observatoryService.getAirkoreaToString();
    }

    @GetMapping("/airkorea/get/Object")
    public List<Observatory> getObservatoryDataObject(){
        log.info("[controller] getObservatoryData 진입");
        return observatoryService.getAirkoreaToObject();
    }

    @PostMapping("/airkorea/update")
    public ObservatoryResponse.UpdateDto updateObservatoryFromAirkorea(){
        log.info("[controller] /update/airkorea 진입 관측소 정보 업데이트");
        return observatoryService.updateObservatoryFromAirkorea();
    }



    //Note 관측데이터 가져오기
    @GetMapping("/data/get/day/range")
    public List<ObservatoryData> getDatafromRangeDate(@RequestBody ObservatoryDataRequest.DayRangeDto request){
        log.info("[Controller] {}부터{}까지 {}지역",request.startDate(),request.endDate(),request.nation());
        return observatoryDataService.getObjectDatasFromDBDate(request);
    }

    @GetMapping("/data/get/hour/range")
    public List<ObservatoryData> getDatafromRangeDate(@RequestBody ObservatoryDataRequest.HourRangeDto request){
        log.info("[Controller] {}부터{}까지 {}지역",request.startDate(),request.endDate(),request.nation());
        return observatoryDataService.getObjectDatasFromDBDate(request);
    }

    @GetMapping("/data/get/all")
    public List<ObservatoryData> getDataAllOfObservatory(){
        //todo 모든거 하나씩 가져오기 from repo
        return observatoryDataService.getDataAllFromDB();
    }

    @PostMapping("/data/update/all")
    public String updateAllofObservatory(){
        log.info("[Controller] upadte AllofData...");
        observatoryDataService.updateObservatoryData();
        return "Success";
    }

    /*
    todo
       날짜 범위 선택해서 값 가져오는거
       마지막 한시간 값 다 가져오기
     */

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

    //Note 수동으로 추가 및 삭제
    @GetMapping("/getAll")
    public List<Observatory> getAllObservatory(){
        log.info("[controller] getAllObservatory 진입");
        return observatoryService.getAllObservatory();
    }
    @PostMapping("/add")
    public Observatory addObservatory(@RequestBody ObservatoryRequest.addDto request){
        log.info("[controller] addObservatory 진입");
        return observatoryService.addObservatory(request);
    }

}

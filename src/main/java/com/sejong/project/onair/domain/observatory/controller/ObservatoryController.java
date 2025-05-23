package com.sejong.project.onair.domain.observatory.controller;

import com.sejong.project.onair.domain.observatory.dto.ObservatoryDataRequest;
import com.sejong.project.onair.domain.observatory.dto.ObservatoryRequest;
import com.sejong.project.onair.domain.observatory.dto.ObservatoryResponse;
import com.sejong.project.onair.domain.observatory.model.Observatory;
import com.sejong.project.onair.domain.observatory.model.ObservatoryData;
import com.sejong.project.onair.domain.observatory.service.ObservatoryDataService;
import com.sejong.project.onair.domain.observatory.service.ObservatoryService;
import com.sejong.project.onair.global.exception.BaseResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/observatory")
@Tag(name = "Observatory", description = "관측소 관련 API")
public class ObservatoryController {
    private static final Logger log = LoggerFactory.getLogger(ObservatoryController.class);

    private final ObservatoryService observatoryService;

    //Note CSV파일 입력해서 관측소 데이터 저장하기
    @GetMapping("/get/csv")
    public BaseResponse<?> readObservatoryByCsv(@RequestParam("file") MultipartFile file){
        log.info("[controller] getObservatory/csv 진입");
        return BaseResponse.onSuccess(observatoryService.readObserbatoryDataByCsv(file));
    }
    @PostMapping("/add/csv")
    public BaseResponse<?> addObservatoryByCsv(@RequestParam("file") MultipartFile file){
        log.info("[controller] addObservatory/csv 진입");
        return BaseResponse.onSuccess(observatoryService.addObserbatoryDataByCsv(file));
    }

    //Note 수동으로 추가 및 삭제
    @GetMapping("/getAll")
    public BaseResponse<?> getAllObservatory(){
        log.info("[controller] getAllObservatory 진입");
        return BaseResponse.onSuccess(observatoryService.getAllObservatory());
    }
    @PostMapping("/add")
    public BaseResponse<?> addObservatory(@RequestBody ObservatoryRequest.addDto request){
        log.info("[controller] addObservatory 진입");
        return BaseResponse.onSuccess(observatoryService.addObservatory(request));
    }

    @GetMapping("/get/loc")
    public BaseResponse<?> getObesrvatoryLoca(){
        return BaseResponse.onSuccess(observatoryService.getAllObservatoryLoca());
    }

}

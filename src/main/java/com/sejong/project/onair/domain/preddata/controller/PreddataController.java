package com.sejong.project.onair.domain.preddata.controller;

import com.sejong.project.onair.domain.preddata.dto.PreddataResponse;
import com.sejong.project.onair.domain.preddata.model.Preddata;
import com.sejong.project.onair.domain.preddata.service.PreddataService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
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

    private final PreddataService preddataService;

    @GetMapping("/get/all")
    public List<Preddata> getAllPreddata() {
        return preddataService.getAllPreddata();
    }

    @GetMapping("/get")
    public List<PreddataResponse.SpecificDataDto> getSpecificData(
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateTime,
            @RequestParam String airType
    ){
        return preddataService.getSpecificData(dateTime, airType);
    }

    @GetMapping("/get/file")
    public List<String> getHeader(){
        return preddataService.readHeader();
    }

}

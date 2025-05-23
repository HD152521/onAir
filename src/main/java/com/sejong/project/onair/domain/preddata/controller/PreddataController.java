package com.sejong.project.onair.domain.preddata.controller;

import com.sejong.project.onair.domain.preddata.dto.PreddataResponseDto;
import com.sejong.project.onair.domain.preddata.model.Preddata;
import com.sejong.project.onair.domain.preddata.service.PreddataService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/pred")
public class PreddataController {

    private final PreddataService preddataService;

    @GetMapping("/get/all")
    public List<Preddata> getAllPreddata() {
        return preddataService.getAllPreddata();
    }

}

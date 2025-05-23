package com.sejong.project.onair.domain.preddata.service;

import com.sejong.project.onair.domain.preddata.dto.PreddataResponseDto;
import com.sejong.project.onair.domain.preddata.model.Preddata;
import com.sejong.project.onair.domain.preddata.repository.PreddataRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PreddataService {

    private static final Logger log = LoggerFactory.getLogger(PreddataService.class);
    private final PreddataRepository preddataRepository;

    public List<Preddata> getAllPreddata() {
        log.info("[Service] getAllPreddata 진입");
        List<Preddata> datas = new ArrayList<>();
        try{
            datas = preddataRepository.findAll();
        }catch (Exception e){
            log.info(e.getMessage());
        }
        return datas;
    }

    public List<PreddataResponseDto> getAllPreddataResponse(){
        List<Preddata> datas = getAllPreddata();
        List<PreddataResponseDto> response = new ArrayList<>();
        for(Preddata data : datas){
            response.add(PreddataResponseDto.from(data));
        }
        log.info("finish");
        return response;
    }
}

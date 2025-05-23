package com.sejong.project.onair.domain.compWeather.service;

import com.sejong.project.onair.domain.compWeather.dto.CompWeatherResponseDto;
import com.sejong.project.onair.domain.compWeather.model.CompWeather;
import com.sejong.project.onair.domain.compWeather.repository.CompWeatherRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CompWeatherService {

    private static final Logger log = LoggerFactory.getLogger(CompWeatherService.class);
    private final CompWeatherRepository compWeatherRepository;

    public List<CompWeather> getAll(){
        try{
            List<CompWeather> datas = compWeatherRepository.findAll();
            return datas;
        }catch (Exception e){
            log.warn("weather측정소 가져오는데 실패");
        }
        return null;
    }

    public List<CompWeatherResponseDto> getLoca(){
        List<CompWeather> datas = getAll();
        try{
            List<CompWeatherResponseDto> response = new ArrayList<>();
            for(CompWeather comp : datas){
                response.add(CompWeatherResponseDto.from(comp));
            }
            return response;
        }catch (Exception e){
            log.warn("responseDto변환하는데 실패함");
        }
        return null;
    }
}

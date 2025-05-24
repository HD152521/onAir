package com.sejong.project.onair.domain.compWeather.service;

import com.sejong.project.onair.domain.compWeather.dto.CompWeatherResponseDto;
import com.sejong.project.onair.domain.compWeather.model.CompWeather;
import com.sejong.project.onair.domain.compWeather.repository.CompWeatherRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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

    public List<CompWeatherResponseDto> getLoca(LocalDateTime startDate){
        LocalDateTime start = startDate.withMinute(0)
                .withSecond(0)
                .withNano(0);
        LocalDateTime end   = start.plusHours(1);

        List<CompWeather> datas = new ArrayList<>();
        try{
            return compWeatherRepository.findByDateTimeBetween(start,end)
                    .parallelStream()
                    .map(data -> CompWeatherResponseDto.from(data))
                    .collect(Collectors.toList());
        }catch (Exception e){
            log.warn("responseDto변환하는데 실패함");
        }
        return null;
    }
}

package com.sejong.project.onair.domain.preddata.service;

import com.sejong.project.onair.domain.preddata.dto.PreddataResponse;
import com.sejong.project.onair.domain.preddata.model.Preddata;
import com.sejong.project.onair.domain.preddata.repository.PreddataRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class PreddataService {

    private static final Logger log = LoggerFactory.getLogger(PreddataService.class);
    private final PreddataRepository preddataRepository;

    @PersistenceContext
    private EntityManager em;

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

    public List<PreddataResponse.ResponseDto> getAllPreddataResponse(){
        List<Preddata> datas = getAllPreddata();
        List<PreddataResponse.ResponseDto> response = new ArrayList<>();
        for(Preddata data : datas){
            response.add(PreddataResponse.ResponseDto.from(data));
        }
        log.info("finish");
        return response;
    }

    public List<PreddataResponse.SpecificDataDto> getSpecificData(LocalDateTime dateTime, String airType){

        LocalDateTime start = dateTime .withMinute(0)
                .withSecond(0)
                .withNano(0);
        LocalDateTime end   = start.plusHours(1);

        return preddataRepository.findByMeasurementTimeBetween(start, end)
                .parallelStream()
                .map(data -> PreddataResponse.SpecificDataDto.from(data, airType))
                .collect(Collectors.toList());
    }

    private static final String CSV_FILE_PATH = "C:/Users/yongsik/Desktop/result.csv";
    public List<String> readHeader() {
        log.info("come1");
        Path path = Paths.get(CSV_FILE_PATH);
        if (Files.notExists(path)) {
            // 파일이 없으면 빈 리스트 반환

            return Collections.emptyList();
        }
        log.info("come");
        try (BufferedReader br = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            String headerLine = br.readLine();
            if (headerLine == null || headerLine.isBlank()) {
                return Collections.emptyList();
            }
            return Arrays.stream(headerLine.split(","))
                    .map(String::trim)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            // 로깅 후 빈 리스트 반환하거나, RuntimeException 으로 재던지기
            e.printStackTrace();
            return Collections.emptyList();
        }
    }


}

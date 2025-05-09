package com.sejong.project.onair.domain.observatory.service;

import com.sejong.project.onair.domain.observatory.model.Observatory;
import com.sejong.project.onair.domain.observatory.repository.ObservatoryRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ObservatoryService {

    private static final Logger log = LoggerFactory.getLogger(ObservatoryService.class);
    private final ObservatoryRepository observatoryRepository;

    public List<Observatory> readObserbatoryDataByCsv(MultipartFile file){
        List<String> lines = new ArrayList<>();
        List<Observatory> observatories = new ArrayList<>();
        String line;
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {

            boolean isFirstLine = true;

            // CSV 헤더 건너뛰기
            while ((line = reader.readLine()) != null) {
                if (isFirstLine) {
                    isFirstLine = false;
                    continue;
                }

                // 필드 분리: 큰따옴표 안의 콤마를 무시하기 위해 정규식 사용
                String[] tokens = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");

                // 최소 7개 필드가 있어야 함
                if (tokens.length < 7) {
                    continue;
                }

                Observatory obs = Observatory.builder()
                        .centerName(tokens[0].trim())
                        .address(tokens[1].trim())
                        .latitue(Double.parseDouble(tokens[2].trim()))   // dmX: 위도
                        .longitude(Double.parseDouble(tokens[3].trim())) // dmY: 경도
                        .manageName(tokens[4].trim())
                        .year(Integer.parseInt(tokens[6].trim()))
                        .build();

                observatories.add(obs);
            }
        } catch (IOException e) {
            log.warn("[Service] CSV파일 읽기 실패");
            throw new RuntimeException("CSV 파일 읽기 실패", e);
        }
        return observatories;
    }

    @Transactional
    public List<Observatory> addObserbatoryDataByCsv(MultipartFile file) {
        List<Observatory> observatories = readObserbatoryDataByCsv(file);
        try {
            observatoryRepository.saveAll(observatories);
        }catch (Exception e){
            log.warn("[Service] csv데이터 mysql저장하는데 오류 발생");
        }
        return observatories;
    }

}

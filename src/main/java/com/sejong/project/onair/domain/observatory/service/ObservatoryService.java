package com.sejong.project.onair.domain.observatory.service;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.sejong.project.onair.domain.observatory.dto.ObservatoryRequest;
import com.sejong.project.onair.domain.observatory.dto.ObservatoryResponse;
import com.sejong.project.onair.domain.observatory.model.Observatory;
import com.sejong.project.onair.domain.observatory.model.ObservatoryData;
import com.sejong.project.onair.domain.observatory.repository.ObservatoryRepository;
import com.sejong.project.onair.global.exception.BaseException;
import com.sejong.project.onair.global.exception.BaseResponse;
import com.sejong.project.onair.global.exception.codes.ErrorCode;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ObservatoryService {

    private static final Logger log = LoggerFactory.getLogger(ObservatoryService.class);
    private final ObservatoryRepository observatoryRepository;
    private final AirKoreaApiService airKoreaApiService;

    public String getAirkoreaToString(){
        return airKoreaApiService.getObservatory();
    }

    public List<Observatory> getAirkoreaToObject(){
        return parseObservatoryList(getAirkoreaToString());
    }


    @Cacheable(value = "observatoryList", unless = "#result == null")
    public List<Observatory> getAllObservatory(){
        try{
            return observatoryRepository.findAll()
                    .parallelStream()
                    .collect(Collectors.toList());
        }catch(Exception e){
            log.warn("모든 관측소 가져오는데 실패 ");
        }
        return null;
    }

    public List<Observatory> getRandomObservatory(){
        List<Observatory> observatories = getAllObservatory();
        List<Observatory> response = new ArrayList<>();
        Random random = new Random();
        for(int i=0;i<20;i++){
            response.add(observatories.get(random.nextInt(observatories.size())));
        }
        return response;
    }

    @Transactional
    public Observatory addObservatory(ObservatoryRequest.addDto request){
        Observatory observatory = ObservatoryRequest.addDto.to(request);
        try {
            observatoryRepository.save(observatory);
        }catch(Exception e){
//            throw new BaseException(ErrorCode.DATA_SAVE_ERROR);
            log.warn("{}",ErrorCode.DATA_SAVE_ERROR);
        }
        return observatory;
    }


    @Scheduled(cron = "0 0 3 1 * ?")
    @CacheEvict(value = "observatoryList", allEntries = true, beforeInvocation = true)
    //todo 매월 업데이트하기
    public ObservatoryResponse.UpdateDto updateObservatoryFromAirkorea() {
        List<Observatory> myObservatoreis = getAllObservatory();
        if(myObservatoreis.isEmpty() || myObservatoreis == null){
            log.warn("데이터 가져오는데 실패");
            return null;
        }
        List<Observatory> airkoreaObservatories = getAirkoreaToObject();

        List<Observatory> newObservatories = new ArrayList<>(airkoreaObservatories);
        List<Observatory> deletedObservatories = new ArrayList<>(myObservatoreis);

        newObservatories.removeAll(myObservatoreis);                //새로 생긴 Observatory
        deletedObservatories.removeAll(airkoreaObservatories);      //삭제된 Observatory

        log.info("[Service] updateObservatoryFromAirkorea newObservatories:{}", newObservatories);
        log.info("[Service] updateObservatoryFromAirkorea deletedObservatories:{}", deletedObservatories);

        if (newObservatories.isEmpty() && deletedObservatories.isEmpty()) log.info("[Service] 현재 관측소 정보가 최신입니다.");
        observatoryRepository.deleteAll(deletedObservatories);
        observatoryRepository.saveAll(newObservatories);
        log.info("[Service] 업데이트 완료");

        return new ObservatoryResponse.UpdateDto(newObservatories, deletedObservatories);
    }


    public List<Observatory> parseObservatoryList(String json){
        List<Observatory> list = new ArrayList<>();
        try{
            ObjectMapper mapper = new ObjectMapper();
            // LocalDateTime 파싱 위해 모듈 등록
            mapper.registerModule(new JavaTimeModule());

            mapper.configure(MapperFeature.USE_ANNOTATIONS, true);
            // JSON에 없는 프로퍼티 무시
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

            log.info("[Service] parseObservatoryList mapper");

            JsonNode root = mapper.readTree(json);
            JsonNode items = root
                    .path("response")
                    .path("body")
                    .path("items");

            log.info("[Service] parseObservatoryList JsonNode");

            list = new ArrayList<>();
            if (items.isArray()) {
                for (JsonNode node : items) {
                    // node → ObservatoryData 로 자동 변환
                    Observatory data = mapper.treeToValue(node, Observatory.class);
                    list.add(data);
                }
            }
            log.info("[Service] parseObservatoryDataList list분해");
        }catch (Exception e){
            log.warn("json to Observatory객체 변환중 오류발생");
        }
        return list;
    }

    public List<Observatory> stringListToObjects(List<String> stationNames){
        return observatoryRepository.findByStationNameIn(stationNames);
    }

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
                        .stationName(tokens[0].trim())
                        .addr(tokens[1].trim())
                        .dmX(Double.parseDouble(tokens[2].trim()))   // dmX: 위도
                        .dmY(Double.parseDouble(tokens[3].trim())) // dmY: 경도
                        .mangName(tokens[4].trim())
                        .year(Integer.parseInt(tokens[6].trim()))
                        .build();

                observatories.add(obs);
            }
        } catch (IOException e) {
            log.warn("[Service] CSV파일 읽기 실패");
            log.warn("{}",ErrorCode.FILE_READ_ERROR);
//            throw new BaseException(ErrorCode.FILE_READ_ERROR);
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
            log.warn("{}",ErrorCode.DATA_SAVE_ERROR);
//            throw new BaseException(ErrorCode.DATA_SAVE_ERROR);
        }
        return observatories;
    }

    public void checkObservatory(ObservatoryData observatoryData){
        Observatory observatory =  observatoryRepository.findObservatoryByStationName(observatoryData.getStationName())
                .orElseThrow(() -> new BaseException(ErrorCode.OBSERVATORY_NOT_FOUND));
    }

    public ObservatoryResponse.FeatureCollectionDto getAllObservatoryLoca(){
        List<Observatory> observatories = getAllObservatory();
        return ObservatoryResponse.FeatureCollectionDto.from(observatories);
    }

    @CacheEvict(value = "observatoryList", allEntries = true)
    public ObservatoryResponse.LocDto getObservatoryLocaByStationName(String statinoName){
        try{
            Observatory observatory = observatoryRepository.findObservatoryByStationName(statinoName).orElseThrow(
                    () -> new BaseException(ErrorCode.OBSERVATORY_NOT_FOUND)
            );
            return new ObservatoryResponse.LocDto(observatory.getDmX(),observatory.getDmY());
        }catch (Exception e){
            log.warn("{} 좌표 가져오는데 실패",statinoName);
        }
        return null;
    }
}

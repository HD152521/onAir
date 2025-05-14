package com.sejong.project.onair.domain.observatory.service;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.sejong.project.onair.domain.observatory.dto.ObservatoryDataRequest;
import com.sejong.project.onair.domain.observatory.model.Observatory;
import com.sejong.project.onair.domain.observatory.model.ObservatoryData;
import com.sejong.project.onair.domain.observatory.repository.ObservatoryDataRepository;
import com.sejong.project.onair.global.exception.BaseException;
import com.sejong.project.onair.global.exception.codes.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ObservatoryDataService {

    private static final Logger log = LoggerFactory.getLogger(ObservatoryDataService.class);
    private final AirKoreaApiService airKoreaApiService;
    private final ObservatoryService observatoryService;
    private final ObservatoryDataRepository observatoryDataRepository;


    private final List<String> failedList = new ArrayList<>();
    private int lastHour = LocalDateTime.now().getHour();


    public String getAirkoreaDataToString(ObservatoryDataRequest.nationDto request){
        return airKoreaApiService.getDatabyObservatory(request.nation());
    }

    public List<ObservatoryData> getAirkoreaDataToObject(ObservatoryDataRequest.nationDto request){
        return parseObservatoryDataList(getAirkoreaDataToString(request),request.nation());
    }

    public ObservatoryData getAirkoreaDataToObjectLast(String nation){
        List<ObservatoryData> datas = parseObservatoryDataList(getAirkoreaDataToString(new ObservatoryDataRequest.nationDto(nation)),nation);
        int len = datas.size();
        return datas.get(len-1);
    }

    //note 관측소별 오늘 데이터 가져오기
    public List<List<ObservatoryData>> getTodayObservatoryDataFromAirkorea(){
        List<Observatory> observatories = observatoryService.getAllObservatory();
        List<List<ObservatoryData>> allObservatoryData = new ArrayList<>();
        for(int i=0;i<observatories.size();i++){
            try{
                String nation = observatories.get(i).getStationName();
                allObservatoryData.add(getAirkoreaDataToObject(new ObservatoryDataRequest.nationDto(nation)));
            }catch (Exception e){
                log.warn(e.getMessage());
                log.warn("[Service] getTodayObservatoryData {}번째 관측소 데이터 가져오는데 실패",i);
            }
        }
        return allObservatoryData;
    }

    //note 관측소 별 마지막 데이터 가져오기   fixme 근데 필요한가?
    public List<ObservatoryData> getCurrentlyObservatoryDataFromAirkorea(){
        log.info("[Service] 관측소 별 최신 데이터 하나만 가져오기");
        List<ObservatoryData> observatorieDatas = new ArrayList<>();
        List<List<ObservatoryData>> todayObservatorieDatas = getTodayObservatoryDataFromAirkorea();

        for(List<ObservatoryData> observatoryDataList : todayObservatorieDatas){
            int lastindex = observatoryDataList.size()-1;
            observatorieDatas.add(observatoryDataList.get(lastindex));
        }

        return observatorieDatas;
    }

    @Scheduled(cron = "0 */10 * * * *", zone = "Asia/Seoul")
    @Transactional
    public void updateObservatoryData(){
        int currentHour = LocalDateTime.now().getHour();

        if(currentHour != lastHour){
            log.info("[Service] updateObservatoryData 관측소 측정 데이터 업데이트 시작");

            //todo list에 있는 값 저장하기
            failedList.clear();

            List<List<ObservatoryData>> todayObservatoryData = getTodayObservatoryDataFromAirkorea();

            for(List<ObservatoryData> observatoryDataList : todayObservatoryData){
                int size = observatoryDataList.size()-1;
                try{
                    checkBeforeSave(observatoryDataList.get(size));
                }catch (Exception e){
                    String stationName = observatoryDataList.get(0).getStationName();
                    log.warn("[Service] {} 관측소 업데이트 실패 / msg:{}",stationName,e.getMessage());
                    failedList.add(stationName);
                    continue;
                }
                observatoryDataRepository.save(observatoryDataList.get(size));
            }
            log.info("[Service] updateObservatoryData 관측소 측정 데이터 업데이트 완료!");

        }else{
            if(failedList.isEmpty()) return;
            for(String station: failedList){
                ObservatoryData data = getAirkoreaDataToObjectLast(station);
                if(data.getDataTime().getHour() == LocalDateTime.now().getHour()){
                    observatoryDataRepository.save(data);
                    failedList.remove(station);
                }
            }
        }

    }

    public void checkBeforeSave(ObservatoryData observatoryData){
        checkAirkoreaUpdate(observatoryData);
        checkAlreadySave(observatoryData);
    }

    public void checkAlreadySave(ObservatoryData observatoryData){
        String station = observatoryData.getStationName();
        ObservatoryData lastObservatoryData = observatoryDataRepository.findTopByStationNameOrderByDataTimeDesc(station);
        if(observatoryData.getDataTime().equals(lastObservatoryData.getDataTime())) throw new BaseException(ErrorCode.AIRKOREA_API_ALREADY_UPDATE);

    }

    public void checkAirkoreaUpdate(ObservatoryData observatoryData){
        LocalDateTime now = LocalDateTime.now();
        if(observatoryData.getDataTime().getHour() != now.getHour()) throw new BaseException(ErrorCode.AIRKOREA_API_UPDATE_ERROR);
    }

    public List<ObservatoryData> getDatafromdate(ObservatoryDataRequest.rangeDto request){
        LocalDate start = request.startDate();
        LocalDate finish = request.endDate();
        String stationName = request.nation();
        log.info("[Service] {}부터 {}까지 {}데이터 가져오기 시작 ...",start,finish,stationName);

        LocalDateTime startDateTime = start.atStartOfDay();
        LocalDateTime endDateTime   = finish.atTime(LocalTime.MAX);
        List<ObservatoryData> datas = observatoryDataRepository.findByStationNameAndDataTimeBetweenOrderByDataTimeAsc(stationName,startDateTime, endDateTime);
        if(datas.isEmpty()){
            log.warn("[Service] 해당 기간에 데이터가 없습니다.");
        }
        return datas;
    }

    /*
       TODO 필요한 CONTROLLER
        1. 클라에서 요청시 각 관측소별 최근 데이터 전송하기
        2. 한 관측소에 관해서 지난 데이터 전송해주기 (얼마나 전인지 모름)
            2-1) 해당 일주일 데이터
            2-2) 지난 달 데이터 (원하는 달 데이터 주기)
            2-3) 지난 년도 데이터 가져가는거
        3. 1년 지나면 데이터들 옮기는거

     */

    public List<ObservatoryData> getCurrentlyDataFromDB(){
        List<ObservatoryData> observatoryDataList = new ArrayList<>();
        return observatoryDataList;
    }


    public List<ObservatoryData> parseObservatoryDataList(String json,String stationName){
        List<ObservatoryData> list = new ArrayList<>();
        try{
            ObjectMapper mapper = new ObjectMapper();
            // LocalDateTime 파싱 위해 모듈 등록
            mapper.registerModule(new JavaTimeModule());

            mapper.configure(MapperFeature.USE_ANNOTATIONS, true);
            // JSON에 없는 프로퍼티 무시
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

            log.info("[Service] parseObservatoryDataList mapper");

            JsonNode root = mapper.readTree(json);
            JsonNode items = root
                    .path("response")
                    .path("body")
                    .path("items");

            log.info("[Service] parseObservatoryDataList JsonNode");

            list = new ArrayList<>();
            if (items.isArray()) {
                for (JsonNode node : items) {
                    // node → ObservatoryData 로 자동 변환
                    ObservatoryData data = mapper.treeToValue(node, ObservatoryData.class);
                    data.setStationName(stationName);
                    list.add(data);
                }
            }
            log.info("[Service] parseObservatoryDataList list분해");
        }catch (Exception e){
            log.warn("json to Observatory객체 변환중 오류발생");
        }
        return list;
    }
}

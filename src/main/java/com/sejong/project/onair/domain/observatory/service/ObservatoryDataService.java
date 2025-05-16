package com.sejong.project.onair.domain.observatory.service;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.sejong.project.onair.domain.observatory.dto.ObservatoryDataRequest;
import com.sejong.project.onair.domain.observatory.model.Observatory;
import com.sejong.project.onair.domain.observatory.model.ObservatoryData;
import com.sejong.project.onair.domain.observatory.repository.ObservatoryDataRepository;
import com.sejong.project.onair.domain.observatory.repository.ObservatoryRepository;
import com.sejong.project.onair.global.entity.NullOnInvalidDoubleDeserializer;
import com.sejong.project.onair.global.entity.NullOnInvalidIntegerDeserializer;
import com.sejong.project.onair.global.exception.BaseException;
import com.sejong.project.onair.global.exception.codes.ErrorCode;
import io.swagger.v3.core.util.Json;
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
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ObservatoryDataService {

    private static final Logger log = LoggerFactory.getLogger(ObservatoryDataService.class);
    private final AirKoreaApiService airKoreaApiService;
    private final ObservatoryService observatoryService;
    private final ObservatoryDataRepository observatoryDataRepository;


    private final List<String> failedList = new ArrayList<>();
    //    private int lastHour = LocalDateTime.now().getHour();
    private int lastHour = -1;

    /*
       TODO 필요한 CONTROLLER
        3. 1년 지나면 데이터들 옮기는거
     */


    public String getStringDatasFromAirkorea(ObservatoryDataRequest.nationDto request){
        return airKoreaApiService.getDatabyObservatory(request.nation());
    }

    public List<ObservatoryData> getObjectDatasFromAirkorea(ObservatoryDataRequest.nationDto request){
        return parseObservatoryDataList(getStringDatasFromAirkorea(request),request.nation());
    }


    public ObservatoryData getLastObjectDataFromAirkorea(String nation){
        List<ObservatoryData> datas = parseObservatoryDataList(getStringDatasFromAirkorea(new ObservatoryDataRequest.nationDto(nation)),nation);
        int len = datas.size();
        return datas.get(len-1);
    }


    //note 관측소별 오늘 데이터 가져오기
    public List<List<ObservatoryData>> getTodayObjectDataFromAirkorea(){
        List<Observatory> observatories = observatoryService.getAllObservatory();

        List<List<ObservatoryData>> allObservatoryData = new ArrayList<>();
        for(int i=0;i<observatories.size();i++){
            try{
                String nation = observatories.get(i).getStationName();
                allObservatoryData.add(getObjectDatasFromAirkorea(new ObservatoryDataRequest.nationDto(nation)));
            }catch (Exception e){
                log.warn(e.getMessage());
                log.warn("[Service] getTodayObservatoryData {}번째 관측소 데이터 가져오는데 실패",i);
            }
        }
        log.info("[Service] observatory 최근 1개 데이터만 가져옴");
        return allObservatoryData;
    }


    //note 관측소 별 마지막 데이터 가져오기   fixme 근데 필요한가?
    public List<ObservatoryData> getLastObjectDatasFromAirkorea(){
        log.info("[Service] 관측소 별 최신 데이터 하나만 가져오기");
        List<ObservatoryData> observatorieDatas = new ArrayList<>();
        List<List<ObservatoryData>> todayObservatorieDatas = getTodayObjectDataFromAirkorea();

        for(List<ObservatoryData> observatoryDataList : todayObservatorieDatas){
            int lastindex = observatoryDataList.size()-1;
            observatorieDatas.add(observatoryDataList.get(lastindex));
        }

        return observatorieDatas;
    }

//    @Scheduled(cron = "0 */10 * * * *", zone = "Asia/Seoul")
    @Transactional
    public void updateObservatoryData(){
        int currentHour = LocalDateTime.now().getHour();
        log.info("[Service] update서비스 들어옴 시간:{} 시간:{}",currentHour,lastHour);
        List<ObservatoryData> saveList = new ArrayList<>();

        if(currentHour != lastHour){
            log.info("[Service] updateObservatoryData 관측소 측정 데이터 업데이트 시작");

            //todo list에 있는 값 저장하기
            failedList.clear();

            List<ObservatoryData> todayLastObservatoryData = getLastObjectDatasFromAirkorea();

            for(ObservatoryData observatoryData : todayLastObservatoryData){
                try{
                    checkBeforeSave(observatoryData);
                    saveList.add(observatoryData);
                }catch (Exception e){
                    String stationName = observatoryData.getStationName();
                    log.warn("[Service] {} 관측소 업데이트 실패 / msg:{}",stationName,e.getMessage());
                    failedList.add(stationName);
                }
            }
            observatoryDataRepository.saveAll(saveList);
            lastHour=currentHour;
            log.info("[Service] updateObservatoryData 관측소 측정 데이터 업데이트 완료!");
            log.info("[Service] currentHour : {}, lastHour:{}",currentHour,lastHour);
        }else{
            if(failedList.isEmpty()) return;
            for(String station: failedList){
                ObservatoryData data = getLastObjectDataFromAirkorea(station);
                if(data.getDataTime().getHour() == LocalDateTime.now().getHour()){
                    observatoryDataRepository.save(data);
                    failedList.remove(station);
                }
            }
        }
    }


    public List<ObservatoryData> getObjectDatasFromDBDate(ObservatoryDataRequest.DayRangeDto request){
        String stationName = request.nation();
        LocalDateTime startDateTime = request.startDate().atStartOfDay();
        LocalDateTime endDateTime   = request.endDate().atTime(LocalTime.MAX);
        log.info("[Service] {}부터 {}까지 {}데이터 가져오기 시작 ...",startDateTime,endDateTime,stationName);
        return getObjectDatasFromDBDate(new ObservatoryDataRequest.HourRangeDto(startDateTime,endDateTime, stationName));
    }

    public List<ObservatoryData> getObjectDatasFromDBDate(ObservatoryDataRequest.HourRangeDto request){
        List<ObservatoryData> datas = new ArrayList<>();
        try {
             datas = observatoryDataRepository.
                    findByStationNameAndDataTimeBetweenOrderByDataTimeAsc(
                            request.nation(),
                            request.startDate(),
                            request.endDate());
        }catch (Exception e){
            log.warn(e.getMessage());
        }
        if(datas.isEmpty()){
            log.warn("[Service] 해당 기간에 데이터가 없습니다.");
            //todo 없을 경우 에어 코리아에서 가져오기
        }
        return datas;
    }




    public List<ObservatoryData> getDataAllFromDB(){
        List<Observatory> observatories = observatoryService.getAllObservatory();
        List<ObservatoryData> datas = new ArrayList<>();
        for(Observatory ob : observatories){
            try {
                ObservatoryData tmpOb = observatoryDataRepository.findTopByStationNameOrderByDataTimeDesc(ob.getStationName());
                datas.add(tmpOb);
            }catch (Exception e){
                log.warn(e.getMessage());
                log.warn("[Service] {} 마지막 데이터 가져오는데 오류",ob.getStationName());
            }
        }
        return datas;
    }

    public List<ObservatoryData> parseObservatoryDataList(String json,String stationName){
        JsonNode tmpJson=null;

        List<ObservatoryData> list = new ArrayList<>();
        try{
            SimpleModule module = new SimpleModule();
            module.addDeserializer(Integer.class, new NullOnInvalidIntegerDeserializer());
            module.addDeserializer(Double.class, new NullOnInvalidDoubleDeserializer());

            ObjectMapper mapper = new ObjectMapper();
            // LocalDateTime 파싱 위해 모듈 등록
            mapper.registerModule(new JavaTimeModule());
            mapper.registerModule(module);
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
                log.info("매핑 개수 : {}",items.size());
                for (JsonNode node : items) {
                    // node → ObservatoryData 로 자동 변환
                    tmpJson = node;
                    ObservatoryData data = mapper.treeToValue(node, ObservatoryData.class);
                    data.setStationName(stationName);
                    list.add(data);
                }
            }
            log.info("[Service] parseObservatoryDataList list분해");
        }catch (Exception e){
            log.info("Json node :{}",tmpJson.toString());
            log.warn("json to Observatory객체 변환중 오류발생");
            log.warn(e.getMessage());
        }
        return list;
    }
    public void checkBeforeSave(ObservatoryData observatoryData){
        checkAirkoreaUpdate(observatoryData);
        checkAlreadySave(observatoryData);
        observatoryService.checkObservatory(observatoryData);
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

}

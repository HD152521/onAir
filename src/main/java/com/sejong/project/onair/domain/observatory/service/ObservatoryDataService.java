package com.sejong.project.onair.domain.observatory.service;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.sejong.project.onair.domain.observatory.dto.ObservatoryDataRequest;
import com.sejong.project.onair.domain.observatory.dto.ObservatoryDataResponse;
import com.sejong.project.onair.domain.observatory.dto.ObservatoryResponse;
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
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

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

    @Transactional
    public List<ObservatoryData> saveObjectDataFromJson(String json,String nation){
        log.info("json:{}",json);
        List<ObservatoryData> datas = parseObservatoryDataList(json,nation);
        try{
            for(ObservatoryData obd: datas){
                checkBeforeSave(obd);
                log.info(obd.toString());
            }
            observatoryDataRepository.saveAll(datas);
        }catch (Exception e){
            log.warn(e.getMessage());
            log.warn("{}",ErrorCode.DATA_SAVE_ERROR);
//            throw new BaseException(ErrorCode.DATA_SAVE_ERROR);
        }
        return datas;
    }

    public ObservatoryData getLastObjectDataFromAirkorea(String nation){
        List<ObservatoryData> datas = parseObservatoryDataList(getStringDatasFromAirkorea(new ObservatoryDataRequest.nationDto(nation)),nation);
        int len = datas.size();
        return datas.get(0);
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
        return allObservatoryData;
    }


    //note 에어코리아에서 관측소 별 마지막 데이터 가져오기
    public List<ObservatoryData> getLastObjectDatasFromAirkorea(){
        log.info("[Service] 관측소 별 최신 데이터 하나만 가져오기");
        List<Observatory> observatories = observatoryService.getAllObservatory();
        List<ObservatoryData> observatorieDatas = new ArrayList<>();

        for(Observatory ob : observatories){
            try {
                observatorieDatas.add(getLastObjectDataFromAirkorea(ob.getStationName()));
            }catch(Exception e){
                log.info("[Service] {}관측소 데이터 가져오는데 실패", ob.getStationName());
            }
        }
        return observatorieDatas;
    }

    public List<ObservatoryData> getRandomObjectDatasFromAirkorea(){
        log.info("[Service] 관측소 별 최신 데이터 하나만 가져오기");
        List<Observatory> observatories = observatoryService.getAllObservatory();
        List<ObservatoryData> observatorieDatas = new ArrayList<>();

        Random rand = new Random();

        for(int i=0;i<10;i++){
            Observatory tmp = observatories.get(rand.nextInt(664)+1);
            try {
                observatorieDatas.add(getLastObjectDataFromAirkorea(tmp.getStationName()));
            }catch(Exception e){
                log.info("[Service] {}관측소 데이터 가져오는데 실패", tmp.getStationName());
            }
        }

        return observatorieDatas;
    }



//    @Scheduled(cron = "0 */10 * * * *", zone = "Asia/Seoul")
    @Transactional
    public void updateObservatoryData(){
//        int currentHour = LocalDateTime.now().getHour();
        int currentHour = -10;
        log.info("[Service] update서비스 들어옴 시간:{} 시간:{}",currentHour,lastHour);
        List<ObservatoryData> saveList = new ArrayList<>();
        if(currentHour != lastHour){
            log.info("[Service] updateObservatoryData 관측소 측정 데이터 업데이트 시작");

            //todo list에 있는 값 저장하기
            failedList.clear();

            //Note 모든거 하나씩 가져오기
//            List<ObservatoryData> todayLastObservatoryData = getLastObjectDatasFromAirkorea();
            //note random값 10개
            List<ObservatoryData> todayLastObservatoryData = getRandomObjectDatasFromAirkorea();

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
            try{
                observatoryDataRepository.saveAll(saveList);
            }catch (Exception e){
//                throw new BaseException(ErrorCode.OBSERVATORY_DATA_SAVE_ERROR);
                log.warn("{}",ErrorCode.OBSERVATORY_DATA_SAVE_ERROR);
            }
            lastHour=LocalDateTime.now().getHour();
            log.info("[Service] updateObservatoryData 관측소 측정 데이터 업데이트 완료!");
            log.info("[Service] currentHour : {}, lastHour:{}",currentHour,lastHour);
        }else{
            log.info("시간 달라서 실패한것만 확인함");
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


    public List<ObservatoryDataResponse.FlagFilterDto> getObjectDatasFromDBDate(ObservatoryDataRequest.DayRangeDto request){
        String stationName = request.nation();
        LocalDateTime startDateTime = request.startDate().atStartOfDay();
        LocalDateTime endDateTime   = request.endDate().atTime(LocalTime.MAX);
        log.info("[Service] {}부터 {}까지 {}데이터 가져오기 시작 ...",startDateTime,endDateTime,stationName);
        return getObjectDatasFromDBDate(new ObservatoryDataRequest.HourRangeDto(startDateTime,endDateTime, stationName));
    }


    public List<ObservatoryDataResponse.FlagFilterDto> getObjectDatasFromDBDate(ObservatoryDataRequest.HourRangeDto request){
        List<ObservatoryData> datas = new ArrayList<>();
        List<ObservatoryDataResponse.FlagFilterDto> response = new ArrayList<>();

        try{
            datas = observatoryDataRepository.
                    findByStationNameAndDataTimeBetweenOrderByDataTimeAsc(
                            request.nation(),
                            request.startDate(),
                            request.endDate());
        }catch(Exception e){
            log.warn("datas가져오는 실패함");
        }

        if(datas.isEmpty()) {
            log.warn("[Service] 해당 기간에 데이터가 없습니다.");
//            throw new BaseException(ErrorCode.DATA_NOT_FOUND);
            //todo 없을 경우 에어 코리아에서 가져오기
        }
        response = ObservatoryDataResponse.FlagFilterDto.toAll(datas);

        return response;
    }

    //note db에서 마지막 데이터만 가져옴
    public List<ObservatoryDataResponse.FlagFilterDto> getLastDataAllFromDB(){
        List<Observatory> observatories = observatoryService.getAllObservatory();
        List<ObservatoryDataResponse.FlagFilterDto> response = new ArrayList<>();
        for(Observatory ob : observatories){
            try {
                ObservatoryData tmpOb = observatoryDataRepository.findTopByStationNameOrderByDataTimeDesc(ob.getStationName());
                response.add(ObservatoryDataResponse.FlagFilterDto.to(tmpOb));
            }catch (Exception e){
                log.warn(e.getMessage());
                log.warn("[Service] {} 마지막 데이터 가져오는데 오류",ob.getStationName());
            }
        }
        return response;
    }

    //todo 마지막 데이터 가져와서 현재 시간이랑 같은지 확인하기.
    public List<ObservatoryDataResponse.FlagFilterDto> getNowDataAllFromDB(){
        List<ObservatoryDataResponse.FlagFilterDto> dtos = getLastDataAllFromDB();
        List<ObservatoryDataResponse.FlagFilterDto> response = new ArrayList<>();
        int nowHour = LocalDateTime.now().getHour();

        for(ObservatoryDataResponse.FlagFilterDto dto: dtos){
            if(dto.dataTime().getHour()==nowHour) response.add(dto);
        }

        if(response.isEmpty()){
            log.warn("현재 데이터 가져오는데 실패함.");
//            throw new BaseException(ErrorCode.OBSERVATORY_DATA_NOT_FOUND);
        }

        return response;
    }

    @Transactional
    public List<ObservatoryDataResponse.FlagFilterDto> saveDummyData(){
        List<Observatory> observatories = observatoryService.getAllObservatory();
        List<ObservatoryData> dataList = new ArrayList<>();

        LocalDateTime start = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        log.info("[Service] dummy data 주입중...");
        for (Observatory ob : observatories) {
            String dateString = start.format(formatter);

            ObservatoryData data = ObservatoryData.builder()
                    .so2Grade(1)
                    .coFlag(null)
                    .khaiValue(50)
                    .so2Value(0.005)
                    .coValue(0.4)
                    .pm25Flag(null)
                    .pm10Flag(null)
                    .pm10Value(30)
                    .o3Grade(2)
                    .khaiGrade(3)
                    .pm25Value(20)
                    .no2Flag(null)
                    .no2Grade(1)
                    .o3Flag(null)
                    .pm25Grade(2)
                    .so2Flag(null)
                    .dataTimeString(dateString)
                    .coGrade(1)
                    .no2Value(0.02)
                    .pm10Grade(2)
                    .o3Value(0.03)
                    .stationName(ob.getStationName())
                    .build();
            data.changeDate();
            dataList.add(data);
        }
        System.out.println("observatories.size = " + observatories.size());
        System.out.println("dataList.size = " + dataList.size());
        // MySQL에 저장
        try{
            observatoryDataRepository.saveAll(dataList);
        }catch (Exception e){
            log.warn(e.getMessage());
        }
        log.info("[Service] dummy data 주입 완료");
        return ObservatoryDataResponse.FlagFilterDto.toAll(dataList);
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
            //note json테스트 안하면 json빼기
            JsonNode items = root
//                    .path("json")
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
                    data.changeDate();
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
        try{
            checkAirkoreaUpdate(observatoryData);
            checkAlreadySave(observatoryData);
            observatoryService.checkObservatory(observatoryData);
        }catch (Exception e){
            log.warn(e.getMessage());
        }
    }


    public void checkFlag(ObservatoryData data){
        log.info("flag값 확인 flag가 널 아니면 해당 변수 값을 널로 해줘야함");
        if (data.getSo2Flag() != null) {
            data.setSo2Value(null);
            data.setSo2Grade(null);
        }
        // CO
        if (data.getCoFlag() != null) {
            data.setCoValue(null);
            data.setCoGrade(null);
        }
        // NO2
        if (data.getNo2Flag() != null) {
            data.setNo2Value(null);
            data.setNo2Grade(null);
        }
        // O3
        if (data.getO3Flag() != null) {
            data.setO3Value(null);
            data.setO3Grade(null);
        }
        // PM10
        if (data.getPm10Flag() != null) {
            data.setPm10Value(null);
            data.setPm10Grade(null);
        }
        // PM2.5
        if (data.getPm25Flag() != null) {
            data.setPm25Value(null);
            data.setPm25Grade(null);
        }
    }

    public void checkAlreadySave(ObservatoryData observatoryData){
        log.info("데이터가 이미 있는지 확인");
        String station = observatoryData.getStationName();
        ObservatoryData lastObservatoryData = observatoryDataRepository.findTopByStationNameOrderByDataTimeDesc(station);
        if(lastObservatoryData == null) return;
        if(observatoryData.getDataTime().equals(lastObservatoryData.getDataTime())) throw new BaseException(ErrorCode.AIRKOREA_API_ALREADY_UPDATE);
    }

    public void checkAirkoreaUpdate(ObservatoryData observatoryData){
        log.info("에어코리아 데이터가 최신인지 확인 / 데이터 시간:{}",observatoryData.getDataTime().getHour());
        LocalDateTime now = LocalDateTime.now();
        if(observatoryData.getDataTime().getHour() != now.getHour()) throw new BaseException(ErrorCode.AIRKOREA_API_UPDATE_ERROR);
    }

}

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
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.apache.commons.math3.util.Precision.round;

@Service
@RequiredArgsConstructor
public class ObservatoryDataService {

    private static final Logger log = LoggerFactory.getLogger(ObservatoryDataService.class);
    private final AirKoreaApiService airKoreaApiService;
    private final ObservatoryService observatoryService;
    private final ObservatoryDataRepository observatoryDataRepository;
    private final ObservatoryDataSaveService observatoryDataSaveService;

    private static final int BATCH_SIZE = 100;

    @PersistenceContext
    private EntityManager em;

    @Qualifier("apiExecutor")
    private final Executor apiExecutor;


    private final List<String> failedList = new ArrayList<>();
//        private int lastHour = ZonedDateTime.now(ZoneId.of("Asia/Seoul")).toLocalDateTime().getHour();
    private int lastHour = -2;

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
            log.info("json:{}",json.toString());

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
    public ObservatoryData parseLastJsonToObservatoryData(String json,String stationName){
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
            log.info("json:{}",json.toString());

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
                tmpJson = items.get(0);
                ObservatoryData data = mapper.treeToValue(tmpJson, ObservatoryData.class);
                data.setStationName(stationName);
                data.changeDate();
                return data;
            }
            log.info("[Service] parseObservatoryDataList list분해");
        }catch (Exception e){
            log.info("Json node :{}",tmpJson.toString());
            log.warn("json to Observatory객체 변환중 오류발생");
            log.warn(e.getMessage());
        }
        return null;
    }

    //note 모든 데이터 가져오는데 병렬처리 (하루치)
    public List<ObservatoryData> getAllObjectsFromAirKorea(){
        List<Observatory> observatories = observatoryService.getAllObservatory();
//        List<Observatory> observatories = observatoryService.getRandomObservatory();
        List<CompletableFuture<List<ObservatoryData>>> futures = observatories.stream()
                .map(st ->
                        CompletableFuture.supplyAsync(
                                () -> {
                                    try {
                                        return parseObservatoryDataList(airKoreaApiService.getDatabyObservatory(st.getStationName()),st.getStationName());
                                    } catch (Exception e) {
                                        log.warn("실패: " + st, e);
                                        return null;
                                    }
                                },
                                apiExecutor
                        )
                )
                .collect(Collectors.toList());

        CompletableFuture<Void> allDone = CompletableFuture
                .allOf(futures.toArray(new CompletableFuture[0]));
        allDone.join();

        return futures.stream()
                .map(CompletableFuture::join)
                .flatMap(List::stream)
                .collect(Collectors.toList());
    }
    public List<ObservatoryData> getLastObjectsFromAirKorea(){
        List<Observatory> observatories = observatoryService.getAllObservatory();
//        List<Observatory> observatories = observatoryService.getRandomObservatory();
        List<CompletableFuture<ObservatoryData>> futures = observatories.stream()
                .map(st ->
                        CompletableFuture.supplyAsync(
                                () -> {
                                    try {
                                        return parseLastJsonToObservatoryData(airKoreaApiService.getDatabyObservatory(st.getStationName()),st.getStationName());
                                    } catch (Exception e) {
                                        log.warn("실패: " + st, e);
                                        return null;
                                    }
                                },
                                apiExecutor
                        )
                )
                .collect(Collectors.toList());

        CompletableFuture<Void> allDone = CompletableFuture
                .allOf(futures.toArray(new CompletableFuture[0]));
        allDone.join();

        return futures.stream()
                .map(CompletableFuture::join)
                .collect(Collectors.toList());
    }

    @Transactional
    public void batchSave(List<ObservatoryData> dataList) {
        int count = 0;
        for (ObservatoryData data : dataList) {
            em.persist(data);
            if (++count % BATCH_SIZE == 0) {
                em.flush();
                em.clear();
            }
        }
        // 남은 것들 flush/clear
        em.flush();
        em.clear();
    }

    @Transactional
    public List<ObservatoryData> saveTodayData(){
        List<ObservatoryData> allData = getAllObjectsFromAirKorea();

        // 2) 중복 체크 등 비즈니스 로직
        List<ObservatoryData> toSave = allData.stream()
                .filter(d -> {
                    try {
                        checkAlreadySave(d);
                        return true;
                    } catch (Exception ex) {
                        log.warn("[{}] 검증 실패: {}", d.getStationName(), ex.getMessage());
                        return false;
                    }
                })
                .collect(Collectors.toList());

        // 3) 배치 저장
        batchSave(toSave);

        return toSave;
    }


    @Transactional
    public List<ObservatoryData> saveLastData() {
        List<ObservatoryData> result = Collections.synchronizedList(new ArrayList<>());
        ExecutorService executor = Executors.newFixedThreadPool(20); // 병렬 스레드 제한

        List<CompletableFuture<Void>> futures = observatoryService.getAllObservatory().stream()
//        List<CompletableFuture<Void>> futures = observatoryService.getRandomObservatory().stream()
                .map(obs -> CompletableFuture.runAsync(() -> {
                    try {
                        List<ObservatoryData> dataList = getLastObjectsFromAirKorea();
                        if (dataList != null && !dataList.isEmpty()) {
                            ObservatoryData data = dataList.get(0);

                            try {
                                checkBeforeSave(data);
                                log.info("{} 관측소 검증 성공", data.getStationName());

                                synchronized (result) {
                                    result.add(data);
                                    observatoryDataSaveService.saveData(data);
                                    if (result.size() % BATCH_SIZE == 0) {
                                        em.flush();
                                        em.clear();
                                    }
                                }

                            } catch (Exception e) {
                                log.warn("검증 실패 [{}]: {}", data.getStationName(), e.getMessage());
                                failedList.add(data.getStationName());
                            }
                        }
                    } catch (Exception e) {
                        log.warn("[{}] 관측소 API 실패: {}", obs.getStationName(), e.getMessage());
                        failedList.add(obs.getStationName());
                    }
                }, executor))
                .collect(Collectors.toList());

        // 모든 작업 완료될 때까지 대기
        futures.forEach(CompletableFuture::join);

        em.flush();
        em.clear();
        executor.shutdown();

        log.info("모든 데이터 저장 완료");
        return result;
    }

    @Transactional
    public List<ObservatoryData> savefailedData(List<String> failedList){
        List<ObservatoryData> result = new ArrayList<>();
        try {
            observatoryService.stringListToObjects(failedList).stream()
                    // 2. 각 관측소별 API 호출 → 데이터 리스트 스트림으로 변환
                    .flatMap(obs -> {
                        try {
                            return getObjectDatasFromAirkorea(new ObservatoryDataRequest.nationDto(obs.getStationName()))
                                    .stream()
                                    .limit(1);
                        } catch (Exception ex) {
                            log.warn("[{}] 관측소 데이터 조회 실패: {}", obs.getStationName(), ex.getMessage());
                            return Stream.empty();
                        }
                    })
                    // 3. 유효성 검사 통과한 데이터만
                    .filter(data -> {
                        try {
                            checkBeforeSave(data);
                            log.info("{} 관측소 성공", data.getStationName());
                            return true;
                        } catch (Exception ex) {
                            log.warn("검증 실패 [{}]: {}", data.getStationName(), ex.getMessage());
                            return false;
                        }
                    })
                    // 4. 배치 저장
                    .forEachOrdered(data -> {
                        try {
                            log.info("{} 관측소 데이터 배치까지 가져옴",data.getStationName());
                            result.add(data);
                            em.persist(data);  // 영속화
                            if (result.size() % BATCH_SIZE == 0) {
                                em.flush();
                                em.clear();  // 1차 캐시 비우기
                            }
                            failedList.remove(data.getStationName());
                        }catch (Exception e){
                            log.error("[{}] 저장 실패: {}", data.getStationName(), e.getMessage(), e);
                        }
                    });
        }catch (Exception e){
            log.warn("하루치 데이터 저장하는데 오류가 발생함");
        }

        log.info("save종료");

        // 남은 건들 flush
        em.flush();
        em.clear();

        return result;
    }

//    @Scheduled(cron = "0 */10 * * * *", zone = "Asia/Seoul")
    @Transactional
    @CacheEvict(value = "observatoryDataList", allEntries = true, beforeInvocation = true)
    public void updateObservatoryData(){

        LocalDateTime now = ZonedDateTime.now(ZoneId.of("Asia/Seoul")).toLocalDateTime();
        int currentHour = now.getHour();
        log.info("[Service] update서비스 들어옴 시간:{} /  지난 마지막 업데이트 시간:{}",currentHour,lastHour);

        if(now.getMinute()==10 || currentHour!=lastHour){
            log.info("[Service] new updateObservatoryData 관측소 측정 데이터 새로 업데이트 시작");

            failedList.clear();
            saveLastData();

            lastHour=ZonedDateTime.now(ZoneId.of("Asia/Seoul")).toLocalDateTime().getHour();
            log.info("[Service] {}시간 updateObservatoryData 관측소 측정 데이터 업데이트 완료!",currentHour);

            log.info("실패 관측소 모음 : {}",failedList);
        }else{
            if(failedList.isEmpty()) return;
            savefailedData(failedList);
            log.info("아직 실패인 관측소 모음 : {}",failedList);
        }
    }


    public List<ObservatoryDataResponse.FlagFilterDto> getObjectDatasFromDBDate(ObservatoryDataRequest.DayRangeDto request){
        String stationName = request.nation();
        LocalDateTime startDateTime = request.startDate().atStartOfDay();
        LocalDateTime endDateTime   = request.endDate().atTime(LocalTime.MAX);
        log.info("[Service] {}부터 {}까지 {}데이터 가져오기 시작 ...",startDateTime,endDateTime,stationName);
        return getObjectDatasFromDBDate(new ObservatoryDataRequest.HourRangeDto(startDateTime,endDateTime, stationName));
    }

    @Cacheable(value = "observatoryDataList", unless = "#result == null")
    public List<ObservatoryDataResponse.FlagFilterDto> getObjectDatasFromDBDate(ObservatoryDataRequest.HourRangeDto request){
        try{
            return observatoryDataRepository.
                    findByStationNameAndDataTimeBetweenOrderByDataTimeAsc(
                            request.nation(),
                            request.startDate(),
                            request.endDate()
                    ).parallelStream()
                    .map(data -> ObservatoryDataResponse.FlagFilterDto.to(data))
                    .collect(Collectors.toList());
        }catch(Exception e){
            log.warn("datas가져오는 실패함");
        }

        return null;
    }

    //note db에서 마지막 데이터만 가져옴
    @Cacheable(value = "observatoryDataList", unless = "#result == null")
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

    @Cacheable(value = "observatoryDataList", unless = "#result == null")
    public List<ObservatoryDataResponse.FlagFilterDto> getNowDataAllFromDB(){
        log.info("now 데이터 가져오기 시작...");
        LocalDateTime now = ZonedDateTime.now(ZoneId.of("Asia/Seoul")).toLocalDateTime().withMinute(0).withSecond(0).withNano(0);
        LocalDateTime oneHourLater = now.plusHours(1);
        log.info("시작시간:{} 끝 시간{}",now,oneHourLater);

            try {
                return observatoryDataRepository.findByDataTimeBetween(now,oneHourLater)
                        .parallelStream()
                        .map(data -> ObservatoryDataResponse.FlagFilterDto.to(data))
                        .collect(Collectors.toList());
            }catch (Exception e){
                log.warn(e.getMessage());
                log.warn("관측 데이터 가져오는데 실패");
            }

        return null;
    }

    @Transactional
    public List<ObservatoryDataResponse.FlagFilterDto> saveDummyData(LocalDateTime startDateTime, LocalDateTime endDateTime){
        List<Observatory> observatories = observatoryService.getAllObservatory();
        List<ObservatoryData> dataList = new ArrayList<>();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        ThreadLocalRandom r = ThreadLocalRandom.current();

        log.info("[Service] dummy data 주입중...");
        for (LocalDateTime dt = startDateTime; !dt.isAfter(endDateTime); dt = dt.plusHours(1)) {
            for (Observatory ob : observatories) {
                String dateString = dt.format(formatter);
                ObservatoryData data = ObservatoryData.builder()
                        .so2Grade(r.nextInt(1, 5))
                        .coGrade(r.nextInt(1, 5))
                        .no2Grade(r.nextInt(1, 5))
                        .o3Grade(r.nextInt(1, 5))
                        .pm10Grade(r.nextInt(1, 5))
                        .pm25Grade(r.nextInt(1, 5))

                        // 수치 값: 적절한 범위 설정 (예: ppm 단위, μg/m³ 단위 등)
                        .so2Value(round(r.nextDouble(0.0, 0.1), 3))       // 0.000 ~ 0.099 ppm
                        .coValue(round(r.nextDouble(0.0, 2.0), 2))        // 0.00  ~ 1.99 ppm
                        .no2Value(round(r.nextDouble(0.0, 0.2), 3))       // 0.000 ~ 0.199 ppm
                        .o3Value(round(r.nextDouble(0.0, 0.2), 3))        // 0.000 ~ 0.199 ppm
                        .pm10Value(r.nextInt(0, 151))                    // 0 ~ 150 µg/m³
                        .pm25Value(r.nextInt(0, 76))                     // 0 ~ 75  µg/m³

                        // 종합 대기지수
                        .khaiValue(r.nextInt(0, 601))                    // 0 ~ 600
                        .khaiGrade(r.nextInt(1, 5))                      // 1 ~ 4

                        // Flag 필드는 측정 이상 여부나 null 허용 등으로
                        .so2Flag(null)
                        .coFlag(null)
                        .no2Flag(null)
                        .o3Flag(null)
                        .pm10Flag(null)
                        .pm25Flag(null)

                        .dataTimeString(dateString)
                        .stationName(ob.getStationName())
                        .build();
                data.changeDate();
                dataList.add(data);
            }
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

    public void checkBeforeSave(ObservatoryData observatoryData){
        try{
            checkAirkoreaUpdate(observatoryData);
//            checkAlreadySave(observatoryData);
        }catch (Exception e){
            log.warn(e.getMessage());
            throw e;
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
        if(lastObservatoryData == null) throw  new BaseException(ErrorCode.DATA_NOT_FOUND);
        if(observatoryData.getDataTime().equals(lastObservatoryData.getDataTime())) throw new BaseException(ErrorCode.AIRKOREA_API_ALREADY_UPDATE);
    }

    public void checkAirkoreaUpdate(ObservatoryData observatoryData){
        LocalDateTime now = ZonedDateTime.now(ZoneId.of("Asia/Seoul")).toLocalDateTime();
        log.info("에어코리아 데이터가 최신인지 확인 / 데이터 시간:{} / 지금 시간:{}",observatoryData.getDataTime().getHour(),now.getHour());
        if(observatoryData.getDataTime().getHour() != now.getHour()) throw new BaseException(ErrorCode.AIRKOREA_API_UPDATE_ERROR);
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
}

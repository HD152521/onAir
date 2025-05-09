package com.sejong.project.onair.domain.observatory.service;

import com.sejong.project.onair.domain.observatory.dto.ObservatoryRequest;
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
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ObservatoryService {

    private static final Logger log = LoggerFactory.getLogger(ObservatoryService.class);
    private final ObservatoryRepository observatoryRepository;

    String serviceKey="%2FNVRnCn1bzQ%2F4cga43dtjmuDuRGXfjO6aBApVgpK6FTXRkpXAIl1LNYLtc02sLwjlaBnKwTuALillxw%2BrKDtig%3D%3D";

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

    public List<Observatory> getAllObservatory(){
        List<Observatory> observatories = observatoryRepository.findAll();
        if(observatories.isEmpty()){
            log.warn("[Service] 모든 observatory 가져오는데 빈 값임");
        }
        return observatories;
    }

    @Transactional
    public Observatory addObservatory(ObservatoryRequest.addDto request){
        Observatory observatory = ObservatoryRequest.addDto.to(request);
        observatoryRepository.save(observatory);
        return observatory;
    }

    public String getObservatoryData(){
        StringBuilder sb =null;
        //Note 665개임
        try{
            StringBuilder urlBuilder = new StringBuilder("http://apis.data.go.kr/B552584/MsrstnInfoInqireSvc/getMsrstnList"); /*URL*/
            urlBuilder.append("?" + URLEncoder.encode("serviceKey","UTF-8") + "="+ serviceKey); /*Service Key*/
            urlBuilder.append("&" + URLEncoder.encode("returnType","UTF-8") + "=" + URLEncoder.encode("json", "UTF-8")); /*xml 또는 json*/
            urlBuilder.append("&" + URLEncoder.encode("numOfRows","UTF-8") + "=" + URLEncoder.encode("700", "UTF-8")); /*한 페이지 결과 수*/
            urlBuilder.append("&" + URLEncoder.encode("pageNo","UTF-8") + "=" + URLEncoder.encode("1", "UTF-8")); /*페이지번호*/
//            urlBuilder.append("&" + URLEncoder.encode("addr","UTF-8") + "=" + URLEncoder.encode("서울", "UTF-8")); /*주소*/
//            urlBuilder.append("&" + URLEncoder.encode("stationName","UTF-8") + "=" + URLEncoder.encode("종로구", "UTF-8")); /*측정소명*/
            URL url = new URL(urlBuilder.toString());
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Content-type", "application/json");
            System.out.println("Response code: " + conn.getResponseCode());
            BufferedReader rd;
            if(conn.getResponseCode() >= 200 && conn.getResponseCode() <= 300) {
                rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            } else {
                rd = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
            }
            sb = new StringBuilder();
            String line;
            while ((line = rd.readLine()) != null) {
                sb.append(line);
            }
            rd.close();
            conn.disconnect();
            System.out.println(sb.toString());
        }catch (Exception e){
            log.warn("관측소 정보 가져오기 실패");
        }
        return sb.toString();
    }

    /*
        TODO CONTROLLER
            1. AIRKOREA기준 관측소 업데이트 하기 (없어진건 삭제, 생긴건 추가)
     */

}

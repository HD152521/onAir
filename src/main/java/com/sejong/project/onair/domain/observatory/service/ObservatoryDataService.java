package com.sejong.project.onair.domain.observatory.service;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.sejong.project.onair.domain.observatory.model.ObservatoryData;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ObservatoryDataService {

    private static final Logger log = LoggerFactory.getLogger(ObservatoryDataService.class);
    String serviceKey="%2FNVRnCn1bzQ%2F4cga43dtjmuDuRGXfjO6aBApVgpK6FTXRkpXAIl1LNYLtc02sLwjlaBnKwTuALillxw%2BrKDtig%3D%3D";

    public String getAirkoreaDataToString(){
        return getAirkoreaData();
    }

    public List<ObservatoryData> getAirkoreaDataToObject(){
        return parseObservatoryDataList(getAirkoreaData());
    }

    public void updateObservatoryData(){
        /*
        todo
         1. 일단 관측소 정보 가져와서 하나씩 데이터를 가져와야함.
         2. 데이터를 저장함. 다만 가져온 데이터가 현재 시간 기준으로 맞는 시간인지 봄.
            2-1) 맞으면 저장
            2-2) 아닌 데이터들 센터명만 list로 따로 관리 10분마다 계속 가져옴. -> 시간 바뀔경우 그냥 null값으로 하고 버림
         */

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


    public String getAirkoreaData(){

        /*
            todo
                1. 관측소명 가져오기
                2. 마지막 데이터만 가져오고 싶은데
         */

        StringBuilder sb=null;
        try {
            StringBuilder urlBuilder = new StringBuilder("http://apis.data.go.kr/B552584/ArpltnInforInqireSvc/getMsrstnAcctoRltmMesureDnsty"); /*URL*/
            //Note 필수
            urlBuilder.append("?" + URLEncoder.encode("serviceKey", "UTF-8") + "="+serviceKey); /*Service Key*/
            urlBuilder.append("&" + URLEncoder.encode("returnType", "UTF-8") + "=" + URLEncoder.encode("json", "UTF-8"));
            urlBuilder.append("&" + URLEncoder.encode("stationName", "UTF-8") + "=" + URLEncoder.encode("종로구", "UTF-8"));
            urlBuilder.append("&" + URLEncoder.encode("dataTerm", "UTF-8") + "=" + URLEncoder.encode("DAILY", "UTF-8")); /*요청 데이터기간(1일: DAILY, 1개월: MONTH, 3개월: 3MONTH)*/
            //Note 선택
            urlBuilder.append("&" + URLEncoder.encode("numOfRows", "UTF-8") + "=" + URLEncoder.encode("100", "UTF-8")); /*한 페이지 결과 수*/
            urlBuilder.append("&" + URLEncoder.encode("pageNo", "UTF-8") + "=" + URLEncoder.encode("1", "UTF-8")); /*페이지번호*/
            urlBuilder.append("&" + URLEncoder.encode("ver", "UTF-8") + "=" + URLEncoder.encode("1.0", "UTF-8")); /*버전별 상세 결과 참고*/

            URL url = new URL(urlBuilder.toString());
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Content-type", "application/json");
            BufferedReader rd;

            if (conn.getResponseCode() >= 200 && conn.getResponseCode() <= 300) {
                rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            } else {
                rd = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
            }

            log.info("rd:{}",rd);

            sb = new StringBuilder();
            String line;
            while ((line = rd.readLine()) != null) {
                sb.append(line);
            }
            rd.close();
            conn.disconnect();
//            System.out.println(sb.toString());
        }catch (Exception e){
            log.warn("Airkorea데이터 가져오는데 실패");
        }
        return sb.toString();
    }

    public List<ObservatoryData> parseObservatoryDataList(String json){
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
                    log.info("[Service] parseObservatoryDataList 자동 변환 전");        
                    ObservatoryData data = mapper.treeToValue(node, ObservatoryData.class);
                    log.info("[Service] parseObservatoryDataList 자동 변환  후");
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

package com.sejong.project.onair.domain.observatory.service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AirKoreaApiService {

    private static final Logger log = LoggerFactory.getLogger(ObservatoryDataService.class);
    //내꺼
//    String serviceKey="%2FNVRnCn1bzQ%2F4cga43dtjmuDuRGXfjO6aBApVgpK6FTXRkpXAIl1LNYLtc02sLwjlaBnKwTuALillxw%2BrKDtig%3D%3D";
    //인권
    String serviceKey= "zYkhXG%2FOHd59eb3FlvnXTpA91Y0oAGJL2bHrsWB11MQGSXQMJEbM0AfQWodWVsu7fJ%2BgMAH5bXXFrahK13VZ2A%3D%3D";
    public String getObservatory(){
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

    public String getDatabyObservatory(String nation){
        
        StringBuilder sb=new StringBuilder();
        try {
            StringBuilder urlBuilder = new StringBuilder("http://apis.data.go.kr/B552584/ArpltnInforInqireSvc/getMsrstnAcctoRltmMesureDnsty"); /*URL*/
            //Note 필수
            urlBuilder.append("?" + URLEncoder.encode("serviceKey", "UTF-8") + "="+serviceKey); /*Service Key*/
            urlBuilder.append("&" + URLEncoder.encode("returnType", "UTF-8") + "=" + URLEncoder.encode("json", "UTF-8"));
            urlBuilder.append("&" + URLEncoder.encode("stationName", "UTF-8") + "=" + URLEncoder.encode(nation, "UTF-8"));
            urlBuilder.append("&" + URLEncoder.encode("dataTerm", "UTF-8") + "=" + URLEncoder.encode("DAILY", "UTF-8")); /*요청 데이터기간(1일: DAILY, 1개월: MONTH, 3개월: 3MONTH)*/
            //Note 선택
            urlBuilder.append("&" + URLEncoder.encode("numOfRows", "UTF-8") + "=" + URLEncoder.encode("10", "UTF-8")); /*한 페이지 결과 수*/
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
            String line;
            while ((line = rd.readLine()) != null) {
                sb.append(line);
            }
            rd.close();
            conn.disconnect();
            log.info("satation :{} 데이터 가져옴 ",nation);
        }catch (Exception e){
            log.warn("Airkorea데이터 가져오는데 실패");
        }
        return sb.toString();
    }

    public String getData(){

        StringBuilder sb=new StringBuilder();
        try {
            StringBuilder urlBuilder = new StringBuilder("http://apis.data.go.kr/B552584/ArpltnInforInqireSvc/getCtprvnRltmMesureDnsty"); /*URL*/
            //Note 필수
            urlBuilder.append("?" + URLEncoder.encode("serviceKey", "UTF-8") + "="+serviceKey); /*Service Key*/
            urlBuilder.append("&" + URLEncoder.encode("returnType", "UTF-8") + "=" + URLEncoder.encode("json", "UTF-8"));
            urlBuilder.append("&" + URLEncoder.encode("numOfRows", "UTF-8") + "=" + URLEncoder.encode("5000", "UTF-8"));
            urlBuilder.append("&" + URLEncoder.encode("pageNo", "UTF-8") + "=" + URLEncoder.encode("1", "UTF-8")); /*페이지번호*/
            urlBuilder.append("&" + URLEncoder.encode("sidoName", "UTF-8") + "=" + URLEncoder.encode("전국", "UTF-8")); /*한 페이지 결과 수*/
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
            String line;
            while ((line = rd.readLine()) != null) {
                sb.append(line);
            }
            rd.close();
            conn.disconnect();
        }catch (Exception e){
            log.warn("Airkorea데이터 가져오는데 실패");
        }
        return sb.toString();
    }




}

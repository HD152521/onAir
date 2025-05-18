package com.sejong.project.onair.domain.observatory.model;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.sejong.project.onair.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.format.DateTimeFormatter;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLDelete(sql = "UPDATE observatory_data SET is_deleted = true, deleted_at = now() where id = ?")
@SQLRestriction("is_deleted is FALSE")
@Getter
@Setter
@AllArgsConstructor
@Builder
// JSON에 없는 필드는 무시
@JsonIgnoreProperties(ignoreUnknown = true)
// 이 클래스의 인스턴스를 만들 때 ObservatoryDataBuilder를 쓰도록 지시
@JsonDeserialize(builder = ObservatoryData.ObservatoryDataBuilder.class)
public class ObservatoryData extends BaseEntity {

    private static final Logger log = LoggerFactory.getLogger(ObservatoryData.class);

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    /** 가스별 등급 ("1", "2" 등) */
    private Integer so2Grade;

    /** 일산화탄소 플래그 (null 또는 문자열) */
    private String coFlag;

    /** 통합대기환경수치 */
    private Integer khaiValue;

    /** 아황산가스 농도 (ppm) */
    private Double so2Value;

    /** 일산화탄소 농도 (ppm) */
    private Double coValue;

    /** 미세먼지 플래그 */
    private String pm25Flag;

    /** 초미세먼지(pl) 농도 (㎍/m³) */
    private String pm10Flag;

    /** 초미세먼지 농도 (㎍/m³) */
    private Integer pm10Value;

    /** 오존 등급 */
    private Integer o3Grade;

    /** 통합대기환경 등급 */
    private Integer khaiGrade;

    /** 미세먼지 농도 (㎍/m³) */
    private Integer pm25Value;

    /** 이산화질소 플래그 */
    private String no2Flag;

    /** 이산화질소 등급 */
    private Integer no2Grade;

    /** 오존 플래그 */
    private String o3Flag;

    /** 미세먼지 등급 */
    private Integer pm25Grade;

    /** SO2 플래그 (중복 필드명 주의, JSON에 두 번 등장하나 실제 테이블에는 하나만) */
    private String so2Flag;

    /** 측정 시각 */
    @JsonIgnore
    private LocalDateTime dataTime;
    /** 측정 시각을 파싱하기 위한 원본 문자열 */
    @Transient
    @JsonProperty("dataTime")  // JSON 키 dataTime → 이 필드에 매핑
    private String dataTimeString;
    /** 일산화탄소 등급 */

    private Integer coGrade;

    /** 이산화질소 농도 (ppm) */
    private Double no2Value;

    /** 초미세먼지 등급 */
    private Integer pm10Grade;

    /** 오존 농도 (ppm) */
    private Double o3Value;

    @JsonProperty(value = "stationName", access = JsonProperty.Access.READ_ONLY)
    private String stationName;

    //널값인지 확인하기 위해 추가
    @PostLoad
    public void postLoad() {
        if (this.dataTime != null) {
            this.dataTimeString = this.dataTime.format(
                    DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
            );
        }
    }

    public void changeDate(){
        if (this.dataTime == null && this.dataTimeString != null && !this.dataTimeString.isBlank()) {
            try{
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
                this.dataTime = LocalDateTime.parse(this.dataTimeString, formatter);
            }catch (Exception e){
                log.warn("[OvservatoryData] 날짜 변환 오류");
            }
        }
    }

    @JsonPOJOBuilder(withPrefix = "")
    public static class ObservatoryDataBuilder {
        // Lombok이 생성한 빌더 메서드들을 Jackson이 인식하게 해줍니다.
    }

    @JsonIgnore
    public void setStationName(String stationName){
        this.stationName = stationName;
    }

    @Override
    public String toString() {
        return "ObservatoryData{" +
                "id=" + id +
                ", so2Grade=" + so2Grade +
                ", coFlag='" + coFlag + '\'' +
                ", khaiValue=" + khaiValue +
                ", so2Value=" + so2Value +
                ", coValue=" + coValue +
                ", pm25Flag='" + pm25Flag + '\'' +
                ", pm10Flag='" + pm10Flag + '\'' +
                ", pm10Value=" + pm10Value +
                ", o3Grade=" + o3Grade +
                ", khaiGrade=" + khaiGrade +
                ", pm25Value=" + pm25Value +
                ", no2Flag='" + no2Flag + '\'' +
                ", no2Grade=" + no2Grade +
                ", o3Flag='" + o3Flag + '\'' +
                ", pm25Grade=" + pm25Grade +
                ", so2Flag='" + so2Flag + '\'' +
                ", dataTime=" + dataTime +
                ", dataTimeString='" + dataTimeString + '\'' +
                ", coGrade=" + coGrade +
                ", no2Value=" + no2Value +
                ", pm10Grade=" + pm10Grade +
                ", o3Value=" + o3Value +
                ", stationName='" + stationName + '\'' +
                '}';
    }
}

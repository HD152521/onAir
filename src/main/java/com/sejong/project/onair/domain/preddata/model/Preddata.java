package com.sejong.project.onair.domain.preddata.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "pred_air")  // 실제 DB 테이블 이름으로 바꿔주세요
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Preddata {

    /** 기본 키(PK)가 없다면, 측정시간을 복합키나 단일키로 쓸 수도 있습니다. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "측정시간")
    private LocalDateTime measurementTime;

    @Column(name = "3시간신적설(cm)")
    private Double snow3h;

    @Column(name = "강수량(mm)")
    private Double precipitation;

    @Column(name = "기온(°C)")
    private Double temperature;

    @Column(name = "습도(%)")
    private Double humidity;

    @Column(name = "시정(10m)")
    private Double visibility10m;

    @Column(name = "이슬점온도(°C)")
    private Double dewPoint;

    @Column(name = "일사(MJ/m2)")
    private Double solarRadiation;

    @Column(name = "일조(hr)")
    private Double sunshineHours;

    @Column(name = "적설(cm)")
    private Double snowDepth;

    @Column(name = "전운량(10분위)")
    private Double totalCloudOcta;

    @Column(name = "중하층운량(10분위)")
    private Double midLowCloudOcta;

    @Column(name = "증기압(hPa)")
    private Double vaporPressure;

    @Column(name = "지면온도(°C)")
    private Double groundTemp;

    @Column(name = "최저운고(100m )")
    private Double cloudBase100m;

    @Column(name = "풍속(m/s)")
    private Double windSpeed;

    @Column(name = "풍향(16방위)")
    private Double windDirection;

    @Column(name = "해면기압(hPa)")
    private Double seaLevelPressure;

    @Column(name = "현지기압(hPa)")
    private Double localPressure;

    @Column(name = "CO")
    private Double co;

    @Column(name = "CO2(ppm)")
    private Double co2;

    @Column(name = "CO2_pred")
    private Double co2Pred;

    @Column(name = "CO2_pred_flag")
    private Long co2PredFlag;

    @Column(name = "CH4(ppm)")
    private Double ch4;

    @Column(name = "CH4_pred")
    private Double ch4Pred;

    @Column(name = "CH4_pred_flag")
    private Long ch4PredFlag;

    @Column(name = "NO(ppm)(Avg)")
    private Double noAvg;

    @Column(name = "NO2")
    private Double no2;

    @Column(name = "O3")
    private Double o3;

    @Column(name = "PM10")
    private Double pm10;

    @Column(name = "PM25")
    private Double pm25;

    @Column(name = "SO2")
    private Double so2;

// === 측정소 정보 ===

    @Column(name = "대기측정망")
    private String monitoringNetwork;

    @Column(name = "대기측정소명")
    private String stationName;

    @Column(name = "대기측정소명_협의체")
    private String stationNameAgreement;

    @Column(name = "대기측정소주소")
    private String stationAddress;

    @Column(name = "대기측정소코드")
    private Long stationCode;

    @Column(name = "최근접기상관측소명")
    private String nearestObservatoryName;

    @Column(name = "대기측정소위도")
    private double dmX;

    @Column(name = "대기측정소경도")
    private double dmY;

}

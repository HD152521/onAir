package com.sejong.project.onair.domain.compWeather.model;

import com.sejong.project.onair.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "comp_weather")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CompWeather extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "원본인덱스")
    private Long id;

    @Column(name = "지점")
    private String stationId;

    @Column(name = "지점명")
    private String stationName;

    @Column(name = "일시")
    private LocalDateTime dateTime;

    @Column(name = "기온(°C)")
    private Double temperature;

    @Column(name = "기온 QC플래그")
    private String temperatureQcFlag;

    @Column(name = "강수량(mm)")
    private Double precipitation;

    @Column(name = "강수량 QC플래그")
    private String precipitationQcFlag;

    @Column(name = "풍속(m/s)")
    private Double windSpeed;

    @Column(name = "풍속 QC플래그")
    private String windSpeedQcFlag;

    @Column(name = "풍향(16방위)")
    private Integer windDirection;

    @Column(name = "풍향 QC플래그")
    private String windDirectionQcFlag;

    @Column(name = "습도(%)")
    private Double humidity;

    @Column(name = "습도 QC플래그")
    private String humidityQcFlag;

    @Column(name = "증기압(hPa)")
    private Double vaporPressure;

    @Column(name = "이슬점온도(°C)")
    private Double dewPoint;

    @Column(name = "현지기압(hPa)")
    private Double stationPressure;

    @Column(name = "현지기압 QC플래그")
    private String stationPressureQcFlag;

    @Column(name = "해면기압(hPa)")
    private Double seaLevelPressure;

    @Column(name = "해면기압 QC플래그")
    private String seaLevelPressureQcFlag;

    @Column(name = "일조(hr)")
    private Double sunshineDuration;

    @Column(name = "일조 QC플래그")
    private String sunshineQcFlag;

    @Column(name = "일사(MJ/m2)")
    private Double solarRadiation;

    @Column(name = "일사 QC플래그")
    private String solarRadiationQcFlag;

    @Column(name = "적설(cm)")
    private Double snowDepth;

    @Column(name = "3시간신적설(cm)")
    private Double snowDepth3h;

    @Column(name = "전운량(10분위)")
    private Integer totalCloudAmount;

    @Column(name = "중하층운량(10분위)")
    private Integer midLowCloudAmount;

    @Column(name = "운형(운형약어)")
    private String cloudTypeAbbr;

    @Column(name = "최저운고(100m )")
    private Integer lowestCloudBase;

    @Column(name = "시정(10m)")
    private Integer visibility;

    @Column(name = "지면상태(지면상태코드)")
    private String groundConditionCode;

    @Column(name = "현상번호(국내식)")
    private String phenomenonCode;

    @Column(name = "지면온도(°C)")
    private Double groundTemperature;

    @Column(name = "지면온도 QC플래그")
    private String groundTempQcFlag;

    @Column(name = "5cm 지중온도(°C)")
    private Double soilTemp5cm;

    @Column(name = "10cm 지중온도(°C)")
    private Double soilTemp10cm;

    @Column(name = "20cm 지중온도(°C)")
    private Double soilTemp20cm;

    @Column(name = "30cm 지중온도(°C)")
    private Double soilTemp30cm;

    @Column(name = "지점명_station")
    private String stationDisplayName;

    @Column(name = "지점주소")
    private String stationAddress;

    @Column(name = "관리관서")
    private String managingOffice;

    @Column(name = "위도")
    private double dmX;

    @Column(name = "경도")
    private double dmY;

    @Column(name = "노장해발고도(m)")
    private Integer elevationMeters;

    @Column(name = "기압계(관측장비지상높이(m))")
    private Integer barometerHeight;

    @Column(name = "기온계(관측장비지상높이(m))")
    private Integer thermometerHeight;

    @Column(name = "풍속계(관측장비지상높이(m))")
    private Integer anemometerHeight;

    @Column(name = "강우계(관측장비지상높이(m))")
    private Integer rainGaugeHeight;
}
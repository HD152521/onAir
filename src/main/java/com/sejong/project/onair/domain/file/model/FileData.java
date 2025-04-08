package com.sejong.project.onair.domain.file.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDateTime;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLDelete(sql = "UPDATE member SET is_deleted = true; deleted_at = now() where id = ?")
@SQLRestriction("is_deleted is FALSE")
public class FileData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime time;
    private double co2;
    private double ch4_ppb;
    private double ch4_ppm;
    private String type;
    private String province;
    private String city;
    private String district;
    private String code;
    private double so2_ppm;
    private double no2_ppm;
    private double o3_ppm;
    private double co_ppm;
    private double pm10;
    private double pm2_5;
    private double nox_ppm;
    private double no_ppm;
    private int windDirection;  //todo 일단은 다 정수형 같은데 혹시모름
    private double windSpeed;
    private double temperature;
    private double humidity;


}

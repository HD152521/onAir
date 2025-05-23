package com.sejong.project.onair.domain.file.model;

import com.sejong.project.onair.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDateTime;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLDelete(sql = "UPDATE file_data SET is_deleted = true; deleted_at = now() where id = ?")
@SQLRestriction("is_deleted is FALSE")
public class FileData extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private UploadFile uploadFile;

    private String fileId;
    private LocalDateTime time;
    private double co2;
    private double ch4_ppb;
    private double ch4_ppm;
    private String type;
    private String province;
    private String city;
    private String district;
    private String observatoryName;
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

    @Builder
    public FileData(LocalDateTime time, double co2, double ch4_ppb, double ch4_ppm,
                    String type, String province, String city, String district, String observatoryName,String code,
                    double so2_ppm, double no2_ppm, double o3_ppm, double co_ppm,
                    double pm10, double pm2_5, double nox_ppm, double no_ppm,
                    int windDirection, double windSpeed, double temperature, double humidity,String fileId,UploadFile uploadFile) {
        this.time = time;
        this.co2 = co2;
        this.ch4_ppb = ch4_ppb;
        this.ch4_ppm = ch4_ppm;
        this.type = type;
        this.province = province;
        this.city = city;
        this.district = district;
        this.observatoryName = observatoryName;
        this.code = code;
        this.so2_ppm = so2_ppm;
        this.no2_ppm = no2_ppm;
        this.o3_ppm = o3_ppm;
        this.co_ppm = co_ppm;
        this.pm10 = pm10;
        this.pm2_5 = pm2_5;
        this.nox_ppm = nox_ppm;
        this.no_ppm = no_ppm;
        this.windDirection = windDirection;
        this.windSpeed = windSpeed;
        this.temperature = temperature;
        this.humidity = humidity;
        this.fileId = fileId;
        this.uploadFile = uploadFile;
    }


}

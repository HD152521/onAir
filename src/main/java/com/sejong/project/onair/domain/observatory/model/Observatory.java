package com.sejong.project.onair.domain.observatory.model;

import com.sejong.project.onair.global.entity.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLDelete(sql = "UPDATE observatory SET is_deleted = true, deleted_at = now() where id = ?")
@SQLRestriction("is_deleted is FALSE")
public class Observatory extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String centerName;
    private String address;
    private double latitue;     //위도
    private double longitude;   //경도
    private String manageName;
    private int year;

    @Builder
    public Observatory(String centerName, String address,double latitue, double longitude,String manageName,int year){
        this.centerName = centerName;
        this.address = address;
        this.latitue = latitue;
        this.longitude = longitude;
        this.manageName = manageName;
        this.year = year;
    }
}

package com.sejong.project.onair.domain.observatory.model;

import com.sejong.project.onair.global.entity.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
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

    private String consultativeGroup;
    private String centerName;
    private MeasuringType measuringType;
    private String centerLocation;
    private String centerCode;
    private double latitue;     //위도
    private double longitude;   //경도


    private enum MeasuringType{
        CO2("CO2"),
        CH4("CH4"),
        BOTH("BOTH"),
        NONE("NONE");

        private final String type;

        MeasuringType(String type){
            this.type = type;
        }
    }
}

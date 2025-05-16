package com.sejong.project.onair.domain.observatory.model;

import com.sejong.project.onair.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.util.List;
import java.util.Objects;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLDelete(sql = "UPDATE observatory SET is_deleted = true, deleted_at = now() where id = ?")
@SQLRestriction("is_deleted is FALSE")
public class Observatory extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String stationName;
    private String addr;
    private double dmX;     //위도`
    private double dmY;   //경도
    private String mangName;
    private int year;

    @Builder
    public Observatory(String stationName, String addr,double dmX, double dmY,String mangName,int year){
        this.stationName = stationName;
        this.addr = addr;
        this.dmX = dmX;
        this.dmY = dmY;
        this.mangName = mangName;
        this.year = year;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Observatory)) return false;
        Observatory that = (Observatory) o;
        return Objects.equals(this.stationName, that.stationName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(stationName);
    }

}

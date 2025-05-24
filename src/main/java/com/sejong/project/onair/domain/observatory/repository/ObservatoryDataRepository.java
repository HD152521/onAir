package com.sejong.project.onair.domain.observatory.repository;

import com.sejong.project.onair.domain.observatory.model.ObservatoryData;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface ObservatoryDataRepository extends JpaRepository<ObservatoryData,Long> {
    ObservatoryData findTopByStationNameOrderByDataTimeDesc(String stationName);
    List<ObservatoryData>  findByStationNameAndDataTimeBetweenOrderByDataTimeAsc(
            String stationName,
            LocalDateTime start,
            LocalDateTime end
    );
    List<ObservatoryData> findByDataTimeBetween(LocalDateTime start, LocalDateTime end);
}

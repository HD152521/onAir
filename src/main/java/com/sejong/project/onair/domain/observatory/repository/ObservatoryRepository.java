package com.sejong.project.onair.domain.observatory.repository;

import com.sejong.project.onair.domain.observatory.model.Observatory;
import com.sejong.project.onair.domain.observatory.model.ObservatoryData;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ObservatoryRepository extends JpaRepository<Observatory,Long> {
    Optional<Observatory> findObservatoryByStationName(String stationName);
}


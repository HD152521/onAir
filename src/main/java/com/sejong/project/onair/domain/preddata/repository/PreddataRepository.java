package com.sejong.project.onair.domain.preddata.repository;

import com.sejong.project.onair.domain.preddata.model.Preddata;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface PreddataRepository extends JpaRepository<Preddata,Long> {
    Preddata findPreddataById(Long id);
    List<Preddata> findByMeasurementTimeBetween(LocalDateTime start, LocalDateTime end);
}

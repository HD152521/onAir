package com.sejong.project.onair.domain.compWeather.repository;

import com.sejong.project.onair.domain.compWeather.model.CompWeather;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface CompWeatherRepository extends JpaRepository<CompWeather,Long> {
    List<CompWeather> findByDateTimeBetween(LocalDateTime start, LocalDateTime end);
}

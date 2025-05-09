package com.sejong.project.onair.domain.observatory.repository;

import com.sejong.project.onair.domain.observatory.model.Observatory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ObservatoryRepository extends JpaRepository<Observatory,Long> {

}

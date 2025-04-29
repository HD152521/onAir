package com.sejong.project.onair.domain.observatory.repository;

import com.sejong.project.onair.domain.observatory.model.Observatory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ObervatoryRepository extends JpaRepository<Observatory,Long> {
}

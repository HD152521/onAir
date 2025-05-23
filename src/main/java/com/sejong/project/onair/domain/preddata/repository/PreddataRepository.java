package com.sejong.project.onair.domain.preddata.repository;

import com.sejong.project.onair.domain.preddata.model.Preddata;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PreddataRepository extends JpaRepository<Preddata,Long> {
}

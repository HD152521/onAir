package com.sejong.project.onair.domain.file.repository;

import com.sejong.project.onair.domain.file.model.FileData;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FileDataRepository extends JpaRepository<FileData,Long> {
}

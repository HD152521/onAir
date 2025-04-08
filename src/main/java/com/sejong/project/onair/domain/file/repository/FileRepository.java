package com.sejong.project.onair.domain.file.repository;

import com.sejong.project.onair.domain.file.model.File;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FileRepository extends JpaRepository<File,Long> {
}

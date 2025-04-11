package com.sejong.project.onair.domain.file.repository;

import com.sejong.project.onair.domain.file.model.UploadFile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FileRepository extends JpaRepository<UploadFile,Long> {
    UploadFile findUploadFileByFileId (String fileId);
}

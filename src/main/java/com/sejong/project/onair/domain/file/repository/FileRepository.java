package com.sejong.project.onair.domain.file.repository;

import com.sejong.project.onair.domain.file.model.UploadFile;
import com.sejong.project.onair.domain.member.Member;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.List;

public interface FileRepository extends JpaRepository<UploadFile,Long> {
    UploadFile findUploadFileByFileId (String fileId);
    List<UploadFile> findUploadFilesByMember(Member member);
}

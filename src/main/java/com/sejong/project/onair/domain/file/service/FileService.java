package com.sejong.project.onair.domain.file.service;


import com.sejong.project.onair.domain.file.dto.FileResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public interface FileService {
    FileResponse.HeaderDto uploadFile(MultipartFile file);
}

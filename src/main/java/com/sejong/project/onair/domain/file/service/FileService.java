package com.sejong.project.onair.domain.file.service;


import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public interface FileService {
    String uploadFile(MultipartFile file);
}

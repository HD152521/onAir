package com.sejong.project.onair.domain.file.service;


import com.sejong.project.onair.domain.file.dto.DataDto;
import com.sejong.project.onair.domain.file.dto.FileRequest;
import com.sejong.project.onair.domain.file.dto.FileResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public interface FileService {
    FileResponse.HeaderDto uploadFile(MultipartFile file);
    List<DataDto> readFileData(FileRequest.MappingResultDto mappingResultDto);
    List<FileResponse.FileLogDto> getUploadLog();
}

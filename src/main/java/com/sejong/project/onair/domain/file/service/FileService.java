package com.sejong.project.onair.domain.file.service;


import com.sejong.project.onair.domain.file.dto.DataDto;
import com.sejong.project.onair.domain.file.dto.FileRequest;
import com.sejong.project.onair.domain.file.dto.FileResponse;
import com.sejong.project.onair.domain.file.model.UploadFile;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public interface FileService {

    List<String> readHeader(MultipartFile file);
    List<DataDto> readFileData(UploadFile uploadFile, List<Integer> headers);


    //fixme 없애야하는거 기존에 있던거임====================
//    FileResponse.HeaderDto uploadFile(MultipartFile file);
//    List<DataDto> readFileData(FileRequest.MappingResultDto mappingResultDto);
//    List<FileResponse.FileLogDto> getUploadLog();
}

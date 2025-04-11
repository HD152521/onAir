package com.sejong.project.onair.domain.file.controller;


import com.sejong.project.onair.domain.file.dto.DataDto;
import com.sejong.project.onair.domain.file.dto.FileRequest;
import com.sejong.project.onair.domain.file.dto.FileResponse;
import com.sejong.project.onair.domain.file.service.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class FileController {
    private final FileService fileService;

    @PostMapping("/uploadFile")
    public FileResponse.HeaderDto uploadFile(@RequestParam("file") MultipartFile file){
        return fileService.uploadFile(file);
    }

    @PostMapping("/mappingData")
    public List<DataDto> mappingData(@RequestBody  FileRequest.MappingResultDto mappingResultDto){
        return fileService.readFileData(mappingResultDto);
    }
}

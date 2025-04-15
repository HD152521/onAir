package com.sejong.project.onair.domain.file.controller;


import com.sejong.project.onair.domain.file.dto.DataDto;
import com.sejong.project.onair.domain.file.dto.FileRequest;
import com.sejong.project.onair.domain.file.dto.FileResponse;
import com.sejong.project.onair.domain.file.service.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/file")
public class FileController {
    private final FileService fileService;

    @PostMapping("/upload")
    public FileResponse.HeaderDto uploadFile(@RequestParam("file") MultipartFile file){
        return fileService.uploadFile(file);
    }

    @PostMapping("/mapping")
    public List<DataDto> mappingData(@RequestBody  FileRequest.MappingResultDto mappingResultDto){
        return fileService.readFileData(mappingResultDto);
    }

    @GetMapping("/upload/log")
    public List<FileResponse.FileLogDto> getUploadLog(){
        return fileService.getUploadLog();
    }
}

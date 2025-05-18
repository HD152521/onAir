package com.sejong.project.onair.domain.file.controller;


import com.sejong.project.onair.domain.file.dto.DataDto;
import com.sejong.project.onair.domain.file.dto.FileRequest;
import com.sejong.project.onair.domain.file.dto.FileResponse;
import com.sejong.project.onair.domain.file.service.FileService;
import com.sejong.project.onair.domain.file.service.FileServiceImpl;
import com.sejong.project.onair.domain.file.swagger.FileUploadLog;
import com.sejong.project.onair.domain.file.swagger.MappingFile;
import com.sejong.project.onair.domain.file.swagger.UploadFile;
import com.sejong.project.onair.global.exception.BaseResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/file")
@Tag(name = "File", description = "파일 업로드, 읽기 관련 API")
public class FileController {
    private final FileServiceImpl fileService;

    @PostMapping("/upload")
    @UploadFile
    public BaseResponse<?> uploadFile(@RequestParam("file") MultipartFile file){
        return BaseResponse.onSuccess(fileService.uploadFile(file));
    }

    @PostMapping("/mapping")
    @MappingFile
    public BaseResponse<?> mappingData(@RequestBody FileRequest.MappingResultDto mappingResultDto){
        return BaseResponse.onSuccess(fileService.readMappingData(mappingResultDto));
    }

    @GetMapping("/upload/log")
    @FileUploadLog
    public BaseResponse<?>  getUploadLog(){
        return BaseResponse.onSuccess(fileService.getUploadLog());
    }

    @GetMapping("/readData")
    public BaseResponse<?> readData(@RequestParam("file") MultipartFile file){
        return BaseResponse.onSuccess(fileService.readData(file));
    }

    @GetMapping("/read/{fileId}")
    public BaseResponse<?> readFileFromId(@PathVariable String fileId){
        return BaseResponse.onSuccess(fileService.readDataFromId(fileId));
    }
}

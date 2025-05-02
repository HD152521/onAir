package com.sejong.project.onair.domain.file.controller;


import com.sejong.project.onair.domain.file.dto.DataDto;
import com.sejong.project.onair.domain.file.dto.FileRequest;
import com.sejong.project.onair.domain.file.dto.FileResponse;
import com.sejong.project.onair.domain.file.service.FileService;
import com.sejong.project.onair.domain.file.service.FileServiceImpl;
import com.sejong.project.onair.global.exception.BaseResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/file")
public class FileController {
    private final FileServiceImpl fileService;

    @PostMapping("/upload")
    public BaseResponse<?> uploadFile(@RequestParam("file") MultipartFile file){
        return BaseResponse.onSuccess(fileService.uploadFile(file));
    }

    @PostMapping("/mapping")
    public BaseResponse<?> mappingData(@RequestBody FileRequest.MappingResultDto mappingResultDto){
        return BaseResponse.onSuccess(fileService.readData(mappingResultDto));
    }

    @GetMapping("/upload/log")
    public BaseResponse<?>  getUploadLog(){
        return BaseResponse.onSuccess(fileService.getUploadLog());
    }

}

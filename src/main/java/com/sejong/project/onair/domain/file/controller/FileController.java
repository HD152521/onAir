package com.sejong.project.onair.domain.file.controller;


import com.sejong.project.onair.domain.file.dto.DataDto;
import com.sejong.project.onair.domain.file.dto.FileRequest;
import com.sejong.project.onair.domain.file.dto.FileResponse;
import com.sejong.project.onair.domain.file.service.FileService;
import com.sejong.project.onair.domain.file.service.FileServiceImpl;
import com.sejong.project.onair.domain.file.swagger.FileUploadLog;
import com.sejong.project.onair.domain.file.swagger.MappingFile;
import com.sejong.project.onair.domain.file.swagger.UploadFile;
import com.sejong.project.onair.domain.member.dto.MemberDetails;
import com.sejong.project.onair.domain.member.model.Member;
import com.sejong.project.onair.domain.member.service.MemberService;
import com.sejong.project.onair.global.exception.BaseResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/file")
@Tag(name = "File", description = "파일 업로드, 읽기 관련 API")
public class FileController {
    private static final Logger log = LoggerFactory.getLogger(FileController.class);
    private final FileServiceImpl fileService;
    private final MemberService memberService;

    @PostMapping("/upload")
    @UploadFile
    public BaseResponse<?> uploadFile(@RequestParam("file") MultipartFile file,@AuthenticationPrincipal MemberDetails memberDetails){
        Member member = memberService.getMember(memberDetails);
        return BaseResponse.onSuccess(fileService.uploadFile(file,member));
    }

    @PostMapping("/mapping")
    @MappingFile
    public BaseResponse<?> mappingData(@RequestBody FileRequest.MappingResultDto mappingResultDto,@AuthenticationPrincipal MemberDetails memberDetails){
        Member member = memberService.getMember(memberDetails);
        return BaseResponse.onSuccess(fileService.readMappingData(mappingResultDto,member));
    }

    @GetMapping("/upload/log")
    @FileUploadLog
    public BaseResponse<?> getUploadLog(@AuthenticationPrincipal MemberDetails memberDetails){
        Member member = memberService.getMember(memberDetails);
        return BaseResponse.onSuccess(fileService.getUploadLog(member));
    }

    @GetMapping("/readData")
    public BaseResponse<?> readData(@RequestParam("file") MultipartFile file){
        return BaseResponse.onSuccess(fileService.readData(file));
    }

    @GetMapping("/read/{fileId}")
    public BaseResponse<?> readFileFromId(@PathVariable String fileId,@AuthenticationPrincipal MemberDetails memberDetails){
        Member member = memberService.getMember(memberDetails);
        log.info("fileId:{} membername:{}",fileId,member.getMemberName());
        return BaseResponse.onSuccess(fileService.readDataFromId(fileId,member));
    }
}

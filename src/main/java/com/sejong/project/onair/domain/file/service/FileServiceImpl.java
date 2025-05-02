package com.sejong.project.onair.domain.file.service;

import com.sejong.project.onair.domain.file.dto.DataDto;
import com.sejong.project.onair.domain.file.dto.FileRequest;
import com.sejong.project.onair.domain.file.dto.FileResponse;
import com.sejong.project.onair.domain.file.model.FileType;
import com.sejong.project.onair.domain.file.model.UploadFile;
import com.sejong.project.onair.domain.file.repository.FileRepository;
import com.sejong.project.onair.domain.member.model.Member;
import com.sejong.project.onair.global.beanProcessor.FileTypeHandlerProcessor;
import com.sejong.project.onair.global.exception.BaseException;
import com.sejong.project.onair.global.exception.codes.ErrorCode;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


@Service
@RequiredArgsConstructor
public class FileServiceImpl{

    private static final Logger log = LoggerFactory.getLogger(FileServiceImpl.class);
    private final FileRepository fileRepository;
    private final FileTypeHandlerProcessor processor;


    // /없으면 상대경로임
    private String dir = "./upload";
    private Path fileDir;

    @PostConstruct
    public void postConstruct() {
        fileDir = Paths.get(dir).toAbsolutePath().normalize();
        try {
            Files.createDirectories(fileDir);
            for(FileType fileType : FileType.values()){
                log.info("postConstruct filtType:{}",fileType.getExtension());
                String tmpDir = dir+"/"+fileType.getExtension();
                Files.createDirectories(Paths.get(tmpDir).toAbsolutePath().normalize());
            }
        }catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    @Transactional
    public FileResponse.HeaderDto uploadFile(MultipartFile file){
        log.info("[File] upload controller진입");


        Member member = null;
        //fixme 임시로 null값 처리함.
//        Member member = memberRepository.findMemberByEmail(principal.getAttributes().get("email").toString())
//                .orElseThrow(() -> new BaseException(ErrorCode.MEMBER_NOT_FOUND));

        FileType fileType = FileType.fromMimeType(file.getContentType());
        String uploadFileName = StringUtils.cleanPath(file.getOriginalFilename());
        String uuid = UUID.randomUUID().toString();
        String realName =  uuid+ "_" + uploadFileName;

        FileService fileService = getFileServiceByFileType(fileType);

        //Note 각 확장자명에 맞는 파일에 저장됨.
        String tmpDir = dir+"/"+fileType.getExtension();
        log.info("tmpDir:{}",tmpDir);
        fileDir = Paths.get(tmpDir).toAbsolutePath().normalize();

        log.info("fildDir: {}",fileDir.toString());
        Path targetLocation = fileDir.resolve(realName);

        try {
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            log.warn(e.getMessage());
            log.warn("저장 안됨.");
            throw new BaseException(ErrorCode.FILE_READ_ERROR);
        }

        UploadFile uploadFile = UploadFile.builder()
                .uploadFileName(uploadFileName)
                .storeFileName(realName)
                .filePath(targetLocation.toString())
                .realPath(targetLocation)
                .fileId(uuid)
                .member(member)
                .fileType(fileType)
                .build();

        fileRepository.save(uploadFile);
        return FileResponse.HeaderDto.from(fileService.readHeader(file),uuid);
    }

    public List<FileResponse.FileLogDto> getUploadLog(){
        //fixme member값 가져오기
        //        Member member = memberRepository.findMemberByEmail(principal.getAttributes().get("email").toString())
        //                .orElseThrow(() -> new BaseException(ErrorCode.MEMBER_NOT_FOUND));
        Member member = null;

        List<UploadFile> uploadFiles = fileRepository.findUploadFilesByMember(member);
        if(uploadFiles.isEmpty()) throw new BaseException(ErrorCode.FILELOG_NOT_FOUND);
        List<FileResponse.FileLogDto> logDtos = new ArrayList<>();

        for(UploadFile file: uploadFiles) logDtos.add(FileResponse.FileLogDto.from(file));
        return logDtos;
    }

    public List<DataDto> readData(FileRequest.MappingResultDto mappingResultDto){
        log.info("[File] Mapping Controller 진입");

        UploadFile uploadFile = getFileById(mappingResultDto.fileId()) ;
        log.info("fileId:{}",mappingResultDto.fileId());

        FileService fileService = getFileServiceByFileType(uploadFile.getFileType());
        List<DataDto> dataDtos = new ArrayList<>();

        try{
            dataDtos = fileService.readFileData(uploadFile, mappingResultDto.headers());
        }catch (Exception e){
            log.error(e.getMessage());
            throw new BaseException(ErrorCode.FILE_READ_ERROR);
        }

        return dataDtos;
    }

    public UploadFile getFileById(String fileId){
        return fileRepository.findUploadFileByFileId(fileId).
                orElseThrow(() -> new BaseException(ErrorCode.FILE_NOT_FOUND));
    }

    public FileService getFileServiceByFileType(FileType fileType){
        FileService fileService = processor.getHandlerMap().get(fileType);
        if(fileService==null) throw new BaseException(ErrorCode.FILE_EXTENSION_ERROR);
        return fileService;
    }

}

package com.sejong.project.onair.domain.file.service;

import com.sejong.project.onair.domain.file.dto.DataDto;
import com.sejong.project.onair.domain.file.dto.FileRequest;
import com.sejong.project.onair.domain.file.dto.FileResponse;
import com.sejong.project.onair.domain.file.model.FileData;
import com.sejong.project.onair.domain.file.model.FileType;
import com.sejong.project.onair.domain.file.model.UploadFile;
import com.sejong.project.onair.domain.file.repository.FileDataRepository;
import com.sejong.project.onair.domain.file.repository.FileRepository;
import com.sejong.project.onair.domain.member.Member;
import com.sejong.project.onair.global.beanProcessor.FileTypeHandlerProcessor;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
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

    public FileResponse.HeaderDto uploadFile(MultipartFile file){
        //fixme member값 가져와서 누가 올린 파일인지 알아야함.
        Member member = null;

        log.info("uploadFile진입");

        FileType fileType = FileType.fromMimeType(file.getContentType());
        String uploadFileName = StringUtils.cleanPath(file.getOriginalFilename());
        String uuid = UUID.randomUUID().toString();
        String realName =  uuid+ "_" + uploadFileName;

        FileService fileService = processor.getHandlerMap().get(fileType);
        if (fileService == null) {
            //NOTE 예외 처리 해줘야함
            throw new IllegalArgumentException("지원하지 않는 파일 형식: " + fileType);
        }

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
        Member member = null;

        List<UploadFile> uploadFiles = fileRepository.findUploadFilesByMember(member);
        List<FileResponse.FileLogDto> logDtos = new ArrayList<>();

        for(UploadFile file: uploadFiles){
            logDtos.add(
                    FileResponse.FileLogDto.from(file)
            );
        }

        return logDtos;
    }

    public List<DataDto> readData(FileRequest.MappingResultDto mappingResultDto){

        UploadFile uploadFile = fileRepository.findUploadFileByFileId(mappingResultDto.fileId());
        if(uploadFile==null){
            log.warn("upload파일 발견 안됨");
        }
        log.info("fileId:{}",mappingResultDto.fileId());
        log.info("파일 가져옴");

        FileService fileService = processor.getHandlerMap().get(uploadFile.getFileType());

        List<DataDto> dataDtos = new ArrayList<>();
        try{
            dataDtos = fileService.readFileData(uploadFile, mappingResultDto.headers());
        }catch (Exception e){
            log.error(e.getMessage());
        }

        return dataDtos;
    }

//    public List<DataDto> readFileData(FileRequest.MappingResultDto mappingResultDto){
//
//        log.info("fileId:{}",mappingResultDto.fileId());
//        UploadFile uploadFile = fileRepository.findUploadFileByFileId(mappingResultDto.fileId());
//        log.info("파일 가져옴");
//
//        List<DataDto> dataDtos = new ArrayList<>();
//        try{
//            dataDtos = readFileData(uploadFile, mappingResultDto.headers());
//        }catch (Exception e){
//            log.error(e.getMessage());
//        }
//
//        return dataDtos;
//    }



}

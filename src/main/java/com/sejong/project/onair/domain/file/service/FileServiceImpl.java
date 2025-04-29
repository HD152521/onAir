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
        }catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    public FileResponse.HeaderDto uploadFile(MultipartFile file){
        //fixme member값 가져와서 누가 올린 파일인지 알아야함.
        Member member = null;

        log.info("파일 종류는 {}",file.getContentType());

        FileType fileType = FileType.fromMimeType(file.getContentType());
        String uploadFileName = StringUtils.cleanPath(file.getOriginalFilename());

        log.info("해당 파일의 종류는 {}",fileType.getMimeType());
        log.info("uploadFileName:{}", uploadFileName);

        String uuid = UUID.randomUUID().toString();
        String realName =  uuid+ "_" + uploadFileName;
        log.info("fildDir: {}",fileDir.toString());

        Path targetLocation = fileDir.resolve(realName);

        FileService fileService = processor.getHandlerMap().get(fileType);

//        if (fileService == null) {
//            //NOTE 예외 처리 해줘야함
//            throw new IllegalArgumentException("지원하지 않는 파일 형식: " + fileType);
//        }
//
//        try {
//            //프로젝트에 저장한거
//            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
//        } catch (IOException e) {
//            log.warn(e.getMessage());
//        }
//
//        UploadFile uploadFile = UploadFile.builder()
//                .uploadFileName(uploadFileName)
//                .storeFileName(realName)
//                .filePath(targetLocation.toString())
//                .realPath(targetLocation)
//                .fileId(uuid)
//                .member(member)
//                .fileType(fileType)
//                .build();
//
//        fileRepository.save(uploadFile);
        return FileResponse.HeaderDto.from(fileService.readHeader(file),uuid);
    }

//    public List<FileResponse.FileLogDto> getUploadLog(){
//        //fixme member값 가져오기
//        Member member = null;
//
//        List<UploadFile> uploadFiles = fileRepository.findUploadFilesByMember(member);
//        List<FileResponse.FileLogDto> logDtos = new ArrayList<>();
//
//        for(UploadFile file: uploadFiles){
//            logDtos.add(
//                    FileResponse.FileLogDto.from(file)
//            );
//        }
//
//        return logDtos;
//    }



}

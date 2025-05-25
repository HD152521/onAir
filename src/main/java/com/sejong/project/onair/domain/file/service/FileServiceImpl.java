package com.sejong.project.onair.domain.file.service;

import com.sejong.project.onair.domain.file.dto.DataDto;
import com.sejong.project.onair.domain.file.dto.FileRequest;
import com.sejong.project.onair.domain.file.dto.FileResponse;
import com.sejong.project.onair.domain.file.model.FileData;
import com.sejong.project.onair.domain.file.model.FileType;
import com.sejong.project.onair.domain.file.model.UploadFile;
import com.sejong.project.onair.domain.file.repository.FileDataRepository;
import com.sejong.project.onair.domain.file.repository.FileRepository;
import com.sejong.project.onair.domain.member.dto.MemberDetails;
import com.sejong.project.onair.domain.member.model.Member;
import com.sejong.project.onair.domain.member.repository.MemberRepository;
import com.sejong.project.onair.domain.member.service.MemberService;
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
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class FileServiceImpl{

    private static final Logger log = LoggerFactory.getLogger(FileServiceImpl.class);
    private final FileRepository fileRepository;
    private final FileDataRepository fileDataRepository;
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
        public FileResponse.HeaderDto uploadFile(MultipartFile file, Member member){
        log.info("[File] upload controller진입");

        FileType fileType = FileType.fromMimeType(file.getContentType());
        String uploadFileName = StringUtils.cleanPath(file.getOriginalFilename());
        String uuid = UUID.randomUUID().toString();
        String realName =  uuid+ "_" + uploadFileName;

        //파일 사이즈
        long fileSizeInBytes = file.getSize();
        double fileSizeInKB = fileSizeInBytes / 1024.0; //kb단위로변경

//        if(fileSizeInKB*1024>1){
//            log.warn("용량이 너무 큼");
//            throw new BaseException(ErrorCode.FILE_SIZE_ERROR);
//        }

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
                .fileSize(fileSizeInKB)
                .build();

        fileRepository.save(uploadFile);
        return FileResponse.HeaderDto.from(fileService.readHeader(file),uuid);
    }

    public List<FileResponse.FileLogDto> getUploadLog(Member member){
        List<UploadFile> uploadFiles = fileRepository.findUploadFilesByMember(member);
        if(uploadFiles.isEmpty()) throw new BaseException(ErrorCode.FILELOG_NOT_FOUND);
        List<FileResponse.FileLogDto> logDtos = new ArrayList<>();

        for(UploadFile file: uploadFiles) logDtos.add(FileResponse.FileLogDto.from(file));
        return logDtos;
    }

    public List<DataDto> readMappingData(FileRequest.MappingResultDto mappingResultDto,Member member){
        log.info("[File] Mapping Controller 진입");

        UploadFile uploadFile = getFileById(mappingResultDto.fileId()) ;
        log.info("fileId:{}",mappingResultDto.fileId());

        FileService fileService = getFileServiceByFileType(uploadFile.getFileType());
        List<DataDto> dataDtos = new ArrayList<>();
        log.info("{} service 파일 호출",fileService);
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

    public List<String> readData(MultipartFile file){
        log.info("[File] readdata진입");
        FileService fileService = getFileServiceByFileType(FileType.fromMimeType(file.getContentType()));
        List<String> dataDtos = new ArrayList<>();
        log.info("{} service 파일 호출",fileService);
        try{
            dataDtos = fileService.readAllData(file);
        }catch (Exception e){
            log.error(e.getMessage());
            throw new BaseException(ErrorCode.FILE_READ_ERROR);
        }
        return dataDtos;
    }

    public List<DataDto> readDataFromId(String fileId,Member member){
        List<DataDto> response = new ArrayList<>();
        List<FileData> datas = fileDataRepository.findFileDatasByFileId(fileId)
                .parallelStream()
                .collect(Collectors.toList());
        log.info("파일 데이터 가져옴");

        UploadFile file = null;

        try {
             file = fileRepository.findUploadFileByFileId(fileId).orElseThrow(
                    () -> new BaseException(ErrorCode.FILE_NOT_FOUND));
             log.info("파일 데이터 가져옴 file:{}",file.getFileId());
             if(!file.getMember().getMemberName().equals(member.getMemberName())){
                 log.warn("신청하신 멤버가 아닙니다. (이게 우선임)");
                 throw new BaseException(ErrorCode.MEMBER_NOT_FOUND);
             }
        }catch (Exception e){
            log.warn(e.getMessage());
        }

        if(datas == null){
            log.warn("해당 id 가진 파일존재 안함");
            return null;
        }
        for(FileData data : datas){
            response.add(DataDto.from(data));
        }
        return response;
    }

}

package com.sejong.project.onair.domain.file.service;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class FileServiceImpl implements FileService{
    private static final Logger log = LoggerFactory.getLogger(FileServiceImpl.class);

    // /없으면 상대경로임
    private String dir = "/tmp";
    private Path fileDir;
    private final String TYPE_CSV = "text/csv";

    @PostConstruct
    public void postConstruct() {
        fileDir = Paths.get(dir).toAbsolutePath().normalize();

        try {
            Files.createDirectories(fileDir);
        } catch (IOException e) {
        }
    }

    public String uploadFile(MultipartFile file){
        if(TYPE_CSV.equals(file.getContentType()) == false){
        }
        String uploadFileName = StringUtils.cleanPath(file.getOriginalFilename());

        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(file.getInputStream(), "UTF-8"));
        } catch (IOException e) {
        }

        String realName = UUID.randomUUID().toString() + "_" + uploadFileName;
        Path targetLocation = fileDir.resolve(realName);
        try {
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
        }
//        File.builder()
//                .displayName(uploadFileName)
//                .size(file.getSize())
//                .count(fileTargetCount)
//                .build();
        log.info("{} / {} / {}",uploadFileName,file.getSize(),targetLocation);
        return "success";


    }
//    public String uploadFile(MultipartFile file){
//        try {
//            if(file.isEmpty()) log.warn("uploadFile is empty");
//            if(!isCSVFile(file.getName())) log.warn("uploadFile is not CSV file");
//
//            log.info("upload {} success",file.getOriginalFilename());
//        } catch (Exception e) {
//            return "upload file error";
//        }
//        return "success";
//    }

    public static boolean isCSVFile(String fileName) {
        String CSV_FILE_REGEX = "^.+\\.csv$";
        Pattern pattern = Pattern.compile(CSV_FILE_REGEX, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(fileName);
        return matcher.matches();
    }
}

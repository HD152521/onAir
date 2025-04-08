package com.sejong.project.onair.domain.file.service;

import com.sejong.project.onair.domain.file.dto.FileResponse;
import com.sejong.project.onair.domain.file.model.UploadFile;
import com.sejong.project.onair.domain.file.repository.FileRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class FileServiceImpl implements FileService{

    private static final Logger log = LoggerFactory.getLogger(FileServiceImpl.class);
    private final FileRepository fileRepository;

    // /없으면 상대경로임
    private String dir = "./upload";
    private Path fileDir;
    private final String TYPE_CSV = "text/csv";

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

        if(TYPE_CSV.equals(file.getContentType())) log.warn("파일 종류가 csv가 아님");
        log.info("해당 파일의 종류는 {}",file.getContentType());

        String uploadFileName = StringUtils.cleanPath(file.getOriginalFilename());
        log.info("uploadFileName:{}", uploadFileName);

        String realName = UUID.randomUUID().toString() + "_" + uploadFileName;
        Path targetLocation = fileDir.resolve(realName);

        try {
            //프로젝트에 저장한거
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            log.warn(e.getMessage());
        }

        UploadFile uploadFile = UploadFile.builder()
                .uploadFileName(uploadFileName)
                .storeFileName(realName)
                .filePath(targetLocation.toString())
                .realPath(targetLocation)
                .build();

        fileRepository.save(uploadFile);

        return FileResponse.HeaderDto.from(readHeader(file));
    }

    public List<String> readHeader(Row row){
        if(row==null) log.warn("row is null");

        log.info("readHeader cell개수:{}",row.getPhysicalNumberOfCells());
        List<String> headers = new ArrayList<>();
        for(Cell cell: row){
            System.out.print(cell.getStringCellValue()+" ");
            headers.add(cell.getStringCellValue());
        }
        System.out.println();
        return headers;
    }

    public List<String> readHeader(MultipartFile file){
        Row row = null;
        try{
            Workbook workbook = WorkbookFactory.create(file.getInputStream());
            int sheetSize = workbook.getNumberOfSheets();
            Sheet sheet = workbook.getSheetAt(0);

            row = sheet.getRow(0);
        }catch (Exception e){
            log.error(e.getMessage());
        }
        return readHeader(row);
    }

    public void readFileData(MultipartFile file){

        try {
            Workbook workbook = WorkbookFactory.create(file.getInputStream());
            int sheetSize = workbook.getNumberOfSheets();
            Sheet sheet = workbook.getSheetAt(0);

            List<String> headers = readHeader(sheet.getRow(0));

            // 데이터 행 읽기
            int rowCnt=1;
            while(true){
                Row row = sheet.getRow(rowCnt++);
                if(row==null) break;
                for(int i=0;i<headers.size();i++){
                    Cell cell = row.getCell(i);
                    if(cell==null) continue;

                    if (cell.getCellType() == CellType.STRING) {
                        System.out.print("String: " + cell.getStringCellValue() + " ");

                    } else if (cell.getCellType() == CellType.NUMERIC) {
                        if (DateUtil.isCellDateFormatted(cell)) {
                            // 날짜인 경우
                            Date date = cell.getDateCellValue();
                            LocalDateTime localDateTime = date.toInstant()
                                    .atZone(ZoneId.systemDefault())
                                    .toLocalDateTime();
                            System.out.print("Date: " + localDateTime + " ");
                        } else {
                            double val = cell.getNumericCellValue();
                            // 정수인지 확인
                            if (val == Math.floor(val)) {
                                System.out.print("Int: " + (int) val + " ");
                            } else {
                                System.out.print("Double: " + val + " ");
                            }
                        }
                    } else {
                        System.out.print("Unknown ");
                    }

                }
            }
            workbook.close();
        } catch (NullPointerException e) {
            log.warn(e.getMessage());
        } catch (Exception e){
            log.warn(e.getMessage());
        }
    }

}

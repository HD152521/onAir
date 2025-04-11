package com.sejong.project.onair.domain.file.service;

import com.sejong.project.onair.domain.file.dto.DataDto;
import com.sejong.project.onair.domain.file.dto.FileRequest;
import com.sejong.project.onair.domain.file.dto.FileResponse;
import com.sejong.project.onair.domain.file.model.FileData;
import com.sejong.project.onair.domain.file.model.UploadFile;
import com.sejong.project.onair.domain.file.repository.FileDataRepository;
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
import java.io.InputStream;
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
    private final FileDataRepository fileDataRepository;

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

        String uuid = UUID.randomUUID().toString();
        String realName =  uuid+ "_" + uploadFileName;
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
                .fileId(uuid)
                .build();

        fileRepository.save(uploadFile);

        return FileResponse.HeaderDto.from(readHeader(file),uuid);
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

    public List<DataDto> readFileData(FileRequest.MappingResultDto mappingResultDto){

        log.info("fileId:{}",mappingResultDto.fileId());
        UploadFile uploadFile = fileRepository.findUploadFileByFileId(mappingResultDto.fileId());
        log.info("파일 가져옴");

        List<DataDto> dataDtos = new ArrayList<>();
        try{
            dataDtos = readFileData(uploadFile, mappingResultDto.headers());
        }catch (Exception e){
            log.error(e.getMessage());
        }

        return dataDtos;
    }

    public List<DataDto> readFileData(UploadFile uploadFile, List<Integer> headers){

        List<FileData> datas = new ArrayList<>();


        try {
            Path path = Paths.get(uploadFile.getFilePath());
            Workbook workbook = WorkbookFactory.create(Files.newInputStream(path));
            int sheetSize = workbook.getNumberOfSheets();
            Sheet sheet = workbook.getSheetAt(0);

            // 데이터 행 읽기
            int rowCnt=1;
            while(true){
                Row row = sheet.getRow(rowCnt++);
                if(row==null) break;

                //fixme  gpt추천콛
                LocalDateTime time = null;
                Cell timeCell = row.getCell(headers.get(0));
                if (timeCell != null && DateUtil.isCellDateFormatted(timeCell)) {
                    time = timeCell.getDateCellValue().toInstant()
                            .atZone(ZoneId.systemDefault()).toLocalDateTime();
                }

                double co2 = getDoubleCellValue(row, headers.get(1));
                double ch4_ppb = getDoubleCellValue(row, headers.get(2));
                double ch4_ppm = getDoubleCellValue(row, headers.get(3));
                String type = getStringCellValue(row, headers.get(4));
                String province = getStringCellValue(row, headers.get(5));
                String city = getStringCellValue(row, headers.get(6));
                String district = getStringCellValue(row, headers.get(7));
                String code = getStringCellValue(row, headers.get(8));
                double so2_ppm = getDoubleCellValue(row, headers.get(9));
                double no2_ppm = getDoubleCellValue(row, headers.get(10));
                double o3_ppm = getDoubleCellValue(row, headers.get(11));
                double co_ppm = getDoubleCellValue(row, headers.get(12));
                double pm10 = getDoubleCellValue(row, headers.get(13));
                double pm2_5 = getDoubleCellValue(row, headers.get(14));
                double nox_ppm = getDoubleCellValue(row, headers.get(15));
                double no_ppm = getDoubleCellValue(row, headers.get(16));
                int windDirection = getIntCellValue(row, headers.get(17));
                double windSpeed = getDoubleCellValue(row, headers.get(18));
                double temperature = getDoubleCellValue(row, headers.get(19));
                double humidity = getDoubleCellValue(row, headers.get(20));

                datas.add(FileData.builder()
                        .time(time)
                        .co2(co2)
                        .ch4_ppb(ch4_ppb)
                        .ch4_ppm(ch4_ppm)
                        .type(type)
                        .province(province)
                        .city(city)
                        .district(district)
                        .code(code)
                        .so2_ppm(so2_ppm)
                        .no2_ppm(no2_ppm)
                        .o3_ppm(o3_ppm)
                        .co_ppm(co_ppm)
                        .pm10(pm10)
                        .pm2_5(pm2_5)
                        .nox_ppm(nox_ppm)
                        .no_ppm(no_ppm)
                        .windDirection(windDirection)
                        .windSpeed(windSpeed)
                        .temperature(temperature)
                        .humidity(humidity)
                                .fileId(uploadFile.getFileId())
                                .uploadFile(uploadFile)
                        .build());
            }
            workbook.close();
        } catch (NullPointerException e) {
            log.warn(e.getMessage());
        } catch (Exception e){
            log.warn(e.getMessage());
        }

        List<DataDto> dataDtos = new ArrayList<>();
        for(FileData data: datas){
            if(data==null)continue;
            fileDataRepository.save(data);
            dataDtos.add(DataDto.from(data));
        }


        return dataDtos;
    }

    private String getStringCellValue(Row row, int idx) {
        Cell cell = row.getCell(idx);
        return (cell == null) ? null : cell.toString().trim();
    }

    private double getDoubleCellValue(Row row, int idx) {
        Cell cell = row.getCell(idx);
        if (cell == null) return 0.0;

        if (cell.getCellType() == CellType.NUMERIC) {
            return cell.getNumericCellValue();
        } else {
            try {
                return Double.parseDouble(cell.toString());
            } catch (NumberFormatException e) {
                return 0.0;
            }
        }
    }

    private int getIntCellValue(Row row, int idx) {
        return (int) getDoubleCellValue(row, idx);
    }

}

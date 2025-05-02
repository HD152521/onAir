package com.sejong.project.onair.domain.file.service;

import com.sejong.project.onair.domain.file.annotation.FileTypeHandler;
import com.sejong.project.onair.domain.file.dto.DataDto;
import com.sejong.project.onair.domain.file.dto.FileRequest;
import com.sejong.project.onair.domain.file.model.FileData;
import com.sejong.project.onair.domain.file.model.FileType;
import com.sejong.project.onair.domain.file.model.UploadFile;
import com.sejong.project.onair.domain.file.repository.FileDataRepository;
import com.sejong.project.onair.domain.file.repository.FileRepository;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoField;
import java.util.ArrayList;
import java.util.List;

@Service("xlsx")
@FileTypeHandler(FileType.XLSX)
@RequiredArgsConstructor
public class FileServiceXlsx implements FileService{
    private final FileDataRepository fileDataRepository;
    private final FileRepository fileRepository;

    private static final Logger log = LoggerFactory.getLogger(FileServiceXls.class);

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

    public List<DataDto> readFileData(UploadFile uploadFile, List<Integer> headers){

        List<FileData> datas = new ArrayList<>();

        try {
            Path path = Paths.get(uploadFile.getFilePath());
            Workbook workbook = WorkbookFactory.create(Files.newInputStream(path));
            FormulaEvaluator formulaEvaluator = workbook.getCreationHelper().createFormulaEvaluator();
            int sheetSize = workbook.getNumberOfSheets();
            Sheet sheet = workbook.getSheetAt(0);

            // 데이터 행 읽기
            int rowCnt=1;
            while(true){
                Row row = sheet.getRow(rowCnt++);
                if(row==null) break;

                printData(row);
                LocalDateTime time = null;
                Cell timeCell = row.getCell(headers.get(0)-1);
                if (timeCell != null) {
                    if (DateUtil.isCellDateFormatted(timeCell)) {
                        time = timeCell.getDateCellValue().toInstant()
                                .atZone(ZoneId.systemDefault())
                                .toLocalDateTime();
                    } else {
                        // 날짜 형식이 아닌 경우, 텍스트로 변환 후 날짜 파싱
                        String timeString = timeCell.getStringCellValue();
                        log.info("timeStinrg : {}",timeString);

                        if (timeString != null && !timeString.trim().isEmpty()) {
                            DateTimeFormatter formatter = new DateTimeFormatterBuilder()
                                    .appendValue(ChronoField.YEAR, 4)
                                    .appendLiteral('-')
                                    .appendValue(ChronoField.MONTH_OF_YEAR)  // 1자리도 허용
                                    .appendLiteral('-')
                                    .appendValue(ChronoField.DAY_OF_MONTH)
                                    .appendLiteral(' ')
                                    .appendValue(ChronoField.HOUR_OF_DAY)
                                    .appendLiteral(':')
                                    .appendValue(ChronoField.MINUTE_OF_HOUR)
                                    .toFormatter();
                            try {
                                time = LocalDateTime.parse(timeString.trim(), formatter);
                            } catch (DateTimeParseException e) {
                                log.warn("시간 파싱 실패: '{}'", timeString);
                            }
                        }
                    }
                }

                double co2 = getDoubleCellValue(row, headers.get(1), formulaEvaluator);
                double ch4_ppb = getDoubleCellValue(row, headers.get(2), formulaEvaluator);
                double ch4_ppm = getDoubleCellValue(row, headers.get(3), formulaEvaluator);
                String type = getStringCellValue(row, headers.get(4), formulaEvaluator);
                String province = getStringCellValue(row, headers.get(5), formulaEvaluator);
                String city = getStringCellValue(row, headers.get(6), formulaEvaluator);
                String district = getStringCellValue(row, headers.get(7), formulaEvaluator);
                String observatoryName = getStringCellValue(row, headers.get(8), formulaEvaluator);
                String code = getStringCellValue(row, headers.get(9), formulaEvaluator);
                double so2_ppm = getDoubleCellValue(row, headers.get(10), formulaEvaluator);
                double no2_ppm = getDoubleCellValue(row, headers.get(11), formulaEvaluator);
                double o3_ppm = getDoubleCellValue(row, headers.get(12), formulaEvaluator);
                double co_ppm = getDoubleCellValue(row, headers.get(13), formulaEvaluator);
                double pm10 = getDoubleCellValue(row, headers.get(14), formulaEvaluator);
                double pm2_5 = getDoubleCellValue(row, headers.get(15), formulaEvaluator);
                double nox_ppm = getDoubleCellValue(row, headers.get(16), formulaEvaluator);
                double no_ppm = getDoubleCellValue(row, headers.get(17), formulaEvaluator);
                int windDirection = getIntCellValue(row, headers.get(18), formulaEvaluator);
                double windSpeed = getDoubleCellValue(row, headers.get(19), formulaEvaluator);
                double temperature = getDoubleCellValue(row, headers.get(20), formulaEvaluator);
                double humidity = getDoubleCellValue(row, headers.get(21), formulaEvaluator);

                datas.add(FileData.builder()
                        .time(time)
                        .co2(co2)
                        .ch4_ppb(ch4_ppb)
                        .ch4_ppm(ch4_ppm)
                        .type(type)
                        .province(province)
                        .city(city)
                        .district(district)
                        .observatoryName(observatoryName)
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

    private double getDoubleCellValue(Row row, int idx, FormulaEvaluator formulaEvaluator) {
        Cell cell = row.getCell(--idx);
        if (cell == null) {
            log.info("[File] 해당 데이터가 null임");
            return 0.0;
        }

        // 수식이 있을 경우 수식 평가
        CellValue cellValue = formulaEvaluator.evaluate(cell);
        System.out.println(cell.toString()+" "+cellValue.toString());
        if (cellValue.getCellType() == CellType.NUMERIC) {
            return cellValue.getNumberValue();
        } else {
            try {
                return Double.parseDouble(cell.toString());
            } catch (NumberFormatException e) {
                log.info("[File] Xls 형변환 실패");
                return 0.0;
            }
        }
    }

    private String getStringCellValue(Row row, int idx, FormulaEvaluator formulaEvaluator) {
        Cell cell = row.getCell(--idx);
        if (cell == null) {
            log.info("[File] 해당 데이터가 null임");
            return null;
        }

        // 수식이 있을 경우 수식 평가
        CellValue cellValue = formulaEvaluator.evaluate(cell);
        if (cellValue.getCellType() == CellType.STRING) {
            return cellValue.getStringValue().trim();
        }
        return cell.toString().trim();
    }

    private int getIntCellValue(Row row, int idx, FormulaEvaluator formulaEvaluator) {
        return (int) getDoubleCellValue(row, idx,formulaEvaluator);
    }

    private void printData(Row row){
        for(int i=0;i<22;i++){
            System.out.print(row.getCell(i)+" | ");
        }System.out.println();
    }
}

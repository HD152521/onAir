package com.sejong.project.onair.domain.file.service;

import com.sejong.project.onair.domain.file.annotation.FileTypeHandler;
import com.sejong.project.onair.domain.file.dto.DataDto;
import com.sejong.project.onair.domain.file.model.FileData;
import com.sejong.project.onair.domain.file.model.FileType;
import com.sejong.project.onair.domain.file.model.UploadFile;
import com.sejong.project.onair.domain.file.repository.FileDataRepository;
import com.sejong.project.onair.global.exception.BaseException;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.mozilla.universalchardet.UniversalDetector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoField;
import java.util.ArrayList;
import java.util.List;

@Service("csv")
@FileTypeHandler(FileType.CSV)
@RequiredArgsConstructor
public class FileServiceCsv implements FileService{

    private static final Logger log = LoggerFactory.getLogger(FileServiceCsv.class);
    private final FileDataRepository fileDataRepository;


    public List<String> readHeader(MultipartFile file){

        String encoding = null;
        try{
            encoding = detectCharset(file.getInputStream());
        }catch (Exception e){
            log.warn(e.getMessage()+" == encoding 감지 실패");
        }
        log.info("Enter csv readHeader content type:{}  /  encoding type:{}",file.getContentType(),encoding);

        List<String> headers = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream(), Charset.forName(encoding)))) {
            String firstLine = reader.readLine(); // 첫 번째 줄만 읽기
            if (firstLine != null) {
                String[] values = firstLine.split(",");
                for (String value : values) {
                    headers.add(value.trim());
                }
            }
        } catch (Exception e) {
            log.error("CSV 헤더 읽기 실패: {}", e.getMessage(), e);
        }
        return headers;
    }

    public String detectCharset(InputStream input) throws IOException {
        byte[] buf = new byte[4096];
        UniversalDetector detector = new UniversalDetector(null);

        int nread;
        while ((nread = input.read(buf)) > 0 && !detector.isDone()) {
            detector.handleData(buf, 0, nread);
        }
        detector.dataEnd();
        String encoding = detector.getDetectedCharset();
        return (encoding != null) ? encoding : "UTF-8"; // fallback
    }

    public List<DataDto> readFileData(UploadFile uploadFile, List<Integer> headers){
        log.info("[File] CSV 서비스 readFileData 진입");
        List<FileData> datas = new ArrayList<>();
        try {
            Path path = Paths.get(uploadFile.getFilePath());
            log.info("파일 path가져옴");

            // 인코딩 감지를 위한 스트림 설정
            BufferedInputStream bis = new BufferedInputStream(Files.newInputStream(path));
            bis.mark(4096); // reset용
            String encoding = detectCharset(bis);
            bis.reset();
            log.info("[File] CSV path값으로 buffer가져오기");

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(bis, Charset.forName(encoding)))) {
                String line;
                int rowCnt = 0;
                while ((line = reader.readLine()) != null) {
                    rowCnt++;
                    if (rowCnt == 1) continue; // 헤더는 건너뜀

                    String[] tokens = line.split(",");

                    //Note 테스트용 출력문
                    printData(tokens);

                    LocalDateTime time = null;
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
                    String timeString = tokens[headers.get(0) - 1].trim();

                    try {
                        time = LocalDateTime.parse(timeString, formatter);
                    } catch (DateTimeParseException e) {
                        log.warn("시간 파싱 실패: '{}'", timeString);
                    }

                    double co2 = parseDouble(tokens, headers.get(1));
                    double ch4_ppb = parseDouble(tokens, headers.get(2));
                    double ch4_ppm = parseDouble(tokens, headers.get(3));
                    String type = parseString(tokens, headers.get(4));
                    String province = parseString(tokens, headers.get(5));
                    String city = parseString(tokens, headers.get(6));
                    String district = parseString(tokens, headers.get(7));
                    String observatoryName = parseString(tokens, headers.get(8));
                    String code = parseString(tokens, headers.get(9));
                    double so2_ppm = parseDouble(tokens, headers.get(10));
                    double no2_ppm = parseDouble(tokens, headers.get(11));
                    double o3_ppm = parseDouble(tokens, headers.get(12));
                    double co_ppm = parseDouble(tokens, headers.get(13));
                    double pm10 = parseDouble(tokens, headers.get(14));
                    double pm2_5 = parseDouble(tokens, headers.get(15));
                    double nox_ppm = parseDouble(tokens, headers.get(16));
                    double no_ppm = parseDouble(tokens, headers.get(17));
                    int windDirection = parseInt(tokens, headers.get(18));
                    double windSpeed = parseDouble(tokens, headers.get(19));
                    double temperature = parseDouble(tokens, headers.get(20));
                    double humidity = parseDouble(tokens, headers.get(21));

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
            }
        } catch (Exception e) {
            log.warn("[File] CSV read error: {}", e.getMessage());
        }

        List<DataDto> dataDtos = new ArrayList<>();

        for (FileData data : datas) {
            if (data == null) continue;
            fileDataRepository.save(data);
            dataDtos.add(DataDto.from(data));
        }
        return dataDtos;

    }

    private String parseString(String[] tokens, int index) {
        index--;
        if(checkData(tokens,index)) return null;
        return tokens[index];
    }

    private double parseDouble(String[] tokens, int index) {
        index--;
        if(checkData(tokens,index)) return 0.0;
        try {
            return Double.parseDouble(tokens[index]);
        } catch (NumberFormatException e) {
            log.info("[File] CSV 파일 값을 읽는 도중 double값이 아닌 값을 발견 {}번째",index);
            return 0.0;
        }
    }

    private int parseInt(String[] tokens, int index) {
        index--;
        if(checkData(tokens,index)) return 0;
        try {
            return (int)Double.parseDouble(tokens[index]);
        } catch (NumberFormatException e) {
            log.info("[File] CSV 파일 값을 읽는 도중 int 값이 아닌 값을 발견");
            return 0;
        }

    }

    private boolean checkData(String[] tokens,int index){
        if (index >= tokens.length) return true;
        if (tokens[index].isBlank()){
            log.warn("index is blank");
            return true;
        }
        return false;
    }

    private void printData(String[] tokens){
        for(int i=0;i< tokens.length;i++){
            System.out.print(tokens[i]+" | ");
        }
        System.out.println();
    }
}

package com.sejong.project.onair.domain.file.service;

import com.sejong.project.onair.domain.file.annotation.FileTypeHandler;
import com.sejong.project.onair.domain.file.model.FileType;
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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

@Service("csv")
@FileTypeHandler(FileType.CSV)
@RequiredArgsConstructor
public class FileServiceCsv implements FileService{

    private static final Logger log = LoggerFactory.getLogger(FileServiceCsv.class);

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
}

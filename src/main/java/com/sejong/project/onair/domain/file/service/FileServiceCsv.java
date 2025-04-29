package com.sejong.project.onair.domain.file.service;

import com.sejong.project.onair.domain.file.annotation.FileTypeHandler;
import com.sejong.project.onair.domain.file.model.FileType;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service("csv")
@FileTypeHandler(FileType.CSV)
@RequiredArgsConstructor
public class FileServiceCsv implements FileService{

    public List<String> readHeader(MultipartFile file){
        List<String> list= null;
        return list;
    }
}

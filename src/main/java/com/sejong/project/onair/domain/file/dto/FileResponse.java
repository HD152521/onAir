package com.sejong.project.onair.domain.file.dto;

import com.sejong.project.onair.domain.file.model.UploadFile;

import java.util.List;

public class FileResponse {
    public record HeaderDto(
            List<String> headers,
            String fileId
    ){
        public static HeaderDto from(List<String> headers,String fileId){
            return new HeaderDto(
                    headers,
                    fileId
            );
        }
    }

    public record FileLogDto(
            String filename,
            String fileId
    ){
        public static FileLogDto from(UploadFile uploadFile){
            return new FileLogDto(
                uploadFile.getUploadFileName(),
                uploadFile.getFileId()
            );
        }
    }
}

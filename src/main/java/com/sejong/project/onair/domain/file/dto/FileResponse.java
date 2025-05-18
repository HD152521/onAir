package com.sejong.project.onair.domain.file.dto;

import com.sejong.project.onair.domain.file.model.FileData;
import com.sejong.project.onair.domain.file.model.UploadFile;

import java.time.LocalDateTime;
import java.util.List;

public class FileResponse {

    public record HeaderDto(
            List<String> headers,
            String fileId,
            List<String> fieldNames
    ){
        public static HeaderDto from(List<String> headers,String fileId){
            return new HeaderDto(
                    headers,
                    fileId,
                    List.of(
                            "time", "co2", "ch4_ppb", "ch4_ppm", "type", "province", "city",
                            "district", "observatoryName", "code", "so2_ppm", "no2_ppm",
                            "o3_ppm", "co_ppm", "pm10", "pm2_5", "nox_ppm", "no_ppm",
                            "windDirection", "windSpeed", "temperature", "humidity"
                    )
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

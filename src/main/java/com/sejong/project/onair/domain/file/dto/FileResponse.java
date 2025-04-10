package com.sejong.project.onair.domain.file.dto;

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
}

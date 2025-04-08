package com.sejong.project.onair.domain.file.dto;

import java.util.List;

public class FileResponse {
    public record HeaderDto(
            List<String> headers
    ){
        public static HeaderDto from(List<String> headers){
            return new HeaderDto(headers);
        }
    }
}

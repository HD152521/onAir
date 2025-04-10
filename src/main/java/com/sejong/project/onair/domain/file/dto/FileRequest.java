package com.sejong.project.onair.domain.file.dto;

import java.util.List;

public class FileRequest {
    public record MappingResultDto(
            List<Integer> headers,
            String fileId
    ){}
}

package com.sejong.project.onair.domain.file.swagger;

import com.sejong.project.onair.domain.file.dto.FileRequest;
import com.sejong.project.onair.domain.file.dto.FileResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.MediaType;
import org.springframework.web.ErrorResponse;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@Operation(
        summary = "헤더 매핑",
        description = "헤더 데이터들과 매핑하여 실질적으로 데이터베이스에 값 저장하기",
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "매핑 전 헤더 번호들",
                required = true,
                content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                        schema = @Schema(implementation = FileRequest.MappingResultDto.class))
        )
)
@ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "파일 업로드를 완료하였습니다.",
                content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                        schema = @Schema(implementation = FileResponse.HeaderDto.class))),
        @ApiResponse(responseCode = "4xx", description = "파일 업로드 실패",
                content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                        schema = @Schema(implementation = ErrorResponse.class)))
})
public @interface MappingFile {
}

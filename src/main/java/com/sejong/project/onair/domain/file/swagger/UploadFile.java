package com.sejong.project.onair.domain.file.swagger;

import com.sejong.project.onair.domain.file.dto.DataDto;
import com.sejong.project.onair.domain.file.dto.FileResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.MediaType;
import org.springframework.web.ErrorResponse;
import org.springframework.web.multipart.MultipartFile;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.List;

@Retention(RetentionPolicy.RUNTIME)
@Operation(
        summary = "파일 업로드",
        description = "파일을 업로드 하고 해당 업로드 파일의 헤더 컬럼들을 list형태로 반환해줌",
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "파일 확장자(xls, csv)",
                required = true,
                content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                        schema = @Schema(implementation = MultipartFile.class))
        )
)
@ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "파일 매핑을 완료하였습니다.",
                content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                        schema = @Schema(implementation = DataDto.class))),
        @ApiResponse(responseCode = "4xx", description = "파일 매핑 실패",
                content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                        schema = @Schema(implementation = ErrorResponse.class)))
})
public @interface UploadFile {
}

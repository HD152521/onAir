package com.sejong.project.onair.global.exception.codes;

import com.sejong.project.onair.global.exception.codes.reason.Reason;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum SuccessCode implements BaseCode{

    SUCCESS(HttpStatus.OK, "SUCCESS-0000", "요청에 성공하였습니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    @Override
    public Reason.ReasonDto getReasonHttpStatus() {
        return null;
    }
}

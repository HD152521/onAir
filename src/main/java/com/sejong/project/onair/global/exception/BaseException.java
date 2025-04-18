package com.sejong.project.onair.global.exception;
import com.sejong.project.onair.global.exception.codes.BaseCode;
import com.sejong.project.onair.global.exception.codes.reason.Reason;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class BaseException extends RuntimeException {

    private BaseCode code;

    public Reason.ReasonDto getErrorReasonHttpStatus(){
        return this.code.getReasonHttpStatus();
    }
}
package com.sejong.project.onair.global.exception;

import com.sejong.project.onair.global.exception.codes.reason.Reason;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BaseException.class)
    public ResponseEntity<BaseResponse<?>> handleBaseException(BaseException e) {
        Reason.ReasonDto reason = e.getErrorReasonHttpStatus();
        return new ResponseEntity<>(
                BaseResponse.onFailure(reason.getCode(), reason.getMessage(),null),
                HttpStatus.valueOf(reason.getHttpStatus().value())
        );
    }
}

package com.sejong.project.onair.global.exception.codes.reason;

import lombok.*;
import org.springframework.http.HttpStatus;

public class Reason {

    @Builder
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ReasonDto{
        HttpStatus httpStatus;
        String code;
        String message;
        Boolean isSuccess;
    }
}

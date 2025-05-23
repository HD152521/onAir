package com.sejong.project.onair.global.exception.codes;


import com.sejong.project.onair.global.exception.codes.reason.Reason;

public interface BaseCode {
    public Reason.ReasonDto getReasonHttpStatus();
}

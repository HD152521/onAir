package com.sejong.project.onair.domain;

import com.sejong.project.onair.global.exception.BaseException;
import com.sejong.project.onair.global.exception.BaseResponse;
import com.sejong.project.onair.global.exception.codes.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TestService {
    public BaseResponse<?> testFail(int status){
        if(status!=1) checkStatus(status);
        return BaseResponse.onSuccess("success");
    }

    public void checkStatus(int status){
        throw new BaseException(ErrorCode.FILELOG_NOT_FOUND);
    }
}

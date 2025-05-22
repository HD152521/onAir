package com.sejong.project.onair.domain;

import com.sejong.project.onair.global.exception.BaseException;
import com.sejong.project.onair.global.exception.BaseResponse;
import com.sejong.project.onair.global.exception.codes.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class TestController {

    @GetMapping("/test")
    public ResponseEntity<String> test(){
        return ResponseEntity.ok("OK");
    }

    @GetMapping("/health")
    public ResponseEntity<String> health(){
        return ResponseEntity.ok("OK");
    }

    @GetMapping("/checkAuth")
    public BaseResponse<?> checkAuth(){
        return BaseResponse.onSuccess("success");
    }

    private final TestService testService;
    @GetMapping("/test/fail")
    public BaseResponse<?> checkFail(@RequestParam int status){
        return BaseResponse.onSuccess(testService.testFail(status));
    }
}

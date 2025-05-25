package com.sejong.project.onair.domain.member.controller;

import com.sejong.project.onair.domain.member.dto.MemberDetails;
import com.sejong.project.onair.domain.member.dto.MemberRequest;
import com.sejong.project.onair.domain.member.dto.MemberResponse;
import com.sejong.project.onair.domain.member.service.MemberService;
import com.sejong.project.onair.global.exception.BaseResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/member")
public class MemberController {

    private static final Logger log = LoggerFactory.getLogger(MemberController.class);
    private final MemberService memberService;

    @PostMapping("/login/google")
    public MemberResponse.LoginResponseDto googleLoginByToken(@RequestBody MemberRequest.GoogleLoginDto googleLoginDto, HttpServletResponse response){
        log.info("enter google controller");
        return memberService.googleLoginByToken(googleLoginDto,response);
    }
    //todo access refresh토큰 주는거 조금 더 보기

    @PostMapping("/login/testuser")
    MemberResponse.LoginResponseDto googleLoginByToken(HttpServletResponse response){
        log.info("enter google controller");
        return memberService.testLogin(response);
    }

    @PostMapping("/refresh")
    public ResponseEntity<BaseResponse<?>> refreshToken(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {
        log.info("come refresh");
        return memberService.updateRefreshToken(request,response);
    }

    @GetMapping("/profile")
    public BaseResponse<?> getProfile(@AuthenticationPrincipal MemberDetails memberDetails){
        return  BaseResponse.onSuccess(memberService.getMemberProfile(memberDetails));
    }
}

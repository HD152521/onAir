package com.sejong.project.onair.domain.member.controller;

import com.sejong.project.onair.domain.member.dto.MemberRequest;
import com.sejong.project.onair.domain.member.dto.MemberResponse;
import com.sejong.project.onair.domain.member.service.MemberService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}

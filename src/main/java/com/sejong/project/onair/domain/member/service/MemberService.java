package com.sejong.project.onair.domain.member.service;


import com.sejong.project.onair.domain.member.dto.MemberRequest;
import com.sejong.project.onair.domain.member.dto.MemberResponse;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Service
public interface MemberService {
    MemberResponse.LoginResponseDto googleLoginByToken(MemberRequest.GoogleLoginDto googleLoginDto, HttpServletResponse response);
    MemberResponse.LoginResponseDto testLogin(HttpServletResponse response);

}

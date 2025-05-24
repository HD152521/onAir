package com.sejong.project.onair.domain.member.service;


import com.sejong.project.onair.domain.member.dto.MemberDetails;
import com.sejong.project.onair.domain.member.dto.MemberRequest;
import com.sejong.project.onair.domain.member.dto.MemberResponse;
import com.sejong.project.onair.domain.member.model.Member;
import com.sejong.project.onair.global.exception.BaseResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Service
public interface MemberService {
    MemberResponse.LoginResponseDto googleLoginByToken(MemberRequest.GoogleLoginDto googleLoginDto, HttpServletResponse response);
    MemberResponse.LoginResponseDto testLogin(HttpServletResponse response);
    ResponseEntity<BaseResponse<?>> updateRefreshToken(HttpServletRequest request, HttpServletResponse response);
    Member getMember(MemberDetails memberDetails);
    Member getMember(String email);
}

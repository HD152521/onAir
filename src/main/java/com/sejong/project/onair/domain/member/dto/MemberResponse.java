package com.sejong.project.onair.domain.member.dto;

import com.sejong.project.onair.domain.member.model.Member;
import com.sejong.project.onair.global.token.vo.AccessToken;

public class MemberResponse {
    public record LoginResponseDto(
            String memberName,
            String email,
            boolean isFirstLogin
    ){
        public static LoginResponseDto from(Member member){
            return new LoginResponseDto(
                    member.getMemberName(),
                    member.getEmail(),
                    member.isFirstLogin()
            );
        }
    }
}

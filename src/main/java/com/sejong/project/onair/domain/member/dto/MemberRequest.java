package com.sejong.project.onair.domain.member.dto;

public class MemberRequest {
    public record GoogleLoginDto(
            String code,
            String redirectUrl
    ){}
}

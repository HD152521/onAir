package com.sejong.project.onair.domain.member.dto;

import com.sejong.project.onair.domain.member.model.Member;
import lombok.Builder;

@Builder
public record MemberAuthContext(
        Long id,
        String name,
        String role,
        String email,
        String password
) {
    public static MemberAuthContext of(Member member) {
        return MemberAuthContext.builder()
                .email(member.getEmail())
                .name(member.getMemberName())
                .role(member.getRole().toString())
                .id(member.getId())
                .build();
    }
}
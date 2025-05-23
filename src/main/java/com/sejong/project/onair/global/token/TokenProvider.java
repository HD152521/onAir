package com.sejong.project.onair.global.token;


import com.sejong.project.onair.domain.member.model.Member;
import com.sejong.project.onair.global.token.vo.AccessToken;
import com.sejong.project.onair.global.token.vo.RefreshToken;
import lombok.RequiredArgsConstructor;

public interface TokenProvider {
    AccessToken generateAccessToken(Member member);

    RefreshToken generateRefreshToken(Member member);
}

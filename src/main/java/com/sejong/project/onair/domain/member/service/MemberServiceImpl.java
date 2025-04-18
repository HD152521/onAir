package com.sejong.project.onair.domain.member.service;

import com.sejong.project.onair.domain.member.Member;
import com.sejong.project.onair.domain.member.dto.MemberRequest;
import com.sejong.project.onair.domain.member.dto.MemberResponse;
import com.sejong.project.onair.domain.member.repository.MemberRepository;
import com.sejong.project.onair.global.Oauth.dto.GoogleTokenDto;
import com.sejong.project.onair.global.Oauth.dto.GoogleUserProfileDto;
import com.sejong.project.onair.global.Oauth.service.OAuth2UserAuthCodeServiceImpl;
import com.sejong.project.onair.global.token.JwtProvider;
import com.sejong.project.onair.global.token.vo.AccessToken;
import com.sejong.project.onair.global.token.vo.RefreshToken;
import com.sejong.project.onair.global.token.vo.TokenResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.sejong.project.onair.global.token.JwtProperties.*;

@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService{

    private static final Logger log = LoggerFactory.getLogger(MemberServiceImpl.class);
    private final OAuth2UserAuthCodeServiceImpl oauth2UserAuthCodeService;
    private final JwtProvider jwtProvider;
    private final MemberRepository memberRepository;

    public MemberResponse.LoginResponseDto googleLoginByToken(MemberRequest.GoogleLoginDto googleLoginDto, HttpServletResponse response){
        log.info("google Service들어옴");
        GoogleTokenDto googleToken = oauth2UserAuthCodeService.getGoogleAccessToken(googleLoginDto);
        log.info("service에서 accessToken:{}",googleToken.access_token());
        GoogleUserProfileDto googleUserProfile = oauth2UserAuthCodeService.getGoogleUserProfile(googleToken.access_token());
        log.info("로그인 성공");

        boolean isFirstLogin = false;
        Member member = memberRepository.findMemberByEmail(googleUserProfile.email());
        if(member == null){
            member = createMember(googleUserProfile.name(),googleUserProfile.email());
            log.info("처음 로그인 하는 회원");
            return MemberResponse.LoginResponseDto.from(member,getTokenResponse(response,member).accessToken());
        }
        //토큰 어떻게 넘어가고 인증하는지 보고 설정 추가하기
        return MemberResponse.LoginResponseDto.from(member,getTokenResponse(response,member).accessToken());
    }

    @Transactional
    public Member createMember(String username, String email) {

        log.info("new member create {} {}", username, email);
        Member member = Member.builder()
                .memberName(username)
                .email(email)
                .build();

        memberRepository.save(member);

        return member;
    }

    @NotNull
    private TokenResponse getTokenResponse(HttpServletResponse response, Member member) {

        AccessToken accessToken = jwtProvider.generateAccessToken(member);
        RefreshToken refreshToken = jwtProvider.generateRefreshToken(member);
        TokenResponse tokenResponse = TokenResponse.of(accessToken, refreshToken);
        response.addHeader(JWT_ACCESS_TOKEN_HEADER_NAME, JWT_ACCESS_TOKEN_TYPE + accessToken.token());

        Cookie refreshTokenCookie = new Cookie(JWT_REFRESH_TOKEN_COOKIE_NAME, refreshToken.token());
        refreshTokenCookie.setHttpOnly(true);
        refreshTokenCookie.setSecure(true);
        refreshTokenCookie.setMaxAge((int) REFRESH_TOKEN_EXPIRE_TIME);
        refreshTokenCookie.setPath("/"); //path로 지정된 곳에서만 쿠키데이터를 읽을 수 있음.
        response.addCookie(refreshTokenCookie);

        return tokenResponse;
    }

}

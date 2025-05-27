package com.sejong.project.onair.domain.member.service;

import com.sejong.project.onair.domain.file.dto.FileResponse;
import com.sejong.project.onair.domain.file.model.UploadFile;
import com.sejong.project.onair.domain.file.service.FileServiceImpl;
import com.sejong.project.onair.domain.member.dto.MemberDetails;
import com.sejong.project.onair.domain.member.model.Member;
import com.sejong.project.onair.domain.member.dto.MemberRequest;
import com.sejong.project.onair.domain.member.dto.MemberResponse;
import com.sejong.project.onair.domain.member.repository.MemberRepository;
import com.sejong.project.onair.global.Oauth.dto.GoogleTokenDto;
import com.sejong.project.onair.global.Oauth.dto.GoogleUserProfileDto;
import com.sejong.project.onair.global.Oauth.service.OAuth2UserAuthCodeServiceImpl;
import com.sejong.project.onair.global.exception.BaseException;
import com.sejong.project.onair.global.exception.BaseResponse;
import com.sejong.project.onair.global.exception.codes.ErrorCode;
import com.sejong.project.onair.global.token.JwtProvider;
import com.sejong.project.onair.global.token.vo.AccessToken;
import com.sejong.project.onair.global.token.vo.RefreshToken;
import com.sejong.project.onair.global.token.vo.TokenResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.sejong.project.onair.global.token.JwtProperties.*;

@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService{

    private static final Logger log = LoggerFactory.getLogger(MemberServiceImpl.class);
    private final OAuth2UserAuthCodeServiceImpl oauth2UserAuthCodeService;
    private final JwtProvider jwtProvider;
    private final MemberRepository memberRepository;
    private final FileServiceImpl fileService;

    public MemberResponse.LoginResponseDto googleLoginByToken(MemberRequest.GoogleLoginDto googleLoginDto, HttpServletResponse response){
        log.info("google Service들어옴");
        GoogleTokenDto googleToken = oauth2UserAuthCodeService.getGoogleAccessToken(googleLoginDto);
        GoogleUserProfileDto googleUserProfile = oauth2UserAuthCodeService.getGoogleUserProfile(googleToken.access_token());
        log.info("로그인 성공 사용자:{} 이메일:{} 사진:{}",googleUserProfile.name(),googleUserProfile.email(),googleUserProfile.picture());

        Member member = getMember(googleUserProfile.email());
        if(member == null){
            String imgUrl = googleUserProfile.picture();
            member = createMember(googleUserProfile.name(),googleUserProfile.email(),imgUrl);
            log.info("처음 로그인 하는 회원");
        }

        //todo 반환 로직 바꾸기
        TokenResponse tokenResponse = getTokenResponse(response,member);
        addTokenCookies(response,tokenResponse);

        return MemberResponse.LoginResponseDto.from(member);
    }

    public MemberResponse.LoginResponseDto testLogin(HttpServletResponse response){
        Member testUser = getMember("onAir@gmail.com");

        if(testUser==null){
            log.info("user널임");
            testUser= createMember("onAir","onAir@gmail.com",null);
        }

        TokenResponse tokenResponse = getTokenResponse(response,testUser);
        addTokenCookies(response,tokenResponse);

        return MemberResponse.LoginResponseDto.from(testUser);
    }

    private void addTokenCookies(HttpServletResponse response, TokenResponse tokenResponse) {
        // Access Token 쿠키
        ResponseCookie accessCookie = ResponseCookie.from("ACCESS_TOKEN", tokenResponse.accessToken().token())
                .httpOnly(true)            // JS 접근 차단
                .secure(true)              // HTTPS 전용
                .path("/")                 // 전체 경로에 대해 전송
                .maxAge(ACCESS_TOKEN_EXPIRE_TIME) // 만료 시간
                .sameSite("none")        // CSRF 방어
                .build();

        ResponseCookie refreshCookie = ResponseCookie.from("REFRESH_TOKEN", tokenResponse.accessToken().token())
                .httpOnly(true)
                .secure(true)
                .path("/auth/refresh")      // 리프레시 전용 엔드포인트에만 전송
                .maxAge(REFRESH_TOKEN_EXPIRE_TIME)
                .sameSite("none")
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, accessCookie.toString());
        response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());
    }


    @Transactional
    public Member createMember(String username, String email,String img) {
        log.info("new member create {} {}", username, email);
        Member member = Member.builder()
                .memberName(username)
                .email(email)
                .imgUrl(img)
                .build();

        memberRepository.save(member);

        return member;
    }

    @NotNull
    private TokenResponse getTokenResponse(HttpServletResponse response, Member member) {

        AccessToken accessToken = jwtProvider.generateAccessToken(member);
        RefreshToken refreshToken = jwtProvider.generateRefreshToken(member);
        TokenResponse tokenResponse = TokenResponse.of(accessToken, refreshToken);
        return tokenResponse;
    }

    public ResponseEntity<BaseResponse<?>> updateRefreshToken(HttpServletRequest request, HttpServletResponse response){
        // 1) REFRESH_TOKEN 쿠키에서 값 꺼내기
        String refreshToken = jwtProvider.resolveRefreshToken(request, JWT_REFRESH_TOKEN_COOKIE_NAME);
        if (refreshToken == null) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(BaseResponse.onFailure("AUTH002", "리프레시 토큰이 없습니다", null));
        }

        // 2) 리프레시 토큰 유효성 검증
        if (!jwtProvider.validateRefreshToken(refreshToken)) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(BaseResponse.onFailure("AUTH003", "리프레시 토큰이 유효하지 않습니다", null));
        }

        // 3) 토큰에서 Authentication 정보 추출
        Authentication authentication = jwtProvider.getAuthentication(refreshToken);

        // 4) 새 Access Token 생성
        String newAccessToken = jwtProvider.createAccessToken(authentication);

        // 5) HttpOnly + Secure 쿠키로 세팅
        ResponseCookie cookie = ResponseCookie.from(JWT_ACCESS_TOKEN_COOKIE_NAME, newAccessToken)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(ACCESS_TOKEN_EXPIRE_TIME)
                .sameSite("none")
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
        return ResponseEntity.ok(BaseResponse.onSuccess("token 발급 완료"));
    }

    public Member getMember(MemberDetails memberDetails){
        try {
            String email = memberDetails.getUsername();
            return getMember(email);
        } catch (Exception e){
            log.warn(e.getMessage());
            throw new BaseException(ErrorCode.MEMBER_NOT_FOUND);
        }
    }

    public Member getMember(String email){
        try {
            return memberRepository.findMemberByEmail(email)
                    .orElseThrow(() -> new BaseException(ErrorCode.MEMBER_NOT_FOUND));
        } catch (BaseException e){
            log.warn("멤버 조회 실패: {}", e.getMessage());
            return null;
        } catch (Exception e){
            log.error("알 수 없는 에러 발생: {}", e.getMessage(), e);
            return null;
        }
    }

    public MemberResponse.MemberProfileDto getMemberProfile(MemberDetails memberDetails){
        log.info("{}",memberDetails.getUsername());
        Member member = getMember(memberDetails);
        List<FileResponse.FileLogDto> logs = fileService.getUploadLog(member);
        MemberResponse.MemberProfileDto profile = MemberResponse.MemberProfileDto.from(member,logs);
        log.info("member profile반환");
        return  profile;
    }

}

package com.sejong.project.onair.global.Oauth.service;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.sejong.project.onair.domain.member.dto.MemberRequest;
import com.sejong.project.onair.global.Oauth.dto.GoogleTokenDto;
import com.sejong.project.onair.global.Oauth.dto.GoogleUserProfileDto;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class OAuth2UserAuthCodeServiceImpl {
    private static final Logger log = LoggerFactory.getLogger(OAuth2UserAuthCodeServiceImpl.class);
    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    String clientId;
    @Value("${spring.security.oauth2.client.registration.google.client-secret}")
    String clientSecret;

    public GoogleTokenDto getGoogleAccessToken(MemberRequest.GoogleLoginDto googleLoginDto) {
        log.info("getGoogleAccessToken");
        String requestUrl = "https://oauth2.googleapis.com/token";

        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/x-www-form-urlencoded");

        String code = googleLoginDto.code();
        String redirectUrl = googleLoginDto.redirectUrl();

        log.info("code: {}",code);
        log.info("redirectUrl: {}",redirectUrl);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();

        params.add("code", code);
        params.add("client_id", clientId);
        params.add("client_secret", clientSecret);
        params.add("redirect_uri", redirectUrl);
        params.add("grant_type", "authorization_code");
        params.add("scope", "email profile openid");
        log.info("구글로 post 보냄");

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);
        ResponseEntity<String> response = null;
        try {
            log.info("구글 토큰 교환 요청 보내는 중...");
            response = restTemplate.postForEntity(requestUrl, request, String.class);
            log.info("구글 응답 받음 - 상태: {}, 본문: {}",
                    response.getStatusCode(), response.getBody());
        } catch (HttpClientErrorException he) {
            log.error("구글 HTTP 에러 - 상태: {}, 본문: {}",
                    he.getStatusCode(), he.getResponseBodyAsString());
        } catch (Exception e) {
            log.error("구글 토큰 교환 중 예외 발생", e);
        }

        log.info("구글에서 Response값 받음");

        log.info("{}",response.getStatusCode());
        log.info("{}",response.getBody());

        GoogleTokenDto result = null;

        if (response.getStatusCode() == HttpStatus.OK) {
            try {
                String responseBody = response.getBody();
                ObjectMapper objectMapper = new ObjectMapper();
                result = objectMapper.readValue(responseBody, GoogleTokenDto.class);
                log.info("구글 로그인 성공");
                log.info("넘어가기전 accessToken:{}",result.access_token());
                return result;
            } catch (Exception e) {
                log.warn("구글 로그인 실패");
            }
        }else log.warn("getGoogleAccessToken HTTP상태가 안됨");
        return result;
    }

    public GoogleUserProfileDto getGoogleUserProfile(String accessToken){
        log.info("accessToken:{}",accessToken);
        String requestUrl = "https://openidconnect.googleapis.com/v1/userinfo";

        RestTemplate restTemplate = new RestTemplate();
        log.info("response보내기 전임");

        HttpHeaders headers = new HttpHeaders();

        headers.setBearerAuth(accessToken);
        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<String> response=null;
        try {
            response = restTemplate.exchange(
                    requestUrl,
                    HttpMethod.GET,
                    entity,
                    String.class
            );
        }catch(Exception e){
            log.warn("getGoogleUserProfile에서 안됨");
            log.warn(e.getMessage());
        }

        log.info("getGooleUserProfile 구현 완료, 가져옴");
        GoogleUserProfileDto result=null;

        if (response.getStatusCode() == HttpStatus.OK) {
            try{
                log.info("{}",response.getBody());
                String responseBody = response.getBody();
                ObjectMapper objectMapper = new ObjectMapper();
                result = objectMapper.readValue(responseBody, GoogleUserProfileDto.class);
                return result;
            }catch(Exception e){
                log.warn("구글 프로필을 가져올 수가 없음");
            }

        }else log.warn("getGoogleUserProflie HTTP상태가 안됨");
        return result;
    }

}

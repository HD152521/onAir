package com.sejong.project.onair.global.Oauth.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
public class PrincipalOauth2UserService extends DefaultOAuth2UserService {

    private static final Logger log = LoggerFactory.getLogger(PrincipalOauth2UserService.class);

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        log.info("getClientRegistration: "+userRequest.getClientRegistration());
        log.info("getAccessToken:"+userRequest.getAccessToken());
        log.info("getAttributes:"+super.loadUser(userRequest).getAttributes());


        return super.loadUser(userRequest);
    }

}

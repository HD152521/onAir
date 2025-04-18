package com.sejong.project.onair.global.config;

import com.sejong.project.onair.global.Oauth.service.PrincipalOauth2UserService;
import com.sejong.project.onair.global.filter.AuthenticationTokenFilter;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private static final Logger log = LoggerFactory.getLogger(SecurityConfig.class);
    private final PrincipalOauth2UserService principalOauth2UserService;
    private final SecurityContextRepositoryImpl securityContextRepository;

    private final AuthenticationTokenFilter authenticationTokenFilter;

}

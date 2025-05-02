package com.sejong.project.onair.global.config;

import com.sejong.project.onair.global.Oauth.service.PrincipalOauth2UserService;
import com.sejong.project.onair.global.filter.AuthenticationTokenFilter;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.LogoutConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.CorsUtils;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private static final Logger log = LoggerFactory.getLogger(SecurityConfig.class);
    private final PrincipalOauth2UserService principalOauth2UserService;
    private final SecurityContextRepositoryImpl securityContextRepository;

    private final AuthenticationTokenFilter authenticationTokenFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{

        // 접근 권한 설정
        http
                .cors(cors -> cors
                        .configurationSource(corsConfigurationSource()))
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests((auth) -> auth
                                .requestMatchers(HttpMethod.OPTIONS,"/**/*").permitAll() //preflight요청 허용함.
                                .requestMatchers(CorsUtils::isPreFlightRequest).permitAll() //preflight요청 허용함.
                                .requestMatchers("/login/**", "/home/**","/test").permitAll()
                                .requestMatchers( "/v3/api-docs",
                                        "/v3/api-docs/**",
                                        "/swagger-ui.html",
                                        "/swagger-ui/**",
                                        "/swagger-resources/**",
                                        "/api-test"
                                        ,"/api-test").permitAll()
                        //fixme 임시로 허용 해주는거 나중에 지우기 (test위해서임)
                        .requestMatchers("/file/**").permitAll()
                                .anyRequest().authenticated()
                )
                .securityContext((securityContext) -> {
                    securityContext
                            .securityContextRepository(securityContextRepository.securityContextRepository())
                            .requireExplicitSave(true);
                })
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .formLogin(AbstractHttpConfigurer::disable)
                .logout(LogoutConfigurer::permitAll)
                .addFilterBefore(authenticationTokenFilter, UsernamePasswordAuthenticationFilter.class);


        // OAuth 2.0 로그인 방식 설정
        http
                .oauth2Login((auth) -> auth
                        .userInfoEndpoint(userInfoEndpoint -> userInfoEndpoint
                                .userService(principalOauth2UserService))
                        .permitAll());

        return http.build();
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder(){
        return new BCryptPasswordEncoder();
    }


    //cors추가한내용
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();

        configuration.addAllowedOriginPattern("http://localhost:5173");
        configuration.addAllowedHeader("*");
        configuration.addAllowedMethod("*");
        configuration.addExposedHeader("Authorization");
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

}

package com.sejong.project.onair.global.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sejong.project.onair.domain.member.dto.MemberDetails;
import com.sejong.project.onair.domain.member.service.MemberDetailsService;
import com.sejong.project.onair.global.exception.BaseException;
import com.sejong.project.onair.global.exception.BaseResponse;
import com.sejong.project.onair.global.exception.codes.ErrorCode;
import com.sejong.project.onair.global.token.JwtProvider;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

import static com.sejong.project.onair.global.token.JwtProperties.JWT_ACCESS_TOKEN_HEADER_NAME;
import static com.sejong.project.onair.global.token.JwtProperties.JWT_ACCESS_TOKEN_TYPE;

@RequiredArgsConstructor
@Slf4j
@Component
public class AuthenticationTokenFilter extends OncePerRequestFilter {

    private final JwtProvider jwtProvider;
    private final MemberDetailsService memberDetailsService;
    private final SecurityContextRepository securityContextRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        //path보고 안봐도 되는거 거르기
        String path = request.getServletPath();
        log.info("진입 path:{}",path);

        final String[] WHITELIST_PATHS = {
                "/observatory/airkorea",
                "/observatory/get/loc",
                "/observatory/data",
                "/compWeather/get",
                "/file/readData",
                "/pred",
                "/member/login/google",
                "/member/login/testuser",
                "/swagger",
                "/test",
                "/health",
                "/api-test",
                "/swagger-ui",
                "/v3"
//                "/" //임시용
        };

        for (String allowed : WHITELIST_PATHS) {
            if (path.startsWith(allowed)) {
                filterChain.doFilter(request, response);
                return;
            }
        }

        log.info(request.getRequestURI());
        log.info("멤버 인증 시작!!");
        String token = jwtProvider.resolvAccesseToken(request);
        log.info("최종 token:{}",token);

        if (token == null) {
            sendErrorResponse(response, ErrorCode.EMPTY_TOKEN_PROVIDED);
            return;
        }

        try {
            // 3) 토큰 유효성 검증 (만료도 여기서 체크됨)
            if (!jwtProvider.validateToken(token)) {
                log.info("만료임");
                sendErrorResponse(response, ErrorCode.EXPIRED_ACCESS_TOKEN);
                return;
            }

            //note security context 이번만 사용 Stateless방식
            Authentication authentication = jwtProvider.getAuthentication(token);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            filterChain.doFilter(request, response);

            //note 컨텍스트를 저장소에 저장 stateful 세션방식
//            SecurityContext context = SecurityContextHolder.createEmptyContext();
//            context.setAuthentication(authentication);
//            SecurityContextHolder.setContext(context);
//            securityContextRepository.saveContext(context, request, response);
//            filterChain.doFilter(request, response);

        } catch (io.jsonwebtoken.ExpiredJwtException ex) {
            // 만료 예외를 별도로 잡아서도 처리 가능
            log.warn("authentication에서 오류발생");
            sendErrorResponse(response, ErrorCode.EXPIRED_ACCESS_TOKEN);
        }


    }


    private void sendErrorResponse(HttpServletResponse response, ErrorCode errorCode) throws IOException {
        // 1) HTTP 상태 및 인코딩/타입 설정
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json; charset=UTF-8");
        response.setCharacterEncoding("UTF-8");

        // 2) BaseResponse 형식으로 에러 객체 생성
        BaseResponse<Object> errorBody = BaseResponse.onFailure(errorCode.getCode(), errorCode.getMessage(), null);

        // 3) JSON 직렬화 후 쓰기
        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(errorBody);
        response.getWriter().write(json);
    }
}

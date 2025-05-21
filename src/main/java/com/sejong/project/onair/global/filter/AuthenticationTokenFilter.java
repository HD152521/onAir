package com.sejong.project.onair.global.filter;

import com.sejong.project.onair.domain.member.dto.MemberDetails;
import com.sejong.project.onair.domain.member.service.MemberDetailsService;
import com.sejong.project.onair.global.exception.BaseException;
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
                "/member",
                "/signup",
                "/swagger",
                "/observatory",
                "/test",
                "/health"
        };

        for (String allowed : WHITELIST_PATHS) {
            if (path.startsWith(allowed)) {
                filterChain.doFilter(request, response);
                return;
            }
        }

        log.info(request.getRequestURI());

        log.info("멤버 인증 시작!!");
        String token = resolveToken(request);
        if (token == null) {
            sendErrorResponse(response, "토큰이 없습니다");
            return;
        }

        try {
            // 3) 토큰 유효성 검증 (만료도 여기서 체크됨)
            if (!jwtProvider.validateToken(token)) {
                sendErrorResponse(response, "만료입니다");
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
            sendErrorResponse(response, "만료입니다");
        }


    }

    private String resolveToken(HttpServletRequest httpServletRequest) {
        // 쿠키에서 꺼내기
        if (httpServletRequest.getCookies() != null) {
            for (Cookie cookie : httpServletRequest.getCookies()) {
                if ("ACCESS_TOKEN".equals(cookie.getName())) {
                    log.info("cookieValue:{}",cookie.getValue());
                    return cookie.getValue();
                }
            }
        }
        // 헤더에서 꺼내기
        String bearer = httpServletRequest.getHeader(HttpHeaders.AUTHORIZATION);
        if (StringUtils.hasText(bearer) && bearer.startsWith("Bearer ")) {
            log.info("Header:{}",bearer);
            return bearer.substring(7);
        }
        return null;


        //note 밑으로 원래코드
//        String authorization = httpServletRequest.getHeader("Authorization");
//        if (authorization == null) {
//            throw new BaseException(ErrorCode.EMPTY_TOKEN_PROVIDED);
//        }
//        if (authorization.startsWith("Bearer ")) { // Bearer 기반 토큰 인증을 함
//            return authorization.substring(7);
//        }
//
//        throw new BaseException(ErrorCode.EMPTY_TOKEN_PROVIDED);
    }


    private void sendErrorResponse(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write("{\"message\":\"" + message + "\"}");
    }
}

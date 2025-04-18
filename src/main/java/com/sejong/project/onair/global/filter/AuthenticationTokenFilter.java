package com.sejong.project.onair.global.filter;

import com.sejong.project.onair.domain.member.dto.MemberDetails;
import com.sejong.project.onair.domain.member.service.MemberDetailsService;
import com.sejong.project.onair.global.exception.BaseException;
import com.sejong.project.onair.global.exception.codes.ErrorCode;
import com.sejong.project.onair.global.token.JwtProvider;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.stereotype.Component;
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
        if (path.startsWith("/login") || path.startsWith("/signup") || path.startsWith("/swagger")) {
            filterChain.doFilter(request, response);
            return;
        }

        log.info(request.getRequestURI());

        String jwtHeader = request.getHeader("Authorization");
        if (jwtHeader == null || !jwtHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        log.info("멤버 인증 시작!!");

        String token = resolveToken(request);

        String aud = jwtProvider.parseAudience(token); // 토큰 Aud에 Member email을 기록하고 있음

        MemberDetails userDetails = memberDetailsService.loadUserByUsername(aud); // memberId를 기반으로 조회

        Authentication authentication
                = new UsernamePasswordAuthenticationToken(
                userDetails,
                null,
                userDetails.getAuthorities());


        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);
        securityContextRepository.saveContext(context, request, response);

        filterChain.doFilter(request, response);
    }

    private String resolveToken(HttpServletRequest httpServletRequest) {
        String authorization = httpServletRequest.getHeader("Authorization");
        if (authorization == null) {
            throw new BaseException(ErrorCode.EMPTY_TOKEN_PROVIDED);
        }
        if (authorization.startsWith("Bearer ")) { // Bearer 기반 토큰 인증을 함
            return authorization.substring(7);
        }

        throw new BaseException(ErrorCode.EMPTY_TOKEN_PROVIDED);
    }
}

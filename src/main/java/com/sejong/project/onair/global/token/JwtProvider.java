package com.sejong.project.onair.global.token;


import com.sejong.project.onair.domain.member.dto.MemberDetails;
import com.sejong.project.onair.domain.member.model.Member;
import com.sejong.project.onair.domain.member.service.MemberDetailsService;
import com.sejong.project.onair.global.exception.BaseException;
import com.sejong.project.onair.global.exception.codes.ErrorCode;
import com.sejong.project.onair.global.token.vo.AccessToken;
import com.sejong.project.onair.global.token.vo.RefreshToken;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Base64;
import java.util.Date;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.JwtParser;
import org.springframework.util.StringUtils;

import static com.sejong.project.onair.global.token.JwtProperties.*;
import static com.sejong.project.onair.global.token.JwtProperties.JWT_ACCESS_TOKEN_TYPE;


@Getter
@Component
@Slf4j
public class JwtProvider implements TokenProvider {

    private final SecretKey SECRET_KEY;
    private final String ISS = "github.com/SophistRing";
    private final MemberDetailsService memberDetailsService;
    private final JwtParser jwtParser;


    public JwtProvider(
            @Value("${jwt.secret}") String SECRET_KEY,
            MemberDetailsService memberDetailsService
    ) {
        byte[] keyBytes = Base64.getDecoder()
                .decode(SECRET_KEY.getBytes(StandardCharsets.UTF_8));
        this.SECRET_KEY = new SecretKeySpec(keyBytes, "HmacSHA256");
        this.memberDetailsService = memberDetailsService;
        //fixme jwtParser수정하기
        this.jwtParser = Jwts
                .parser()
                .setSigningKey(this.SECRET_KEY)
                .build();
    }


    public AccessToken generateAccessToken(Member member) {
        if (member.getEmail() == null || member.getEmail().isBlank()) {
            return AccessToken.of("");
        }
        return this.generateAccessToken(member.getEmail());
    }

    private AccessToken generateAccessToken(String email) {

        byte[] keyBytes = Base64.getDecoder().decode(SECRET_KEY.getEncoded());  // Base64 디코딩
        Key key = Keys.hmacShaKeyFor(keyBytes);

        String token = Jwts.builder()
                .claim("type", "access")
                .issuer(ISS)
                .audience().add(email).and()
                .issuedAt(new Date())
                .expiration(new Date(new Date().getTime() + ACCESS_TOKEN_EXPIRE_TIME))
                .signWith(SECRET_KEY)
                .compact();

        log.info("[generateAccessToken] {}", token);
        return AccessToken.of(token);
    }

    public RefreshToken generateRefreshToken(Member member) {
        if (member.getEmail() == null || member.getEmail().isBlank()) {
            return RefreshToken.of("");
        }
        return this.generateRefreshToken(member.getEmail());
    }

    private RefreshToken generateRefreshToken(String email) {

        byte[] keyBytes = Base64.getDecoder().decode(SECRET_KEY.getEncoded());  // Base64 디코딩
        Key key = Keys.hmacShaKeyFor(keyBytes);

        String token = Jwts.builder()
                .claim("type", "refresh")
                .issuer(ISS)
                .audience().add(email).and()
                .issuedAt(new Date())
                .expiration(new Date(new Date().getTime() + REFRESH_TOKEN_EXPIRE_TIME))
                .signWith(SECRET_KEY)
                .compact();

        log.info("[generateRefreshToken] {}", token);
        return RefreshToken.of(token);
    }

    public String parseAudience(String token) {
        try {
            Jws<Claims> claims = Jwts.parser()
                    .verifyWith(SECRET_KEY)
                    .build()
                    .parseSignedClaims(token);

            if (claims.getPayload()
                    .getExpiration()
                    .before(new Date())) {
                throw new BaseException(ErrorCode.EXPIRED_ACCESS_TOKEN);
            }

            String aud = claims.getPayload()
                    .getAudience()
                    .iterator()
                    .next();

            return aud;
        } catch (JwtException | IllegalArgumentException e) {
            log.warn("[parseAudience] {} :{}", ErrorCode.INVALID_TOKEN, token);
            throw new BaseException(ErrorCode.INVALID_TOKEN);
        } catch (BaseException e) {
            log.warn("[parseAudience] {} :{}", ErrorCode.EXPIRED_ACCESS_TOKEN, token);
            throw new BaseException(ErrorCode.EXPIRED_ACCESS_TOKEN);
        }
    }

    public boolean validateToken(String accessToken) {
        log.info("validToken 진입");
        try {
            // 서명·만료 검증을 동시에 수행
            Jws<Claims> jws = jwtParser.parseClaimsJws(accessToken);

            Date expiration = jws.getBody().getExpiration();
            log.info("validToken: {}", expiration.after(new Date()));
            log.info("expiration: {}", expiration);

            return expiration.after(new Date());
        } catch (ExpiredJwtException e) {
            log.warn("토큰 만료", e);
            return false;
        } catch (JwtException | IllegalArgumentException e) {
            log.warn("토큰 검증 오류", e);
            return false;
        }
//        if (accessToken == null) {
//            return false;
//        }
//        log.info("validToken 진입(2)");
//        try {
//            Jws<Claims> claims = Jwts.parser()
//                    .verifyWith(SECRET_KEY)
//                    .build()
//                    .parseSignedClaims(accessToken);
//
//            log.info("validToken:{}",claims.getPayload()
//                    .getExpiration()
//                    .after(new Date()));
//            log.info("valid(2):{}",claims.getPayload()
//                    .getExpiration());
//
//            return claims.getPayload()
//                    .getExpiration()
//                    .after(new Date());
//        }
//        catch (Exception e) {
//            return false;
//        }
    }

    public Authentication getAuthentication(String token){
        String aud = parseAudience(token); // 토큰 Aud에 Member email을 기록하고 있음
        MemberDetails userDetails = memberDetailsService.loadUserByUsername(aud); // memberId를 기반으로 조회
        Authentication authentication
                = new UsernamePasswordAuthenticationToken(
                userDetails,
                null,
                userDetails.getAuthorities());
        return authentication;
    }

    public boolean validateRefreshToken(String token) {
        try {
            jwtParser.parseClaimsJws(token);  // 만료나 서명 오류 시 예외 발생
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    public String createAccessToken(Authentication auth) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + ACCESS_TOKEN_EXPIRE_TIME * 1000);
        return Jwts.builder()
                .setSubject(auth.getName())
                .claim("roles", auth.getAuthorities())
                .setIssuedAt(now)
                .setExpiration(expiry)
                .signWith(SECRET_KEY)      // Keys.hmacShaKeyFor 방식으로 세팅된 키
                .compact();
    }

    public String resolveRefreshToken(HttpServletRequest request, String name){
        if (request.getCookies() == null) return null;
        for (Cookie cookie : request.getCookies()) {
            log.info("cookie종류 보기 : {}",cookie.getName());
            if (name.equals(cookie.getName())) {
                log.info("refreshToken:{}",cookie.getValue());
                return cookie.getValue();
            }
        }
        log.warn("refresh토큰 못찾음");
        return null;
    }

    public String resolvAccesseToken(HttpServletRequest request) {

        // 쿠키에서 꺼내기
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("ACCESS_TOKEN".equals(cookie.getName())) {
                    log.info("cookieValue:{}",cookie.getValue());
                    return cookie.getValue();
                }
            }
        }
        // 헤더에서 꺼내기
        String bearer = request.getHeader(JWT_ACCESS_TOKEN_HEADER_NAME);
        if (StringUtils.hasText(bearer) && bearer.startsWith(JWT_ACCESS_TOKEN_TYPE)) {
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
}


package com.sejong.project.onair.global.token;


import com.sejong.project.onair.domain.member.dto.MemberDetails;
import com.sejong.project.onair.domain.member.model.Member;
import com.sejong.project.onair.domain.member.service.MemberDetailsService;
import com.sejong.project.onair.global.exception.BaseException;
import com.sejong.project.onair.global.exception.codes.ErrorCode;
import com.sejong.project.onair.global.token.vo.AccessToken;
import com.sejong.project.onair.global.token.vo.RefreshToken;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Base64;
import java.util.Date;

import static com.sejong.project.onair.global.token.JwtProperties.ACCESS_TOKEN_EXPIRE_TIME;
import static com.sejong.project.onair.global.token.JwtProperties.REFRESH_TOKEN_EXPIRE_TIME;


@Getter
@Component
@Slf4j
public class JwtProvider implements TokenProvider {

    private final SecretKey SECRET_KEY;
    private final String ISS = "github.com/SophistRing";
    private final MemberDetailsService memberDetailsService;

    public JwtProvider(
            @Value("${jwt.secret}") String SECRET_KEY,
            MemberDetailsService memberDetailsService
    ) {
        byte[] keyBytes = Base64.getDecoder()
                .decode(SECRET_KEY.getBytes(StandardCharsets.UTF_8));
        this.SECRET_KEY = new SecretKeySpec(keyBytes, "HmacSHA256");
        this.memberDetailsService = memberDetailsService;
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
        if (accessToken == null) {
            return false;
        }
        try {
            Jws<Claims> claims = Jwts.parser()
                    .verifyWith(SECRET_KEY)
                    .build()
                    .parseSignedClaims(accessToken);
            return claims.getPayload()
                    .getExpiration()
                    .after(new Date());
        }
        catch (Exception e) {
            return false;
        }
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
}


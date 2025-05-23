package com.sejong.project.onair.global.token.vo;

public record RefreshToken(
        String token
) {
    public static RefreshToken of(String token) {
        return new RefreshToken(token);
    }
}



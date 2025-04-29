package com.sejong.project.onair.global.Oauth.dto;

public record GoogleTokenDto(
        String access_token,
        int expires_in,
        String scope,
        String token_type,
        String id_token
) {
}

package com.sejong.project.onair.global.Oauth.dto;

public record GoogleUserProfileDto (
        String sub,
        String name,
        String given_name,
        String family_name,
        String picture,
        String email,
        boolean email_verified
){}


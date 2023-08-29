package com.newyearletter.newyearletter.domain.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AuthLogoutRequest {

    private String accessToken;
    private String refreshToken;
}

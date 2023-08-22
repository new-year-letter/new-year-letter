package com.newyearletter.newyearletter.domain.dto.user;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserLogoutRequest {

    private String accessToken;
    private String refreshToken;
}

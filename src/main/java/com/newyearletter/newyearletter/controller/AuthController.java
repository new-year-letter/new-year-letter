package com.newyearletter.newyearletter.controller;

import com.newyearletter.newyearletter.domain.dto.Response;
import com.newyearletter.newyearletter.domain.dto.user.*;
import com.newyearletter.newyearletter.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth/token")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final UserService userService;

    /**
     * 로그인 페이지
     */
    @PostMapping()
    public Response<UserLoginResponse> login(@RequestBody UserLoginRequest request){
        UserLoginResponse response = userService.login(request.getUserID(), request.getPassword());
        return Response.success(response);
    }


    /**
     * 로그아웃 페이지
     */
    @DeleteMapping()
    public void logout(@RequestBody UserLogoutRequest request){
        //logout logic
        // accessToken, refreshToken을 받고 삭제
    }

    /**
     * token 재발행
     */
    @PostMapping("/access")
    public void accessToken(@RequestBody RefreshTokenRequest request, Authentication authentication){
        //access token logic
        // refreshToken을 받고 access Token 재발행
    }


}

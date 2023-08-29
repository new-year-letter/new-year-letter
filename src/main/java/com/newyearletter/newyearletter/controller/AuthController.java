package com.newyearletter.newyearletter.controller;

import com.newyearletter.newyearletter.domain.dto.Response;
import com.newyearletter.newyearletter.domain.dto.auth.AuthLogoutResponse;
import com.newyearletter.newyearletter.domain.dto.auth.AuthReissueRequest;
import com.newyearletter.newyearletter.domain.dto.auth.AuthReissueResponse;
import com.newyearletter.newyearletter.domain.dto.user.*;
import com.newyearletter.newyearletter.exception.AppException;
import com.newyearletter.newyearletter.exception.ErrorCode;
import com.newyearletter.newyearletter.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth/token")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final AuthService authService;

    /**
     * 로그인 페이지
     */
    @PostMapping()
    public Response<UserLoginResponse> login(@RequestBody UserLoginRequest request){
        UserLoginResponse response = authService.login(request.getUserID(), request.getPassword());
        return Response.success(response);
    }


    /**
     * 로그아웃 페이지
     */
    @DeleteMapping()
   public Response<AuthLogoutResponse> logout(Authentication authentication){
        //logout logic
        String userName = null;
        try{
            userName = authentication.getName();
        } catch (Exception e){
            throw new AppException(ErrorCode.INVALID_TOKEN, ErrorCode.INVALID_TOKEN.getMessage());
        }
        // refreshToken을 받고 삭제
        // accessToken 블랙리스트 처리
        AuthLogoutResponse response = authService.logout(userName);
        return Response.success(response);
    }


    /**
     * token 재발행
     */
    @PostMapping("/access")
    public Response<AuthReissueResponse> reissue(@RequestBody AuthReissueRequest request){
        //access token logic
        // refreshToken을 받고 access Token 재발행
        AuthReissueResponse response = authService.reissue(request);
        return Response.success(response);
    }


}

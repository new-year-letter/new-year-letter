package com.newyearletter.newyearletter.controller;

import com.newyearletter.newyearletter.domain.dto.Response;
import com.newyearletter.newyearletter.domain.dto.auth.AuthLogoutRequest;
import com.newyearletter.newyearletter.domain.dto.auth.AuthLogoutResponse;
import com.newyearletter.newyearletter.domain.dto.auth.RefreshTokenRequest;
import com.newyearletter.newyearletter.domain.dto.user.*;
import com.newyearletter.newyearletter.exception.AppException;
import com.newyearletter.newyearletter.exception.ErrorCode;
import com.newyearletter.newyearletter.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
//        public String logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication){
        //logout logic
        String userName = null;
        try{
            userName = authentication.getName();
//            log.info("authentication.getName:{}",userName);
        } catch (Exception e){
            throw new AppException(ErrorCode.INVALID_TOKEN, ErrorCode.INVALID_TOKEN.getMessage());
        }
//        // accessToken, refreshToken을 받고 삭제
        AuthLogoutResponse response = authService.logout(userName);
        return Response.success(response);
//        return "Success";
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

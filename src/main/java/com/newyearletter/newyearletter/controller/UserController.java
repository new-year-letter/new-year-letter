package com.newyearletter.newyearletter.controller;

import com.newyearletter.newyearletter.domain.dto.*;
import com.newyearletter.newyearletter.domain.dto.rabbit.RabbitMyPageResponse;
import com.newyearletter.newyearletter.domain.dto.rabbit.RabbitResponse;
import com.newyearletter.newyearletter.domain.dto.user.*;
import com.newyearletter.newyearletter.exception.AppException;
import com.newyearletter.newyearletter.exception.ErrorCode;
import com.newyearletter.newyearletter.service.RabbitService;
import com.newyearletter.newyearletter.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;
    /**
     * 회원가입 페이지
     */
    @PostMapping()
    public Response<UserJoinResponse> join(@RequestBody UserJoinRequest request){
        UserDto user = userService.join(request);
        return Response.success(new UserJoinResponse(user.getNickName(), user.getUuid()));
    }



    /**
     * 마이페이지 조회
     */
    @GetMapping("/me")
    public Response<RabbitMyPageResponse> myPage(Authentication authentication){

        String userID = null;
        try {
            userID = authentication.getName();
        } catch (Exception e) {
            throw new AppException(ErrorCode.INVALID_TOKEN, ErrorCode.INVALID_TOKEN.getMessage());
        }
        RabbitMyPageResponse mypageResponse = userService.mypage(userID);
        return Response.success(mypageResponse);
    }

    /**
     * 친구 페이지 조회
     */
    @GetMapping("/{uuid}")
    public Response<RabbitResponse> friendPage(@PathVariable String uuid){
        RabbitResponse rabbitResponse = userService.friendPage(uuid);
        return Response.success(rabbitResponse);
    }
}

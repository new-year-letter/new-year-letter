package com.newyearletter.newyearletter.controller;

import com.newyearletter.newyearletter.domain.dto.*;
import com.newyearletter.newyearletter.domain.dto.rabbit.RabbitCustomDto;
import com.newyearletter.newyearletter.domain.dto.rabbit.RabbitCustomResponse;
import com.newyearletter.newyearletter.domain.dto.rabbit.RabbitMyPageResponse;
import com.newyearletter.newyearletter.domain.dto.rabbit.RabbitResponse;
import com.newyearletter.newyearletter.exception.AppException;
import com.newyearletter.newyearletter.exception.ErrorCode;
import com.newyearletter.newyearletter.service.RabbitService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/rabbit")
@RequiredArgsConstructor
public class RabbitController {
    private final RabbitService rabbitService;

    /**
     * 커스텀 페이지 조회
     */
    @GetMapping("/mypage/{uuid}/custom")
    public Response<RabbitCustomDto> getCustom(@PathVariable String uuid, Authentication authentication){
        String userID = authentication.getName();
        RabbitCustomDto customDto = rabbitService.getCustom(uuid, userID);
        return Response.success(customDto);
    }

    /**
     * 커스텀 페이지 저장
     */
    @PostMapping("/mypage/{uuid}/custom")
    public Response<RabbitCustomResponse> addCustom(@PathVariable String uuid, @RequestBody RabbitCustomDto request, Authentication authentication){
        String userID = authentication.getName();
        RabbitCustomResponse customResponse = rabbitService.saveCustom(uuid, userID, request);
        return Response.success(customResponse);
    }


}

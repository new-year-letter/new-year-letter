package com.newyearletter.newyearletter.controller;

import com.newyearletter.newyearletter.domain.dto.auth.AuthReissueResponse;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/test")
public class TestController {
    @GetMapping
    public String Test(){
        return "TEST";
    }

    @PostMapping("/token")
    public String Test(Authentication authentication){

        return "TOKEN TEST";
    }
}

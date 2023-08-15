package com.newyearletter.newyearletter.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class TestController {

    @GetMapping("/test")
    public String test(){
        return "CICD Test 성공입니다";
    }

    @GetMapping("/test2")
    public String test2(){
        return "CICD Test Success";
    }
}

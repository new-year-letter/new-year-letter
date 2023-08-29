package com.newyearletter.newyearletter.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry){
        registry.addMapping("/**") //해당 서버의 모든 URL 요청 허용
                .allowedOrigins("https://www.newyearletter.com",
                                // Develop 환경 일시적 허용 (삭제 예정)
                                "http://localhost:8484",
                                "http://localhost:3000")
                .allowedMethods("*")
                .allowCredentials(true);
    }

}

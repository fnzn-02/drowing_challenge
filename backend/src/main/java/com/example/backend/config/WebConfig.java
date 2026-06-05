package com.example.backend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry){
        registry.addMapping("/**") // 모든 URL에 적용
                .allowedOrigins("http://localhost:3000") // React 주소 허용
                .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH") // 허용할 HTTP 메서드
                .allowCredentials(true); // 세션 쿠키 허용
    }
    
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:" + System.getProperty("user.dir") + "/uploads/");
    } 
}

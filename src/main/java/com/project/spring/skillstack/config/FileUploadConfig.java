package com.project.spring.skillstack.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class FileUploadConfig implements WebMvcConfigurer {
    
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // /uploads/** 경로로 요청이 오면 실제 uploads/ 폴더에서 파일을 서빙
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:uploads/");
    }
}
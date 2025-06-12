package com.project.spring.pawple.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 원본 이미지 (예: /uploads/images/abc.jpg)
        registry.addResourceHandler("/uploads/images/**")
                .addResourceLocations("file:" + System.getProperty("user.dir") + "/uploads/images/");

        // 썸네일 이미지 (예: /uploads/thumb/thumb_abc.jpg)
        registry.addResourceHandler("/uploads/thumb/**")
                .addResourceLocations("file:" + System.getProperty("user.dir") + "/uploads/thumb/");
                
        // ✅ 상품 이미지
        registry.addResourceHandler("/uploads/product/**")
                .addResourceLocations("file:" + System.getProperty("user.dir") + "/uploads/product/");
                
        // 게시글 이미지 (예: /uploads/post/abc.jpg)
        registry.addResourceHandler("/uploads/post/**")
                .addResourceLocations("file:" + System.getProperty("user.dir") + "/uploads/post/");
    }    
}

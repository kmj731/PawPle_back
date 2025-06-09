package com.project.spring.pawple.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Value("${swagger.setting.title}")
    String title;

    @Value("${swagger.setting.description}")
    String description;

    @Value("${swagger.setting.version}")
    String version;

    @Value("${swagger.setting.email}")
    String email;

    @Value("${swagger.setting.name}")
    String name;

    @Value("${swagger.setting.url}")
    String url;

    @Value("${swagger.license.name}")
    String licenseName;

    @Value("${swagger.license.url}")
    String licenseUrl;

    @Bean
    public OpenAPI swaggerSetting() {
        return new OpenAPI()
                .components(new Components())
                .info(new Info()
                        .title(title)
                        .description(description)
                        .version(version)
                        .contact(new Contact()
                                .name(name)
                                .email(email)
                                .url(url))
                        .license(new License()
                                .name(licenseName)
                                .url(licenseUrl)));
    }
}

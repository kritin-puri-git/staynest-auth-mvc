package com.project.staynest.auth.config;

import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;




@Configuration
public class WebConfig implements WebMvcConfigurer {
    @PostConstruct
    public void init() {
        System.out.println(">>> WebConfig Loaded");
    }

    @Override
    public void addCorsMappings(CorsRegistry registry){
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:4000") 
                .allowedMethods("*")
                .allowedHeaders("*") 
                .allowCredentials(true);

    }
}

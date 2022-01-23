package com.example.pkscl.config;

import com.example.pkscl.Interceptor.LoginCheckInterceptor;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer{
    
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
    registry.addInterceptor(new LoginCheckInterceptor())
    .order(1)
    .addPathPatterns("/**")
    .excludePathPatterns("/","/css/**", "/*.ico", "/error", "/login/**", "/signup/**", "/major-list", "/index.html" );
    }
    
}

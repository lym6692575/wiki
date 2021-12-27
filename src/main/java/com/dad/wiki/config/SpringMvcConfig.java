package com.dad.wiki.config;

import com.dad.wiki.interceptor.LogInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.Resource;

@Configuration
public class SpringMvcConfig implements WebMvcConfigurer {

    @Resource
    LogInterceptor LogInterceptor;

    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(LogInterceptor)
                .addPathPatterns("/**").excludePathPatterns("/login");
    }
}

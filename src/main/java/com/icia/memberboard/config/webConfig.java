package com.icia.memberboard.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class webConfig implements WebMvcConfigurer {
    private String resourcepath = "/upload/**";

    private String savePath = "file:///D:/memberBoard_img/";

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry){
        registry.addResourceHandler(resourcepath)
                .addResourceLocations(savePath);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry){
        registry.addInterceptor(new LoginCheckInterceptor())
                .order(1) // 해당 인터셉터의 우선순위
                .addPathPatterns("/**") // 인터셉터로 체크할 주소(모든주소)
                .excludePathPatterns("/", "/member/save", "/member/login", "/member/login/axios", "/member/duplicate", "/member/logout", "/member/memberCheck",
                        "/board","/board/detail/**", "/board/delete/{id}", "/board/update",
                        "/upload/**", "/comment/**",
                        "/js/**", "/css/**", "/images/**",
                        "/*.ico", "/favicon/**"); // 인터셉터 검증을 하지 않을 주소
    }
}

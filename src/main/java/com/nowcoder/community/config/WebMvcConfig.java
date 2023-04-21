package com.nowcoder.community.config;

import com.nowcoder.community.controller.interceptor.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    @Autowired
    private Alphainterceptor alphainterceptor;
    @Autowired
    private LoginTicketInterceptor loginTicketInterceptor;
//    @Autowired
//    private LoginRequiredInterceptor loginRequiredInterceptor;
    @Autowired
    private MessageInterceptor messageInterceptor;
    @Autowired
    private DataInterceptor dataInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
//        registry.addInterceptor(alphainterceptor)
//                .addPathPatterns("/**")
//                .excludePathPatterns("/","/cs/**","/js/**","/img/**","/login");
        registry.addInterceptor(loginTicketInterceptor)
//                .addPathPatterns("/**")
//                .excludePathPatterns("/cs/**","/js/**","/img/**");
                .excludePathPatterns("/**/*.css","/**/*.js","/**/*.png","/**/*.jpg","/**/*.jpeg");
// 废弃原有的登录拦截器
//        registry.addInterceptor(loginRequiredInterceptor)
//                .addPathPatterns("/**")
//                .excludePathPatterns("/","/cs/**","/js/**","/img/**","/login");
//                .excludePathPatterns("/**/*.css","/**/*.js","/**/*.png","/**/*.jpg","/**/*.jpeg");
        registry.addInterceptor(messageInterceptor)
//                .addPathPatterns("/**")
//                .excludePathPatterns("/","/cs/**","/js/**","/img/**");
                .excludePathPatterns("/**/*.css","/**/*.js","/**/*.png","/**/*.jpg","/**/*.jpeg");
        registry.addInterceptor(dataInterceptor)
//                .addPathPatterns("/**")
//                .excludePathPatterns("/","/cs/**","/js/**","/img/**");
                .excludePathPatterns("/**/*.css","/**/*.js","/**/*.png","/**/*.jpg","/**/*.jpeg");
    }

}

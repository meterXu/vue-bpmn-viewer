///*
// * Copyright sipsd All right reserved.
// */
//package com.sipsd.flow.config;
//
//import com.sipsd.license.LicenseCheckInterceptor;
//import org.springframework.stereotype.Component;
//import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
//import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
//
///**
// * @author 高强
// * @title: Configuration
// * @projectName demo
// * @description: 证书安装
// * @date 2021/7/8下午4:47
// */
//@Component
//public class LicenseConfiguration implements WebMvcConfigurer
//{
//    @Override
//    public void addInterceptors(InterceptorRegistry registry) {
//        // 自定义拦截器，添加拦截路径和排除拦截路径
//        registry.addInterceptor(new LicenseCheckInterceptor()).addPathPatterns("/**");
//    }
//}
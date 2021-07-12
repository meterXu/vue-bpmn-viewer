///*
// * Copyright sipsd All right reserved.
// */
//package com.sipsd.flow.config;
//
//import com.sipsd.license.LicenseCheckInterceptor;
//import org.springframework.stereotype.Component;
//import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
//import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
//
///**
// * @author 高强
// * @title: Configuration
// * @projectName demo
// * @description: TODO
// * @date 2021/7/8下午4:47
// */
//@Component
//public class LicenseConfiguration extends WebMvcConfigurationSupport
//{
//    //添加自定义拦截器
//    @Override
//    protected void addInterceptors(InterceptorRegistry registry) {
//        registry.addInterceptor(new LicenseCheckInterceptor()).addPathPatterns("/**");
//        super.addInterceptors(registry);
//    }
//}
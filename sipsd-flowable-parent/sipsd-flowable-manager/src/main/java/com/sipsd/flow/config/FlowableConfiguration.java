/*
 * Copyright sipsd All right reserved.
 */
package com.sipsd.flow.config;

import lombok.AllArgsConstructor;
import org.flowable.spring.SpringProcessEngineConfiguration;
import org.flowable.spring.boot.EngineConfigurationConfigurer;
import org.flowable.spring.boot.FlowableProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author 高强
 * @title: FlowableConfiguration
 * @projectName sipsd-flowable-parent
 * @description: 拓展自定义属性配置
 * @date 2021/3/23下午3:24
 */
@Configuration
@AllArgsConstructor
@EnableConfigurationProperties(FlowableProperties.class)
public class FlowableConfiguration implements EngineConfigurationConfigurer<SpringProcessEngineConfiguration>
{
    private FlowableProperties flowableProperties;

    @Override
    public void configure(SpringProcessEngineConfiguration engineConfiguration) {
        engineConfiguration.setActivityFontName(flowableProperties.getActivityFontName());
        engineConfiguration.setLabelFontName(flowableProperties.getLabelFontName());
        engineConfiguration.setAnnotationFontName(flowableProperties.getAnnotationFontName());
        //配置自定义的Bpmn转换器
        ExtBpmnJsonConverter.addJsonToBpmnConverts();
    }
}
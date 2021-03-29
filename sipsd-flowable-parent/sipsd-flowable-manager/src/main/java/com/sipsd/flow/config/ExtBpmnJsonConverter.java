/*
 * Copyright sipsd All right reserved.
 */
package com.sipsd.flow.config;

import org.flowable.editor.language.json.converter.BpmnJsonConverter;

/**
 * @author 高强
 * @title: ExtBpmnJsonConverter
 * @projectName sipsd-flowable-parent
 * @description: 拓展自定义属性
 * @date 2021/3/23下午2:48
 */
public class ExtBpmnJsonConverter extends BpmnJsonConverter
{

    public static void addJsonToBpmnConverts() {
        convertersToBpmnMap.put(STENCIL_TASK_USER, ExtUserTaskJsonConverter.class);
        ExtUserTaskJsonConverter.customFillTypes(convertersToBpmnMap, convertersToJsonMap);
    }
}
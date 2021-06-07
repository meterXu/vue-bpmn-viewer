/*
 * Copyright sipsd All right reserved.
 */
package com.sipsd.flow.validation;

import com.alibaba.fastjson.JSON;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * @author 高强
 * @title: JsonValidator
 * @projectName sipsd-flowable-parent
 * @description: TODO
 * @date 2021/6/2下午3:13
 */
public class JsonValidator implements ConstraintValidator<JsonValidation, String>
{
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return  isJSON2(value);
    }

    @Override
    public void initialize(JsonValidation constraintAnnotation) {

    }

    public static boolean isJSON2(String str) {
        boolean result = false;
        try {
            Object obj= JSON.parse(str);
            result = true;
        } catch (Exception e) {
            result=false;
        }
        return result;
    }
}
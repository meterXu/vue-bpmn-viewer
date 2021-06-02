package com.sipsd.flow.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

/**
 * @author 高强
 * @title: JsonValidation
 * @projectName sipsd-flowable-parent
 * @description: TODO
 * @date 2021/6/2下午3:08
 */
@Documented
@Constraint(validatedBy = {JsonValidator.class})
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface JsonValidation
{
    String message() default "业务附加信息不是json格式!";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}

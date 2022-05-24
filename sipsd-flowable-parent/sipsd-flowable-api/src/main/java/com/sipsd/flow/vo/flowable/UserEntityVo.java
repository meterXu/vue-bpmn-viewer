/*
 * Copyright sipsd All right reserved.
 */
package com.sipsd.flow.vo.flowable;

import lombok.Data;

/**
 * @author 高强
 * @title: UserEntityVo
 * @projectName sipsd-flowable-parent
 * @description: TODO
 * @date 2022/4/27下午4:25
 */
@Data
public class UserEntityVo
{
    protected String id;
    protected String firstName;
    protected String lastName;
    protected String displayName;
    protected String email;
    protected String password;
    protected String tenantId;
}
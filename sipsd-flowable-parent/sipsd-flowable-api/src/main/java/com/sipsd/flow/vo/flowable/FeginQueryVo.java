/*
 * Copyright sipsd All right reserved.
 */
package com.sipsd.flow.vo.flowable;

import com.sipsd.flow.common.page.Query;
import lombok.Data;

import java.io.Serializable;

/**
 * @author 高强
 * @title: FeginQueryVo
 * @projectName sipsd-flowable-parent
 * @description: 支持fegin参数调用
 * @date 2022/2/25下午5:07
 */
@Data
public class FeginQueryVo extends Query implements Serializable
{
    /**
     * 用户工号
     */
    private String userCode;

    /**
     * 表单名称
     */
    private String formName;

    /**
     * 业务主键
     */
    private String businessKey;
}
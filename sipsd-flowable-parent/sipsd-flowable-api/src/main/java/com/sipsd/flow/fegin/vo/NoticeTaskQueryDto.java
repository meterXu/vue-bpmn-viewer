package com.sipsd.flow.fegin.vo;

import com.sipsd.flow.common.page.Query;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @Author: suxy
 * @Description: 抄送查询参数类
 * @DateTime:: 2021/11/15 10:20
 */
@Data
public class NoticeTaskQueryDto extends Query implements Serializable
{

    /**
     * 用户编码
     */
    @ApiModelProperty(value = "用户编码")
    String userCode;

    /**
     * 流程实例Id
     */
    @ApiModelProperty(value = "流程实例Id")
    String processInstanceId;


    /**
     * 任务实例Id
     */
    @ApiModelProperty(value = "任务实例Id")
    String taskId;

    /**
     * 流程名称
     *
     */
    @ApiModelProperty(value = "流程名称")
    String formName;
}

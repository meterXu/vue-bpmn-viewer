package com.sipsd.flow.vo.flowable;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author: suxy
 * @Description: 测试流程参数
 * @DateTime:: 2021/11/26 13:40
 */
@Data
public class TestProcessIntsanceVo {

    /**
     * 流程定义key 必填
     */
    @ApiModelProperty(value = "流程定义keys", required = true)
    @NotBlank(message = "流程定义key 必填")
    private String processDefinitionKey;

    /**
     * 启动流程变量 选填
     */
    @ApiModelProperty("启动流程变量")
    private Map<String, Object> variables = new HashMap<String, Object>();
    /**
     * 申请人工号 必填
     */
    @ApiModelProperty(value = "申请人ID", required = true)
    @NotBlank(message = "申请人工号(id) 必填")
    private String currentUserCode;
    /**
     * 系统标识 必填
     */
    @ApiModelProperty(value = "系统标识(租户ID)", required = true)
    @NotBlank(message = "系统标识(租户ID) 必填")
    private String systemSn;

}

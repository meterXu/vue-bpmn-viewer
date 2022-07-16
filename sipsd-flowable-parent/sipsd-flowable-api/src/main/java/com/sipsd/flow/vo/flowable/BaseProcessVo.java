package com.sipsd.flow.vo.flowable;

import java.io.Serializable;

import javax.validation.constraints.NotBlank;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Description: 流程执行过程中的基本参数VO
 * @Author: gaoqiang
 */
@Data
@ApiModel("流程执行过程中的基本参数VO")
public class BaseProcessVo implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/********************** 任务相关的参数 **********************/
	/**
	 * 任务id 必填
	 */
	@ApiModelProperty(value = "任务id", required = true)
	@NotBlank(message = "任务id 必填")
	private String taskId;
	/********************** 审批意见的参数 **********************/
	/**
	 * 操作人code 必填
	 */
	@ApiModelProperty(value = "操作人code", required = true)
	@NotBlank(message = " 操作人code 必填")
	private String userCode;
	/**
	 * 审批意见 必填
	 */
	@ApiModelProperty(value = "审批意见", required = true)
	@NotBlank(message = "审批意见 必填")
	private String message;

	/**
	 * 流程实例的id 必填
	 */
	@ApiModelProperty(value = "流程实例的id", required = true)
	@NotBlank(message = "流程实例的id 必填")
	private String processInstanceId;
	/**
	 * 审批类型 必填
	 */
	@ApiModelProperty(value = "审批类型")
	private String type;
}

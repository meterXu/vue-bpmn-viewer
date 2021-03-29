package com.sipsd.flow.vo.flowable;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * @Description: 流程执行过程中的基本参数VO
 * @Author: chengtg
 */
@Data
@ApiModel("返回原节点流程执行过程中的基本参数VO")
public class PreBackTaskVo implements Serializable {
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
}

package com.sipsd.flow.vo.flowable;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * @Description: 流程执行过程中的基本参数VO
 * @Author: chengtg
 */
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


	/**
	 * 任务参数 选填
	 */
	@ApiModelProperty("任务参数")
	private Map<String, Object> variables = new HashMap<String, Object>();

	public Map<String, Object> getVariables() {
		return variables;
	}

	public void setVariables(Map<String, Object> variables) {
		this.variables = variables;
	}

	public String getTaskId()
	{
		return taskId;
	}

	public void setTaskId(String taskId)
	{
		this.taskId = taskId;
	}

	public String getMessage()
	{
		return message;
	}

	public void setMessage(String message)
	{
		this.message = message;
	}

	public String getProcessInstanceId()
	{
		return processInstanceId;
	}

	public void setProcessInstanceId(String processInstanceId)
	{
		this.processInstanceId = processInstanceId;
	}
}

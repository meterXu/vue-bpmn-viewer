package com.sipsd.flow.vo.flowable;


import com.sipsd.validation.constraints.JsonValidation;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * @author : gaoqiang
 * @title: : StartProcessVo
 * @projectName : flowable
 * @description: 启动流程VO
 * @date : 2019/11/1314:50
 */
@Data
public class StartProcessInstanceVo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 流程定义key 必填
	 */
	@ApiModelProperty(value = "流程定义keys", required = true)
	@NotBlank(message = "流程定义key 必填")
	private String processDefinitionKey;
	/**
	 * 业务系统id 必填
	 */
	@ApiModelProperty(value = "业务系统id", required = true)
	@NotBlank(message = "业务系统id 必填")
	private String businessKey;

	/**
	 * 业务附加信息
	 */
	@JsonValidation(message = "必须是json格式")
	private String businessInfo;
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
	/**
	 * 表单显示名称 必填
	 */
	@ApiModelProperty(value = "表单显示(工作流业务名称)名称", required = true)
	@NotBlank(message = "表单显示名称 必填")
	private String formName;
//	/**
//	 * 流程提交人工号 必填
//	 */
//	@ApiModelProperty(" 流程提交人工号(ID)")
//	@NotBlank(message = " 流程提交人工号(ID) 必填")
//	private String creator;

}

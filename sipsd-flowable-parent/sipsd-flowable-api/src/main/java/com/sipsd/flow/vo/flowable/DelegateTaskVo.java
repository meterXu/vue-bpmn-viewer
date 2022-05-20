package com.sipsd.flow.vo.flowable;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;

/**
 * @author : gaoqiang
 * @title: : DelegateTaskVo
 * @projectName : flowable
 * @description: 委派
 * @date : 2019/11/1315:51
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel("委派")
public class DelegateTaskVo extends BaseProcessVo {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 被委派人
	 */
	@ApiModelProperty(value = "被委派人", required = true)
	@NotBlank(message = "被委派人 必填")
	private String delegateUserCode;
	

}

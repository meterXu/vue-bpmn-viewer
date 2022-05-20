package com.sipsd.flow.vo.flowable;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;

/**
 * @Description: 驳回的实体VO
 * @Author: gaoqiang
 * @Since:9:19 2018/9/8 那个男人真牛逼牛逼 2018 ~ 2030 版权所有
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel("驳回的实体VO")
public class BackTaskVo extends BaseProcessVo {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 需要驳回的节点id 必填
	 */
	@ApiModelProperty(value = "需要驳回的节点id", required = true)
	@NotBlank(message = "需要驳回的节点id 必填")
	private String distFlowElementId;
}

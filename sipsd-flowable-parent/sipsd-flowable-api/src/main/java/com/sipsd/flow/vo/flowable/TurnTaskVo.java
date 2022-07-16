package com.sipsd.flow.vo.flowable;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;

/**
 * @author : gaoqiang
 * @title: : TurnTaskVo
 * @projectName : flowable
 * @description: 转办Vo
 * @date : 2019/11/1315:34
 */
@Data
@EqualsAndHashCode(callSuper=false)
@ApiModel("转办Vo")
public class TurnTaskVo extends BaseProcessVo {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
     * 被转办人工号 必填
     */
	@ApiModelProperty(value = "被转办人工号", required = true)
	@NotBlank(message = "被转办人工号 必填")
    private String turnToUserId;
  
}

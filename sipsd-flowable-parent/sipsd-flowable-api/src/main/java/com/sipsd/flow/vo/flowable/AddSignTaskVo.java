package com.sipsd.flow.vo.flowable;

import java.util.List;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author : chengtg
 * @title: : AddSignTaskVo
 * @projectName : flowable
 * @description: 加签Vo
 * @date : 2019/12/515:47
 */
@Data
@EqualsAndHashCode(callSuper=false)
@ApiModel("加签Vo")
public class AddSignTaskVo extends BaseProcessVo {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
     * 被加签人
     */
	@ApiModelProperty(value = "被加签人")
    private List<String> signPersoneds;

}

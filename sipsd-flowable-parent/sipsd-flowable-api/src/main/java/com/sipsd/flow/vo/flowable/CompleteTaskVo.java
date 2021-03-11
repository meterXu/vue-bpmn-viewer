package com.sipsd.flow.vo.flowable;


import java.util.HashMap;
import java.util.Map;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author : chengtg
 * @title: : CompleteTaskVo
 * @projectName : flowable
 * @description: 执行任务Vo
 * @date : 2019/11/1315:27
 */
@Data
@EqualsAndHashCode(callSuper=false)
@ApiModel("执行任务")
public class CompleteTaskVo extends BaseProcessVo {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String candidateGroup;
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
}

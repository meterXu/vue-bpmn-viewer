package com.sipsd.flow.vo.flowable.ret;

import java.io.Serializable;

import lombok.Data;

/**
 * @author : chengtg
 * @title: : GroupVo
 * @projectName : flowable
 * @description: 组的VO
 * @date : 2019/12/313:59
 */
@Data
public class GroupVo implements Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
     * 组的id
     */
    private String id;
    /**
     * 组的名称
     */
    private String groupName;
    /****************************扩展字段****************************/
    /**
     * 组的标识
     */
    private String groupSn;

}

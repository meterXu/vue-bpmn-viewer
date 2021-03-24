package com.sipsd.flow.vo.flowable;

import java.io.Serializable;

/**
 * @author : chengtg
 * @title: : TaskVo
 * @projectName : flowable
 * @description: 任务VO
 * @date : 2019/11/1315:11
 */
public class TaskQueryVo implements Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
     * 用户工号
     */
    private String userCode;

    /**
     * 表单名称
     */
    private String formName;

    /**
     * 业务主键
     */
    private String businessKey;

    public String getUserCode() {
        return userCode;
    }

    public void setUserCode(String userCode) {
        this.userCode = userCode;
    }

    public String getFormName()
    {
        return formName;
    }

    public void setFormName(String formName)
    {
        this.formName = formName;
    }

    public String getBusinessKey()
    {
        return businessKey;
    }

    public void setBusinessKey(String businessKey)
    {
        this.businessKey = businessKey;
    }
}

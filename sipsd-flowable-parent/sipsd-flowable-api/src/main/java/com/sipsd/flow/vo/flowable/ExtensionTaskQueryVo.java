package com.sipsd.flow.vo.flowable;

import java.io.Serializable;

/**
 * @author : 高强
 * @title: : TaskVo
 * @projectName : flowable
 * @description: 自定义属性任务VO
 * @date : 2019/11/1315:11
 */
public class ExtensionTaskQueryVo implements Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
    /**
     * 流程实例id
     */
    private String processInstanceId;

    /**
     * 任务id
     */
    private String taskId;

    /**
     * 节点审批最大天数
     */
    private String customTaskMaxDay;

    public String getProcessInstanceId()
    {
        return processInstanceId;
    }

    public void setProcessInstanceId(String processInstanceId)
    {
        this.processInstanceId = processInstanceId;
    }

    public String getCustomTaskMaxDay()
    {
        return customTaskMaxDay;
    }

    public void setCustomTaskMaxDay(String customTaskMaxDay)
    {
        this.customTaskMaxDay = customTaskMaxDay;
    }

    public String getTaskId()
    {
        return taskId;
    }

    public void setTaskId(String taskId)
    {
        this.taskId = taskId;
    }
}

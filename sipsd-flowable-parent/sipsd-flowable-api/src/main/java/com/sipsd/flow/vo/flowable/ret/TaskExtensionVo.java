package com.sipsd.flow.vo.flowable.ret;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * @author : 高强
 * @title: : TaskExtensionVo
 * @projectName : flowable
 * @description: 任务VO
 * @date : 2019/11/1315:11
 */
public class TaskExtensionVo implements Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;



    private Integer id;


    /**
     * 版本号
     */
    private Integer version;

    /**
     * 流程运行唯一键
     */
    private String executionId;

    /**
     * 流程实例id
     */
    private String processInstanceId;

    /**
     * 流程定义
     */
    private String processDefinitionId;
    /**
     * 节点审批最大天数
     */
    private String taskMaxDay;

    /**
     * 审批人
     */
    private String assignee;

    /**
     * 审批角色
     */
    private String groupId;

    /**
     * 创建时间
     */
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private Date startTime;

    /**
     * 结束时间
     */
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private Date endTime;

    /**
     * 更新时间
     */
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private Date updateTime;


    @ApiModelProperty("秒")
    @Setter
    @Getter
    private Long restTime;
    /**
     * 审批节点id
     */
    private String taskId;
    /**
     * 节点审批最大天数
     */
    private String customTaskMaxDay;
    /**
     * 审批节点key
     */
    private String taskDefinitionKey;

    /**
     * 上个审批节点key
     */
    private String fromKey;

    /**
     * 租户
     */
    private String tenantId;

    /**
     * 表单名称
     */
    private String formName;
    /**
     * 业务主键
     */
    private String businessKey;

    /**
     * 流程节点名称
     */
    private String taskName;

    /**
     * 流程节点类型 串联/并联
     */
    private String flowType;


    /**
     * 代办或者已办状态
     */
    private String status;

    public Integer getId()
    {
        return id;
    }

    public void setId(Integer id)
    {
        this.id = id;
    }

    public String getTaskName()
    {
        return taskName;
    }

    public void setTaskName(String taskName)
    {
        this.taskName = taskName;
    }

    public String getExecutionId()
    {
        return executionId;
    }

    public void setExecutionId(String executionId)
    {
        this.executionId = executionId;
    }

    public String getProcessInstanceId()
    {
        return processInstanceId;
    }

    public void setProcessInstanceId(String processInstanceId)
    {
        this.processInstanceId = processInstanceId;
    }

    public String getProcessDefinitionId()
    {
        return processDefinitionId;
    }

    public void setProcessDefinitionId(String processDefinitionId)
    {
        this.processDefinitionId = processDefinitionId;
    }

    public String getTenantId()
    {
        return tenantId;
    }

    public void setTenantId(String tenantId)
    {
        this.tenantId = tenantId;
    }

    public String getTaskMaxDay()
    {
        return taskMaxDay;
    }

    public void setTaskMaxDay(String taskMaxDay)
    {
        this.taskMaxDay = taskMaxDay;
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

    public String getTaskDefinitionKey()
    {
        return taskDefinitionKey;
    }

    public void setTaskDefinitionKey(String taskDefinitionKey)
    {
        this.taskDefinitionKey = taskDefinitionKey;
    }

    public Integer getVersion()
    {
        return version;
    }

    public void setVersion(Integer version)
    {
        this.version = version;
    }

    public Date getStartTime()
    {
        return startTime;
    }

    public void setStartTime(Date startTime)
    {
        this.startTime = startTime;
    }

    public Date getUpdateTime()
    {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime)
    {
        this.updateTime = updateTime;
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

    public Date getEndTime()
    {
        return endTime;
    }

    public void setEndTime(Date endTime)
    {
        this.endTime = endTime;
    }

    public String getAssignee()
    {
        return assignee;
    }

    public void setAssignee(String assignee)
    {
        this.assignee = assignee;
    }

    public String getFromKey()
    {
        return fromKey;
    }

    public void setFromKey(String fromKey)
    {
        this.fromKey = fromKey;
    }

    public String getGroupId()
    {
        return groupId;
    }

    public void setGroupId(String groupId)
    {
        this.groupId = groupId;
    }

    public String getStatus()
    {
        return status;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }

    public String getFlowType()
    {
        return flowType;
    }

    public void setFlowType(String flowType)
    {
        this.flowType = flowType;
    }
}

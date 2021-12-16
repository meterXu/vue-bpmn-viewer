package com.sipsd.flow.vo.flowable.ret;

import com.sipsd.validation.constraints.JsonValidation;
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
     * 审批人名称
     */
    private String realName;

    /**
     * 审批角色
     */
    private String groupId;

    /**
     * 审批角色名称
     */
    private String groupName;

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
     * 时间执行时长
     */
    @ApiModelProperty("秒")
    @Setter
    @Getter
    private long duration;

    /**
     * 是否督办过
     */
    private Boolean isDb;

    /**
     * 审批类型(驳回，跳转，审批)
     */
    private String approveType;

    /**
     * 业务上的附加属性
     */
    @JsonValidation
    private String businessInfo;

    /**
     * 激活挂起状态
     */
    private String suspensionState;

    /**
     * 是否延长时间
     */
    private String overtime;

    /**
     * 审批意见
     */
    private String comment;

    /**
     * 挂起/激活 时间
     */
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private Date  suspensionTime;





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

    public String getRealName()
    {
        return realName;
    }

    public void setRealName(String realName)
    {
        this.realName = realName;
    }

    public String getGroupName()
    {
        return groupName;
    }

    public void setGroupName(String groupName)
    {
        this.groupName = groupName;
    }

    public Boolean getDb()
    {
        return isDb;
    }

    public void setDb(Boolean db)
    {
        isDb = db;
    }

    public String getBusinessInfo()
    {
        return businessInfo;
    }

    public void setBusinessInfo(String businessInfo)
    {
        this.businessInfo = businessInfo;
    }

    public String getApproveType()
    {
        return approveType;
    }

    public void setApproveType(String approveType)
    {
        this.approveType = approveType;
    }

    public String getSuspensionState()
    {
        return suspensionState;
    }

    public void setSuspensionState(String suspensionState)
    {
        this.suspensionState = suspensionState;
    }

    public Date getSuspensionTime()
    {
        return suspensionTime;
    }

    public void setSuspensionTime(Date suspensionTime)
    {
        this.suspensionTime = suspensionTime;
    }

    public String getOvertime()
    {
        return overtime;
    }

    public void setOvertime(String overtime)
    {
        this.overtime = overtime;
    }

    public String getComment()
    {
        return comment;
    }

    public void setComment(String comment)
    {
        this.comment = comment;
    }
}

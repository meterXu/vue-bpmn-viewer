package com.sipsd.flow.vo.flowable.ret;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * @author : chengtg
 * @title: : TaskVo
 * @projectName : flowable
 * @description: 任务vo
 * @date : 2019/11/2316:09
 */
public class TaskVo implements Serializable {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

    /**
     * 附加表ID
     */
    private String Id;

	/**
     * 任务id
     */
    private String taskId;
    /**
     * 任务名称
     */
    private String taskName;
    /**
     * 审批人
     */
    private String approver;
    /**
     * 审批人id
     */
    private String approverId;
    /**
     * 表单名称
     */
    private String formName;
    /**
     * 业务主键
     */
    private String businessKey;
    /**
     * 流程实例id
     */
    private String processInstanceId;

    /**
     * 审批节点key
     */
    private String taskDefinitionKey;

    /**
     * 是否督办过
     */
    private Boolean isDb;

    /**
     * 业务上的附加属性
     */
    private String businessInfo;

    /**
     * 开始时间
     */
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private Date startTime ;

    /**
     * 结束时间
     */
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private Date endTime;

    /**
     * 自然时结束时间 应该完成的结束时间
     */
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private Date workEndTime;


    @ApiModelProperty("毫秒")
    @Setter
    @Getter
    private long duration;
    /**
     * 系统标识
     */
    private String systemSn;

    @ApiModelProperty("秒")
    @Setter
    @Getter
    private Long restTime;

    /**
     * 节点审批最大天数
     */
    private String taskMaxDay;

    /**
     * 节点审批最大天数
     */
    private String customTaskMaxDay;

    /**
     * 审批意见
     */
    private String comment;

    /**
     * 用户名称
     */
    private String realName;
    
    /**
     * 表单key
     */
    @Setter
    @Getter
    protected String formKey;
    
    /**
     * 表单数据结构
     */
    @Setter
    @Getter
    protected Object renderedTaskForm;
    
    @Setter
    @Getter
    protected Object Variables;
    
    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getApprover() {
        return approver;
    }

    public void setApprover(String approver) {
        this.approver = approver;
    }

    public String getApproverId() {
        return approverId;
    }

    public void setApproverId(String approverId) {
        this.approverId = approverId;
    }

    public String getFormName() {
        return formName;
    }

    public void setFormName(String formName) {
        this.formName = formName;
    }

    public String getBusinessKey() {
        return businessKey;
    }

    public void setBusinessKey(String businessKey) {
        this.businessKey = businessKey;
    }

    public String getProcessInstanceId() {
        return processInstanceId;
    }

    public void setProcessInstanceId(String processInstanceId) {
        this.processInstanceId = processInstanceId;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public String getSystemSn() {
        return systemSn;
    }

    public void setSystemSn(String systemSn) {
        this.systemSn = systemSn;
    }

    public Long getRestTime()
    {
        return restTime;
    }

    public void setRestTime(Long restTime)
    {
        this.restTime = restTime;
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

    public String getId()
    {
        return Id;
    }

    public void setId(String id)
    {
        Id = id;
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

    public String getTaskDefinitionKey()
    {
        return taskDefinitionKey;
    }

    public void setTaskDefinitionKey(String taskDefinitionKey)
    {
        this.taskDefinitionKey = taskDefinitionKey;
    }

    public String getComment()
    {
        return comment;
    }

    public void setComment(String comment)
    {
        this.comment = comment;
    }

    public String getRealName()
    {
        return realName;
    }

    public void setRealName(String realName)
    {
        this.realName = realName;
    }

    public Date getWorkEndTime()
    {
        return workEndTime;
    }

    public void setWorkEndTime(Date workEndTime)
    {
        this.workEndTime = workEndTime;
    }

}

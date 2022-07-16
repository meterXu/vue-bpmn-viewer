package com.sipsd.flow.vo.flowable;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * @Author: suxy
 * @Description: 抄送任务
 * @DateTime:: 2021/11/11 9:01
 */
@Data
@TableName("act_hi_notice_task")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="抄送任务", description="抄送任务")
public class NoticeTask
{

    @TableId(type = IdType.ASSIGN_UUID)
    String id;

    String procDefId;

    String taskDefId;

    String taskDefKey;

    String procInstId;

    String Name;

    String parentTaskId;

    String description;

    String owner;

    String assignee;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    Date createTime;

    int priority;

    String formKey;

    String category;

    String tenantId;

    String businessInfo;

    String businessKey;

    String formName;




}

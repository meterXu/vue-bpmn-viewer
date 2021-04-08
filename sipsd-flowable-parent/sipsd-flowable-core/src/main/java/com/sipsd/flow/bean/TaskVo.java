package com.sipsd.flow.bean;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.flowable.task.api.Task;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class TaskVo implements Serializable {

    private String taskId;
    private String taskDefinitionId;
    private String taskDefinitionKey;
    private String taskName;
    private String assignee;

    private List<IdentityLinkVo> linkVoList;
    private String taskProcessInstanceId;

    public TaskVo with(Task task){
        if(task==null){
            return this;
        }
        this.setTaskId(task.getId());
        this.setTaskName(task.getName());
        this.setTaskDefinitionId(task.getTaskDefinitionId());
        this.setTaskDefinitionKey(task.getTaskDefinitionKey());
        this.setTaskName(task.getName());
        this.setAssignee(task.getAssignee());
        this.setTaskProcessInstanceId(task.getProcessInstanceId());
        return  this;
    }
}

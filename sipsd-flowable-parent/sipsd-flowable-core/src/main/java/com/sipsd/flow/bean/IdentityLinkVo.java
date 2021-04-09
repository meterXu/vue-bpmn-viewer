package com.sipsd.flow.bean;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.flowable.identitylink.api.IdentityLink;

import java.io.Serializable;

/**
 * 候选人，候选组的交互对象
 */
@Getter
@Setter
@NoArgsConstructor
public class IdentityLinkVo implements Serializable {

    private String type;
    private String userId;
    private String groupId;
    private String taskId;
    private String processInstanceId;
    private String processDefinitionId;

    public IdentityLinkVo with(IdentityLink identityLink){
        if(identityLink==null){
            return this;
        }
        this.setType(identityLink.getType());
        this.setUserId(identityLink.getUserId());
        this.setGroupId(identityLink.getGroupId());
        this.setTaskId(identityLink.getTaskId());
        this.setProcessDefinitionId(identityLink.getProcessDefinitionId());
        this.setProcessInstanceId(identityLink.getProcessInstanceId());
        return  this;
    }
}

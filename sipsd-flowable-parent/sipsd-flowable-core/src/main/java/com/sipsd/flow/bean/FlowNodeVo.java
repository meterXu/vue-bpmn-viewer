package com.sipsd.flow.bean;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.flowable.bpmn.model.FlowElement;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
public class FlowNodeVo implements Serializable {
    private String flowNodeId;
    private String flowNodeName;


    private String flowElementType;

    public FlowNodeVo with(FlowElement flowElement){
        if(flowElement==null){
            return this;
        }
        this.setFlowNodeId(flowElement.getId());
        this.setFlowNodeName(flowElement.getName());
        this.flowElementType=flowElement.getClass().getName();
        return  this;
    }
}

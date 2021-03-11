package com.sipsd.flow.rest.model;

import java.io.Serializable;

import org.flowable.bpmn.model.FlowElement;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class FlowNodeVo implements Serializable {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
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

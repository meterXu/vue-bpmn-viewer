/*
 * Copyright sipsd All right reserved.
 */
package com.sipsd.flow.dao.flowable;

import org.flowable.bpmn.model.Activity;
import org.flowable.bpmn.model.EndEvent;
import org.flowable.bpmn.model.FlowElement;
import org.flowable.bpmn.model.Process;
import org.flowable.engine.RepositoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;

/**
 * @author 高强
 * @title: ProcessDefinitionUtils
 * @projectName sipsd-flowable-parent
 * @description: TODO
 * @date 2021/3/26上午10:54
 */
@Component
public class ProcessDefinitionUtils {

    @Autowired
    private RepositoryService repositoryService;

    /**
     * 获取end节点
     *
     * @param processDefId
     * @return FlowElement
     */
    public FlowElement findEndFlowElement(String processDefId) {
        Process process = repositoryService.getBpmnModel(processDefId).getMainProcess();
        Collection<FlowElement> list = process.getFlowElements();
        for (FlowElement f : list) {
            if (f instanceof EndEvent) {
                return f;
            }
        }
        return null;
    }

    /**
     * 获取指定节点的节点信息
     *
     * @param processDefId
     * @param flowElementId
     * @return FlowElement
     */
    public Activity findFlowElementById(String processDefId, String flowElementId) {
        Process process = repositoryService.getBpmnModel(processDefId).getMainProcess();
        return (Activity) process.getFlowElement(flowElementId);
    }

}
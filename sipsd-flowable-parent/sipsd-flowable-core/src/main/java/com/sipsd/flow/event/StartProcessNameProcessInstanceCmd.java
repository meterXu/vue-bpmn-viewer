package com.sipsd.flow.event;

import lombok.extern.apachecommons.CommonsLog;
import org.flowable.engine.impl.cmd.StartProcessInstanceCmd;

import java.util.Map;


@SuppressWarnings("serial")
@CommonsLog
public class StartProcessNameProcessInstanceCmd<T> extends StartProcessInstanceCmd<T>
{
    public StartProcessNameProcessInstanceCmd(String processInstanceName, 
            String processDefinitionKey,
            String businessKey, 
            Map<String, Object> variables, 
            String tenantId) {
        //TODO

        super(processDefinitionKey, null, businessKey, variables,tenantId);
        log.warn("StartProcessNameProcessInstanceCmd insert!");
        System.out.println("StartProcessNameProcessInstanceCmd.businessKey"+businessKey);
        this.processInstanceName = processInstanceName;
    }
}
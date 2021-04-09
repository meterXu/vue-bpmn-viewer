package com.sipsd.flow.event;

import lombok.extern.apachecommons.CommonsLog;
import org.flowable.common.engine.api.delegate.event.FlowableEvent;
import org.flowable.common.engine.api.delegate.event.FlowableEventListener;
import org.flowable.common.engine.impl.event.FlowableEngineEventImpl;
import org.flowable.engine.delegate.event.impl.FlowableProcessEventImpl;
import org.springframework.stereotype.Component;

/**
 * 任务节点前置监听处理类
 * @author: hul
 * @create: 2019-05-04 20:51
 **/
@Component
@CommonsLog
public class TaskBeforeListener implements FlowableEventListener
{

    @Override
    public void onEvent(FlowableEvent event) {

        // 当前节点任务实体
    	FlowableEngineEventImpl engineEvent=(FlowableProcessEventImpl)event;
    	
        String  executionId = engineEvent.getExecutionId();
        
        String  processDefinitionId =engineEvent.getProcessDefinitionId();
        String  processInstanceId =engineEvent.getProcessInstanceId();

        // TODO 获取到了taskEntity 自己做每个节点的前置操作
        log.info("TaskBeforeListener.onEvent:"+event.hashCode()+";with processInstanceId:"+processInstanceId);
    }

    @Override
    public boolean isFailOnException() {
        return false;
    }

    @Override
    public boolean isFireOnTransactionLifecycleEvent() {
        return false;
    }

    @Override
    public String getOnTransaction() {
        return null;
    }
}
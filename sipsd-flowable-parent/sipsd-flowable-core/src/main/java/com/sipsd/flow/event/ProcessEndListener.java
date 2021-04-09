package com.sipsd.flow.event;

import com.sipsd.flow.util.SpringContextUtils;
import lombok.extern.apachecommons.CommonsLog;
import org.flowable.common.engine.api.delegate.event.FlowableEvent;
import org.flowable.common.engine.api.delegate.event.FlowableEventListener;
import org.flowable.common.engine.impl.event.FlowableEngineEventImpl;

/**
 * 全局的流程结束修改状态监听
 */
@CommonsLog
public class ProcessEndListener implements FlowableEventListener
{
 
    private static final long serialVersionUID = 1L;
 
 
    @Override
    public void onEvent(FlowableEvent event) {
        FlowableEngineEventImpl engineEvent=(FlowableEngineEventImpl)event;
        SpringContextUtils.getApplicationContext().publishEvent(new ProcessEndEvent(engineEvent,engineEvent.getProcessInstanceId()));
        //TODO 统一的流程结束处理
        log.warn("ProcessEndListener is onEvent now! ProcessInstanceId"+ engineEvent.getProcessInstanceId());
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

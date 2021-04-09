package com.sipsd.flow.event;

import lombok.extern.apachecommons.CommonsLog;
import org.flowable.common.engine.impl.event.FlowableEngineEventImpl;

/**
 * 全局的事件处理结束事件
 * @author hul
 *
 */
@CommonsLog
public class ProcessEndEvent {

    
    public ProcessEndEvent(FlowableEngineEventImpl engineEvent, String processInstanceId) {
       log.debug("ProcessEndEvent(FlowableEngineEventImpl:"+engineEvent+", processInstanceId:"+processInstanceId+")");
        // TODO Auto-generated constructor stub

    }
    
    public ProcessEndEvent() {
        log.debug("ContonsTurctDefault:ProcessEndEvent()");
        // TODO Auto-generated constructor stub

    }
}

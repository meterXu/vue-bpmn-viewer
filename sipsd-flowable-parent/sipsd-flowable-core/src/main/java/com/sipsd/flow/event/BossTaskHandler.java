package com.sipsd.flow.event;


import org.flowable.engine.delegate.TaskListener;
import org.flowable.task.service.delegate.DelegateTask;

/**
 * 
 * @author puhaiyang
 * @date 2018/12/19
 */
public class BossTaskHandler implements TaskListener
{
    
    @Override
    public void notify(DelegateTask delegateTask) {
        
        //TODO  查询出当前属于老板角色的人员
        delegateTask.setAssignee("老板");
        
        System.out.println("delegateTask.setAssignee(老板)");
    }

}

package com.sipsd.flow.service.flowable;


import com.sipsd.flow.common.page.PageModel;
import com.sipsd.flow.common.page.Query;
import com.sipsd.flow.utils.Result;
import com.sipsd.flow.vo.flowable.ProcessDefinitionQueryVo;
import com.sipsd.flow.vo.flowable.ret.ProcessDefinitionVo;

/**
 * @author : gaoqiang
 * @title: : IFlowProcessDi
 * @projectName : flowable
 * @description: 流程定义
 * @date : 2019/11/1314:11
 */
public interface IFlowableProcessDefinitionService {

    /**
     * 通过条件查询流程定义
     *
     * @param params
     * @return
     */
    public PageModel<ProcessDefinitionVo> getPagerModel(ProcessDefinitionQueryVo params, Query query);

    /**
     * 通过流程定义id获取流程定义的信息
     *
     * @param processDefinitionId 流程定义id
     * @return
     */
    public ProcessDefinitionVo getById(String processDefinitionId);

    /**
     * 挂起流程定义
     *
     * @param processDefinitionId 流程定义id
     * @param suspensionState     状态1挂起 2激活
     * @param suspensionState     状态1延长时间 2不需要延长时间
     */
    public Result suspendOrActivateProcessDefinitionById(String processDefinitionId, int suspensionState, int overtime);

}

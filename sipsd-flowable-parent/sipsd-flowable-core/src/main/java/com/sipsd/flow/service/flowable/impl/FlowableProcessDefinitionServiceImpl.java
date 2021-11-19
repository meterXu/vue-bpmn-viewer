package com.sipsd.flow.service.flowable.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sipsd.cloud.common.core.util.Result;
import com.sipsd.flow.common.page.PageModel;
import com.sipsd.flow.common.page.Query;
import com.sipsd.flow.dao.flowable.IFlowableProcessDefinitionDao;
import com.sipsd.flow.service.flowable.IFlowableExtensionTaskService;
import com.sipsd.flow.service.flowable.IFlowableProcessDefinitionService;
import com.sipsd.flow.vo.flowable.ProcessDefinitionQueryVo;
import com.sipsd.flow.vo.flowable.ret.ProcessDefinitionVo;
import org.flowable.engine.RepositoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author : chengtg
 * @title: : FlowableProcessDefinitionServiceImpl
 * @projectName : flowable
 * @description: 流程定义service
 * @date : 2019/11/1314:18
 */
@Service
public class FlowableProcessDefinitionServiceImpl extends BaseProcessService implements IFlowableProcessDefinitionService {

	@Autowired
	private RepositoryService repositoryService;

    @Autowired
    private IFlowableProcessDefinitionDao flowableProcessDefinitionDao;

    @Autowired
    private IFlowableExtensionTaskService flowableExtensionTaskService;

    @Override
    public PageModel<ProcessDefinitionVo> getPagerModel(ProcessDefinitionQueryVo params, Query query) {
        PageHelper.startPage(query.getPageNum(), query.getPageSize());
        Page<ProcessDefinitionVo> page = flowableProcessDefinitionDao.getPagerModel(params);
        return new PageModel<>(page);
    }

    @Override
    public ProcessDefinitionVo getById(String processDefinitionId) {
        return flowableProcessDefinitionDao.getById(processDefinitionId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result suspendOrActivateProcessDefinitionById(String processInstanceId,int suspensionState) {
        Result result = null;
        if (suspensionState == 2){
//            repositoryService.suspendProcessDefinitionById(processDefinitionId, true, null);
            runtimeService.suspendProcessInstanceById(processInstanceId);
            result = Result.sucess("挂起成功");
        }else if(suspensionState == 1){
//            repositoryService.activateProcessDefinitionById(processDefinitionId, true, null);
            runtimeService.activateProcessInstanceById(processInstanceId);
            result = Result.sucess("激活成功");
        }
        else {
            return Result.failed("请输入正确的状态：1-激活 2-挂起");
        }
        //停止/恢复时间-更新act_ru_extension_task表  TODO 当前附加表所有的数据都会更新，实际上只用更新正在运行的任务即可-可以新增历史附加表来实现
        flowableExtensionTaskService.updateSuspensionStateByProcessInstanceId(processInstanceId,String.valueOf(suspensionState));
        return result;
    }

}

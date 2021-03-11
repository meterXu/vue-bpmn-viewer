package com.sipsd.flow.service.flowable;

import java.util.Map;

import org.flowable.engine.runtime.ProcessInstance;

import com.sipsd.cloud.common.core.util.Result;
import com.sipsd.flow.common.page.PageModel;
import com.sipsd.flow.common.page.Query;
import com.sipsd.flow.vo.flowable.EndProcessVo;
import com.sipsd.flow.vo.flowable.ProcessInstanceQueryVo;
import com.sipsd.flow.vo.flowable.RevokeProcessVo;
import com.sipsd.flow.vo.flowable.StartProcessInstanceVo;
import com.sipsd.flow.vo.flowable.ret.ProcessInstanceVo;

/**
 * @author : chengtg
 * @projectName : flowable
 * @description: 流程实例service
 * @date : 2019/10/2511:40
 */
public interface IFlowableProcessInstanceService {

    /**
     * 启动流程
     *
     * @param startProcessInstanceVo 参数
     * @return
     */
    public Result<ProcessInstance> startProcessInstanceByKey(StartProcessInstanceVo startProcessInstanceVo);

    /**
     * 查询流程实例列表
     *
     * @param params 参数
     * @param query  分页参数
     * @return
     */
    public PageModel<ProcessInstanceVo> getPagerModel(ProcessInstanceQueryVo params, Query query);

    /**
     * 查询我发起的流程实例
     *
     * @param params 参数
     * @param query  分页参数
     * @return
     */
    public PageModel<ProcessInstanceVo> getMyProcessInstances(ProcessInstanceQueryVo params, Query query);

    /**
     * 获取流程图图片
     *
     * @param processInstanceId 流程实例id
     * @return
     */
    public byte[] createImage(String processInstanceId);

    /**
     * 删除流程实例
     *
     * @param processInstanceId 流程实例id
     * @return
     */
    public Result<String> deleteProcessInstanceById(String processInstanceId);

    /**
     * 激活流程定义
     *
     * @param processInstanceId 流程实例id
     * @param suspensionState   2激活 1挂起
     */
    public Result<String> suspendOrActivateProcessInstanceById(String processInstanceId, Integer suspensionState);

    /**
     * 终止流程
     * @param endVo 参数
     * @return
     */
    public Result<String> stopProcessInstanceById(EndProcessVo endVo) ;

    /**
     * 撤回流程
     * @param revokeVo 参数
     * @return
     */
    public Result<String> revokeProcess(RevokeProcessVo revokeVo);

    /**
     * 获取动态表单信息
     * @param processInstanceId
     * @return
     */
	Result<Map<String, Object>> formData(String processInstanceId);

	Result<Map<String, Object>> taskFormData(String taskId);
}

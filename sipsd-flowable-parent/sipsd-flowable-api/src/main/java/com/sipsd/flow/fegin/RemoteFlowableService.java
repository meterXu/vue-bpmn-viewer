/*
 *
 *      Copyright (c) 2018-2025, gaoq All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice,
 *  this list of conditions and the following disclaimer.
 *  Redistributions in binary form must reproduce the above copyright
 *  notice, this list of conditions and the following disclaimer in the
 *  documentation and/or other materials provided with the distribution.
 *  Neither the name of the sipsdcloudd.com developer nor the names of its
 *  contributors may be used to endorse or promote products derived from
 *  this software without specific prior written permission.
 *  Author: gaoq (wangiegie@gmail.com)
 *
 */

package com.sipsd.flow.fegin;


import com.sipsd.flow.common.page.PageModel;
import com.sipsd.flow.common.page.Query;
import com.sipsd.flow.fegin.config.FeignConfiguration;
import com.sipsd.flow.fegin.fallback.RemoteFlowableServiceFallback;
import com.sipsd.flow.fegin.vo.AbstractModel;
import com.sipsd.flow.fegin.vo.FeginQueryVo;
import com.sipsd.flow.fegin.vo.FlowElementDto;
import com.sipsd.flow.fegin.vo.NoticeTaskQueryDto;
import com.sipsd.flow.utils.Result;
import com.sipsd.flow.vo.flowable.*;
import com.sipsd.flow.vo.flowable.ret.FlowNodeVo;
import com.sipsd.flow.vo.flowable.ret.TaskExtensionVo;
import com.sipsd.flow.vo.flowable.ret.TaskVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author gaoq
 * @date 2020/6/22
 */
@Api(tags = "工作流fegin接口")
@FeignClient(name = "RemoteFlowableService", url = "${base.path}",fallbackFactory = RemoteFlowableServiceFallback.class,configuration = FeignConfiguration.class)
public interface RemoteFlowableService
{
	/**
	 * 流程启动
	 *
	 * @param startProcessInstanceVo 参数
	 * @return
	 */
	@ApiOperation("流程启动")
	@PostMapping(value = "/rest/formdetail/startProcess")
	public Result<String> startProcess(@Validated @RequestBody StartProcessInstanceVo startProcessInstanceVo);


	/**
	 * 审批任务
	 *
	 * @param params 参数
	 * @return
	 */
	@ApiOperation("审批任务")
	@PostMapping(value = "/rest/formdetail/complete")
	public Result<String> complete(@RequestBody @Validated CompleteTaskVo params);

	/**
	 * 根据流程实例id查询自定义任务属性表
	 *
	 * @param processInstanceId 参数
	 * @return
	 */
	@ApiOperation("根据流程实例id查询待办任务")
	@GetMapping(value = "/rest/extension/task/get-extension-tasks")
	public PageModel<TaskExtensionVo> getExtensionTaskByProcessInstanceId(@RequestParam(required = false) String processInstanceId,@SpringQueryMap Query query);


	/**
	 * 根据流程实例id查询历史自定义任务属性表(已办代办查询)
	 *
	 * @param processInstanceId 参数
	 * @return
	 */
	@ApiOperation("根据流程实例id查询已办任务")
	@GetMapping(value = "/rest/extension/task/get-finish-extension-tasks")
	public PageModel<TaskExtensionVo> getFinishExtensionTaskByProcessInstanceId(@RequestParam(required = false) String processInstanceId,@SpringQueryMap Query query);


	/**
	 * 根据流程实例id查询历史自定义任务属性表(已办代办查询)
	 *
	 * @param processInstanceId 参数
	 * @return
	 */
	@ApiOperation("根据流程实例id查询全部任务")
	@GetMapping(value = "/rest/extension/task/get-all-extension-tasks")
	public PageModel<TaskExtensionVo> getAllExtensionTaskByProcessInstanceId(@RequestParam(required = false) String processInstanceId,@SpringQueryMap Query query);

	/**
	 * 获取待办任务列表
	 *
	 * @param feginQueryVo 参数
	 * @return
	 */
	@ApiOperation("获取待办任务列表")
	@GetMapping(value = "/rest/task/get-applying-tasks")
	public PageModel<TaskVo> getApplyingTasks(@SpringQueryMap FeginQueryVo feginQueryVo);


	/**
	 * 获取已办任务列表
	 *
	 * @param feginQueryVo 参数
	 * @return
	 */
	@ApiOperation("获取已办任务列表")
	@GetMapping(value = "/rest/task/get-applyed-tasks")
	public PageModel<TaskVo> getApplyedTasks(@SpringQueryMap FeginQueryVo feginQueryVo);


	/**
	 * 获取待办已办全部任务列表
	 *
	 * @param feginQueryVo 参数
	 * @return
	 */
	@ApiOperation("获取全部任务列表(包括已办待办)")
	@GetMapping(value = "/rest/task/get-all-tasks")
	public PageModel<TaskVo> getAllTasks(@SpringQueryMap FeginQueryVo feginQueryVo);

	/**
	 * 跳转
	 *
	 * @param params 参数
	 * @return
	 */
	@ApiOperation("跳转")
	@PostMapping(value = "/rest/formdetail/doJumpStep")
	public Result<String> doJumpStep(@Validated @RequestBody BackTaskVo params);

	/**
	 * 驳回
	 *
	 * @param params 参数
	 * @return
	 */
	@ApiOperation("驳回")
	@PostMapping(value = "/rest/formdetail/doBackStep")
	public Result<String> doBackStep(@Validated @RequestBody PreBackTaskVo params);


	/**
	 * 终止
	 *
	 * @param params 参数
	 * @return
	 */
	@ApiOperation("终止")
	@PostMapping(value = "/rest/formdetail/stopProcess")
	public Result<String> stopProcess(@Validated @RequestBody EndProcessVo params);


	/**
	 * 获取可驳回节点列表
	 *
	 * @param processInstanceId 流程实例id
	 * @return
	 */
	@ApiOperation("获取可驳回节点列表")
	@GetMapping(value = "/rest/formdetail/getBackNodesByProcessInstanceId/{processInstanceId}/{taskId}")
	public Result<List<FlowNodeVo>> getBackNodesByProcessInstanceId(@PathVariable(value = "processInstanceId") String processInstanceId,
																	@PathVariable(value = "taskId") String taskId);

	//TODO 流程所有节点接口


	/**
	 * 获取当前以及下一任务节点
	 * @param node
	 * @param taskId
	 * @return
	 */
	@ApiOperation("获取当前以及下一任务节点信息")
	@GetMapping(value = "/rest/processInstance/nextFlowNode")
	public Result nextFlowNode(@RequestParam String node, @RequestParam String taskId);


	/**
	 * 获取流程所有节点
	 *
	 * @param processKey 流程实例id
	 * @return
	 */
	@ApiOperation("获取流程所有节点")
	@GetMapping(value = "/getAllNodeList/{processKey}")
	public Result<List<FlowElementDto>> getBackNodesByProcessInstanceId(@PathVariable(value = "processKey") String processKey);

	/**
	 * 根据流程实例ID获取当前流程所有节点信息
	 * @param processInstanceId
	 * @return
	 */
	@ApiOperation("根据流程实例ID获取当前流程所有节点信息")
	@GetMapping(value = "/rest/processInstance/getAllNodeListByProcessInstanceId")
	public Result<List<FlowElementDto>> getAllNodeListByProcessInstanceId(@RequestParam String processInstanceId);


	@ApiOperation("查询model流程列表(最新)")
	@GetMapping(value = "/rest/model/page-model")
	public Result<PageModel<AbstractModel>> pageModel(@SpringQueryMap Query query);

	/**
	 * 签收
	 *
	 * @param params 参数
	 * @return
	 */
	@ApiOperation("签收")
	@PostMapping(value = "/rest/formdetail/claimTask")
	public Result<String> claimTask(@Validated @RequestBody ClaimTaskVo params);

	/**
	 * 反签收
	 *
	 * @param params 参数
	 * @return
	 */
	@ApiOperation("反签收")
	@PostMapping(value = "/rest/formdetail/unClaimTask")
	public Result<String> unClaimTask(@Validated @RequestBody ClaimTaskVo params);

	/**
	 * 委派
	 *
	 * @param params 参数
	 * @return
	 */
	@ApiOperation("委派")
	@PostMapping(value = "/rest/formdetail/delegateTask")
	public Result<String> delegateTask(@Validated @RequestBody DelegateTaskVo params);

	/**
	 * 转办
	 *
	 * @param params 参数
	 * @return
	 */
	@ApiOperation("转办")
	@PostMapping(value = "/rest/formdetail/turnTask")
	public Result<String> turnTask(@Validated @RequestBody TurnTaskVo params);

	/**
	 * 激活或者挂起流程定义
	 * @param id
	 * @return
	 */
	@ApiOperation("激活或者挂起")
	@PostMapping(value = "/rest/definition/saDefinitionById")
	public Result<String> saDefinitionById(@RequestParam String id,@RequestParam int suspensionState,@RequestParam int overtime);


	/**
	 * 通过流程实例id来更新最大审批天数值
	 *
	 * @param params 参数
	 * @return
	 */
	@ApiOperation("通过流程实例id和任务Id来更新最大审批天数值")
	@PostMapping(value = "/rest/extension/task/update-extension-tasks")
	public Result<String> updateExtensionCustomTaskById(@RequestBody ExtensionTaskQueryVo params);

	/**
	 * 根据流程ID和任务ID来更新督办信息
	 *
	 * @param params 参数
	 * @return
	 */
	@ApiOperation("根据流程实例id和任务id来更新督办信息")
	@PostMapping(value = "/rest/extension/task/update-supervision-task")
	public Result<String> updateSupervisionTask(@RequestBody TaskExtensionVo params);

	/**
	 * 多实例加签
	 *
	 * @param params 参数
	 * @return
	 */
	@ApiOperation("并联加签")
	@PostMapping(value = "/rest/formdetail/addMultiInstanceExecution")
	public Result<String> addMultiInstanceExecution(@Validated @RequestBody AddSignTaskVo params);

	/**
	 * 向前加签
	 *
	 * @param params 参数
	 * @return
	 */
	@ApiOperation("向前加签")
	@PostMapping(value = "/rest/formdetail/beforeAddSignTask")
	public Result<String> beforeAddSignTask(@Validated @RequestBody AddSignTaskVo params);


	/**
	 * 向后加签
	 *
	 * @param params 参数
	 * @return
	 */
	@ApiOperation("向后加签")
	@PostMapping(value = "/rest/formdetail/afterAddSignTask")
	public Result<String> afterAddSignTask(@Validated @RequestBody AddSignTaskVo params);


	/**
	 * 查询用户组
	 *
	 * @param groupIds 组ids
	 * @return
	 */
	@ApiOperation("查询用户组")
	@GetMapping("/rest/user/queryUserListByGroupIds")
	public Result<String> queryUserListByGroupIds(@RequestParam(value = "groupIds") List<String> groupIds);

	/**
	 * 获取抄送任务列表
	 *
	 * @param noticeTaskQueryDto 参数
	 * @return
	 */
	@ApiOperation("获取抄送任务列表")
	@GetMapping(value = "/rest/task/getNoticeTasks")
	public PageModel<NoticeTask> getNoticeTasks(@SpringQueryMap NoticeTaskQueryDto noticeTaskQueryDto);

	/**
	 * 删除流程实例haha
	 *
	 * @param processInstanceId 参数
	 * @return
	 */
	@ApiOperation("根据流程实例id删除流程信息")
	@GetMapping(value = "/rest/processInstance/deleteProcessInstanceById/{processInstanceId}")
	public Result<String> deleteProcessInstanceById(@PathVariable(value = "processInstanceId") String processInstanceId);


	/**
	 * 添加用户
	 * @param user  用户
	 * @return
	 */
	@ApiOperation("插入用户")
	@PostMapping("/app/rest/saveUser")
	public Result<String> saveUser(@SpringQueryMap UserEntityVo user);

	/**
	 * 根据userId更新用户
	 * @param user  用户
	 * @return
	 */
	@ApiOperation("根据userId更新用户")
	@PostMapping("/app/rest/updateUser")
	public Result<String> updateUser(@SpringQueryMap UserEntityVo user);

	/**
	 * 根据userId删除用户
	 * @param userId  用户id
	 * @return
	 */
	@ApiOperation("根据userId删除用户")
	@PostMapping("/app/rest/deleteUser")
	public Result<String> updateUser(@RequestParam("userId") String userId);

}

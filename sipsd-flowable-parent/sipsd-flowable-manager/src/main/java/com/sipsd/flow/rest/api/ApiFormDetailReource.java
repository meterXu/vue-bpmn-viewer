package com.sipsd.flow.rest.api;

import cn.hutool.core.collection.CollUtil;
import com.sipsd.flow.bean.FlowElementVo;
import com.sipsd.flow.service.flowable.IFlowableCommentService;
import com.sipsd.flow.service.flowable.IFlowableProcessInstanceService;
import com.sipsd.flow.service.flowable.IFlowableTaskService;
import com.sipsd.flow.utils.Result;
import com.sipsd.flow.vo.flowable.*;
import com.sipsd.flow.vo.flowable.ret.CommentVo;
import com.sipsd.flow.vo.flowable.ret.FlowNodeVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.apachecommons.CommonsLog;
import org.apache.commons.lang3.StringUtils;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.flowable.engine.runtime.ProcessInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

/**
 * @author : gaoqiang
 * @title: : ApiFlowCommentReource
 * @projectName : flowable
 * @description: 备注
 * @date : 2019/11/2413:13
 */
@Api(tags = { "流程操作" })
@RestController
@RequestMapping("/rest/formdetail")
@CommonsLog
public class ApiFormDetailReource extends BaseResource {
	private static final Logger LOGGER = LoggerFactory.getLogger(ApiFormDetailReource.class);
	@Autowired
	private IFlowableCommentService flowableCommentService;
	@Autowired
	private IFlowableTaskService flowableTaskService;
	@Autowired
	private IFlowableProcessInstanceService flowableProcessInstanceService;

	/**
	 * 通过流程实例id获取审批意见
	 *
	 * @param processInstanceId 流程实例id
	 * @return
	 */
	@ApiOperation("通过流程实例id获取审批意见")
	@GetMapping("/commentsByProcessInstanceId")
	public List<CommentVo> commentsByProcessInstanceId(String processInstanceId) {
		List<CommentVo> datas = flowableCommentService.getFlowCommentVosByProcessInstanceId(processInstanceId);
		return datas;
	}

	/**
	 * 流程启动
	 *
	 * @param startProcessInstanceVo 参数
	 * @return
	 */
	@ApiOperation("流程启动")
	@PostMapping(value = "/startProcess")
	public Result<String> startProcess(@Validated @RequestBody StartProcessInstanceVo startProcessInstanceVo) {

//		User user = SecurityUtils.getCurrentUserObject();
//		startProcessInstanceVo.setCreator(user.getId());
//		startProcessInstanceVo.setCurrentUserCode(user.getId());
//		startProcessInstanceVo.setFormName("请假流程");
//		startProcessInstanceVo.setSystemSn("flow");
//		startProcessInstanceVo.setProcessDefinitionKey("leave");

		Result<ProcessInstance> startResult = flowableProcessInstanceService
				.startProcessInstanceByKey(startProcessInstanceVo);
		if (startResult.getCode() == Result.SUCCESS) {
			String processInstanceId = startResult.getData().getProcessInstanceId();
			return Result.ok(processInstanceId, startResult.getMessage());
		} else {
			return Result.failed(startResult.getMessage());
		}
	}

	/**
	 * 审批任务
	 *
	 * @param params 参数
	 * @return
	 */
	@ApiOperation("审批任务")
	@PostMapping(value = "/complete")
	public Result<String> complete(@RequestBody @Validated CompleteTaskVo params) {
		boolean flag = this.isSuspended(params.getProcessInstanceId());
		Result<String> result = null;
		if (flag) {
			// params.setUserCode(this.getLoginUser().getId());
			result = flowableTaskService.complete(params);
		} else {
			result = Result.failed("流程已挂起，请联系管理员激活!");
		}
		return result;
	}

	/**
	 * 终止
	 *
	 * @param params 参数
	 * @return
	 */
	@ApiOperation("终止")
	@PostMapping(value = "/stopProcess")
	public Result<String> stopProcess(@Validated @RequestBody EndProcessVo params) {
		boolean flag = this.isSuspended(params.getProcessInstanceId());
		Result<String> result = null;
		if (flag) {
			// params.setUserCode(this.getLoginUser().getId());
			result = flowableProcessInstanceService.stopProcessInstanceById(params);
		} else {
			result = Result.failed("流程已挂起，请联系管理员激活!");
		}
		return result;
	}

	/**
	 * 撤回
	 *
	 * @param params 参数
	 * @return
	 */
	@ApiOperation("撤回")
	@PostMapping(value = "/revokeProcess")
	public Result<String> revokeProcess(@Validated @RequestBody RevokeProcessVo params) {
		// params.setUserCode(this.getLoginUser().getId());
		Result<String> Result = flowableProcessInstanceService.revokeProcess(params);
		return Result;
	}

	/**
	 * 转办
	 *
	 * @param params 参数
	 * @return
	 */
	@ApiOperation("转办")
	@PostMapping(value = "/turnTask")
	public Result<String> turnTask(@Validated @RequestBody TurnTaskVo params) {
		Result<String> result = null;
		if (!StringUtils.isBlank(params.getTurnToUserId())) {
			// params.setUserCode(this.getLoginUser().getId());
			// params.setTurnToUserId(userCodes[0]);
			result = flowableTaskService.turnTask(params);
		} else {
			result = Result.failed("请选择人员");
		}
		return result;
	}

	/**
	 * 委派
	 *
	 * @param params 参数
	 * @return
	 */
	@ApiOperation("委派")
	@PostMapping(value = "/delegateTask")
	public Result<String> delegateTask(@Validated @RequestBody DelegateTaskVo params) {
		Result<String> result = null;
		if (!StringUtils.isBlank(params.getDelegateUserCode())) {
			result = flowableTaskService.delegateTask(params);
		} else {
			result = Result.failed("请选择人员");
		}
		return result;
	}

	/**
	 * 签收
	 *
	 * @param params 参数
	 * @return
	 */
	@ApiOperation("签收")
	@PostMapping(value = "/claimTask")
	public Result<String> claimTask(@Validated @RequestBody ClaimTaskVo params) {
		// params.setUserCode(this.getLoginUser().getId());
		Result<String> Result = flowableTaskService.claimTask(params);
		return Result;
	}

	/**
	 * 反签收
	 *
	 * @param params 参数
	 * @return
	 */
	@ApiOperation("反签收")
	@PostMapping(value = "/unClaimTask")
	public Result<String> unClaimTask(@Validated @RequestBody ClaimTaskVo params) {
		// params.setUserCode(this.getLoginUser().getId());
		Result<String> Result = flowableTaskService.unClaimTask(params);
		return Result;
	}

	/**
	 * 向前加签
	 *
	 * @param params 参数
	 * @return
	 */
	@ApiOperation("向前加签")
	@PostMapping(value = "/beforeAddSignTask")
	public Result<String> beforeAddSignTask(@Validated @RequestBody AddSignTaskVo params) {
		Result<String> result = null;
//		if (userCodes != null && userCodes.length > 0) {
//			// params.setUserCode(this.getLoginUser().getId());
//			params.setSignPersoneds(Arrays.asList(userCodes));
//			result = flowableTaskService.beforeAddSignTask(params);
//		} else {
//			result = Result.failed("请选择人员");
//		}

		result = flowableTaskService.beforeAddSignTask(params);
		return result;
	}

	/**
	 * 向后加签
	 *
	 * @param params 参数
	 * @return
	 */
	@ApiOperation("向后加签")
	@PostMapping(value = "/afterAddSignTask")
	public Result<String> afterAddSignTask(@Validated @RequestBody AddSignTaskVo params) {
		Result<String> result = null;
//		if (userCodes != null && userCodes.length > 0) {
//			// params.setUserCode(this.getLoginUser().getId());
//			params.setSignPersoneds(Arrays.asList(userCodes));
//			result = flowableTaskService.afterAddSignTask(params);
//		} else {
//			result = Result.failed("请选择人员");
//		}
		result = flowableTaskService.afterAddSignTask(params);
		return result;
	}


	/**
	 * 多实例加签
	 *
	 * @param params 参数
	 * @return
	 */
	@ApiOperation("多实例加签")
	@PostMapping(value = "/addMultiInstanceExecution")
	public Result<String> addMultiInstanceExecution(@Validated @RequestBody AddSignTaskVo params) {
		Result<String> result = null;
		List<String> signPersoneds = params.getSignPersoneds();
		if (CollUtil.isNotEmpty(signPersoneds)) {
			result = flowableTaskService.addMultiInstanceExecution(params);
		} else {
			result = Result.failed("请选择加签人员");
		}
		return result;
	}

	@ApiOperation("流程图片")
	@GetMapping(value = "/image/{processInstanceId}")
	public void image(@PathVariable String processInstanceId, HttpServletResponse response) {
		try {
			byte[] b = flowableProcessInstanceService.createImage(processInstanceId);
			response.setHeader("Content-type", "text/xml;charset=UTF-8");
			response.getOutputStream().write(b);
		} catch (Exception e) {
			LOGGER.error("ApiFormDetailReource-image:" + e);
			e.printStackTrace();
		}
	}

	/**
	 * 生成流程图
	 *
	 * @param processInstanceId 流程实例IDD
	 */
	@GetMapping(value = "getprocessDiagram/{processInstanceId}")
	@ResponseBody
	@ApiOperation("根据流程编号（来自启动）生成流程图")
	public void genProcessDiagram(@ApiParam(value = "流程ID") @PathVariable("processInstanceId") String processInstanceId,
			HttpServletResponse httpServletResponse) throws Exception {
		InputStream in = null;
		OutputStream out = null;
		try {
			byte[] b = flowableProcessInstanceService.createImage(processInstanceId);
			out = httpServletResponse.getOutputStream();
			out.write(b);
			IOUtils.copy(in, out);
		} catch (Exception e) {
			log.error("processInstanceId:【" + processInstanceId + "】生成流程图:" + e.getMessage());
			log.debug("错误堆栈:", e);
		} finally {
			if (in != null) {
				IOUtils.closeQuietly(in);
			}
			if (out != null) {
				IOUtils.closeQuietly(out);
			}
		}
	}

	/**
	 * 生成流程图
	 *
	 * @param processInstanceId 流程实例IDD
	 */
	@GetMapping(value = "getprocessXml/{processInstanceId}")
	@ResponseBody
	@ApiOperation("根据流程编号（来自启动）生成流程xml")
	public void getprocessXml(@ApiParam(value = "流程ID") @PathVariable("processInstanceId") String processInstanceId,
								  HttpServletResponse httpServletResponse) throws Exception {
		InputStream in = null;
		OutputStream out = null;
		try {
			byte[] b = flowableProcessInstanceService.createXml(processInstanceId);
			out = httpServletResponse.getOutputStream();
			out.write(b);
			IOUtils.copy(in, out);
		} catch (Exception e) {
			log.error("processInstanceId:【" + processInstanceId + "】生成xml:" + e.getMessage());
			log.debug("错误堆栈:", e);
		} finally {
			if (in != null) {
				IOUtils.closeQuietly(in);
			}
			if (out != null) {
				IOUtils.closeQuietly(out);
			}
		}
	}

	/**
	 * 获取流程所有节点
	 * 
	 * @param processKey 流程实例id
	 * @return
	 */
	@ApiOperation("获取流程所有节点")
	@GetMapping(value = "/getAllNodeList/{processKey}")
	public Result<List<FlowElementVo>> getBackNodesByProcessInstanceId(@PathVariable String processKey) {
		Result<List<FlowElementVo>> result = flowableTaskService.getProcessNodeList(processKey);
		result.setMessage("查询返回节点成功");
		return result;

	}

	/**
	 * 获取可驳回节点列表
	 * 
	 * @param processInstanceId 流程实例id
	 * @return
	 */
	@ApiOperation("获取可驳回节点列表")
	@GetMapping(value = "/getBackNodesByProcessInstanceId/{processInstanceId}/{taskId}")
	public Result<List<FlowNodeVo>> getBackNodesByProcessInstanceId(@PathVariable String processInstanceId,
			@PathVariable String taskId) {
		List<FlowNodeVo> datas = flowableTaskService.getBackNodesByProcessInstanceId(processInstanceId, taskId);
		Result<List<FlowNodeVo>> result = Result.sucess("查询返回节点成功");
		result.setData(datas);
		return result;

	}

	/**
	 * 跳转
	 * 
	 * @param params 参数
	 * @return
	 */
	@ApiOperation("跳转")
	@PostMapping(value = "/doJumpStep")
	public Result<String> doJumpStep(@Validated @RequestBody BackTaskVo params) {
		// params.setUserCode(this.getLoginUser().getId());
		Result<String> result = flowableTaskService.jumpToStepTask(params);
		return result;
	}

	/**
	 * 驳回
	 *
	 * @param params 参数
	 * @return
	 */
	@ApiOperation("驳回")
	@PostMapping(value = "/doBackStep")
	public Result<String> doBackStep(@Validated @RequestBody PreBackTaskVo params) {
		Result<String> result = flowableTaskService.backToStepTask(params);
		return result;
	}

	
    /**
     * 获取流程动态表单信息
     * @param taskId
     * @return
     */
    @GetMapping(value = "/formData/taskId")
    public Result formData(@RequestParam String taskId) {
    	return flowableProcessInstanceService.taskFormData(taskId);
    }



}

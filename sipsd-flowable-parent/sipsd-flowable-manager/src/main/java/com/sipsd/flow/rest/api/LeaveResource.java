package com.sipsd.flow.rest.api;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.idm.api.User;
import org.flowable.ui.common.security.SecurityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sipsd.cloud.common.core.util.Result;
import com.sipsd.flow.common.UUIDGenerator;
import com.sipsd.flow.common.page.PageModel;
import com.sipsd.flow.common.page.Query;
import com.sipsd.flow.model.leave.Leave;
import com.sipsd.flow.service.flowable.IFlowableProcessInstanceService;
import com.sipsd.flow.service.leave.ILeaveService;
import com.sipsd.flow.vo.flowable.StartProcessInstanceVo;

/**
 * @author : admin
 * @date : 2019-11-20 19:06:48 description : 请假单Controller
 */
@RestController
@RequestMapping("/rest/leave")
public class LeaveResource extends BaseResource {
	private static Logger logger = LoggerFactory.getLogger(LeaveResource.class);

	private final String nameSpace = "leave";

	@Autowired
	private ILeaveService LeaveService;
	@Autowired
	private IFlowableProcessInstanceService flowableProcessInstanceService;

	@GetMapping("/list")
	public PageModel<Leave> list(Leave Leave, Query query, String sessionId) {
		PageModel<Leave> pm = null;
		try {
			pm = this.LeaveService.getPagerModelByQuery(Leave, query);
		} catch (Exception e) {
			logger.error("LeaveController-ajaxList:", e);
			e.printStackTrace();
		}
		return pm;
	}

	// 添加
	@PostMapping("/add")
	public Result add(Leave leave, String sessionId) {
		Result result = Result.failed("添加失败");
		try {
			String leaveId = UUIDGenerator.generate();
			leave.setId(leaveId);
			StartProcessInstanceVo startProcessInstanceVo = new StartProcessInstanceVo();
			startProcessInstanceVo.setBusinessKey(leaveId);
			User user = SecurityUtils.getCurrentUserObject();
			//startProcessInstanceVo.setCreator(user.getId());
			startProcessInstanceVo.setCurrentUserCode(user.getId());
			startProcessInstanceVo.setFormName("请假流程");
			startProcessInstanceVo.setSystemSn("flow");
			startProcessInstanceVo.setProcessDefinitionKey("leave");
			Map<String, Object> variables = new HashMap<>();
			variables.put("days", leave.getDays());
			startProcessInstanceVo.setVariables(variables);
			// 设置三个人作为多实例的人员
			List<String> userList = new ArrayList<>();
			userList.add("00000005");
			userList.add("00000006");
			variables.put("userList", userList);

			Result<ProcessInstance> returnStart = flowableProcessInstanceService
					.startProcessInstanceByKey(startProcessInstanceVo);
			if (returnStart.getCode() == Result.SUCCESS) {
				String processInstanceId = returnStart.getData().getProcessInstanceId();
				leave.setProcessInstanceId(processInstanceId);
				this.LeaveService.insertLeave(leave);
				result = Result.sucess("添加成功");
			} else {
				result = Result.failed(returnStart.getMessage());
			}
		} catch (Exception e) {
			logger.error("LeaveController-add:", e);
			e.printStackTrace();
		}
		return result;
	}

	// 修改
	@PostMapping("/update")
	public Result update(Leave Leave, String sessionId) {
		Result result = Result.failed("修改失败");
		try {
			this.LeaveService.updateLeave(Leave);
			result = Result.sucess("修改成功");
		} catch (Exception e) {
			logger.error("LeaveController-update:", e);
			e.printStackTrace();
		}
		return result;
	}

	@PostMapping("/updateLeaveStatus")
	public void updateLeaveStatus(@RequestBody String json) {
		logger.error("修改状态" + json);
	}

}

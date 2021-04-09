package com.sipsd.flow.rest.v3.api;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.sipsd.flow.bean.Process;
import com.sipsd.flow.bean.*;
import com.sipsd.flow.bean.enums.AuditStatus;
import com.sipsd.flow.bean.enums.OperateType;
import com.sipsd.flow.service.v3.services.BPMDeploymentService;
import com.sipsd.flow.service.v3.services.ProcessService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.apachecommons.CommonsLog;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.flowable.bpmn.model.FlowElement;
import org.flowable.task.api.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * 工作流管理服务-v3-兼容旧版本
 * @author hul
 *
 */
@Controller
@RequestMapping(value = "/rpc/process")
@CommonsLog
@Api(tags={"工作流实例管理控制服务"})
public class RPCProcessController {
    @Autowired
    private ProcessService bpmWorkFlowProcessService;

    @Autowired
    private BPMDeploymentService bpmDeploymentService;
   

    /**
     *根据模型key 启动模型
     * @param paras    参数信息  必须包含业务类型bussesnessType和流程key processKey  和用户信息
     *                 任务ID（taskId）
     *                 用户ID（ userId）
     *                 子系统标志 type  默认xzsp
     *                 审批类型（OperateType 同意(AGREE)，拒绝 (REJECT)）
     *                 业务类型（ businessType，可以放事项ID）
     *                 业务ID（ businessID 可以放事务ID）

     */
    @PostMapping(value = "/startProcess")
    @ResponseBody
    @ApiOperation("启动流程")
    public  Map<String,Object> startWorkFlowBykey(@ApiParam(value = "paras")@RequestBody Map<String,Object> paras) {
        Map<String,Object> res =new HashMap<>();
        ProcessParam procesMap=new ProcessParam();
        //TODO  判断用户ID processKey 是否存在 不存在返回空对象
        String userId=paras.get("userId")==null?null:paras.get("userId").toString();
        if (StringUtils.isEmpty(userId)) {
            res.put("msg","启动流程失败,用户信息不存在");
            res.put("res","1");
            return res;

        }
        String processKey=paras.get("processKey")==null?null:paras.get("processKey").toString();
        if (StringUtils.isEmpty(processKey)) {
            res.put("msg","启动流程失败,流程Key不存在");
            res.put("res","1");
            return res;

        }
        String userName=paras.get("userName")==null?null:paras.get("userName").toString();
        String userGroup=paras.get("userGroup")==null?null:paras.get("userGroup").toString();
        User user=new User();
        user.setId(userId);
        user.setGroup(userGroup);
        user.setName(userName);
//        procesMap.setAuditors(Arrays.asList(new CandidateParam(userId, userName, CandidateType.USER)));
        procesMap.setType(paras.get("type")==null?"xzsp":paras.get("type").toString());
        procesMap.setBusinessType(paras.get("businessType")==null?null:paras.get("businessType").toString());
        procesMap.setBusinessID(paras.get("businessID")==null?null:paras.get("businessID").toString());
        procesMap.setProcessId(processKey);
        procesMap.setParams(paras);
        Map<String,Object> flowParas=new HashMap<>();
        Process newProcess=null;
        try {
            newProcess=bpmWorkFlowProcessService.start(user,procesMap);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            res.put("msg","启动流程失败：" +e.getMessage());
            res.put("res","0");
            res.put("paras",procesMap);
            res.put("data",newProcess);
            return res;
        }
        if (null == newProcess){
            res.put("msg","启动流程失败,流程实例不存在");
            res.put("res","1");
            return res;
        }
        res.put("msg","启动流程成功");
        res.put("res","0");
        res.put("data",newProcess);
        return res;

    }

    /**
     * 生成流程图
     *
     * @param processInstanceId 流程实例IDD
     */
    @GetMapping(value = "getprocessDiagram/{processInstanceId}")
    @ResponseBody
    @ApiOperation("根据流程编号（来自启动）生成流程图")
    public void genProcessDiagram(@ApiParam(value = "流程ID") @PathVariable("processInstanceId")String processInstanceId, HttpServletResponse httpServletResponse) throws Exception {
        InputStream in=null;
        OutputStream out = null;
        try {
            in=bpmWorkFlowProcessService.genProcessDiagram( processInstanceId);
            out = httpServletResponse.getOutputStream();
            IOUtils.copy(in, out);
        } catch(Exception e){
            log.error("processInstanceId:【"+processInstanceId+ "】生成流程图:"+e.getMessage());
            log.debug("错误堆栈:",e);
        }finally {
            if (in != null) {
                IOUtils.closeQuietly(in);
            }
            if (out != null) {
                IOUtils.closeQuietly(out);
            }
        }
    }
    /**
     *撤销流程
     * @param userId  当前用户
     * @param processInstanceId    流程实例ID
     * @param reason  撤销原因
     */
    @PostMapping(value = "/cancel")
    @ResponseBody
    @ApiOperation("撤销流程")
    public  Map<String,Object> cancel(@ApiParam(value = "当前用户") @RequestParam String userId,
                  @ApiParam(value = "流程实例ID")@RequestParam String  processInstanceId,
                                      @ApiParam(value = "撤销原因")@RequestParam String  reason ) {
        Map<String,Object> res =new HashMap<>();
        User user=new User();
        user.setId(userId);
        try {
            bpmWorkFlowProcessService.cancel(user,processInstanceId,reason);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            res.put("msg","撤销流程失败：" +e.getMessage());
            res.put("res","0");

            return res;
        }
        res.put("msg","撤销流程成功");
        res.put("res","1");
        return res;

    }

    /**
     * @param userId      当前登陆用户
     * @param auditType 类型  存放事项类型
     * @param pageNum   页码，从1开始
     * @param pageSize  一页多少个
     * @return 待我审批的流程列表
     */
    @GetMapping(value = "/waitAuditList")
    @ResponseBody
    @ApiOperation("待我审批的流程列表查询")
    public Object waitAuditList(@ApiParam(value = "当前用户") @RequestParam String userId,
                                @ApiParam(value = "当前用户") @RequestParam(required = false) String groupId,
                                @ApiParam(value = "审批类型")  @RequestParam(required = false) String auditType,
                                @ApiParam(value = "起始页数")  @RequestParam(required = false) int pageNum,
                                @ApiParam(value = "页码条数")  @RequestParam(required = false)int pageSize) {
        JSONArray tasksJson=null;
        List<Process> tasks=null;
        tasksJson=new JSONArray();
        if(StringUtils.isEmpty(userId)){
            return  tasksJson;
        }

        User user=new User();
        user.setId(userId);
        user.setGroup(groupId);
        if(pageNum<1){
            pageNum=1;
        }
        if(pageSize<1){
            pageSize=10;
        }
        try {
            tasks = bpmWorkFlowProcessService.waitAuditList(user,auditType,pageNum,pageSize);
            if(CollectionUtils.isEmpty(tasks)){
                log.info("没有对应的任务");
                return null;
            }
            tasksJson=new JSONArray();
            for (Process task : tasks) {
                System.out.println(task.toString());
                JSONObject taskJson=new JSONObject();
                taskJson.put("taskId", task.getTaskId());
                taskJson.put("taskName", task.getTaskName());
                taskJson.put("taskProcessInstanceId", task.getProcessInstanceId());
                tasksJson.add(taskJson);
            }

        } catch (Exception e) {
            log.error("获取流程的审批管理列表错误:"+e.getMessage());
            log.debug("错误堆栈:",e);
        }
       
        return tasksJson;
    }



    /**
     * @param user        当前登陆用户
     * @param auditType   类型
     * @param auditStatus 流程状态
     * @param pageNum     页码，从1开始
     * @param pageSize    一页多少个
     * @return 我已审批的流程列表
     */
    @GetMapping(value = "/auditList")
    @ResponseBody
    @ApiOperation("我已审批的流程列表查询")
    public Object auditList(@ApiParam(value = "当前用户") @RequestBody User user,
                                @ApiParam(value = "审批类型") @RequestBody String auditType,
                                @ApiParam(value = "审批状态") @RequestBody AuditStatus auditStatus,
                                @ApiParam(value = "起始页数") @RequestBody int pageNum,
                                @ApiParam(value = "页码条数") @RequestBody int pageSize) {
        JSONArray tasksJson=null;
        List<Process> tasks=null;
        try {
            tasks = bpmWorkFlowProcessService.auditList(user,auditType,auditStatus,pageNum,pageSize);
            if(CollectionUtils.isEmpty(tasks)){
                log.info("没有对应的任务");
                return null;
            }
            tasksJson=new JSONArray();
            for (Process task : tasks) {
                System.out.println(task.toString());
                JSONObject taskJson=new JSONObject();
                taskJson.put("taskId", task.getTaskId());
                taskJson.put("taskName", task.getTaskName());
                taskJson.put("taskProcessInstanceId", task.getProcessInstanceId());
                tasksJson.add(taskJson);
            }

        } catch (Exception e) {
            log.error("获取流程的审批管理列表错误:"+e.getMessage());
            log.debug("错误堆栈:",e);
        }

        return tasksJson;
    }


    /**
     * @param userId        当前登陆用户
//     * @param auditType   类型
//     * @param auditStatus 流程状态
     * @param pageNum     页码，从1开始
     * @param pageSize    一页多少个
     * @return 查看我创建的流程列表
     */
    @GetMapping(value = "/mineList")
    @ResponseBody
    @ApiOperation("查看我创建的流程列表")
    public Object mineList(@ApiParam(value = "当前用户") @RequestParam String userId,
                            @ApiParam(value = "审批类型") @RequestParam(required = false)  String auditType,
                            @ApiParam(value = "审批状态") @RequestParam(required = false) String auditStatus,
                            @ApiParam(value = "起始页数")  @RequestParam(required = false)  int pageNum,
                            @ApiParam(value = "页码条数")  @RequestParam(required = false) int pageSize) {
        JSONArray tasksJson=null;
        List<Process> tasks=null;
        tasksJson=new JSONArray();
        if(StringUtils.isEmpty(userId)){
            return  tasksJson;
        }

        User user=new User();
        user.setId(userId);
        if(pageNum<1){
            pageNum=1;
        }
        if(pageSize<1){
            pageSize=10;
        }
        try {
            tasks = bpmWorkFlowProcessService.mineList(user,null,null,pageNum,pageSize);
            if(CollectionUtils.isEmpty(tasks)){
                log.info("没有对应的任务");
                return null;
            }

            for (Process task : tasks) {
                System.out.println(task.toString());
                JSONObject taskJson=new JSONObject();
                taskJson.put("taskId", task.getTaskId());
                taskJson.put("taskName", task.getTaskName());
                taskJson.put("taskProcessInstanceId", task.getProcessInstanceId());
                tasksJson.add(taskJson);
            }

        } catch (Exception e) {
            log.error("获取流程的审批管理列表错误:"+e.getMessage());
            log.debug("错误堆栈:",e);
        }

        return tasksJson;
    }


    /**
     * @param userId   当前用户
     * @param groupId   当前用户组  两个 条件是或
     * @param taskId 用户任务id
     * @return 当前用户是否是这个申请的用户任务的审批人、候选人或者候选组
     */
    @PostMapping(value = "/isAssigneeOrCandidate")
    @ResponseBody
    @ApiOperation("当前用户是否可以操作此任务")
    public Object isAssigneeOrCandidate(@ApiParam(value = "当前用户") @RequestParam String userId,
                                        @ApiParam(value = "当前用户组") @RequestParam(required = false) String groupId,
                           @ApiParam(value = "任务ID") @RequestParam String taskId) {
        JSONObject res=new JSONObject();

        if(StringUtils.isEmpty(userId)){
            res.put("msg","查询用户的ID 不能为空");
            res.put("res","1");
            return  res;
        }

        User user=new User();
        user.setId(userId);
        if(!StringUtils.isEmpty(groupId)){
            user.setGroup(groupId);
        }
        boolean isAssigneeOrCandidate=false;
        try {
            isAssigneeOrCandidate = bpmWorkFlowProcessService.isAssigneeOrCandidate(user,taskId);
            res.put("msg","查询用户是否是候选人：" +isAssigneeOrCandidate);
            res.put("isAssigneeOrCandidate",isAssigneeOrCandidate);
            res.put("res","0");
        } catch (Exception e) {
            log.error("获取流程的审批管理列表错误:"+e.getMessage());
            log.debug("错误堆栈:",e);
            res.put("msg","获取流程的审批管理列表错误:"+e.getMessage());
            res.put("res","1");
        }

        return res;
    }

    /**
     * 审批操作
     * 因为流程引擎的用户任务完成后，会立马触发下一个流程，所以这里需要先处理，并且处于两个事物中
     *
     * @param paras        参数列表  包含：
     *                     任务ID（taskId）
     *                     用户ID（ userId）
     *                     用户姓名（userName）
     *                     用户组（userGroup）
     *                     审批类型（operateType 同意(AGREE)，拒绝 (REJECT)）
     *                     业务类型（bussesnessType，可以放事项ID）
     *                     业务ID（可以放事务ID）
     *                     审批原因描述（reason）
     */
    @PostMapping(value = "/agreeOrReject")
    @ResponseBody
    @ApiOperation("审批或拒绝服务")
    public Object agreeOrReject(@ApiParam(value = "paras")@RequestBody Map<String,Object> paras) {
        JSONObject res=new JSONObject();
        ReasonParam reasonParam =new ReasonParam();
        ProcessParam procesMap=new ProcessParam();
        //TODO  判断用户ID processKey 是否存在 不存在返回空对象
        String userId=paras.get("userId")==null?null:paras.get("userId").toString();
        if (StringUtils.isEmpty(userId)) {
            res.put("msg","启动流程失败,用户信息不存在");
            res.put("res","1");
            return res;

        }
        String taskId=paras.get("taskId")==null?null:paras.get("taskId").toString();

        if (StringUtils.isEmpty(taskId)) {
            res.put("msg","启动流程失败,流程Key不存在");
            res.put("res","1");
            return res;

        }
        Task currentTask=bpmWorkFlowProcessService.getRunTimeTaskByID(taskId);
        if (null==currentTask) {
            res.put("msg","启动流程失败,流程Key不存在");
            res.put("res","1");
            return res;

        }
        String userName=paras.get("userName")==null?null:paras.get("userName").toString();
        String userGroup=paras.get("userGroup")==null?null:paras.get("userGroup").toString();
        String businessType=paras.get("businessType")==null?null:paras.get("businessType").toString();
        String businessID=paras.get("businessID")==null?null:paras.get("businessID").toString();
        User user=new User();
        user.setId(userId);
        user.setGroup(userGroup);
        user.setName(userName);
        String operateTypeString=paras.get("operateType")==null?null:paras.get("operateType").toString();

        OperateType operateType=null;
        if("AGREE".equals(operateTypeString)){
            //TODO  测试阶段都当成同意 以后可以增加处理逻辑
            operateType=OperateType.AGREE;
        }else{
            operateType=OperateType.REJECT;
        }

        String reason=paras.get("reason")==null?null:paras.get("reason").toString();

        try {
            if(StringUtils.isEmpty(reason)){
                reason=operateType.name();
            }
            reasonParam.setText(reason);

            String procInstId=currentTask.getProcessInstanceId();
            bpmWorkFlowProcessService.agreeOrRejectOP(taskId,user,operateType,reasonParam);
            //TODO  处理成功之后，根据taskID 查询下一步的task 列表
            List<Task> waitForTasks=bpmWorkFlowProcessService.getTaskListByProcessInstanceID(procInstId);

            boolean isFinish=bpmWorkFlowProcessService.isFinishedProcessInstanceID(procInstId);
            if(!CollectionUtils.isEmpty(waitForTasks)){
                List<HashMap> waitForTaskArray=new ArrayList<>();
                for(Task task:waitForTasks){
                    HashMap taskJson=new HashMap();
                    taskJson.put("taskId",task.getId());
                    taskJson.put("taskName",task.getName());
                    taskJson.put("assignee",task.getAssignee());
                    waitForTaskArray.add(taskJson);
                }
                res.put("waitForTasks",waitForTaskArray);
            }
            res.put("msg","审批成功");
            res.put("isFinish",isFinish);
            res.put("res","0");
        } catch (Exception e) {
            log.error("审批错误:"+e.getMessage());
            log.debug("错误堆栈:",e);
            res.put("msg","审批错误:"+e.getMessage());
            res.put("res","1");
        }

        return res;
    }

    /**
     * 审批操作
     * 因为流程引擎的用户任务完成后，会立马触发下一个流程，所以这里需要先处理，并且处于两个事物中
     *
     * @param paras        参数列表  包含：
     *                     任务ID（taskId）
     *                     用户ID（ userId）
     *                     用户姓名（userName）
     *                     用户组（userGroup）
     *                     审批类型（operateType 同意(AGREE)，拒绝 (REJECT)）
     *                     业务类型（bussesnessType，可以放事项ID）
     *                     业务ID（可以放事务ID）
     *                     审批原因描述（reason）
     */
    @PostMapping(value = "/newAgreeOrReject")
    @ResponseBody
    @ApiOperation("审批或拒绝服务")
    public Object newAgreeOrReject(@ApiParam(value = "paras")@RequestBody Map<String,Object> paras) {
        JSONObject res=new JSONObject();
        ReasonParam reasonParam =new ReasonParam();
        ProcessParam procesMap=new ProcessParam();
        //TODO  判断用户ID processKey 是否存在 不存在返回空对象
        String userId=paras.get("userId")==null?null:paras.get("userId").toString();
        if (StringUtils.isEmpty(userId)) {
            res.put("msg","启动流程失败,用户信息不存在");
            res.put("res","1");
            return res;

        }
        String taskId=paras.get("taskId")==null?null:paras.get("taskId").toString();

        if (StringUtils.isEmpty(taskId)) {
            res.put("msg","启动流程失败,流程Key不存在");
            res.put("res","1");
            return res;

        }
        Task currentTask=bpmWorkFlowProcessService.getRunTimeTaskByID(taskId);
        if (null==currentTask) {
            res.put("msg","启动流程失败,流程Key不存在");
            res.put("res","1");
            return res;

        }
        String userName=paras.get("userName")==null?null:paras.get("userName").toString();
        String userGroup=paras.get("userGroup")==null?null:paras.get("userGroup").toString();
        String businessType=paras.get("businessType")==null?null:paras.get("businessType").toString();
        String businessID=paras.get("businessID")==null?null:paras.get("businessID").toString();
        User user=new User();
        user.setId(userId);
        user.setGroup(userGroup);
        user.setName(userName);
        String operateTypeString=paras.get("operateType")==null?null:paras.get("operateType").toString();

        OperateType operateType=null;
        if("AGREE".equals(operateTypeString)){
            //TODO  测试阶段都当成同意 以后可以增加处理逻辑
            operateType=OperateType.AGREE;
        }else{
            operateType=OperateType.REJECT;
        }

        String reason=paras.get("reason")==null?null:paras.get("reason").toString();

        try {
            if(StringUtils.isEmpty(reason)){
                reason=operateType.name();
            }
            reasonParam.setText(reason);

            String procInstId=currentTask.getProcessInstanceId();
            bpmWorkFlowProcessService.agreeOrRejectOP(taskId,user,operateType,reasonParam);
            //TODO  处理成功之后，根据taskID 查询下一步的task 列表
            List<TaskVo> waitForTasks=bpmWorkFlowProcessService.getTaskVoListByProcessInstanceID(procInstId);
            List<HashMap> waitForTaskArray=new ArrayList<>();
            boolean isFinish=bpmWorkFlowProcessService.isFinishedProcessInstanceID(procInstId);
            if(!CollectionUtils.isEmpty(waitForTasks))
            {
                for(TaskVo task:waitForTasks)
                {
                    if(CollectionUtils.isEmpty(task.getLinkVoList()))
                    {
                        //分配给单个用户/如果是组的话会分配到IdentityLinkVo
                        HashMap taskJson=new HashMap();
                        taskJson.put("taskId",task.getTaskId());
                        taskJson.put("taskName",task.getTaskName());
                        taskJson.put("assignee",task.getAssignee());
                        taskJson.put("groupId","");
                        taskJson.put("candidate",false);
                        waitForTaskArray.add(taskJson);
                    }
                    else
                    {
                        //分配给组/候选组或者多个候选人-组暂时不用
                        for(IdentityLinkVo identityLinkVo:task.getLinkVoList())
                        {
                            HashMap taskJson=new HashMap();
                            taskJson.put("taskId",task.getTaskId());
                            taskJson.put("taskName",task.getTaskName());
                            taskJson.put("assignee",identityLinkVo.getUserId());
                            taskJson.put("groupId",identityLinkVo.getGroupId());
                            taskJson.put("candidate",true);
                            waitForTaskArray.add(taskJson);
                        }

                    }
                }
//                List<List<HashMap>> waitForTaskArray=new ArrayList<>();
//                for(TaskVo task:waitForTasks){
//                    String taskName = task.getTaskName();
//                    taskId = task.getTaskId();
//                    List<HashMap> list=new ArrayList<>();
//                    for(IdentityLinkVo identityLinkVo:task.getLinkVoList())
//                    {
//                        //这个节点是group节点-并且判断是否有多个候选组
//                        HashMap taskJson=new HashMap();
//                        taskJson.put("taskId",taskId);
//                        taskJson.put("taskName",taskName);
//                        taskJson.put("groupId",identityLinkVo.getGroupId());
//                        taskJson.put("userId",identityLinkVo.getUserId());
//                        list.add(taskJson);
//                    }
//                    waitForTaskArray.add(list);
//                }
               res.put("waitForTasks",waitForTaskArray);
            }
            res.put("msg","审批成功");
            res.put("isFinish",isFinish);
            res.put("res","0");
        } catch (Exception e) {
            log.error("审批错误:"+e.getMessage());
            log.debug("错误堆栈:",e);
            res.put("msg","审批错误:"+e.getMessage());
            res.put("res","1");
        }

        return res;
    }

    /**
     * @param processInstanceId        当前实例
     * @return 实例的待办任务列表
     */
    @GetMapping(value = "/getTaskListByProcessInstanceID/{processInstanceId}")
    @ResponseBody
    @ApiOperation("实例的待办任务查询")
    public Object getTaskListByProcessInstanceID(
                            @ApiParam(value = "实例ID") @PathVariable String processInstanceId
                          ) {
        JSONArray tasksJson=null;
        List<Task> tasks=null;
        try {
            tasks = bpmWorkFlowProcessService.getTaskListByProcessInstanceID(processInstanceId);
            if(CollectionUtils.isEmpty(tasks)){
                log.info("没有对应的待办人");
                return null;
            }
            tasksJson=new JSONArray();
            for (Task task : tasks) {
                System.out.println(task.toString());
                JSONObject taskJson=new JSONObject();
                taskJson.put("taskId", task.getId());
                taskJson.put("taskName", task.getName());
                taskJson.put("taskProcessInstanceId", task.getProcessInstanceId());
                taskJson.put("assignee", task.getAssignee());
                tasksJson.add(taskJson);
            }

        } catch (Exception e) {
            log.error("获取流程的待办用户列表错误:"+e.getMessage());
            log.debug("错误堆栈:",e);
        }

        return tasksJson;
    }



    /**
     * @param processInstanceId        当前实例
     * @return 实例的待办任务列表
     */
    @GetMapping(value = "/getTaskListByProcessInstanceID")
    @ResponseBody
    @ApiOperation("实例的待办任务查询")
    public Object getNewTaskListByProcessInstanceID(
            @ApiParam(value = "实例ID") @RequestParam String processInstanceId
    ) {
        String tasksJson=null;
        List<TaskVo> tasks=null;
        try {
            tasks = bpmWorkFlowProcessService.getTaskVoListByProcessInstanceID(processInstanceId);
            if(CollectionUtils.isEmpty(tasks)){
                log.info("没有对应的待办人");
                return null;
            }
            tasksJson= JSON.toJSONString(tasks, SerializerFeature.WriteMapNullValue);

        } catch (Exception e) {
            log.error("获取流程的待办用户列表错误:"+e.getMessage());
            log.debug("错误堆栈:",e);
        }

        return tasksJson;
    }


    /**
     * @param processInstanceId        当前实例
     * @return 实例的待办用户查询
     */
    @GetMapping(value = "/getUserListByProcessInstanceID/{processInstanceId}")
    @ResponseBody
    @ApiOperation("实例的待办用户查询")
    public Object getUserListByProcessInstanceID(
            @ApiParam(value = "实例ID") @PathVariable String processInstanceId
    ) {
        List<HashMap> usersJson=null;
        List<String> users=null;
        try {
            users = bpmWorkFlowProcessService.getUserListByProcessInstanceID(processInstanceId);
            if(CollectionUtils.isEmpty(users)){
                log.info("没有对应的待办人");
                return null;
            }
            usersJson=new ArrayList<>();
            for (String user : users) {
                HashMap map=new HashMap();
                map.put("userID", user);
                map.put("userName", user);
                usersJson.add(map);
            }

        } catch (Exception e) {
            log.error("获取流程的待办用户列表错误:"+e.getMessage());
            log.debug("错误堆栈:",e);
        }

        return usersJson;
    }
    /**
     * @param processInstanceId        当前实例
     * @return 实例的待办用户查询
     */
    @GetMapping(value = "/getUserObjectListByProcessInstanceID/{processInstanceId}")
    @ResponseBody
    @ApiOperation("实例的待办用户对象查询")
    public Object getUserObjectListByProcessInstanceID(
            @ApiParam(value = "实例ID") @PathVariable String processInstanceId
    ) {
        JSONArray usersJson=null;
        List<org.flowable.idm.api.User> users=null;
        try {
            users = bpmWorkFlowProcessService.getUserObjecListByProcessInstanceID(processInstanceId);
            if(CollectionUtils.isEmpty(users)){
                log.info("没有对应的待办人");
                return null;
            }
            usersJson=new JSONArray();
            for (org.flowable.idm.api.User user : users) {
                JSONObject userJson=new JSONObject();
                userJson.put("userID", user.getId());
                userJson.put("userName", user.getDisplayName());
                usersJson.add(userJson);
            }

        } catch (Exception e) {
            log.error("获取流程的待办用户列表错误:"+e.getMessage());
            log.debug("错误堆栈:",e);
        }

        return usersJson;
    }

    /**
     * @param processInstanceId        当前实例
     * @return 返回实例是否结束
     */
    @GetMapping(value = "/isFinishedProcessInstanceID/{processInstanceId}")
    @ResponseBody
    @ApiOperation("查询实例是否结束")
    public Object isFinishedProcessInstanceID(
            @ApiParam(value = "实例ID") @PathVariable String processInstanceId ) {
        JSONObject res=new JSONObject();
        boolean isFinish=false;
        try {
            isFinish = bpmWorkFlowProcessService.isFinishedProcessInstanceID(processInstanceId);
            if(isFinish){
                res.put("msg","流程已经结束");
                res.put("isFinished",isFinish);
                res.put("res","0");

            }else{
                res.put("msg","流程运行中");
                res.put("isFinished",isFinish);
                res.put("res","0");
            }
        } catch (Exception e) {
            log.error("判断流程是否存在出错:"+e.getMessage());
            log.debug("错误堆栈:",e);
            res.put("msg","判断流程是否存在出错:"+e.getMessage());
            res.put("isFinished",isFinish);
            res.put("res","1");
        }
        return res;
    }

    /**
     * 获取默认工作流图片
     * @param modelKey  模型ID
     */
    @GetMapping(value = "genProcessDiagramByModelKey/{modelKey}")
    @ResponseBody
    @ApiOperation("根据模型Key生成模型图")
    public void genProcessDiagramByModelKey(@ApiParam(value = "模型Key") @PathVariable("modelKey")String modelKey,
                                            HttpServletResponse httpServletResponse) {
        InputStream in=null;
        OutputStream out = null;
        try {
            in=bpmDeploymentService.genProcessDiagramByModelKey( modelKey);
            out = httpServletResponse.getOutputStream();
            org.apache.commons.io.IOUtils.copy(in, out);
        } catch(Exception e){
            log.error("modelKey:【"+modelKey+ "】生成流程图:"+e.getMessage());
            log.debug("错误堆栈:",e);
        }finally {
            try{
                if (in != null) {
                    in.close();
                }
                if (out != null) {
                    out.close();
                }
            }catch (Exception e){
                log.debug("错误堆栈:",e);
            }

        }
    }
    /**
     *任务节点跳转
     * @param userID
     * @param processInstanceId
     * @param currentTaskID
     * @param targetTaskDefKey
     * @param reason
     * @return
     */
    @ResponseBody
    @PostMapping(value = "/back2PreFlow")
    @ApiOperation("节点跳转")
    public Object back2PreFlow(@ApiParam(value = "当前用户ID") @RequestParam("userID")String userID,
                               @ApiParam(value = "当前业务的实例ID") @RequestParam("processInstanceId")String  processInstanceId,
                               @ApiParam(value = "当前任务ID") @RequestParam("currentTaskID")String  currentTaskID,
                               @ApiParam(value = "跳转目标定义任务key") @RequestParam("targetTaskDefKey")String  targetTaskDefKey,
                               @ApiParam(value = "跳转原因") @RequestParam("reason")String  reason)  {
        FlowElement sourceFlowElement=null;
        JSONObject res=new JSONObject();
        List<FlowNodeVo> flowNodeVos=null;
        try {
            bpmWorkFlowProcessService.back2PreFlow( userID,processInstanceId,currentTaskID,targetTaskDefKey,reason);
            List<Task> waitForTasks=bpmWorkFlowProcessService.getTaskListByProcessInstanceID(processInstanceId);
            if(!CollectionUtils.isEmpty(waitForTasks)) {
                JSONArray waitForTaskArray = new JSONArray();
                for (Task task : waitForTasks) {
                    JSONObject taskJson = new JSONObject();
                    taskJson.put("taskId", task.getId());
                    taskJson.put("taskName", task.getName());
                    taskJson.put("assignee", task.getAssignee());
                    waitForTaskArray.add(taskJson);
                }
                res.put("waitForTasks", waitForTaskArray);
            }
            res.put("msg","节点跳转成功");
            res.put("res",0);
        } catch(Exception e){
            res.put("msg"," 跳转节点出错:"+e.getMessage());
            res.put("res","1");
            log.error("】跳转节点出错:"+e.getMessage());
            log.debug("错误堆栈:",e);

        }
        return res;
    }
}

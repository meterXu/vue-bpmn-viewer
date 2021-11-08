package com.sipsd.flow.service.flowable.impl;


import com.sipsd.flow.bean.BpmTaskMinModelEntity;
import com.sipsd.flow.bean.BpmTaskModelEntity;
import com.sipsd.flow.bean.ParallelGatwayDTO;
import com.sipsd.flow.bean.UserTaskModelDTO;
import com.sipsd.flow.exception.SipsdBootException;
import com.sipsd.flow.service.flowable.BpmProcessService;
import com.sipsd.flow.util.UserTaskAttrUtil;
import com.sipsd.flow.vo.flowable.BpmTaskModelQuery;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.flowable.bpmn.model.*;
import org.flowable.bpmn.model.Process;
import org.flowable.engine.*;
import org.flowable.engine.repository.ProcessDefinition;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Flowable 6 - 流程管理
 *
 * @author suxy
 */
@Service
@Slf4j
public class FlowableBpmProcessServiceImpl implements BpmProcessService {


    @Autowired
    private RepositoryService repositoryService;


    private List<BpmTaskMinModelEntity> queryMinUserTasks(
            BpmTaskModelQuery query, Process process, ProcessDefinition processDefinition) {
        List<BpmTaskMinModelEntity> list = new ArrayList<>(8);
        List<UserTask> userTasks = process.findFlowElementsOfType(UserTask.class);
        for (UserTask task : userTasks) {
            if (StringUtils.isNotBlank(query.getCategory()) && !query.getCategory().equals(task.getCategory())) {
                continue;
            }
            if (StringUtils.isNotBlank(query.getTaskDefKey()) && !query.getTaskDefKey().equals(task.getId())) {
                continue;
            }
            if (StringUtils.isNotBlank(query.getNodeType()) && !query.getNodeType().equals(UserTaskAttrUtil.getAttr(task, UserTaskAttrUtil.NODE_TYPE_KEY))) {
                continue;
            }
            BpmTaskMinModelEntity userTaskModelEntity = new BpmTaskModelEntity();
            userTaskModelEntity.setTenantId(processDefinition.getTenantId());
            userTaskModelEntity.setProcDefId(processDefinition.getId());
            userTaskModelEntity.setTaskDefKey(task.getId());
            userTaskModelEntity.setTaskName(task.getName());
            userTaskModelEntity.setAssignee(task.getAssignee());
            userTaskModelEntity.setCategory(task.getCategory());
            userTaskModelEntity.setFormKey(task.getFormKey());
            userTaskModelEntity.setSkipExpression(task.getSkipExpression());
            userTaskModelEntity.setHasMultiInstance(task.hasMultiInstanceLoopCharacteristics());
            if (task.hasMultiInstanceLoopCharacteristics()) {
                userTaskModelEntity.setSequential(task.getLoopCharacteristics().isSequential());
                userTaskModelEntity.setAssignee("${" + task.getLoopCharacteristics().getInputDataItem() + "}");
            } else {
                userTaskModelEntity.setSequential(false);
            }
            UserTaskAttrUtil.setAttr(userTaskModelEntity, task);
            list.add(userTaskModelEntity);
        }


        return list;
    }

    @Override
    public UserTaskModelDTO getUserTaskModelDto(BpmTaskModelQuery query) {
        UserTaskModelDTO dto = new UserTaskModelDTO();
        if (StringUtils.isBlank(query.getDefineId())) {
           throw new SipsdBootException("param defineId is null");
        }
        List<BpmTaskModelEntity> resultUserTaskModels = new ArrayList<>(4);
        ProcessDefinition processDefinition = repositoryService
                .createProcessDefinitionQuery()
                .processDefinitionId(query.getDefineId()).singleResult();
        if (processDefinition == null) {
            return dto;
        }
        BpmnModel bpmnModel = repositoryService.getBpmnModel(processDefinition.getId());
        Process process = bpmnModel.getProcesses().get(0);
        dto.setProcess(process);
        // 先查询出所有符合条件的用户任务
        List<BpmTaskMinModelEntity> allUserTasks = queryMinUserTasks(query, process, processDefinition);
        if (CollectionUtils.isEmpty(allUserTasks)) {
            return dto;
        }

        // 查询出所有并行网关中的用户任务
        Map<String, ParallelGatwayDTO> forkGatewayMap = getAllParallelGatewayUserTask(query, processDefinition);

        // 并行网关的用户任务节点
        Map<String, BpmTaskModelEntity> pGatewayUserTaskModelsMap = new LinkedHashMap<>(4);
        forkGatewayMap.forEach((k, v) -> pGatewayUserTaskModelsMap.putAll(v.getUserTaskModels()));

        // 非并行网关的用户任务节点
        List<BpmTaskModelEntity> aloneUserTaskModels = new ArrayList<>(4);
        allUserTasks.stream()
                .filter(item -> !pGatewayUserTaskModelsMap.containsKey(item.getTaskDefKey()))
                .collect(Collectors.toList())
                .forEach(item -> aloneUserTaskModels.add((BpmTaskModelEntity) item));

        // 合并两个结果集
        resultUserTaskModels.addAll(aloneUserTaskModels);
        resultUserTaskModels.addAll(new ArrayList<>(pGatewayUserTaskModelsMap.values()));
        dto.setAllUserTaskModels(resultUserTaskModels);
        dto.setAllForkGatewayMap(forkGatewayMap);
        return dto;
    }


    /**
     * 获取所有并行网关内的节点 和 并行网关之间的关系
     */
    protected Map<String, ParallelGatwayDTO> getAllParallelGatewayUserTask(
            BpmTaskModelQuery limitQuery, ProcessDefinition processDefinition) {
        BpmnModel bpmnModel = repositoryService.getBpmnModel(processDefinition.getId());
        Process process = bpmnModel.getProcesses().get(0);
        Map<String, FlowElement> flowElementMap = process.getFlowElementMap();

        List<ParallelGateway> parallelGateways = process.findFlowElementsOfType(ParallelGateway.class);
        List<InclusiveGateway> inclusiveGateways = process.findFlowElementsOfType(InclusiveGateway.class);

        List<Gateway> allParallelGateways = new ArrayList<>(4);
        allParallelGateways.addAll(parallelGateways);
        allParallelGateways.addAll(inclusiveGateways);


        Map<String, ParallelGatwayDTO> forkGatewayMap = new HashMap<>(4);

        for (Gateway gateway : allParallelGateways) {
            int outgoingFlowsSize = gateway.getOutgoingFlows().size();
            // 从 fork网关节点开始查找
            if (outgoingFlowsSize > 1 && !forkGatewayMap.containsKey(gateway.getId())) {
                ParallelGatwayDTO dto = new ParallelGatwayDTO();
                dto.setForkSize(outgoingFlowsSize);
                dto.setForkId(gateway.getId());
                dto.setTenantId(processDefinition.getTenantId());
                dto.setProcDefId(processDefinition.getId());
                forkGatewayMap.put(gateway.getId(), dto);

                loopForkParallelGateway(limitQuery, dto, gateway.getOutgoingFlows(), forkGatewayMap, flowElementMap);
            }
        }

        // 设置一些并行网关附加信息，用于跳转业务逻辑的判定
        forkGatewayMap.forEach((k, v) -> {
            Set<String> childForkParallelGatwayIds = new HashSet<>(2);
            getChildForkParallelGatwayIds(v.getChildParallelGatways(), childForkParallelGatwayIds);
            v.getUserTaskModels().forEach((k1, v1) -> v1.setChildForkParallelGatewayIds(childForkParallelGatwayIds));
        });
        return forkGatewayMap;
    }

    private void getChildForkParallelGatwayIds(List<ParallelGatwayDTO> childGws, Set<String> childForkParallelGatwayIds) {
        if (!CollectionUtils.isEmpty(childGws)) {
            for (ParallelGatwayDTO item : childGws) {
                childForkParallelGatwayIds.add(item.getForkId());

                getChildForkParallelGatwayIds(item.getChildParallelGatways(), childForkParallelGatwayIds);
            }
        }
    }

    private void convorBpmTaskModelEntity(BpmTaskModelEntity userTaskModelEntity,
                                          ParallelGatwayDTO dto,
                                          UserTask task) {
        userTaskModelEntity.setTenantId(dto.getTenantId());
        userTaskModelEntity.setProcDefId(dto.getProcDefId());
        userTaskModelEntity.setTaskDefKey(task.getId());
        userTaskModelEntity.setTaskName(task.getName());
        userTaskModelEntity.setAssignee(task.getAssignee());
        userTaskModelEntity.setCategory(task.getCategory());
        userTaskModelEntity.setFormKey(task.getFormKey());
        userTaskModelEntity.setInParallelGateway(true);
        userTaskModelEntity.setParallelGatewayForkRef(dto.getTmpForkRef());
        userTaskModelEntity.setForkParallelGatewayId(dto.getForkId());
        userTaskModelEntity.setHasMultiInstance(task.hasMultiInstanceLoopCharacteristics());
        userTaskModelEntity.setSkipExpression(task.getSkipExpression());
        if (task.hasMultiInstanceLoopCharacteristics()) {
            userTaskModelEntity.setSequential(task.getLoopCharacteristics().isSequential());
            userTaskModelEntity.setAssignee("${" + task.getLoopCharacteristics().getInputDataItem() + "}");
        } else {
            userTaskModelEntity.setSequential(false);
        }
        UserTaskAttrUtil.setAttr(userTaskModelEntity, task);
    }

    /**
     * 递归遍历所有并行分支上的
     *
     * @param limitQuery     查询限制
     * @param dto            ParallelGatwayDTO
     * @param outgoingFlows  List<SequenceFlow>
     * @param forkGatewayMap Map<String, ParallelGatwayDTO>
     * @param flowElementMap Map<String, FlowElement>
     */
    private void loopForkParallelGateway(
            BpmTaskModelQuery limitQuery, ParallelGatwayDTO dto, List<SequenceFlow> outgoingFlows,
            Map<String, ParallelGatwayDTO> forkGatewayMap, Map<String, FlowElement> flowElementMap) {
        if (CollectionUtils.isEmpty(outgoingFlows)) {
            return;
        }
        for (SequenceFlow item : outgoingFlows) {
            FlowElement refFlowElement = flowElementMap.get(item.getSourceRef());
            FlowElement targetFlowElement = flowElementMap.get(item.getTargetRef());
            // 设置当前查询的哪一个分支线
            if (refFlowElement instanceof ParallelGateway || refFlowElement instanceof InclusiveGateway) {
                dto.setTmpForkRef(item.getTargetRef());
            }
            if (targetFlowElement instanceof UserTask) {
                UserTask task = (UserTask) targetFlowElement;
                boolean eligible = true;
                if (dto.getUserTaskModels().containsKey(task.getId())) {
                    eligible = false;
                }
                if (StringUtils.isNotBlank(limitQuery.getCategory()) && !limitQuery.getCategory().equals(task.getCategory())) {
                    eligible = false;
                }
                if (StringUtils.isNotBlank(limitQuery.getTaskDefKey()) && !limitQuery.getTaskDefKey().equals(task.getId())) {
                    eligible = false;
                }
                if (StringUtils.isNotBlank(limitQuery.getNodeType()) && !limitQuery.getNodeType().equals(UserTaskAttrUtil.getAttr(task, UserTaskAttrUtil.NODE_TYPE_KEY))) {
                    eligible = false;
                }
                if (eligible) {
                    BpmTaskModelEntity userTaskModelEntity = new BpmTaskModelEntity();
                    // 设置 BpmTaskModelEntity 值
                    convorBpmTaskModelEntity(userTaskModelEntity, dto, task);
                    dto.getUserTaskModels().put(task.getId(), userTaskModelEntity);
                }
                // 递归取下面的节点
                loopForkParallelGateway(limitQuery, dto, ((FlowNode) targetFlowElement).getOutgoingFlows(), forkGatewayMap, flowElementMap);
            }
            if (targetFlowElement instanceof ParallelGateway || targetFlowElement instanceof InclusiveGateway) {
                Gateway gateway = (Gateway) targetFlowElement;
                // 遇到新的并行网关节点
                // 从 fork网关节点开始查找
                if (gateway.getOutgoingFlows().size() > 1) {
                    ParallelGatwayDTO childDto = forkGatewayMap.get(gateway.getId());
                    if (childDto == null) {
                        childDto = new ParallelGatwayDTO();
                        childDto.setTenantId(dto.getTenantId());
                        childDto.setProcDefId(dto.getProcDefId());
                    }
                    childDto.setForkSize(gateway.getOutgoingFlows().size());
                    childDto.setForkId(targetFlowElement.getId());
                    dto.getChildParallelGatways().add(childDto);
                    childDto.setParentParallelGatwayDTO(dto);
                    forkGatewayMap.put(targetFlowElement.getId(), childDto);
                    // 递归取下面的节点
                    loopForkParallelGateway(limitQuery, childDto, gateway.getOutgoingFlows(), forkGatewayMap, flowElementMap);
                } else if (gateway.getIncomingFlows().size() > 1 && gateway.getOutgoingFlows().size() == 1) {
                    // 遇到新的join类型的并行网关节点，此时dto为前面与之对应的fork并行网关节点
                    dto.setJoinId(gateway.getId());
                    dto.getUserTaskModels().forEach((k, v) -> v.setJoinParallelGatewayId(gateway.getId()));

                    if (dto.getParentParallelGatwayDTO() == null) {
                        // 本并行网关里面的用户任务递归完毕
                        break;
                    }
                    // 继续父并行网关的递归取
                    loopForkParallelGateway(limitQuery, dto.getParentParallelGatwayDTO(), gateway.getOutgoingFlows(), forkGatewayMap, flowElementMap);
                }
            }
        }
    }


}

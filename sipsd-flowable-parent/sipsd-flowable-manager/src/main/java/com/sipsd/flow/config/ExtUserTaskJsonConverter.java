/*
 * Copyright sipsd All right reserved.
 */
package com.sipsd.flow.config;

import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import com.sipsd.flow.constant.FlowConstant;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.flowable.bpmn.model.*;
import org.flowable.editor.language.json.converter.BaseBpmnJsonConverter;
import org.flowable.editor.language.json.converter.BpmnJsonConverter;
import org.flowable.editor.language.json.converter.UserTaskJsonConverter;
import org.flowable.editor.language.json.converter.util.CollectionUtils;
import org.flowable.editor.language.json.model.ModelInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * @author 高强
 * @title: ExtUserTaskJsonConverter
 * @projectName sipsd-flowable-parent
 * @description: 拓展自定义属性
 * @date 2021/3/23下午2:46
 */
public class ExtUserTaskJsonConverter extends UserTaskJsonConverter
{
    //固定写法，直接拷贝，注意更改节点类型对应的常量即可
    static void customFillTypes(Map<String, Class<? extends BaseBpmnJsonConverter>> convertersToBpmnMap, Map<Class<? extends BaseElement>, Class<? extends BaseBpmnJsonConverter>> convertersToJsonMap) {
        fillJsonTypes(convertersToBpmnMap);
        fillBpmnTypes(convertersToJsonMap);
    }

    public static void fillJsonTypes(Map<String, Class<? extends BaseBpmnJsonConverter>> convertersToBpmnMap) {
        convertersToBpmnMap.put(STENCIL_TASK_USER, ExtUserTaskJsonConverter.class);
    }

    public static void fillBpmnTypes(Map<Class<? extends BaseElement>, Class<? extends BaseBpmnJsonConverter>> convertersToJsonMap) {
        convertersToJsonMap.put(UserTask.class, ExtUserTaskJsonConverter.class);
    }

    //将Element转为Json
    @Override
    protected void convertElementToJson(ObjectNode propertiesNode, BaseElement baseElement) {
        UserTask userTask = (UserTask) baseElement;
        String assignee = userTask.getAssignee();

        if (StringUtils.isNotEmpty(assignee) || org.flowable.editor.language.json.converter.util.CollectionUtils.isNotEmpty(userTask.getCandidateUsers()) || org.flowable.editor.language.json.converter.util.CollectionUtils.isNotEmpty(userTask.getCandidateGroups())) {

            ObjectNode assignmentNode = objectMapper.createObjectNode();
            ObjectNode assignmentValuesNode = objectMapper.createObjectNode();

            List<ExtensionElement> idmAssigneeList = userTask.getExtensionElements().get("activiti-idm-assignee");
            List<ExtensionElement> idmAssigneeFieldList = userTask.getExtensionElements().get("activiti-idm-assignee-field");
            if (org.flowable.editor.language.json.converter.util.CollectionUtils.isNotEmpty(idmAssigneeList) || org.flowable.editor.language.json.converter.util.CollectionUtils.isNotEmpty(idmAssigneeFieldList)
                    || org.flowable.editor.language.json.converter.util.CollectionUtils.isNotEmpty(userTask.getExtensionElements().get("activiti-idm-candidate-user"))
                    || org.flowable.editor.language.json.converter.util.CollectionUtils.isNotEmpty(userTask.getExtensionElements().get("activiti-idm-candidate-group"))) {

                assignmentValuesNode.put("type", "idm");
                ObjectNode idmNode = objectMapper.createObjectNode();
                assignmentValuesNode.set("idm", idmNode);

                List<ExtensionElement> canCompleteList = userTask.getExtensionElements().get("initiator-can-complete");
                if (org.flowable.editor.language.json.converter.util.CollectionUtils.isNotEmpty(canCompleteList)) {
                    assignmentValuesNode.put("initiatorCanCompleteTask", Boolean.valueOf(canCompleteList.get(0).getElementText()));
                }

                if (StringUtils.isNotEmpty(userTask.getAssignee())) {
                    ObjectNode assigneeNode = objectMapper.createObjectNode();
                    assigneeNode.put("id", userTask.getAssignee());
                    idmNode.set("assignee", assigneeNode);
                    idmNode.put("type", "user");

                    fillProperty("email", "assignee-info-email", assigneeNode, userTask);
                    fillProperty("firstName", "assignee-info-firstname", assigneeNode, userTask);
                    fillProperty("lastName", "assignee-info-lastname", assigneeNode, userTask);
                }

                List<ExtensionElement> idmCandidateUserList = userTask.getExtensionElements().get("activiti-idm-candidate-user");
                if (org.flowable.editor.language.json.converter.util.CollectionUtils.isNotEmpty(userTask.getCandidateUsers()) && org.flowable.editor.language.json.converter.util.CollectionUtils.isNotEmpty(idmCandidateUserList)) {
                    if (userTask.getCandidateUsers().size() > 0) {
                        ArrayNode candidateUsersNode = objectMapper.createArrayNode();
                        idmNode.set("candidateUsers", candidateUsersNode);
                        idmNode.put("type", "users");
                        for (String candidateUser : userTask.getCandidateUsers()) {
                            ObjectNode candidateUserNode = objectMapper.createObjectNode();
                            candidateUserNode.put("id", candidateUser);
                            candidateUsersNode.add(candidateUserNode);

                            fillProperty("email", "user-info-email-" + candidateUser, candidateUserNode, userTask);
                            fillProperty("firstName", "user-info-firstname-" + candidateUser, candidateUserNode, userTask);
                            fillProperty("lastName", "user-info-lastname-" + candidateUser, candidateUserNode, userTask);
                        }
                    }
                }

                List<ExtensionElement> idmCandidateGroupList = userTask.getExtensionElements().get("activiti-idm-candidate-group");
                if (org.flowable.editor.language.json.converter.util.CollectionUtils.isNotEmpty(userTask.getCandidateGroups()) && org.flowable.editor.language.json.converter.util.CollectionUtils.isNotEmpty(idmCandidateGroupList)) {
                    if (userTask.getCandidateGroups().size() > 0) {
                        ArrayNode candidateGroupsNode = objectMapper.createArrayNode();
                        idmNode.set("candidateGroups", candidateGroupsNode);
                        idmNode.put("type", "groups");
                        for (String candidateGroup : userTask.getCandidateGroups()) {
                            ObjectNode candidateGroupNode = objectMapper.createObjectNode();
                            candidateGroupNode.put("id", candidateGroup);
                            candidateGroupsNode.add(candidateGroupNode);

                            fillProperty("name", "group-info-name-" + candidateGroup, candidateGroupNode, userTask);
                        }
                    }
                }

            } else {
                assignmentValuesNode.put("type", "static");

                if (StringUtils.isNotEmpty(assignee)) {
                    assignmentValuesNode.put(PROPERTY_USERTASK_ASSIGNEE, assignee);
                }

                if (org.flowable.editor.language.json.converter.util.CollectionUtils.isNotEmpty(userTask.getCandidateUsers())) {
                    ArrayNode candidateArrayNode = objectMapper.createArrayNode();
                    for (String candidateUser : userTask.getCandidateUsers()) {
                        ObjectNode candidateNode = objectMapper.createObjectNode();
                        candidateNode.put("value", candidateUser);
                        candidateArrayNode.add(candidateNode);
                    }
                    assignmentValuesNode.set(PROPERTY_USERTASK_CANDIDATE_USERS, candidateArrayNode);
                }

                if (org.flowable.editor.language.json.converter.util.CollectionUtils.isNotEmpty(userTask.getCandidateGroups())) {
                    ArrayNode candidateArrayNode = objectMapper.createArrayNode();
                    for (String candidateGroup : userTask.getCandidateGroups()) {
                        ObjectNode candidateNode = objectMapper.createObjectNode();
                        candidateNode.put("value", candidateGroup);
                        candidateArrayNode.add(candidateNode);
                    }
                    assignmentValuesNode.set(PROPERTY_USERTASK_CANDIDATE_GROUPS, candidateArrayNode);
                }
            }

            assignmentNode.set("assignment", assignmentValuesNode);
            propertiesNode.set(PROPERTY_USERTASK_ASSIGNMENT, assignmentNode);
        }

        if (userTask.getPriority() != null) {
            setPropertyValue(PROPERTY_USERTASK_PRIORITY, userTask.getPriority(), propertiesNode);
        }

        setPropertyValue(PROPERTY_SKIP_EXPRESSION, userTask.getSkipExpression(), propertiesNode);

        if (StringUtils.isNotEmpty(userTask.getFormKey())) {
            if (formKeyMap != null && formKeyMap.containsKey(userTask.getFormKey())) {
                ObjectNode formRefNode = objectMapper.createObjectNode();
                ModelInfo modelInfo = formKeyMap.get(userTask.getFormKey());
                formRefNode.put("id", modelInfo.getId());
                formRefNode.put("name", modelInfo.getName());
                formRefNode.put("key", modelInfo.getKey());
                propertiesNode.set(PROPERTY_FORM_REFERENCE, formRefNode);

            } else {
                setPropertyValue(PROPERTY_FORMKEY, userTask.getFormKey(), propertiesNode);
            }
        }

        setPropertyValue(PROPERTY_FORM_FIELD_VALIDATION, userTask.getValidateFormFields(), propertiesNode);
        setPropertyValue(PROPERTY_USERTASK_DUEDATE, userTask.getDueDate(), propertiesNode);
        setPropertyValue(PROPERTY_USERTASK_CATEGORY, userTask.getCategory(), propertiesNode);

        addFormProperties(userTask.getFormProperties(), propertiesNode);

        // region 增强客户端UI界面 节点最大审批天数属性-高强
        List<ExtensionElement> customNodeTypeElements = userTask.getExtensionElements().get(FlowConstant.PROPERTY_USERTASK_TASKMAXDAY);
        if (org.flowable.editor.language.json.converter.util.CollectionUtils.isNotEmpty(customNodeTypeElements)) {
            setPropertyValue(
                    FlowConstant.PROPERTY_USERTASK_TASKMAXDAY,
                    userTask.getExtensionElements().
                            get(FlowConstant.PROPERTY_USERTASK_TASKMAXDAY).
                            get(0).
                            getElementText(),
                    propertiesNode);
        } else {
            String attrValue = "";
            Map<String, List<ExtensionAttribute>> attributeMap = userTask.getAttributes();
            if (MapUtils.isNotEmpty(attributeMap)) {
                List<ExtensionAttribute> values = attributeMap.get(FlowConstant.PROPERTY_USERTASK_TASKMAXDAY);
                if (CollectionUtils.isNotEmpty(values)) {
                    attrValue = values.get(0).getValue();
                }
            }
            setPropertyValue(FlowConstant.PROPERTY_USERTASK_TASKMAXDAY, attrValue, propertiesNode);
        }
    }

    //将Json转为Element
    @Override
    protected FlowElement convertJsonToElement(JsonNode elementNode, JsonNode modelNode, Map<String, JsonNode> shapeMap) {
        UserTask task = new UserTask();
        // region 增强客户端UI界面 节点最大审批天数属性-高强
        List<CustomProperty> customProperties = new ArrayList<>();
        //自定义属性
        //----------------------------------
        String customNodeType = getPropertyValueAsString(FlowConstant.PROPERTY_USERTASK_TASKMAXDAY, elementNode);
        if (StringUtils.isNotBlank(customNodeType)) {
            CustomProperty customProperty = this.createPropery(FlowConstant.PROPERTY_USERTASK_TASKMAXDAY, customNodeType);
            customProperties.add(customProperty);
        }
        //设置自定义属性到usertask
        if (CollectionUtils.isNotEmpty(customProperties)) {
            task.setCustomProperties(customProperties);
        }
        // endregion ----------------------------------
        task.setPriority(getPropertyValueAsString(PROPERTY_USERTASK_PRIORITY, elementNode));
        String formKey = getPropertyValueAsString(PROPERTY_FORMKEY, elementNode);
        if (StringUtils.isNotEmpty(formKey)) {
            task.setFormKey(formKey);
        } else {
            JsonNode formReferenceNode = getProperty(PROPERTY_FORM_REFERENCE, elementNode);
            if (formReferenceNode != null && formReferenceNode.get("id") != null) {

                if (formMap != null && formMap.containsKey(formReferenceNode.get("id").asText())) {
                    task.setFormKey(formMap.get(formReferenceNode.get("id").asText()));
                }
            }
        }

        task.setValidateFormFields(getPropertyValueAsString(PROPERTY_FORM_FIELD_VALIDATION, elementNode));
        task.setDueDate(getPropertyValueAsString(PROPERTY_USERTASK_DUEDATE, elementNode));
        task.setCategory(getPropertyValueAsString(PROPERTY_USERTASK_CATEGORY, elementNode));

        JsonNode assignmentNode = getProperty(PROPERTY_USERTASK_ASSIGNMENT, elementNode);
        if (assignmentNode != null) {
            JsonNode assignmentDefNode = assignmentNode.get("assignment");
            if (assignmentDefNode != null) {

                JsonNode typeNode = assignmentDefNode.get("type");
                JsonNode canCompleteTaskNode = assignmentDefNode.get("initiatorCanCompleteTask");
                if (typeNode == null || "static".equalsIgnoreCase(typeNode.asText())) {
                    JsonNode assigneeNode = assignmentDefNode.get(PROPERTY_USERTASK_ASSIGNEE);
                    if (assigneeNode != null && !assigneeNode.isNull()) {
                        task.setAssignee(assigneeNode.asText());
                    }

                    task.setCandidateUsers(getValueAsList(PROPERTY_USERTASK_CANDIDATE_USERS, assignmentDefNode));
                    task.setCandidateGroups(getValueAsList(PROPERTY_USERTASK_CANDIDATE_GROUPS, assignmentDefNode));

                    if (StringUtils.isNotEmpty(task.getAssignee()) && !"$INITIATOR".equalsIgnoreCase(task.getAssignee())) {

                        if (canCompleteTaskNode != null && !canCompleteTaskNode.isNull()) {
                            addInitiatorCanCompleteExtensionElement(Boolean.valueOf(canCompleteTaskNode.asText()), task);
                        } else {
                            addInitiatorCanCompleteExtensionElement(false, task);
                        }

                    } else if (StringUtils.isNotEmpty(task.getAssignee()) && "$INITIATOR".equalsIgnoreCase(task.getAssignee())) {
                        addInitiatorCanCompleteExtensionElement(true, task);
                    }

                } else if ("idm".equalsIgnoreCase(typeNode.asText())) {
                    JsonNode idmDefNode = assignmentDefNode.get("idm");
                    if (idmDefNode != null && idmDefNode.has("type")) {
                        JsonNode idmTypeNode = idmDefNode.get("type");
                        if (idmTypeNode != null && "user".equalsIgnoreCase(idmTypeNode.asText()) && (idmDefNode.has("assignee") || idmDefNode.has("assigneeField"))) {

                            fillAssigneeInfo(idmDefNode, canCompleteTaskNode, task);

                        } else if (idmTypeNode != null && "users".equalsIgnoreCase(idmTypeNode.asText()) && (idmDefNode.has("candidateUsers") || idmDefNode.has("candidateUserFields"))) {

                            fillCandidateUsers(idmDefNode, canCompleteTaskNode, task);

                        } else if (idmTypeNode != null && "groups".equalsIgnoreCase(idmTypeNode.asText()) && (idmDefNode.has("candidateGroups") || idmDefNode.has("candidateGroupFields"))) {

                            fillCandidateGroups(idmDefNode, canCompleteTaskNode, task);

                        } else {
                            task.setAssignee("$INITIATOR");
                            addExtensionElement("activiti-idm-initiator", String.valueOf(true), task);
                        }
                    }
                }
            }
        }

        task.setSkipExpression(getPropertyValueAsString(PROPERTY_SKIP_EXPRESSION, elementNode));

        convertJsonToFormProperties(elementNode, task);
        return task;
    }

    private CustomProperty createPropery(String propertyName, String propertyValue) {
        CustomProperty customProperty = new CustomProperty();
        customProperty.setId(propertyName);
        customProperty.setName(propertyName);
        customProperty.setSimpleValue(propertyValue);
        return customProperty;
    }

    //convertJsonToElement会调用此方法
    @Override
    protected void convertJsonToFormProperties(JsonNode objectNode, BaseElement element) {
        super.convertJsonToFormProperties(objectNode, element);
        if (element instanceof UserTask) {
            //将Json转为UserTask中的Element
            JsonNode userTaskExpansion = getProperty(FlowConstant.PROPERTY_USERTASK_TASKMAXDAY, objectNode);
            if (userTaskExpansion instanceof TextNode) {
                if (StrUtil.isNotBlank(userTaskExpansion.toString())) {
                    addExpansionPropertiesElement((UserTask) element, (TextNode) userTaskExpansion);
                }
            }
        }
    }

    private void addExpansionPropertiesElement(UserTask userTask, TextNode userTaskExpansion) {
        ExtensionElement extensionElement = new ExtensionElement();
        extensionElement.setName(FlowConstant.PROPERTY_USERTASK_TASKMAXDAY);
        extensionElement.setNamespacePrefix("customs");
        extensionElement.setNamespace(BpmnJsonConverter.MODELER_NAMESPACE);
        if(StringUtils.isNotBlank(userTaskExpansion.asText()) && isInteger(userTaskExpansion.asText()))
        {
            extensionElement.setElementText(userTaskExpansion.asText());
        }
        userTask.addExtensionElement(extensionElement);
    }

    public boolean isInteger(String str) {
        Pattern pattern = Pattern.compile("[0-9]*");
        return pattern.matcher(str).matches();
    }

    public static void main(String[] args)
    {
        Pattern pattern = Pattern.compile("[0-9]*");
        System.out.println(pattern.matcher("-1").matches());
    }
}
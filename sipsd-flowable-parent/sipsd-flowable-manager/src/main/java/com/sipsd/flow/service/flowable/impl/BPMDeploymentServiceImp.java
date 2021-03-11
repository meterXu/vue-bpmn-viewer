//package com.sipsd.flow.service.flowable.impl;
//
//import java.io.ByteArrayInputStream;
//import java.io.InputStream;
//import java.io.UnsupportedEncodingException;
//import java.nio.charset.Charset;
//import java.util.ArrayList;
//import java.util.Collection;
//import java.util.Collections;
//import java.util.Comparator;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//import javax.servlet.http.HttpServletRequest;
//
//import org.apache.http.NameValuePair;
//import org.apache.http.client.utils.URLEncodedUtils;
//import org.flowable.bpmn.converter.BpmnXMLConverter;
//import org.flowable.bpmn.model.BpmnModel;
//import org.flowable.bpmn.model.FlowElement;
//import org.flowable.bpmn.model.Process;
//import org.flowable.bpmn.model.UserTask;
//import org.flowable.cmmn.editor.constants.ModelDataJsonConstants;
//import org.flowable.common.engine.api.FlowableException;
//import org.flowable.editor.language.json.converter.BpmnJsonConverter;
//import org.flowable.engine.RepositoryService;
//import org.flowable.engine.repository.Deployment;
//import org.flowable.engine.repository.DeploymentBuilder;
//import org.flowable.engine.repository.Model;
//import org.flowable.image.impl.DefaultProcessDiagramGenerator;
//import org.flowable.ui.common.model.ResultListDataRepresentation;
//import org.flowable.ui.modeler.domain.AbstractModel;
//import org.flowable.ui.modeler.model.ModelRepresentation;
//import org.flowable.ui.modeler.repository.ModelRepository;
//import org.flowable.ui.modeler.serviceapi.ModelService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//import org.springframework.util.CollectionUtils;
//import org.springframework.util.StringUtils;
//
//import com.fasterxml.jackson.databind.JsonNode;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.fasterxml.jackson.databind.node.ObjectNode;
//import com.sipsd.flow.rest.model.DefileBPMModelVo;
//import com.sipsd.flow.rest.model.FlowNodeVo;
//import com.sipsd.flow.service.flowable.BPMDeploymentService;
//
//import lombok.extern.apachecommons.CommonsLog;
//
///**
// * 基于统一modelKey 和processKey 一致的情况下，进行重现实现。
// * 
// * @author hul
// *
// */
//@Service
//@CommonsLog
//public class BPMDeploymentServiceImp implements BPMDeploymentService {
//
//	protected static final int MIN_FILTER_LENGTH = 1;
//
//	@Autowired
//	private RepositoryService repositoryService;
//
//	@Autowired
//	private ModelService modelService;
//
//	@Autowired
//	private ModelRepository modelRepository;
//
//	/**
//	 * 获取模型所有节点属性
//	 */
//	@Override
//	public List<FlowNodeVo> getProcessNodes(String modelkey) {
//
//		List<org.flowable.ui.modeler.domain.Model> mdels = modelRepository.findByKeyAndType(modelkey, 0);
//		org.flowable.ui.modeler.domain.Model modelData = modelService.getModel(mdels.get(0).getId());
//		// 获取模型
//		BpmnModel model = modelService.getBpmnModel(modelData);
//		// Process对象封装了全部的节点、连线、以及关口等信息。拿到这个对象就能够为所欲为了。
//		Process process = model.getProcesses().get(0);
//		// 获取全部的FlowElement（流元素）信息
//		Collection<FlowElement> flowElements = process.getFlowElements();
//		// return flowElements;
//
//		List<FlowNodeVo> flowNodeVos = null;
//		// 使用流程实例ID，查询正在执行的执行对象表，返回流程实例对象
//		if (CollectionUtils.isEmpty(flowElements)) {
//			return null;
//		}
//		flowNodeVos = new ArrayList<>();
//		for (FlowElement flowElement : flowElements) {
//			if (flowElement instanceof UserTask) {
//				FlowNodeVo flowNodeVo = new FlowNodeVo();
//				flowNodeVo.with(flowElement);
//				flowNodeVos.add(flowNodeVo);
//			}
//
//		}
//		Collections.sort(flowNodeVos, new Comparator<FlowNodeVo>() {
//			@Override
//			public int compare(FlowNodeVo o1, FlowNodeVo o2) {
//				// 升序
//				return o1.getFlowNodeId().compareTo(o2.getFlowNodeId());
//			}
//		});
//		return flowNodeVos;
//
//	}
//
//	/**
//	 * 根据页面定义的model key 获得运行的流程引擎的model
//	 * 
//	 * @param modelKey defineModelKey 只查询最新的一条定义流程
//	 * @return
//	 * @throws UnsupportedEncodingException
//	 */
//	public Model createRuModelByModelKey(String modelKey) throws UnsupportedEncodingException {
//		org.flowable.ui.modeler.domain.Model defineModel = getDefineModelByKey(modelKey);
//		return defineModel == null ? null : createRuModel(defineModel.getId());
//	}
//
//	@Override
//	public List<org.flowable.ui.modeler.domain.Model> getAllDefineModel() {
//		return modelRepository.findByModelType(0, null);
//	}
//
//	/**
//	 * 查询所有流程模版信息
//	 * 
//	 * @return
//	 */
//	@Override
//	public List<DefileBPMModelVo> getAllDefineModelVO() {
//		List<org.flowable.ui.modeler.domain.Model> modelList = modelRepository.findByModelType(0, null);
//		if (CollectionUtils.isEmpty(modelList)) {
//			return null;
//		}
//		List<DefileBPMModelVo> modelVoList = new ArrayList<>();
//		for (org.flowable.ui.modeler.domain.Model tmpModel : modelList) {
//			DefileBPMModelVo tmpModelVo = new DefileBPMModelVo().with(tmpModel);
//			modelVoList.add(tmpModelVo);
//		}
//		return modelVoList;
//	}
//
//	@Override
//	public org.flowable.ui.modeler.domain.Model getDefineModelByID(String modelId) {
//		return modelRepository.get(modelId);
//	}
//
//	/**
//	 * 创建一个可运行的模型,根据模型的定义ID 本方法只是返回一个 reModel 中的对象，对象保存或操作，在发布模型中完成。
//	 * 
//	 * @param modelId 页面定义的流程ID 来之于act_de_model
//	 * @return
//	 * @throws UnsupportedEncodingException
//	 */
//	public Model createRuModel(String modelId) throws UnsupportedEncodingException {
//
//		org.flowable.ui.modeler.domain.Model defineModel = modelRepository.get(modelId);
//		if (defineModel == null) {
//			return null;
//		}
//		// 保证一个modelkey和version 指挥存在一个运行的流程
//		Model resModel = repositoryService.createModelQuery().modelKey(defineModel.getKey())
//				.modelVersion(defineModel.getVersion()).singleResult();
//		if (resModel != null) {
//			return resModel;
//		}
//		resModel = repositoryService.newModel();
//		// 准备metaInfo
//		ObjectMapper objectMapper = new ObjectMapper();
//		ObjectNode editorNode = objectMapper.createObjectNode();
//		editorNode.put("id", defineModel.getId());
//		ObjectNode stencilSetNode = objectMapper.createObjectNode();
//		stencilSetNode.put("namespace", "http://b3mn.org/stencilset/bpmn2.0#");
//
//		ObjectNode modelObjectNode = objectMapper.createObjectNode();
//		modelObjectNode.put(ModelDataJsonConstants.MODEL_NAME, defineModel.getName());
//		modelObjectNode.put(ModelDataJsonConstants.MODEL_REVISION, 1);
//		modelObjectNode.put(ModelDataJsonConstants.MODEL_DESCRIPTION, defineModel.getDescription());
//
//		resModel.setMetaInfo(modelObjectNode.toString());
//		resModel.setName(defineModel.getName());
//		resModel.setKey(defineModel.getKey());
//		resModel.setVersion(defineModel.getVersion());
//		resModel.setCategory("act_de_model:{modelId:" + modelId + "}");
//		resModel.setTenantId(defineModel.getTenantId());
//		return resModel;
//	}
//
//	@Override
//	public org.flowable.ui.modeler.domain.Model getDefineModelByKey(String modelKey) {
//		List<org.flowable.ui.modeler.domain.Model> models = modelRepository.findByKeyAndType(modelKey, 0);
//		if (CollectionUtils.isEmpty(models)) {
//			log.error("BPMN模型没有配置流程");
//			return null;
//		}
//		org.flowable.ui.modeler.domain.Model restultModel = models.get(0);
//		int currentVersion = restultModel.getVersion();
//		// 查询最新版本的的模型定义存储
//		for (org.flowable.ui.modeler.domain.Model tmpModel : models) {
//			if (tmpModel.getVersion() > currentVersion) {
//				restultModel = tmpModel;
//				currentVersion = tmpModel.getVersion();
//			}
//		}
//		return restultModel;
//	}
//
//	/**
//	 * 根据modelkey 获得最新更新的模型，并发布
//	 * 
//	 * @param modelKey
//	 * @return
//	 * @throws Exception
//	 */
//	@Override
//	public Map<String, Object> deployWorkFloyByModeKey(String modelKey) throws Exception {
//		/*
//		 * List<Model>
//		 * tmpModelList=repositoryService.createModelQuery().modelKey(modelKey).
//		 * latestVersion().orderByLastUpdateTime().desc().list(); Model tmpModel=null;
//		 * if( CollectionUtils.isEmpty(tmpModelList)){ return null; }else{
//		 * tmpModel=tmpModelList.get(0); }
//		 */
//		org.flowable.ui.modeler.domain.Model tmpModel = getDefineModelByKey(modelKey);
//		return deployWorkFloyByModeID(tmpModel.getId());
//	}
//
//	/**
//	 * 根据模型关键字生成model 图形
//	 * 
//	 * @param modelKey
//	 * @return 图片流
//	 * @throws Exception
//	 */
//	@Override
//	public InputStream genProcessDiagramByModelKey(String modelKey) throws Exception {
//
//		InputStream res = null;
//		org.flowable.ui.modeler.domain.Model model = getDefineModelByKey(modelKey);
//
//		if (model == null) {
//			return res;
//		}
//		return genProcessDiagramByModel(model);
//
//	}
//
//	/**
//	 * 根据模型关键字生成model 的缩略图
//	 *
//	 * @param modelKey
//	 * @return 图片流
//	 * @throws Exception
//	 */
//	@Override
//	public InputStream genThumbnailByModelKey(String modelKey) throws Exception {
//
//		InputStream res = null;
//		org.flowable.ui.modeler.domain.Model model = getDefineModelByKey(modelKey);
//
//		if (model == null) {
//			return res;
//		}
//		return genThumbnailByModel(model);
//
//	}
//
//	@Override
//	public InputStream genThumbnailByModelID(String modelID) throws Exception {
//
//		InputStream res = null;
//		org.flowable.ui.modeler.domain.Model model = modelRepository.get(modelID);
//
//		if (model == null) {
//			return res;
//		}
//		return genThumbnailByModel(model);
//
//	}
//
//	@Override
//	public InputStream genProcessDiagramByModelID(String modelID) throws Exception {
//
//		InputStream res = null;
//		org.flowable.ui.modeler.domain.Model model = modelRepository.get(modelID);
//
//		if (model == null) {
//			return res;
//		}
//		return genProcessDiagramByModel(model);
//
//	}
//
//	/**
//	 * 根据domain model 生成模型图片
//	 * 
//	 * @param model
//	 * @return
//	 * @throws Exception
//	 */
//	private InputStream genProcessDiagramByModel(org.flowable.ui.modeler.domain.Model model) throws Exception {
//		InputStream res = null;
//		if (model == null) {
//			return res;
//		}
//
//		BpmnModel bpmnModel = modelService.getBpmnModel(model);
//		if (bpmnModel == null) {
//			return res;
//		}
//		DefaultProcessDiagramGenerator defaultProcessDiagramGenerator = new DefaultProcessDiagramGenerator();
//		List<String> highLightedActivities = new ArrayList<String>();
//		res = defaultProcessDiagramGenerator.generateDiagram(bpmnModel, "PNG", highLightedActivities, true);
//		return res;
//	}
//
//	/**
//	 * 根据domain model 生成模型图片
//	 * 
//	 * @param model
//	 * @return
//	 * @throws Exception
//	 */
//	private InputStream genThumbnailByModel(org.flowable.ui.modeler.domain.Model model) throws Exception {
//		InputStream res = null;
//		if (model == null) {
//			return res;
//		}
//		// 获得缩略图
//		res = new ByteArrayInputStream(model.getThumbnail());
//		return res;
//	}
//
//	/**
//	 * 
//	 * @param modelId 模型对应的ID act_de_model 中的。
//	 * @return
//	 * @throws Exception
//	 */
//	@Override
//	public Map<String, Object> deployWorkFloyByModeID(String modelId) throws Exception {
//		if (StringUtils.isEmpty(modelId)) {
//			return null;
//		}
//		org.flowable.ui.modeler.domain.Model modelData = modelService.getModel(modelId);
//		if (modelData == null) {
//			throw new FlowableException("error: 模型数据为空，请先设计流程并成功保存，再进行发布。");
//		}
//		Model repModel = repositoryService.createModelQuery().modelKey(modelData.getKey()).latestVersion()
//				.orderByLastUpdateTime().desc().singleResult();
//		if (repModel == null) {
//			repModel = createRuModel(modelId);
//		}
//		Deployment deployment = null;
//
//		Map<String, Object> res = new HashMap<>();
//		res.put("modelId", modelId);
//
//		byte[] bytes = modelService.getBpmnXML(modelData);
//		if (bytes == null) {
//			throw new FlowableException("error: 模型数据为空，请先设计流程并成功保存，再进行发布。");
//		}
//		BpmnModel bpmnModel = modelService.getBpmnModel(modelData);
//		if (bpmnModel.getProcesses().size() == 0) {
//			throw new FlowableException("error: 数据模型不符要求，请至少设计一条主线流程。");
//		}
//		// TODO 判断流程中是否有form 表单
//
//		List<Process> processes = bpmnModel.getProcesses();
//		Process curProcess = null;
//		if (CollectionUtils.isEmpty(processes)) {
//			log.error("BPMN模型没有配置流程");
//			return null;
//		}
//		res.put("processes", processes);
//		curProcess = processes.get(0);
//		String tmpDelployID = repModel.getDeploymentId();
//		byte[] bpmnBytes = new BpmnXMLConverter().convertToXML(bpmnModel);
//
//		// 发布流程
//		String processName = modelData.getName() + ".bpmn20.xml";
//		if (StringUtils.isEmpty(tmpDelployID)) {
//			// TODO 判断是否存在已经发布的流程 如果要求更改之后 ，要让更改生效，本处就不要判断
//
//			deployment = repositoryService.createDeploymentQuery().deploymentKey(modelData.getKey())
//					.orderByDeploymenTime().desc().latest().singleResult();
//			if (deployment == null) {
//				DeploymentBuilder deploymentBuilder = repositoryService.createDeployment()
//						.tenantId(modelData.getTenantId()).name(modelData.getName()).key(modelData.getKey())
//						.addBpmnModel(processName, bpmnModel).addString(processName, new String(bpmnBytes, "UTF-8"));
//				deployment = deploymentBuilder.deploy();
//
//			}
//
//			repModel.setDeploymentId(deployment.getId());
////            repositoryService.activateProcessDefinitionByKey(curProcess.getId(), modelData.getTenantId());
//			repositoryService.saveModel(repModel);
//			repositoryService.addModelEditorSource(repModel.getId(), modelData.getModelEditorJson().getBytes("utf-8"));
////            repositoryService.getProcessDefinition(curProcess.getId());
//			log.warn("新部署流程 name:" + curProcess.getName() + " ;key " + deployment.getKey() + " deploy " + deployment);
//		} else {
//			deployment = repositoryService.createDeploymentQuery().deploymentId(tmpDelployID).singleResult();
//			log.warn("获得已有流程name:" + curProcess.getName() + " ;key " + deployment.getKey() + " deploy " + deployment);
//		}
//		res.put("deployment", deployment);
//		log.warn("部署流程 name:" + curProcess.getName() + " ;key " + deployment.getKey() + " deploy " + deployment);
//		return res;
//
//	}
//
//	/**
//	 * 根据表单引擎Key 发布form表单数据
//	 * 
//	 * @param formKey
//	 */
//	public void deployForm(String formKey) {
////        //查询最新版本的formKey
////        FormDefinition formDefinition =formRepositoryService.
////                createFormDefinitionQuery().
////                formDefinitionKey(fromKey).
////                latestVersion().
////                singleResult();
////        FormInfo formInfo = formRepositoryService.getFormModelByKey(formDefinition.getKey());
////        if(formDefinition==null){
////            return;
////        }
////        //TODO  表单发布
////        FormDeployment deploy = formRepositoryService.createDeployment().
////                addFormDefinition(formDefinition.getName(),formInfo.getFormModel().toString())
////                .deploy();
//
//	}
//
//	@Override
//	public ByteArrayInputStream exportModelToXML(String modelId) {
//		try {
//			Model modelData = repositoryService.getModel(modelId);
//			BpmnJsonConverter jsonConverter = new BpmnJsonConverter();
//			JsonNode editorNode = new ObjectMapper()
//					.readTree(repositoryService.getModelEditorSource(modelData.getId()));
//			BpmnModel bpmnModel = jsonConverter.convertToBpmnModel(editorNode);
//			BpmnXMLConverter xmlConverter = new BpmnXMLConverter();
//			byte[] bpmnBytes = xmlConverter.convertToXML(bpmnModel);
//			ByteArrayInputStream inputStream = new ByteArrayInputStream(bpmnBytes);
//			return inputStream;
//		} catch (Exception e) {
//			log.error("导出model的xml文件失败：modelId =" + modelId, e.getCause());
//			throw new FlowableException("导出model的xml文件失败：modelId =" + modelId, e.getCause());
//		}
//	}
//
//	/**
//	 * TODO 只要过程中，都存在tenantId 就可以根据租户方式进行分组
//	 * 
//	 * 多租户模式 查询model 定义
//	 * 
//	 * @param modelID
//	 * @param tenantId
//	 * @return
//	 * @throws Exception
//	 */
//	@Override
//	public Deployment deployWorkFloyByModeID(String modelID, String tenantId) throws Exception {
//
//		return null;
//
//	}
//
//	@Override
//	public Map<String, Object> createFlow(String filePath) {
//		return null;
//	}
//
//	/**
//	 * models 的综合查询 （支持租户查询 ）
//	 * 
//	 * @param tenantId
//	 * @param filter
//	 * @param sort
//	 * @param modelType
//	 * @param request
//	 * @return
//	 */
//	@Override
//	public ResultListDataRepresentation getModels(String tenantId, String filter, String sort, Integer modelType,
//			HttpServletRequest request) {
//
//		// need to parse the filterText parameter ourselves, due to encoding issues with
//		// the default parsing.
//		String filterText = null;
//		List<NameValuePair> params = URLEncodedUtils.parse(request.getQueryString(), Charset.forName("UTF-8"));
//		if (params != null) {
//			for (NameValuePair nameValuePair : params) {
//				if ("filterText".equalsIgnoreCase(nameValuePair.getName())) {
//					filterText = nameValuePair.getValue();
//				}
//			}
//		}
//
//		List<ModelRepresentation> resultList = new ArrayList<>();
//		List<org.flowable.ui.modeler.domain.Model> models = null;
//
//		String validFilter = makeValidFilterText(filterText);
//
//		if (validFilter != null) {
//			models = modelRepository.findByModelTypeAndFilter(modelType, validFilter, sort);
//		} else {
//			models = modelRepository.findByModelType(modelType, sort);
//		}
//
//		if (org.flowable.editor.language.json.converter.util.CollectionUtils.isNotEmpty(models)) {
//			List<String> addedModelIds = new ArrayList<>();
//			for (org.flowable.ui.modeler.domain.Model model : models) {
//				if (!addedModelIds.contains(model.getId())) {
//					addedModelIds.add(model.getId());
//					ModelRepresentation representation = createModelRepresentation(model);
//					resultList.add(representation);
//				}
//			}
//		}
//
//		ResultListDataRepresentation result = new ResultListDataRepresentation(resultList);
//		return result;
//	}
//
//	protected ModelRepresentation createModelRepresentation(AbstractModel model) {
//		ModelRepresentation representation = null;
//
//		representation = new ModelRepresentation(model);
//
//		return representation;
//	}
//
//	protected String makeValidFilterText(String filterText) {
//		String validFilter = null;
//
//		if (filterText != null) {
//			String trimmed = org.apache.commons.lang3.StringUtils.trim(filterText);
//			if (trimmed.length() >= MIN_FILTER_LENGTH) {
//				validFilter = "%" + trimmed.toLowerCase() + "%";
//			}
//		}
//		return validFilter;
//	}
//
//}

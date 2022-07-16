package com.sipsd.flow.rest.api;

import com.github.pagehelper.PageHelper;
import com.sipsd.flow.cmd.DeployModelCmd;
import com.sipsd.flow.common.page.PageModel;
import com.sipsd.flow.common.page.Query;
import com.sipsd.flow.model.form.FlowableForm;
import com.sipsd.flow.service.flowable.FlowProcessDiagramGenerator;
import com.sipsd.flow.service.flowable.IFlowableModelService;
import com.sipsd.flow.service.form.FlowableFormService;
import com.sipsd.flow.utils.Result;
import com.sipsd.flow.vo.flowable.ModelVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.flowable.bpmn.model.BpmnModel;
import org.flowable.bpmn.model.FlowElement;
import org.flowable.bpmn.model.StartEvent;
import org.flowable.bpmn.model.UserTask;
import org.flowable.common.engine.api.FlowableObjectNotFoundException;
import org.flowable.engine.ManagementService;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.repository.Deployment;
import org.flowable.engine.repository.DeploymentBuilder;
import org.flowable.ui.common.service.exception.BadRequestException;
import org.flowable.ui.modeler.domain.AbstractModel;
import org.flowable.ui.modeler.domain.Model;
import org.flowable.ui.modeler.repository.ModelRepository;
import org.flowable.ui.modeler.serviceapi.ModelService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author : chengtg/gaoqiang
 * @title: : ApiTask
 * @projectName : flowable
 * @description: 模型API
 * @date : 2019/11/1321:21
 */
@Api(tags = { "模型操作" })
@RestController
@RequestMapping("/rest/model")
public class ApiFlowableModelResource extends BaseResource {
	private static final Logger LOGGER = LoggerFactory.getLogger(ApiFlowableModelResource.class);
	@Autowired
	private IFlowableModelService flowableModelService;
	@Autowired
	private ModelService modelService;
	@Autowired
	private RepositoryService repositoryService;
	@Autowired
	private FlowProcessDiagramGenerator flowProcessDiagramGenerator;
//	@Autowired
//	private IdentityService identityService;
	@Autowired
	protected ManagementService managementService;
	@Autowired
	private FlowableFormService flowableFormService;
	@Autowired
	private ModelRepository modelRepository;

	@ApiOperation("查询model流程列表(最新)")
	@GetMapping(value = "/page-model")
	public Result<PageModel<AbstractModel>> pageModel(Query query) {
		PageHelper.startPage(query.getPageNum(), query.getPageSize());
		Result<PageModel<AbstractModel>> result = Result.sucess("OK");
		List<AbstractModel> datas = modelService.getModelsByModelType(AbstractModel.MODEL_TYPE_BPMN);
		PageModel<AbstractModel> pm = new PageModel<>(datas.size(), datas);
//		pm.getData().forEach(abstractModel -> {
//			User user = identityService.createUserQuery().userId(abstractModel.getCreatedBy()).singleResult();
//			abstractModel.setCreatedBy(user.getFirstName());
//		});
		result.setData(pm);
		return result;
	}

	@ApiOperation("添加model")
	@PostMapping(value = "/addModel")
	public Result<String> addModel(@RequestBody ModelVo params) {
		Result<String> result = Result.sucess("OK");
		try {
			flowableModelService.addModel(params);
		} catch (BadRequestException e) {
			result = Result.failed(e.getMessage());
		}

		return result;
	}

	@ApiOperation("导入model")
	@PostMapping(value = "/import-process-model")
	public Result<String> importProcessModel(@RequestParam("file") MultipartFile file) {
		Result<String> result = Result.sucess("OK");
		try {
			flowableModelService.importProcessModel(file);
		} catch (BadRequestException e) {
			result = Result.failed(e.getMessage());
		}
		return result;
	}

	@ApiOperation("批量导入流程")
	@PostMapping(value = "/import-process-model/batch")
	public Result<String> importProcessModel(@RequestParam("files") MultipartFile[] files) {
		Result<String> result = Result.sucess("OK");
		try {
			flowableModelService.importProcessModelBatch(files);
		} catch (BadRequestException e) {
			result = Result.failed(e.getMessage());
		}
		return result;
	}

	@ApiOperation("根据模型ID发布")
	@PostMapping(value = "/deploy")
	public Result<String> deploy(String modelId) {
		Result<String> result = Result.failed("部署流程失败！");
		if (StringUtils.isBlank(modelId)) {
			result.setMessage("模板ID不能为空！");
			return result;
		}
		try {
			Model model = modelService.getModel(modelId.trim());
			// 到时候需要添加分类
			String categoryCode = "1000";
			BpmnModel bpmnModel = modelService.getBpmnModel(model);
			// 添加隔离信息
			String tenantId = "flow";
			// 必须指定文件后缀名否则部署不成功
			Deployment deploy;
			DeploymentBuilder deploymentBuilder = repositoryService.createDeployment();
				
            org.flowable.bpmn.model.Process process = bpmnModel.getMainProcess();
            Collection<FlowElement> flowElements = process.getFlowElements();
            Map<String, String> formKeyMap = new HashMap<String, String>(16);
            for (FlowElement flowElement : flowElements) {
                String formKey = null;
                if (flowElement instanceof StartEvent) {
                    StartEvent startEvent = (StartEvent) flowElement;
                    if (startEvent.getFormKey() != null && startEvent.getFormKey().length() > 0) {
                        formKey = startEvent.getFormKey();
                    }
                } else if (flowElement instanceof UserTask) {
                    UserTask userTask = (UserTask) flowElement;
                    if (userTask.getFormKey() != null && userTask.getFormKey().length() > 0) {
                        formKey = userTask.getFormKey();
                    }
                }
                if (formKey != null && formKey.length() > 0) {
                    if (formKeyMap.containsKey(formKey)) {
                        continue;
                    } else {
                        String formKeyDefinition = formKey.replace(".form", "");
//                        FlowableFormService flowableFormService =
//                                SpringContextHolder.getBean(FlowableFormService.class);
                        FlowableForm form = flowableFormService.getById(formKeyDefinition);
                        if (form != null && form.getFormJson() != null && form.getFormJson().length() > 0) {
                            byte[] formJson = form.getFormJson().getBytes("UTF-8");
                            ByteArrayInputStream bi = new ByteArrayInputStream(formJson);
                            deploymentBuilder.addInputStream(formKey, bi);
                            formKeyMap.put(formKey, formKey);
                        } else {
                            throw new FlowableObjectNotFoundException("Cannot find formJson with formKey " + formKeyDefinition);
                        }
                    }
                }
            }
            deploy = deploymentBuilder.name(model.getName()).key(model.getKey())
					.category(categoryCode).tenantId(tenantId).addBpmnModel(model.getKey() + ".bpmn", bpmnModel)
					.deploy();
			result.setData(deploy.getId());
			
			

//            byte[] bpmnBytes = new BpmnXMLConverter().convertToXML(bpmnModel);
//            String processName = model.getName()+".bpmn20.xml";
//            Deployment deployment = repositoryService.createDeployment()
//                    .name(model.getName())
//                    .addBytes(processName,bpmnBytes)
//                    .deploy();
//            result.setData(deployment.getId());
			result.setMessage("部署流程成功！");
			result.setCode(Result.SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			result.setMessage(String.format("部署流程异常！- %s", e.getMessage()));
		}
		return result;
	}

	@ApiOperation("根据模型ID批量发布")
	@PostMapping(value = "/deploy/batch")
	public Result<List<String>> batchDeploy(String modelId){
		return flowableModelService.deployBatch(modelId);
	}

	@ApiOperation("根据模型ID发布(cmd)")
	@PostMapping(value = "/deploy/cmd")
	public Result deployModel(String modelId) {
		managementService.executeCommand(new DeployModelCmd(modelId));
		return Result.ok("部署流程成功！");
	}

	/**
	 * 显示xml
	 *
	 * @param modelId
	 * @return
	 */
	@ApiOperation("显示xml")
	@GetMapping(value = "/loadXmlByModelId/{modelId}")
	public void loadXmlByModelId(@PathVariable String modelId, HttpServletResponse response) {
		try {
			Model model = modelService.getModel(modelId);
			byte[] b = modelService.getBpmnXML(model);
			response.setHeader("Content-type", "text/xml;charset=UTF-8");
			response.getOutputStream().write(b);
		} catch (Exception e) {
			LOGGER.error("ApiFlowableModelResource-loadXmlByModelId:" + e);
			e.printStackTrace();
		}
	}

	/**
	 * 通过Id查询model信息
	 *
	 * @param id
	 * @return
	 */
	@ApiOperation("通过Id查询model信息")
	@GetMapping(value = "/queryByModelId/{id}")
	public Model queryByModelId(@PathVariable String id, HttpServletResponse response) {
		return modelRepository.get(id);
	}

	/**
	 * 通过key查询model信息
	 *
	 * @param key
	 * @return
	 */
	@ApiOperation("通过key查询model信息")
	@GetMapping(value = "/queryByModelKey")
	public List<Model> queryByModelKey(@RequestParam String key,@RequestParam Integer modelType, HttpServletResponse response) {
		return modelRepository.findByKeyAndType(key,modelType);
	}

	@ApiOperation("导出bpmn20.xml文件")
	@GetMapping(value = "/downLoadXmlByModelId")
	public void downLoadXmlByModelId(String modelId, HttpServletResponse response) {
		if (StringUtils.isBlank(modelId)) {
			return;
		}
		try {
			Model model = modelService.getModel(modelId);
			byte[]  bpmnXML = modelService.getBpmnXML(model);
			ByteArrayInputStream in = new ByteArrayInputStream(bpmnXML);
			String filename = model.getName() + ".bpmn20.xml";
			response.setContentType("application/xml");
			response.setHeader("Content-Disposition", "attachment; filename=" + URLEncoder.encode(filename,"UTF-8"));
			response.setCharacterEncoding("UTF-8");
			IOUtils.copy(in, response.getOutputStream());  //这句必须放到setHeader下面，否则10K以上的xml无法导出，
			response.flushBuffer();
		} catch (IOException e) {
			LOGGER.info("导出失败,失败信息为"+e.getMessage());
		}
	}



	@GetMapping(value = "/loadPngByModelId/{modelId}")
	public void loadPngByModelId(@PathVariable String modelId, HttpServletResponse response) {
		Model model = modelService.getModel(modelId);
		//BpmnModel bpmnModel = modelService.getBpmnModel(model, new HashMap<>(), new HashMap<>()); 6.4.2
		BpmnModel bpmnModel = modelService.getBpmnModel(model);
		InputStream is = flowProcessDiagramGenerator.generateDiagram(bpmnModel);
		try {
			response.setHeader("Content-Type", "image/png");
			byte[] b = new byte[1024];
			int len;
			while ((len = is.read(b, 0, 1024)) != -1) {
				response.getOutputStream().write(b, 0, len);
			}
		} catch (Exception e) {
			LOGGER.error("ApiFlowableModelResource-loadPngByModelId:" + e);
			e.printStackTrace();
		}
	}



}

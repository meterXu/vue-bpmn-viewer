package com.sipsd.flow.rest.api;

import com.github.pagehelper.PageHelper;
import com.sipsd.flow.cmd.DeployModelCmd;
import com.sipsd.flow.common.page.PageModel;
import com.sipsd.flow.common.page.Query;
import com.sipsd.flow.model.form.FlowableForm;
import com.sipsd.flow.service.flowable.FlowProcessDiagramGenerator;
import com.sipsd.flow.service.flowable.IFlowableModelService;
import com.sipsd.flow.service.form.FlowableFormService;
import com.sipsd.flow.util.FileUtils;
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
import org.flowable.editor.language.json.converter.util.CollectionUtils;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
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
@Api(tags = {"模型操作"})
@RestController
@RequestMapping("/rest/model")
public class ApiFlowableModelResource extends BaseResource
{
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
    @Value("${sipsd.flowable.saveBpmnfile:null}")
    private String downloadBpmnDir;

    @ApiOperation("查询model流程列表(最新)")
    @GetMapping(value = "/page-model")
    public Result<PageModel<AbstractModel>> pageModel(Query query)
    {
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
    public Result<String> addModel(@RequestBody ModelVo params)
    {
        Result<String> result = Result.sucess("OK");
        try
        {
            flowableModelService.addModel(params);
        }
        catch (BadRequestException e)
        {
            result = Result.failed(e.getMessage());
        }

        return result;
    }

    @ApiOperation("导入model")
    @PostMapping(value = "/import-process-model")
    public Result<String> importProcessModel(@RequestParam("file") MultipartFile file)
    {
        Result<String> result = Result.sucess("OK");
        try
        {
            flowableModelService.importProcessModel(file);
        }
        catch (BadRequestException e)
        {
            result = Result.failed(e.getMessage());
        }
        return result;
    }

    @ApiOperation("批量导入流程")
    @PostMapping(value = "/import-process-models")
    public void importProcessModels(@RequestParam("file") MultipartFile[] file)
    {
        flowableModelService.importProcessModelBatch(file);
    }

    @ApiOperation("根据模型ID发布")
    @PostMapping(value = "/deploy")
    public Result<String> deploy(String modelId)
    {
        Result<String> result = Result.failed("部署流程失败！");
        if (StringUtils.isBlank(modelId))
        {
            result.setMessage("模板ID不能为空！");
            return result;
        }
        try
        {
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
            for (FlowElement flowElement : flowElements)
            {
                String formKey = null;
                if (flowElement instanceof StartEvent)
                {
                    StartEvent startEvent = (StartEvent) flowElement;
                    if (startEvent.getFormKey() != null && startEvent.getFormKey().length() > 0)
                    {
                        formKey = startEvent.getFormKey();
                    }
                }
                else if (flowElement instanceof UserTask)
                {
                    UserTask userTask = (UserTask) flowElement;
                    if (userTask.getFormKey() != null && userTask.getFormKey().length() > 0)
                    {
                        formKey = userTask.getFormKey();
                    }
                }
                if (formKey != null && formKey.length() > 0)
                {
                    if (formKeyMap.containsKey(formKey))
                    {
                        continue;
                    }
                    else
                    {
                        String formKeyDefinition = formKey.replace(".form", "");
//                        FlowableFormService flowableFormService =
//                                SpringContextHolder.getBean(FlowableFormService.class);
                        FlowableForm form = flowableFormService.getById(formKeyDefinition);
                        if (form != null && form.getFormJson() != null && form.getFormJson().length() > 0)
                        {
                            byte[] formJson = form.getFormJson().getBytes("UTF-8");
                            ByteArrayInputStream bi = new ByteArrayInputStream(formJson);
                            deploymentBuilder.addInputStream(formKey, bi);
                            formKeyMap.put(formKey, formKey);
                        }
                        else
                        {
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
        }
        catch (Exception e)
        {
            e.printStackTrace();
            result.setMessage(String.format("部署流程异常！- %s", e.getMessage()));
        }
        return result;
    }

    @ApiOperation("根据模型ID批量发布")
    @PostMapping(value = "/deploy/batch")
    public Result<List<String>> batchDeploy(String modelId)
    {
        return flowableModelService.deployBatch(modelId);
    }

    @ApiOperation("根据模型ID发布(cmd)")
    @PostMapping(value = "/deploy/cmd")
    public Result deployModel(String modelId)
    {
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
    public void loadXmlByModelId(@PathVariable String modelId, HttpServletResponse response)
    {
        try
        {
            Model model = modelService.getModel(modelId);
            byte[] b = modelService.getBpmnXML(model);
            response.setHeader("Content-type", "text/xml;charset=UTF-8");
            response.getOutputStream().write(b);
        }
        catch (Exception e)
        {
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
    public Model queryByModelId(@PathVariable String id, HttpServletResponse response)
    {
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
    public List<Model> queryByModelKey(@RequestParam String key, @RequestParam Integer modelType, HttpServletResponse response)
    {
        return modelRepository.findByKeyAndType(key, modelType);
    }

    @ApiOperation("导出bpmn20.xml文件")
    @GetMapping(value = "/downLoadXmlByModelId")
    public void downLoadXmlByModelId(String modelId, HttpServletResponse response)
    {
        if (StringUtils.isBlank(modelId))
        {
            return;
        }
        try
        {
            Model model = modelService.getModel(modelId);
            byte[] bpmnXML = modelService.getBpmnXML(model);
            ByteArrayInputStream in = new ByteArrayInputStream(bpmnXML);
            String filename = model.getName() + ".bpmn20.xml";
            response.setContentType("application/xml");
            response.setHeader("Content-Disposition", "attachment; filename=" + URLEncoder.encode(filename, "UTF-8"));
            response.setCharacterEncoding("UTF-8");
            IOUtils.copy(in, response.getOutputStream());  //这句必须放到setHeader下面，否则10K以上的xml无法导出，
            response.flushBuffer();
        }
        catch (IOException e)
        {
            LOGGER.info("导出失败,失败信息为" + e.getMessage());
        }
    }

    @ApiOperation("批量导出所有bpmn20.xml文件")
    @GetMapping(value = "/downLoadMultiXml")
    public void downLoadMultiXml(HttpServletResponse response) throws Exception
    {
        List<AbstractModel> datas = modelService.getModelsByModelType(AbstractModel.MODEL_TYPE_BPMN);
        if (CollectionUtils.isEmpty(datas))
        {
            LOGGER.info("没有流程可以导出!");
            return;
        }

        if(StringUtils.isEmpty(downloadBpmnDir))
        {
            LOGGER.info("没有可以导出的文件夹!");
            return;
        }

        String path = downloadBpmnDir + "/bpmn/";
        String zippath = downloadBpmnDir + "/bpmn.zip";
        LOGGER.info("流程文件夹地址:" + path);
        //二进制流流程文件输出到文件夹
        for (AbstractModel abstractModel : datas)
        {
            try
            {
                byte[] bpmnXML = modelService.getBpmnXML(abstractModel);
                String filename = abstractModel.getName() + ".bpmn20.xml";
                FileUtils.byteToFile(bpmnXML, path + filename);
            }
            catch (Exception e)
            {
                LOGGER.info("导出失败,失败信息为" + e.getMessage());
            }
        }
        //压缩文件夹到zip
        FileUtils.zipFile(new File(path), "zip");
        File file = new File(zippath);
        // 将文件写入输入流
        FileInputStream fileInputStream = new FileInputStream(file);
        InputStream fis = new BufferedInputStream(fileInputStream);
        byte[] buffer = new byte[fis.available()];
        fis.read(buffer);
        fis.close();
        // 清空response
        response.reset();
        // 设置response的Header
        response.setCharacterEncoding("UTF-8");
        //Content-Disposition的作用：告知浏览器以何种方式显示响应返回的文件，用浏览器打开还是以附件的形式下载到本地保存
        //attachment表示以附件方式下载 inline表示在线打开 "Content-Disposition: inline; filename=文件名.mp3"
        // filename表示文件的默认名称，因为网络传输只支持URL编码的相关支付，因此需要将文件名URL编码后进行传输,前端收到后需要反编码才能获取到真正的名称
        response.addHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(file.getName(), "UTF-8"));
        // 告知浏览器文件的大小
        response.addHeader("Content-Length", "" + file.length());
        OutputStream outputStream = new BufferedOutputStream(response.getOutputStream());
        response.setContentType("application/octet-stream");
        outputStream.write(buffer);
        outputStream.flush();
        //删除文件夹和文件
        if (file.exists())
        {
            FileUtils.deleteFile(file);
        }
        if (new File(path).exists())
        {
            FileUtils.deleteFile(new File(path));
        }
    }

    @GetMapping(value = "/loadPngByModelId/{modelId}")
    public void loadPngByModelId(@PathVariable String modelId, HttpServletResponse response)
    {
        Model model = modelService.getModel(modelId);
        //BpmnModel bpmnModel = modelService.getBpmnModel(model, new HashMap<>(), new HashMap<>()); 6.4.2
        BpmnModel bpmnModel = modelService.getBpmnModel(model);
        InputStream is = flowProcessDiagramGenerator.generateDiagram(bpmnModel);
        try
        {
            response.setHeader("Content-Type", "image/png");
            byte[] b = new byte[1024];
            int len;
            while ((len = is.read(b, 0, 1024)) != -1)
            {
                response.getOutputStream().write(b, 0, len);
            }
        }
        catch (Exception e)
        {
            LOGGER.error("ApiFlowableModelResource-loadPngByModelId:" + e);
            e.printStackTrace();
        }
    }
}

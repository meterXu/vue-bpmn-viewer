package com.sipsd.flow.service.flowable.impl;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Calendar;
import java.util.List;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;

import org.apache.commons.lang3.StringUtils;
import org.flowable.bpmn.BpmnAutoLayout;
import org.flowable.bpmn.converter.BpmnXMLConverter;
import org.flowable.bpmn.model.BpmnModel;
import org.flowable.bpmn.model.Process;
import org.flowable.editor.language.json.converter.BpmnJsonConverter;
import org.flowable.editor.language.json.converter.util.CollectionUtils;
import org.flowable.idm.api.User;
import org.flowable.ui.common.security.SecurityUtils;
import org.flowable.ui.common.service.exception.BadRequestException;
import org.flowable.ui.common.util.XmlUtil;
import org.flowable.ui.modeler.domain.AbstractModel;
import org.flowable.ui.modeler.domain.Model;
import org.flowable.ui.modeler.model.ModelRepresentation;
import org.flowable.ui.modeler.repository.ModelRepository;
import org.flowable.ui.modeler.serviceapi.ModelService;
import org.flowable.validation.ProcessValidator;
import org.flowable.validation.ProcessValidatorFactory;
import org.flowable.validation.ValidationError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.sipsd.cloud.common.core.util.Result;
import com.sipsd.flow.service.flowable.IFlowableModelService;
import com.sipsd.flow.vo.flowable.ModelVo;

/**
 * @author : chengtg
 * @title: : FlowableModelServiceImpl
 * @projectName : flowable
 * @description: 模型service实现类
 * @date : 2019/11/1920:58
 */
@Service
public class FlowableModelServiceImpl implements IFlowableModelService {
    private static final Logger LOGGER = LoggerFactory.getLogger(FlowableModelServiceImpl.class);
    @Autowired
    protected ModelRepository modelRepository;

    @Autowired
    protected ModelService modelService;
    @Autowired
    protected ObjectMapper objectMapper;

    protected BpmnXMLConverter bpmnXmlConverter = new BpmnXMLConverter();
    protected BpmnJsonConverter bpmnJsonConverter = new BpmnJsonConverter();

    @Override
    public Result<String> addModel(ModelVo modelVo) {
        InputStream inputStream = new ByteArrayInputStream(modelVo.getXml().getBytes());
        return this.createModel(inputStream);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
    public Result<String> importProcessModel(MultipartFile file) {
        Result<String> result = Result.sucess("创建模板成功");
        String fileName = file.getOriginalFilename();
        if (fileName != null && (fileName.endsWith(".bpmn") || fileName.endsWith(".bpmn20.xml"))) {
            try {
                InputStream inputStream = file.getInputStream();
                return this.createModel(inputStream);
            } catch (IOException e) {
            	result = Result.sucess("读取文件导入文件出错");
            }
        } else {
        	result = Result.sucess("Invalid file name, only .bpmn and .bpmn20.xml files are supported not " + fileName);
        }
        return result;
    }

    @Override
    public void batchImportProcessModel(MultipartFile[] files) {
        if(files!=null&&files.length>0){
            //循环获取file数组中得文件
            for(int i = 0;i<files.length;i++){
                MultipartFile file = files[i];
                //保存文件
                createModel(file);
            }
        }
    }

    private void createModel(MultipartFile file){
        String fileName = file.getOriginalFilename();
        if (fileName != null && (fileName.endsWith(".bpmn") || fileName.endsWith(".bpmn20.xml"))) {
            try {
                XMLInputFactory xif = XmlUtil.createSafeXmlInputFactory();
                InputStreamReader xmlIn = new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8);
                XMLStreamReader xtr = xif.createXMLStreamReader(xmlIn);
                BpmnModel bpmnModel = bpmnXmlConverter.convertToBpmnModel(xtr);
                if (CollectionUtils.isEmpty(bpmnModel.getProcesses())) {
                    throw new BadRequestException("No process found in definition " + fileName);
                }

                if (bpmnModel.getLocationMap().size() == 0) {
                    BpmnAutoLayout bpmnLayout = new BpmnAutoLayout(bpmnModel);
                    bpmnLayout.execute();
                }

                ObjectNode modelNode = bpmnJsonConverter.convertToJson(bpmnModel);

                Process process = bpmnModel.getMainProcess();
                String name = process.getId();
                if (StringUtils.isNotEmpty(process.getName())) {
                    name = process.getName();
                }
                String description = process.getDocumentation();

                ModelRepresentation model = new ModelRepresentation();
                model.setKey(process.getId());
                model.setName(name);
                model.setDescription(description);
                model.setModelType(AbstractModel.MODEL_TYPE_BPMN);
                modelService.createModel(model, modelNode.toString(), SecurityUtils.getCurrentUserObject());
            } catch (BadRequestException e) {
                throw e;
            } catch (Exception e) {
                LOGGER.error("Import failed for {}", fileName, e);
                throw new BadRequestException("Import failed for " + fileName + ", error message " + e.getMessage());
            }
        } else {
            throw new BadRequestException("Invalid file name, only .bpmn and .bpmn20.xml files are supported not " + fileName);
        }
    }

    private Result<String> createModel(InputStream inputStream) {
        Result<String> result = Result.sucess("创建模板成功");
        try {
            XMLInputFactory xif = XmlUtil.createSafeXmlInputFactory();
            InputStreamReader xmlIn = new InputStreamReader(inputStream, "UTF-8");
            XMLStreamReader xtr = xif.createXMLStreamReader(xmlIn);
            BpmnModel bpmnModel = bpmnXmlConverter.convertToBpmnModel(xtr);
            //模板验证
            ProcessValidator validator = new ProcessValidatorFactory().createDefaultProcessValidator();
            List<ValidationError> errors = validator.validate(bpmnModel);
            if (CollectionUtils.isNotEmpty(errors)) {
                StringBuffer es = new StringBuffer();
                errors.forEach(ve -> es.append(ve.toString()).append("/n"));
                return Result.sucess("模板验证失败，原因: " + es.toString());
            }
            String fileName = bpmnModel.getMainProcess().getName();
            if (CollectionUtils.isEmpty(bpmnModel.getProcesses())) {
                return Result.sucess("No process found in definition " + fileName);
            }
            if (bpmnModel.getLocationMap().size() == 0) {
                BpmnAutoLayout bpmnLayout = new BpmnAutoLayout(bpmnModel);
                bpmnLayout.execute();
            }
            ObjectNode modelNode = bpmnJsonConverter.convertToJson(bpmnModel);
            org.flowable.bpmn.model.Process process = bpmnModel.getMainProcess();
            String name = process.getId();
            if (StringUtils.isNotEmpty(process.getName())) {
                name = process.getName();
            }
            String description = process.getDocumentation();
            User createdBy = SecurityUtils.getCurrentUserObject();
            //查询是否已经存在流程模板
            Model newModel = new Model();
            List<Model> models = modelRepository.findByKeyAndType(process.getId(), AbstractModel.MODEL_TYPE_BPMN);
            if (CollectionUtils.isNotEmpty(models)) {
                Model updateModel = models.get(0);
                newModel.setId(updateModel.getId());
            }
            newModel.setName(name);
            newModel.setKey(process.getId());
            newModel.setModelType(AbstractModel.MODEL_TYPE_BPMN);
            newModel.setCreated(Calendar.getInstance().getTime());
            newModel.setCreatedBy(createdBy.getId());
            newModel.setDescription(description);
            newModel.setModelEditorJson(modelNode.toString());
            newModel.setLastUpdated(Calendar.getInstance().getTime());
            newModel.setLastUpdatedBy(createdBy.getId());
            modelService.createModel(newModel, SecurityUtils.getCurrentUserObject()); //6.4.2
            //modelService.createModel(newModel, SecurityUtils.getCurrentUserObject().getId());
            return result;
        } catch (BadRequestException e) {
            throw e;
        } catch (Exception e) {
            LOGGER.error("Import failed for {}", e);
            result =  Result.sucess("Import failed for , error message " + e.getMessage());
        }
        return result;
    }
}

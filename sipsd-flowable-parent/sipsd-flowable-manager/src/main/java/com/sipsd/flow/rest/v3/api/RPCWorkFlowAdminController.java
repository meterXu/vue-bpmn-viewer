package com.sipsd.flow.rest.v3.api;

import com.sipsd.flow.service.v3.services.BPMDeploymentService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.apachecommons.CommonsLog;
import org.apache.commons.io.IOUtils;
import org.flowable.engine.repository.Deployment;
import org.flowable.ui.modeler.domain.Model;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * 工作流管理api
 *
 * @author hul
 */
@Api(tags={"工作流管理API类（不建议使用）"})
@Controller
@RequestMapping(value = "/rpc/flowAdmin")
@CommonsLog
public class RPCWorkFlowAdminController {
    
    @Autowired
    private BPMDeploymentService bpmDeploymentService;

    
    /**
     *根据模型key 获得模型
     * @param modelKey  模型关键字
     */
    @GetMapping(value = "{modelKey}/getModel")
    @ResponseBody
    @ApiOperation("根据模型key 获得模型")
    public Map<String,Object>  getWorkFlowModelBykey(@ApiParam(value = "模型Key") @PathVariable("modelKey")String modelKey) {
        Model currentModel=null;
        Map<String,Object> res =new HashMap<>();
        try {
            currentModel= bpmDeploymentService.getDefineModelByKey(modelKey);
            res.put("msg", " 查询流程成功");
            res.put("res",1);
            res.put("data", currentModel.getModelEditorJson());
            return res;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            res.put("msg", "获得流程失败：" +e.getMessage());
            res.put("res",1);
            return res;
        }
    }
    
    
    /**
     * 发布模型
     * @param modelId  模型ID
     */
    @GetMapping(value = "deploy/{modelId}")
    @ResponseBody
    @ApiOperation("根据模型ID发布模型")
    public Map<String,Object> deployModelByID(@ApiParam(value = "模型ID") @PathVariable("modelId")String modelId) {
        Deployment currentProcessDeploy = null;
        Map<String,Object> res =new HashMap<>();
        Map<String,Object> data = new HashMap<>();
        try {
            Model getCurrentModel=bpmDeploymentService.getDefineModelByID(modelId);
            if(getCurrentModel==null){
                log.error("启动流程-->modelId="+modelId+" 失败：没有找到流程");
                res.put("msg", "发布流程-->modelId="+modelId+" 失败：没有找到流程");
                return res;
            }
            Map<String,Object> createRes = bpmDeploymentService.deployWorkFloyByModeID(getCurrentModel.getId());

            if (null == createRes){
                res.put("msg","创建流程失败");
                res.put("res","0");
                res.put("data",data);
                return res;
            }
            
            if(createRes.containsKey("deployment")){
                currentProcessDeploy=(Deployment) createRes.get("deployment");
            }else{
                log.error("流程发布失败："+modelId+" 对应的流程发布失败");
                res.put("msg","流程发布失败："+modelId+" 对应的流程发布失败");
                return res;
            }
            List<org.flowable.bpmn.model.Process> processes =(List<org.flowable.bpmn.model.Process>)createRes.get("processes");

            ArrayList<String> ids = new ArrayList<>();
            for (org.flowable.bpmn.model.Process process :processes){
                ids.add(process.getId());
            }
            
            data.put("processKeys",ids);
            data.put("deployId",currentProcessDeploy.getId());
            res.put("data",data);
            res.put("msg","创建流程成功");
            res.put("res","1");
            return res;
           
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return res;
        }
    }
    /**
     * 发布模型
     * @param modelKey  模型ID
     */
    @GetMapping(value = "deployByKey/{modelKey}")
    @ResponseBody
    @ApiOperation("根据模型Key发布模型")
    public Map<String,Object> deployModelByKey(@ApiParam(value = "模型Key") @PathVariable("modelKey")String modelKey) {
        Deployment currentProcessDeploy = null;
        Map<String,Object> res =new HashMap<>();
        Map<String,Object> data = new HashMap<>();
        try {
            Model getCurrentModel=bpmDeploymentService.getDefineModelByKey(modelKey);
            if(getCurrentModel==null){
                log.error("发布模型-->modelKey="+modelKey+" 失败：没有找到模型");
                res.put("msg", "发布模型-->modelKey="+modelKey+" 失败：没有找到模型");
                return res;
            }
            Map<String,Object> createRes= bpmDeploymentService.deployWorkFloyByModeID(getCurrentModel.getId());

            if (null == createRes){
                res.put("msg","发布流程失败");
                res.put("res","0");
                res.put("data",data);
                return res;
            }
            
            if(createRes.containsKey("deployment")){
                currentProcessDeploy=(Deployment) createRes.get("deployment");
            }else{
                log.error("流程发布失败："+modelKey+" 对应的流程发布失败");
                res.put("msg","模型发布失败："+modelKey+" 对应的流程发布失败");
                return res;
            }
            List<org.flowable.bpmn.model.Process> processes =(List<org.flowable.bpmn.model.Process>)createRes.get("processes");

            ArrayList<String> ids = new ArrayList<>();
            for (org.flowable.bpmn.model.Process process :processes){
                ids.add(process.getId());
            }
            
            data.put("processKeys",ids);
            data.put("deployId",currentProcessDeploy.getId());
            res.put("data",data);
            res.put("msg","发布流程成功");
            res.put("res","1");
            return res;
           
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return res;
        }
    }

    
    /**
     * 生成模型图片模型
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
            IOUtils.copy(in, out);
        } catch(Exception e){
            log.error("modelKey:【"+modelKey+ "】生成流程图:"+e.getMessage());
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
     * 发布模型
     * @param filePath  模型文件路径
     */
    @GetMapping(value = "deployByFile/{filePath}")
    @ResponseBody
    @ApiOperation("根据模型文件路径发布模型")
    public Map<String,Object> deployModelByFile(@ApiParam(value = "文件路径") @PathVariable("filePath")String filePath) {
        Deployment currentProcessDeploy = null;
        Map<String,Object> res =new HashMap<>();
        Map<String,Object> data = new HashMap<>();
        try {
            Map<String,Object> createRes=bpmDeploymentService.createFlow(filePath);
            if(res==null){
                log.error("启动流程-->modelKey="+filePath+" 失败：没有找到流程");
                res.put("msg", "启动流程-->modelKey="+filePath+" 失败：没有找到流程");
                return res;
            }
            if (null == createRes|| CollectionUtils.isEmpty(createRes)){
                res.put("msg","创建流程失败");
                res.put("res","0");
                res.put("data",data);
                return res;
            }
            
            if(createRes.containsKey("deployment")){
                currentProcessDeploy=(Deployment) createRes.get("deployment");
            }else{
                log.error("流程发布失败："+filePath+" 对应的流程发布失败");
                res.put("msg","流程发布失败："+filePath+" 对应的流程发布失败");
                return res;
            }
            List<org.flowable.bpmn.model.Process> processes =(List<org.flowable.bpmn.model.Process>)createRes.get("processes");

            ArrayList<String> ids = new ArrayList<>();
            for (org.flowable.bpmn.model.Process process :processes){
                ids.add(process.getId());
            }
            
            data.put("processKeys",ids);
            data.put("deployId",currentProcessDeploy.getId());
            res.put("data",data);
            res.put("msg","创建流程成功");
            res.put("res","1");
            return res;
           
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return res;
        }
    }

    
    
    /** 
     * 导出model 为xml文件
     * @param modelId  模型ID
     * @param fileName  filePath
     */  
    @GetMapping(value = "XML")
    @ResponseBody
    @ApiOperation("根据模型ID导出model 为xml文件")
    public void exportModelByID(@ApiParam(value = "模型ID") @PathVariable("modelId")String modelId,
            @ApiParam(value = "文件名称", name = "fileName")  @RequestParam(required=false) String fileName,
            HttpServletResponse response) {
        String tmpFileName=null;
        Model modelData=null;
        try {
            if(StringUtils.isEmpty(fileName)){
                modelData=bpmDeploymentService.getDefineModelByID(modelId);
                if(modelData==null){
                    log.error("导出model的xml文件失败：modelId={}"+modelId +"} 不存在模型");  
                }else{
                    tmpFileName=modelData.getKey()+ ".bpmn20.xml";
                }
            }else{
                tmpFileName=fileName;
            }
            ByteArrayInputStream  in= bpmDeploymentService.exportModelToXML(modelId);
            IOUtils.copy(in, response.getOutputStream());  
            response.setHeader("Content-Disposition", "attachment; filename=" + tmpFileName);  
            response.flushBuffer();  
          } catch (Exception e) {  
            log.error("导出model的xml文件失败：modelId={"+ modelId+"}", e);  
          }  
           
    }

  
}

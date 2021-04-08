package com.sipsd.flow.service.v3.services;


import org.flowable.engine.repository.Deployment;
import org.flowable.ui.common.model.ResultListDataRepresentation;
import org.flowable.ui.modeler.domain.Model;
import com.sipsd.flow.bean.DefileBPMModelVo;
import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

public interface  BPMDeploymentService {
    
    
    
   /**
    * 
    * @param modelID  模型ID
    * @param tenantId  租户ID
    * @return 返回发布器 信息
    * @throws Exception
    */
    Deployment deployWorkFloyByModeID(String modelID, String tenantId) throws Exception;
    /**
     * 部署工作流，从配置文件中获取，默认存放在process目录下面
     */
    Map<String,Object> createFlow(String filePath)   throws Exception;



    /**
     * 根据ModelID获得页面模型
     * @param modelId
     * @return
     */
    Model getDefineModelByID(String modelId);
    /**
     * 得到所有的模型
     * @return
     */
    List<Model> getAllDefineModel();
    /**
     * 得到所有的模型
     * @return
     */
    List<DefileBPMModelVo> getAllDefineModelVO();


    
    /**
     * 根据modelKey获得页面模型 只返回版本最新的模型。
     * @param modelKey
     * @return
     */
    Model getDefineModelByKey(String modelKey);
    
    /**
     * 根据modelKey获得页面模型 只返回版本最新的模型。
     * @param modelKey
     * @return
     */
    InputStream genProcessDiagramByModelKey(String modelKey) throws Exception;
    /**
     * 根据modelID获得页面模型 只返回版本最新的模型。
     * @param modelID
     * @return
     */
	InputStream genProcessDiagramByModelID(String modelID) throws Exception;
    /**
     * 获得模型导出内容的ByteArrayInputStream数据
     * @param modelId
     * @return
     */
    ByteArrayInputStream exportModelToXML(String modelId);

    /**
     * 根据act_re_model模型ID 发布模型 直接从数据库
     * @param modelID
     * @throws Exception
     */
    Map<String,Object> deployWorkFloyByModeID(String modelID) throws Exception;
    /**
     * 根据模型Key 发布模型 直接从数据库
     * @param modelKey 
     * @throws Exception
     */
    Map<String,Object>  deployWorkFloyByModeKey(String modelKey) throws Exception;

    /**
     * 根据 modelKey 生成缩略图
     * @param modelKey
     * @return
     * @throws Exception
     */
    InputStream genThumbnailByModelKey(String modelKey) throws Exception;

    /**
     * 根据modelID 生成缩略图
     * @param modelID
     * @return
     * @throws Exception
     */
    InputStream genThumbnailByModelID(String modelID) throws Exception;

 /**
  * 流程模型查询
  * @param tenantId 租户ID
  * @param filter  过滤类型，默认process
  * @param sort  排序字符串 默认
  * @param modelType
  * @param request
  * @return
  */
    ResultListDataRepresentation getModels(String tenantId, String filter, String sort, Integer modelType, HttpServletRequest request);
}

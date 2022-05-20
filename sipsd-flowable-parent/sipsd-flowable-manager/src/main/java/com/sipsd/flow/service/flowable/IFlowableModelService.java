package com.sipsd.flow.service.flowable;

import org.springframework.web.multipart.MultipartFile;

import com.sipsd.cloud.common.core.util.Result;
import com.sipsd.flow.vo.flowable.ModelVo;

import java.util.List;

/**
 * @author : gaoqiang
 * @projectName : flowable
 * @description: 模型service
 * @date : 2019/11/1920:56
 */
public interface IFlowableModelService {

    /**
     * 导入模型
     * @param file 文件
     * @return
     */
    public Result<String> importProcessModel(MultipartFile file);


    /**
     * 批量导入流程模型
     * @param files 文件
     * @return
     */
    public void importProcessModelBatch(MultipartFile[] files);

    /**
     * 添加模型
     * @param modelVo
     * @return
     */
    public Result<String> addModel(ModelVo modelVo);

    /**
     * 批量发布
     * @param modelId 文件
     * @return
     */
    public Result<List<String>> deployBatch(String modelId);

}

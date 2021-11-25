package com.sipsd.flow.rest.api;

import com.sipsd.cloud.common.core.util.Result;
import com.sipsd.flow.common.page.PageModel;
import com.sipsd.flow.common.page.Query;
import com.sipsd.flow.service.flowable.IFlowableProcessDefinitionService;
import com.sipsd.flow.vo.flowable.ProcessDefinitionQueryVo;
import com.sipsd.flow.vo.flowable.ret.ProcessDefinitionVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.flowable.common.engine.impl.util.IoUtil;
import org.flowable.engine.RepositoryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;

/**
 * @author : chengtg
 * @title: : ApiTask
 * @projectName : flowable
 * @description: 模型API
 * @date : 2019/11/1321:21
 */
@Api(tags = { "流程定义" })
@RestController
@RequestMapping("/rest/definition")
public class ApiFlowableProcessDefinitionResource extends BaseResource {
    private static final Logger LOGGER = LoggerFactory.getLogger(ApiFlowableProcessDefinitionResource.class);
    @Autowired
    private IFlowableProcessDefinitionService flowableProcessDefinitionService;
    @Autowired
    private RepositoryService repositoryService;

    /**
     * 分页查询流程定义列表
     *
     * @param params 参数
     * @param query  分页
     * @return
     */
	@ApiOperation("分页查询")
    @PostMapping(value = "/page-model")
    public PageModel<ProcessDefinitionVo> pageModel(ProcessDefinitionQueryVo params, Query query) {
        PageModel<ProcessDefinitionVo> pm = flowableProcessDefinitionService.getPagerModel(params, query);
        return pm;
    }

    /**
     * 删除流程定义
     *
     * @param deploymentId 部署id
     */
	@ApiOperation("删除")
    @PostMapping(value = "/deleteDeployment")
    public Result<String> deleteDeployment(String deploymentId) {
        Result<String> result = Result.sucess("OK");
        repositoryService.deleteDeployment(deploymentId, true);
        return result;
    }

    /**
     * 通过id和类型获取图片
     *
     * @param id       定义id
     * @param type     类型
     * @param response response
     */
	@ApiOperation("通过id和类型获取图片")
    @GetMapping(value = "/processFile/{type}/{id}")
    public void processFile(@PathVariable String id, @PathVariable String type, HttpServletResponse response) {
        try {
            byte[] b = null;
            ProcessDefinitionVo pd = flowableProcessDefinitionService.getById(id);
            if (pd != null) {
                if (type.equals("xml")) {
                    response.setHeader("Content-type", "text/xml;charset=UTF-8");
                    InputStream inputStream = repositoryService.getResourceAsStream(pd.getDeploymentId(), pd.getResourceName());
                    b = IoUtil.readInputStream(inputStream, "image inputStream name");
                } else {
                    response.setHeader("Content-Type", "image/png");
                    InputStream inputStream = repositoryService.getResourceAsStream(pd.getDeploymentId(), pd.getDgrmResourceName());
                    b = IoUtil.readInputStream(inputStream, "image inputStream name");
                }
                response.getOutputStream().write(b);
            }
        } catch (Exception e) {
            LOGGER.error("ApiFlowableModelResource-loadXmlByModelId:" + e);
            e.printStackTrace();
        }
    }

    /**
     * 激活或者挂起流程定义
     * @param id
     * @return
     */
	@ApiOperation("激活或者挂起")
    @PostMapping(value = "/saDefinitionById")
    public Result<String> saDefinitionById(String id,int suspensionState,int overtime) {
        Result<String> result =  flowableProcessDefinitionService.suspendOrActivateProcessDefinitionById(id,suspensionState,overtime);
        return result;
    }
}

package com.sipsd.flow.rest.api;


import com.sipsd.flow.common.UUIDGenerator;
import com.sipsd.flow.common.page.PageModel;
import com.sipsd.flow.common.page.Query;
import com.sipsd.flow.model.leave.Purchase;
import com.sipsd.flow.service.flowable.IFlowableProcessInstanceService;
import com.sipsd.flow.service.leave.IPurchaseService;
import com.sipsd.flow.utils.Result;
import com.sipsd.flow.vo.flowable.StartProcessInstanceVo;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.idm.api.User;
import org.flowable.ui.common.security.SecurityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;


/**
 * @author : admin
 * @date : 2019-12-09 10:00:54
 * description : 采购Controller
 */
@RestController
@RequestMapping("/rest/demo/purchase")
public class PurchaseResource extends BaseResource {
    private static Logger logger = LoggerFactory.getLogger(PurchaseResource.class);

    @Autowired
    private IPurchaseService purchaseService;
    @Autowired
    private IFlowableProcessInstanceService flowableProcessInstanceService;

    @GetMapping("/list")
    public PageModel<Purchase> list(Purchase purchase, Query query) {
        PageModel<Purchase> pm = null;
        try {
            pm = this.purchaseService.getPagerModelByQuery(purchase, query);
        } catch (Exception e) {
            logger.error("PurchaseController-ajaxList:", e);
            e.printStackTrace();
        }
        return pm;
    }

    //添加
    @PostMapping("/add")
    public Result add(Purchase purchase, String sessionId) {
        Result result = Result.failed("添加失败");
        try {
            String purchaseId = UUIDGenerator.generate();
            purchase.setId(purchaseId);
            StartProcessInstanceVo startProcessInstanceVo = new StartProcessInstanceVo();
            startProcessInstanceVo.setBusinessKey(purchaseId);
            User user = SecurityUtils.getCurrentUserObject();
            //startProcessInstanceVo.setCreator(user.getId());
            startProcessInstanceVo.setCurrentUserCode(user.getId());
            startProcessInstanceVo.setFormName("采购流程");
            startProcessInstanceVo.setSystemSn("flow");
            startProcessInstanceVo.setProcessDefinitionKey("purchase");
            Map<String, Object> variables = new HashMap<>();
            variables.put("money", purchase.getMoney());
            startProcessInstanceVo.setVariables(variables);
            Result<ProcessInstance> returnStart = flowableProcessInstanceService.startProcessInstanceByKey(startProcessInstanceVo);
            if (returnStart.getCode()== Result.SUCCESS){
                String processInstanceId = returnStart.getData().getProcessInstanceId();
                purchase.setProcessInstanceId(processInstanceId);
                this.purchaseService.insertPurchase(purchase);
                result = Result.sucess("添加成功");
            }else {
            	result = Result.failed(returnStart.getMessage());
            }
        }catch (Exception e){
            logger.error("PurchaseController-add:", e);
            e.printStackTrace();
        }

        return result;
    }

    //修改
    @PostMapping("/update")
    public Result update(Purchase purchase) {
        Result result = Result.failed("修改失败");
        try {
            this.purchaseService.updatePurchase(purchase);
            result = Result.sucess("修改成功");
        } catch (Exception e) {
            logger.error("PurchaseController-update:", e);
            e.printStackTrace();
        }
        return result;
    }

    //删除
    @GetMapping("/dels")
    public Result dels(String[] ids) {
        Result result = Result.failed("删除失败");
        try {
            for(String id: ids) {
                this.purchaseService.delPurchaseById(id);
            }
            result = Result.sucess("删除成功");
        } catch (Exception e) {
            logger.error("PurchaseController-del:", e);
            e.printStackTrace();
        }
        return result;
    }
}

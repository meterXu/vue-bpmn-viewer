package com.sipsd.flow.rest.api;

import java.util.List;

import org.flowable.idm.api.IdmIdentityService;
import org.flowable.idm.api.Privilege;
import org.flowable.idm.api.PrivilegeQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sipsd.cloud.common.core.util.Result;
import com.sipsd.flow.common.page.PageModel;
import com.sipsd.flow.common.page.Query;

/**
 * @author : gaoqiang
 * @title: : ApiFlowableIdentityResource
 * @projectName : flowable
 * @description: 权限
 * @date : 2019/12/222:24
 */
@RestController
@RequestMapping("/rest/privilege")
public class ApiFlowablePrivilegeResource extends BaseResource {

    @Autowired
    private IdmIdentityService idmIdentityService;
    /**
     * 查询权限列表
     *
     * @param name 姓名
     * @return
     */
    @GetMapping("/getPagerModel")
    public PageModel<Privilege> getPagerModel(String name, Query query) {
        PrivilegeQuery privilegeQuery = idmIdentityService.createPrivilegeQuery().privilegeName(name);
        long count = privilegeQuery.count();
        int firstResult = (query.getPageNum() - 1) * query.getPageSize();
        List<Privilege> datas = privilegeQuery.listPage(firstResult, query.getPageSize());
        return new PageModel<>(count, datas);
    }

    /**
     * 添加修改权限
     * @param privilegeName  名称
     * @return
     */
    @PostMapping("/save")
    public Result<String> save(String privilegeName) {
        Result result = Result.sucess("添加成功");
        idmIdentityService.createPrivilege(privilegeName);
        return result;
    }

    /**
     * 删除权限
     * @param privilegeId 权限id
     * @return
     */
    @PostMapping("/delete")
    public Result<String> delete(String privilegeId) {
        Result result = Result.sucess("删除成功");
        idmIdentityService.deletePrivilege(privilegeId);
        return result;
    }
}

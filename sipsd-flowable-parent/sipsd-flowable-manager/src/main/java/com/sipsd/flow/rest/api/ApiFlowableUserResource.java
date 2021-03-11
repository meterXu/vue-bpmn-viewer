package com.sipsd.flow.rest.api;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.flowable.idm.api.IdmIdentityService;
import org.flowable.idm.api.Privilege;
import org.flowable.idm.api.User;
import org.flowable.idm.api.UserQuery;
import org.flowable.idm.engine.impl.persistence.entity.UserEntityImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sipsd.cloud.common.core.util.Result;
import com.sipsd.flow.common.page.PageModel;
import com.sipsd.flow.common.page.Query;
import com.sipsd.flow.service.flowable.IFlowableIdentityService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * @author : chengtg
 * @title: : ApiFlowableIdentityResource
 * @projectName : flowable
 * @description: 用户组
 * @date : 2019/12/222:24
 */
@Api(tags={"用户操作"})
@RestController
@RequestMapping("/rest/user")
public class ApiFlowableUserResource extends BaseResource {

    @Autowired
    private IdmIdentityService idmIdentityService;
    @Autowired
    private IFlowableIdentityService flowableIdentityService;

    /**
     * 查询用户列表
     *
     * @param name 姓名
     * @return
     */
    @ApiOperation("查询用户列表")
    @GetMapping("/getPagerModel")
    public PageModel<User> getPagerModel(String name, Query query) {
        UserQuery userQuery = idmIdentityService.createUserQuery();
        if (StringUtils.isNotBlank(name)){
            userQuery.userFirstNameLike(name);
        }
        long count = userQuery.count();

        int firstResult = (query.getPageNum() - 1) * query.getPageSize();
        List<User> datas = userQuery.orderByUserFirstName().desc().listPage(firstResult, query.getPageSize());
        return new PageModel<>(count, datas);
    }

    /**
     * 添加修改用户
     *
     * @param user
     * @return
     */
    @ApiOperation("添加修改用户")
    @PostMapping("/save")
    public Result<String> save(UserEntityImpl user) {
        Result result = Result.sucess("添加成功");
        long count = idmIdentityService.createUserQuery().userId(user.getId()).count();
        flowableIdentityService.saveUser(user);
        if (count == 0) {
            Privilege privilege = idmIdentityService.createPrivilegeQuery().privilegeName("access-idm").singleResult();
            idmIdentityService.addUserPrivilegeMapping(privilege.getId(), user.getId());
        }
        return result;
    }

    /**
     * 删除用户
     *
     * @param userId
     * @return
     */
    @ApiOperation("删除用户")
    @PostMapping("/delete")
    public Result<String> delete(String userId) {
        Result result = Result.sucess("删除成功");
        idmIdentityService.deleteUser(userId);
        return result;
    }

    /**
     * 添加用户组
     *
     * @param userId   用户id
     * @param groupIds 组ids
     * @return
     */
    @ApiOperation("添加用户组")
    @PostMapping("/addUserGroup")
    public Result<String> addUserGroup(String userId, List<String> groupIds) {
        Result result = Result.sucess("删除成功");
        if (CollectionUtils.isNotEmpty(groupIds)) {
            groupIds.forEach(groupId -> idmIdentityService.createMembership(userId, groupId));
        }
        return result;
    }
}

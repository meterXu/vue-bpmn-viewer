package com.sipsd.flow.rest.api;

import com.sipsd.flow.common.page.PageModel;
import com.sipsd.flow.common.page.Query;
import com.sipsd.flow.service.flowable.IFlowableIdentityService;
import com.sipsd.flow.utils.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections.CollectionUtils;
import org.flowable.idm.api.Group;
import org.flowable.idm.api.GroupQuery;
import org.flowable.idm.api.IdmIdentityService;
import org.flowable.idm.engine.impl.persistence.entity.GroupEntityImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author : gaoqiang
 * @title: : ApiFlowableIdentityResource
 * @projectName : flowable
 * @description: 用户组
 * @date : 2019/12/222:24
 */
@Api(tags={"用户组操作"})
@RestController
@RequestMapping("/rest/group")
public class ApiFlowableGroupResource extends BaseResource {

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
    @ApiOperation("查询用户组列表")
    @GetMapping("/getPagerModel")
    public PageModel<Group> getPagerModel(String name, Query query) {
        GroupQuery groupQuery = idmIdentityService.createGroupQuery().groupNameLike(name);
        long count = groupQuery.count();
        int firstResult = (query.getPageNum() - 1) * query.getPageSize();
        List<Group> datas = groupQuery.orderByGroupName().listPage(firstResult, query.getPageSize());
        return new PageModel<>(count, datas);
    }

    /**
     * 添加修改组
     * @param group  组
     * @return
     */
    @ApiOperation("添加修改组")
    @PostMapping("/save")
    public Result<String> save(GroupEntityImpl group) {
        Result result = Result.sucess( "添加成功");
        flowableIdentityService.saveGroup(group);
        return result;
    }

    /**
     * 删除组
     * @param groupId
     * @return
     */
    @ApiOperation("删除组")
    @PostMapping("/delete")
    public Result<String> delete(String groupId) {
        Result result = Result.sucess( "删除成功");
        idmIdentityService.deleteGroup(groupId);
        return result;
    }

    /**
     * 添加组成员
     * @param groupId 组的id
     * @param userIds 用户的ids
     * @return
     */
    @ApiOperation("添加组成员")
    @PostMapping("/addGroupUser")
    public Result<String> addUserGroup(String groupId, List<String> userIds) {
        Result result = Result.sucess( "删除成功");
        if (CollectionUtils.isNotEmpty(userIds)) {
            userIds.forEach(userId -> {
                idmIdentityService.createMembership(userId, groupId);
            });
        }
        return result;
    }
}

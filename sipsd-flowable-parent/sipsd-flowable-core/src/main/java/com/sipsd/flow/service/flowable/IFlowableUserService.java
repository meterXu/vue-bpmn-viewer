package com.sipsd.flow.service.flowable;

import com.sipsd.flow.bean.User;

import java.util.List;

/**
 * @author : gaoqiang
 * @projectName : flowable
 * @description: 用户查询
 * @date : 2021/06/04 13:00
 */
public interface IFlowableUserService
{
    /**
     * 通过角色列表查询用户集合
     * @param  groupIds 角色组
     * @return
     */
    public List<User> getUserListByGroupIds(List<String> groupIds);
}

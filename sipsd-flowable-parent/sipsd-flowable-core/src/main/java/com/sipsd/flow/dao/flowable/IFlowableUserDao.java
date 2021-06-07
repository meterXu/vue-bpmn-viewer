package com.sipsd.flow.dao.flowable;

import com.sipsd.flow.bean.User;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author : gaoqiang
 * @projectName : flowable
 * @description: 用户查询
 * @date : 2021/06/04 13:00
 */
@Mapper
@Repository
public interface IFlowableUserDao
{

    /**
     * 通过角色列表查询用户集合
     * @param  groupIds 角色组
     * @return
     */
    public List<User> getUserListByGroupIds(List<String> groupIds);

}

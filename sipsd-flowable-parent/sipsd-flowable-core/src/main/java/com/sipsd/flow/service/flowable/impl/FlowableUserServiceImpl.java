package com.sipsd.flow.service.flowable.impl;

import com.sipsd.flow.vo.flowable.User;
import com.sipsd.flow.dao.flowable.IFlowableUserDao;
import com.sipsd.flow.service.flowable.IFlowableUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author : gaoqiang
 * @projectName : flowable
 * @description: 用户查询
 * @date : 2021/06/04 13:00
 */
@Service
public class FlowableUserServiceImpl extends BaseProcessService implements IFlowableUserService
{
	
	@Autowired
	private IFlowableUserDao flowableUserDao;

	@Override
	public List<User> getUserListByGroupIds(List<String> groupIds)
	{
		return flowableUserDao.getUserListByGroupIds(groupIds);
	}
}

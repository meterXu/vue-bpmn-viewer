package com.sipsd.flow.dao.flowable;

import com.sipsd.flow.vo.flowable.ProcessInstanceQueryVo;
import com.sipsd.flow.vo.flowable.ret.ProcessInstanceVo;
import com.github.pagehelper.Page;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * @author : gaoqiang
 * @title: : IFlowableProcessInstentDao
 * @projectName : flowable
 * @description: TODO
 * @date : 2019/11/2317:09
 */
@Mapper
@Repository
public interface IFlowableProcessInstanceDao {

    /**
     * 通过条件查询流程实例VO对象列表
     * @param params 参数
     * @return
     */
    public Page<ProcessInstanceVo> getPagerModel(ProcessInstanceQueryVo params);
}

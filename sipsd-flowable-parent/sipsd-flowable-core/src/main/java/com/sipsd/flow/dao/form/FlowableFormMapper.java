package com.sipsd.flow.dao.form;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.sipsd.flow.model.form.FlowableForm;

/**
 * 流程表单Mapper
 *
 * @author 庄金明
 */
public interface FlowableFormMapper extends BaseMapper<FlowableForm> {
    /**
     * 查询流程表单列表
     *
     * @param page
     * @param entity
     * @return
     */
    public List<FlowableForm> list(IPage<FlowableForm> page, @Param("entity") FlowableForm entity);
}

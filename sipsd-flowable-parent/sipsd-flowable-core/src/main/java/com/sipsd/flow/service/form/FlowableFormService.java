package com.sipsd.flow.service.form;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.sipsd.flow.model.form.FlowableForm;

/**
 * 流程表单Service
 *
 * @author 庄金明
 */
public interface FlowableFormService extends IService<FlowableForm>{
    /**
     * 分页查询流程表单
     *
     * @param page
     * @param flowableForm
     * @return
     */
    IPage<FlowableForm> list(IPage<FlowableForm> page, FlowableForm flowableForm);
}

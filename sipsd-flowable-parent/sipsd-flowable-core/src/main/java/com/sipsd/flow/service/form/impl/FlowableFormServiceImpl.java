package com.sipsd.flow.service.form.impl;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sipsd.flow.dao.form.FlowableFormMapper;
import com.sipsd.flow.model.form.FlowableForm;
import com.sipsd.flow.service.form.FlowableFormService;

/**
 * 流程Service
 *
 * @author 庄金明
 */
@Service
public class FlowableFormServiceImpl extends ServiceImpl<FlowableFormMapper, FlowableForm>  implements FlowableFormService {
    @Override
    public IPage<FlowableForm> list(IPage<FlowableForm> page, FlowableForm flowableForm) {
        return page.setRecords(baseMapper.list(page, flowableForm));
    }
}

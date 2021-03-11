package com.sipsd.flow.model.form;

import javax.validation.constraints.NotNull;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.sipsd.flow.common.BaseModel;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author 庄金明
 * @date 2020年3月23日
 */
@Data
@EqualsAndHashCode(callSuper=false)
@TableName("flowable_form")
public class FlowableForm extends BaseModel {

    private static final long serialVersionUID = 1L;

    @TableId
    @NotNull
    private String formKey;

    @NotNull
    private String formName;

    private String formJson;
}
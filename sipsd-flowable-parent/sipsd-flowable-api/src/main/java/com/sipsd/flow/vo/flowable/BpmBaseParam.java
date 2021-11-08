package com.sipsd.flow.vo.flowable;

import lombok.Data;

import java.io.Serializable;

/**
 * BPM 基础参数
 *
 * @author suxy
 */
@Data
public class BpmBaseParam implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 租户号
     */
    private String tenantId;

}

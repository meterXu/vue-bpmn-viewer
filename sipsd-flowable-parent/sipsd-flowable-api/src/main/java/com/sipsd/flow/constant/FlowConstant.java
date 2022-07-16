package com.sipsd.flow.constant;

/**
 * @author : gaoqiang
 * @title: : FlowConstant
 * @projectName : flowable
 * @description: 常量
 * @date : 2019/11/1314:00
 */
public class FlowConstant {

    public static final String MAPPER_SCAN = "com.sipsd.*.dao.*";

    /**
     * 提交人的变量名称
     */
    public static final String FLOW_SUBMITTER_VAR = "initiator";
    /**
     * 提交人节点名称
     */
    public static final String FLOW_SUBMITTER = "提交人";
    /**
     * 自动跳过节点设置属性
     */
    public static final String FLOWABLE_SKIP_EXPRESSION_ENABLED = "_FLOWABLE_SKIP_EXPRESSION_ENABLED";
    /**
     * 挂起状态
     */
    public static final int SUSPENSION_STATE = 2;
    /**
     * 激活状态
     */
    public static final int ACTIVATE_STATE = 1;
    //后加签
    public static final String AFTER_ADDSIGN = "after";
    //前加签
    public static final String BEFORE_ADDSIGN = "before";
    
    /**
     * 自动跳过key
     */
    public static final String SKIP = "skip";
    
    public final static String BUSINESS_KEY = "businessKey";

    /**
     * BUCKET_NAME
     */
    public final static String BUCKET_NAME = "gaoqiang";

    /**
     * 增强客户端UI界面 节点最大审批天数属性-PROPERTY_USERTASK_TASKMAXDAY-高强
     */
    public final static String PROPERTY_USERTASK_TASKMAXDAY = "task_max_day";

    /**
     * 节点串联值
     */
    public final static String FLOW_PARALLEL = "parallel";

    /**
     * 节点并联值
     */
    public final static String FLOW_SEQUENTIAL = "sequential";
}

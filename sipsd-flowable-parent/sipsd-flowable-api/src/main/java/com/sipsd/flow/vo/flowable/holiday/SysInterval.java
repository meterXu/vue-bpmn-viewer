package com.sipsd.flow.vo.flowable.holiday;

import lombok.Data;

/**
 * @Description: 日历管理
 * @Author: gaoqiang
 * @Date:   2021-09-29
 * @Version: V1.0
 */
@Data
public class SysInterval
{
    
	/**总天数*/
	private Integer total;

	/**工作天数*/
	private Integer workday;

	/**节假日天数*/
	private Integer holiday;
}

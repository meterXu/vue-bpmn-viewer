package com.sipsd.flow.vo.flowable.holiday;

import lombok.Data;

/**
 * @Description: 日历管理
 * @Author: gaoqiang
 * @Date:   2021-09-29
 * @Version: V1.0
 */
@Data
public class SysCalendarVo
{
    
	/**日期编码*/
	private String dateCode;

	/**年份编码*/
	private String yearCode;

	/**关键词*/
	private String key;

	private Boolean IfPaging;
}

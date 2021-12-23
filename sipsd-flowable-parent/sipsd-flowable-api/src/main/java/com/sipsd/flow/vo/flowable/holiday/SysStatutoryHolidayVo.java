package com.sipsd.flow.vo.flowable.holiday;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Description: 节假日管理
 * @Author: gaoqiang
 * @Date:   2021-09-29
 * @Version: V1.0
 */
@Data
public class SysStatutoryHolidayVo
{
	/**节假日标识*/
    @ApiModelProperty(value = "节假日标识")
	private Integer holidaySign;

	/**节假日备注*/
    @ApiModelProperty(value = "节假日备注")
	private String holidayNotes;
}

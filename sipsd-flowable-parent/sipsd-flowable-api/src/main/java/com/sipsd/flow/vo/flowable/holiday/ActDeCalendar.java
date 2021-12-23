package com.sipsd.flow.vo.flowable.holiday;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * @Description: 日历管理
 * @Author: gaoqiang
 * @Date:   2021-09-29
 * @Version: V1.0
 */
@Data
@TableName("Act_de_calendar")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="Act_de_calendar对象", description="日历管理")
public class ActDeCalendar
{

	/**id*/
	@ApiModelProperty(value = "id")
	@TableId(type = IdType.AUTO)
	private String id;

	/**日期编码*/
    @ApiModelProperty(value = "日期编码")
	private String dateCode;
	/**日期*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "日期")
	private Date date;
	/**阳历*/
    @ApiModelProperty(value = "阳历")
	private String solarDate;
	/**农历*/
    @ApiModelProperty(value = "农历")
	private String lunarDate;
	/**年份编码*/
    @ApiModelProperty(value = "年份编码")
	private String yearCode;
	/**干支*/
    @ApiModelProperty(value = "干支")
	private String sexagenaryCycle;
	/**生肖*/
    @ApiModelProperty(value = "生肖")
	private String chineseZodiac;
	/**节气*/
    @ApiModelProperty(value = "节气")
	private String solarTerms;
	/**星座*/
    @ApiModelProperty(value = "星座")
	private String zodiac;
	/**节日*/
    @ApiModelProperty(value = "节日")
	private String festivals;
	/**是否周末*/
    @ApiModelProperty(value = "是否周末")
	private Object ifWeekend;
	/**星期编码*/
    @ApiModelProperty(value = "星期编码")
	private String weekCode;
	/**星期*/
    @ApiModelProperty(value = "星期")
	private String week;
	/**编码*/
	@ApiModelProperty(value = "编码")
	private String code;
	/**组编码*/
	@ApiModelProperty(value = "组编码")
	private String groupCode;
	/**半天标识*/
	@ApiModelProperty(value = "半天标识")
	private Integer halfDaySign;
	/**半天描述*/
	@ApiModelProperty(value = "半天描述")
	private String halfDayDesc;
	/**节假日标识*/
	@ApiModelProperty(value = "节假日标识")
	private Integer holidaySign;
	/**节假日描述*/
	@ApiModelProperty(value = "节假日描述")
	private String holidayDesc;
	/**节假日备注*/
	@ApiModelProperty(value = "节假日备注")
	private String holidayNotes;
}

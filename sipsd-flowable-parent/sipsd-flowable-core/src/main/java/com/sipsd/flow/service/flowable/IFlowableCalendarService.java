package com.sipsd.flow.service.flowable;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.sipsd.flow.vo.flowable.holiday.ActDeCalendar;
import com.sipsd.flow.vo.flowable.holiday.SysCalendarVo;
import com.sipsd.flow.vo.flowable.holiday.SysStatutoryHolidayVo;

import java.util.Date;
import java.util.List;


/**
 * @Description: 日历管理
 * @Author: gaoqiang
 * @Date:   2021-09-29
 * @Version: V1.0
 */
public interface IFlowableCalendarService extends IService<ActDeCalendar>
{
	/**
	 * @param page
	 * @param sysCalendarVo
	 * @return java.util.List<com.sipsd.holiday.web.entity.ActDeCalendar>
	 * @Description  查询日历
	 */
	IPage<List<ActDeCalendar>> QueryCalendars(Page page, SysCalendarVo sysCalendarVo);


	/**
	 *
	 * @param dateCode
	 * @return com.sipsd.holiday.web.entity.ActDeCalendar
	 * @Description 日期编码查询日历
	 */
	ActDeCalendar queryByDateCode(String dateCode);

	/**
	 *
	 * @param sysCalendar
	 * @return void
	 * @Description 根据日期编码修改日历
	 */
	void updateByDateCode(ActDeCalendar sysCalendar);

	/**
	 *
	 * @param dateCode
	 * @return void
	 * @Description 通过日期编码删除记录
	 */
	void removeByDateCode(String dateCode);

	/**
	 * @param start
	 * @param end
	 * @param groupCode
	 * @return java.util.List<com.sipsd.holiday.web.entity.ActDeCalendar>
	 * @Description 获取全部节假日
	 */
	public List<ActDeCalendar> getAllHolidays(String start, String end, String groupCode);

	/**
	 * @param datetime
	 * @return java.lang.Boolean
	 * @Description 是否节假日
	 */
	public Boolean isHoliday(Date datetime);

	/**
	 * @param page
	 * @param sysStatutoryHolidayVo
	 * @return com.baomidou.mybatisplus.core.metadata.IPage<com.sipsd.holiday.web.entity.ActDeCalendar>
	 * @Description 查询法定节假日
	 */
	IPage<List<ActDeCalendar>> queryStatutoryHolidays(Page page, SysStatutoryHolidayVo sysStatutoryHolidayVo);

	/**
	 * @param dateCode
	 * @param halfDaySign
	 * @param holidaySign
	 * @param notes
	 * @return boolean
	 * @Description 设置为法定节假日
	 */
	public boolean setToStatutoryHoliday(String dateCode, Integer halfDaySign, Integer holidaySign, String holidayDesc, String notes);


	/**
	 * @param dateCodes
	 * @param halfDaySign
	 * @param holidaySign
	 * @param holidayDesc
	 * @param notes
	 * @return List<SysStatutoryHoliday>
	 * @Description
	 */
	public void setStatutoryHolidays(List<String> dateCodes, Integer halfDaySign, Integer holidaySign, String holidayDesc, String notes);


	/**
	 *
	 * @param dateCodeList
	 * @return boolean
	 * @Description 批量删除法定节假日
	 */
	public boolean batchDeleteStatutoryHoliday(List<String> dateCodeList);


	/**
	 *
	 * @param groupCode
	 * @return boolean
	 * @Description 删除自定义节假日组
	 */
	public boolean deleteCustomHolidayGroup(String groupCode);


	/**
	 *
	 * @param groupCode
	 * @param dateCode
	 * @return boolean
	 * @Description 用过组编码和日期编码删除自定义节假日组
	 */
	public boolean deleteCustomHoliday(String groupCode,String dateCode);


	/**
	 *
	 * @param dateCodeList
	 * @return boolean
	 * @Description 用过组编码和日期编码列表批量删除自定义节假日组
	 */
	public boolean batchDeleteCustomHoliday(String groupCode,List<String> dateCodeList);

	/**
	 *
	 * @param startDateCode
	 * @param endDateCode
	 * @return java.lang.Integer
	 * @Description 返回日期之间的节假日天数
	 */
	public Integer interval(String startDateCode, String endDateCode);

	/**
	 *
	 * @param startDateCode
	 * @param endDateCode
	 * @return java.lang.Integer
	 * @Description 返回日期之间的总天数
	 */
	public Integer totalDay(String startDateCode, String endDateCode);

	/**
	 *
	 * @param maxday
	 * @return java.lang.Integer
	 * @Description 排除节假日获取真正的间隔天数
	 */
	public Integer totalMaxDay(Integer maxday);
}

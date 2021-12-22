package com.sipsd.flow.dao.flowable;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sipsd.flow.vo.flowable.holiday.ActDeCalendar;
import com.sipsd.flow.vo.flowable.holiday.SysCalendarVo;
import com.sipsd.flow.vo.flowable.holiday.SysStatutoryHolidayVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;


/**
 * @Description: 日历管理
 * @Author: gaoqiang
 * @Date:   2021-09-29
 * @Version: V1.0
 */
public interface IFlowableCalendarMapper extends BaseMapper<ActDeCalendar>
{
	/**
	 * @param page
	 * @param sysCalendarVo
	 * @return java.util.List<com.sipsd.holiday.web.entity.SysCalendar>
	 * @Description  查询日历
	 */
	IPage<List<ActDeCalendar>> QueryCalendars(Page page, @Param("query") SysCalendarVo sysCalendarVo);

	/**
	 *
	 * @param dateCode
	 * @return void
	 * @Description 根据日期编码修改日历
	 */
	void updateByDateCode(String dateCode);

	/**
	 *
	 * @param dateCode
	 * @return void
	 * @Description 通过日期编码删除记录
	 */
	void removeByDateCode(String dateCode);

	/**
	 *
	 * @param page
	 * @param sysStatutoryHolidayVo
	 * @return com.baomidou.mybatisplus.core.metadata.IPage<com.sipsd.holiday.web.entity.SysCalendar>
	 * @Description 查询法定节假日
	 */
	IPage<List<ActDeCalendar>> queryStatutoryHolidays(Page page, @Param("query") SysStatutoryHolidayVo sysStatutoryHolidayVo);


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
	public boolean deleteCustomHoliday(@Param("groupCode") String groupCode, @Param("dateCode") String dateCode);

	/**
	 *
	 * @param groupCode
	 * @param dateCodeList
	 * @return boolean
	 * @Description 用过组编码和日期编码列表批量删除自定义节假日组
	 */
	public boolean batchDeleteCustomHoliday(@Param("groupCode") String groupCode, @Param("dateCodeList") List<String> dateCodeList);

	/**
	 * @param start
	 * @param end
	 * @param groupCode
	 * @return java.util.List<com.sipsd.holiday.web.entity.SysCalendar>
	 * @Description 获取全部节假日
	 */
	public List<ActDeCalendar> getAllHolidays(@Param("start") String start, @Param("end") String end, @Param("groupCode") String groupCode);


	/**
	 *
	 * @param startDateCode
	 * @param endDateCode
	 * @return java.lang.Integer
	 * @Description 返回日期之前的节假日天数
	 */
	public Integer interval(@Param("startDateCode") String startDateCode, @Param("endDateCode") String endDateCode);

	/**
	 *
	 * @param startDateCode
	 * @param endDateCode
	 * @return java.lang.Integer
	 * @Description 返回日期之前的总天数
	 */
	public Integer totalDay(@Param("startDateCode") String startDateCode, @Param("endDateCode") String endDateCode);
}

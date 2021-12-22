package com.sipsd.flow.service.flowable.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sipsd.flow.dao.flowable.IFlowableCalendarMapper;
import com.sipsd.flow.service.flowable.IFlowableCalendarService;
import com.sipsd.flow.util.holiday.LunarInfo;
import com.sipsd.flow.vo.flowable.holiday.ActDeCalendar;
import com.sipsd.flow.vo.flowable.holiday.SysCalendarVo;
import com.sipsd.flow.vo.flowable.holiday.SysStatutoryHolidayVo;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * @Description: 日历管理
 * @Author: gaoqiang
 * @Date:   2021-09-29
 * @Version: V1.0
 */
@Service
public class FlowableCalendarServiceImpl extends ServiceImpl<IFlowableCalendarMapper, ActDeCalendar> implements IFlowableCalendarService
{

	@Override
	public IPage<List<ActDeCalendar>> QueryCalendars(Page page, SysCalendarVo sysCalendarVo)
	{
		//TODO IfPaging 是什么ddx
		IPage<List<ActDeCalendar>> sysCalendarIPage = baseMapper.QueryCalendars(page, sysCalendarVo);
		return sysCalendarIPage;
	}

	@Override
	public ActDeCalendar queryByDateCode(String dateCode)
	{
		if(StringUtils.isEmpty(dateCode))
		{
			SimpleDateFormat chineseDateFormat = new SimpleDateFormat("yyyyMMdd", Locale.CHINA);
			dateCode = chineseDateFormat.format(new Date());
		}
		LambdaQueryWrapper<ActDeCalendar> wrapper = new LambdaQueryWrapper<>();
		wrapper.eq(ActDeCalendar::getDateCode,dateCode);
		return baseMapper.selectOne(wrapper);
	}

	@Override
	public void updateByDateCode(ActDeCalendar sysCalendar)
	{
		LambdaQueryWrapper<ActDeCalendar> wrapper = new LambdaQueryWrapper<>();
		wrapper.eq(ActDeCalendar::getDateCode,sysCalendar.getDate());
		baseMapper.update(sysCalendar,wrapper);
	}

	@Override
	public void removeByDateCode(String dateCode)
	{
		baseMapper.removeByDateCode(dateCode);
	}

	@Override
	public List<ActDeCalendar> getAllHolidays(String start, String end, String groupCode)
	{
		return baseMapper.getAllHolidays(start,end,groupCode);
	}

	@Override
	public Boolean isHoliday(Date datetime)
	{
		SimpleDateFormat df = new SimpleDateFormat("HH");
		String str = df.format(datetime);
		String result = "";
		int a = Integer.parseInt(str);
		if (a > 0 && a < 12) {
			result = "上午";
		}
		else {
			result = "下午";
		}
		SimpleDateFormat chineseDateFormat = new SimpleDateFormat("yyyyMMdd", Locale.CHINA);
		String dateCode = chineseDateFormat.format(datetime);
		LambdaQueryWrapper<ActDeCalendar> wrapper = new LambdaQueryWrapper<>();
		wrapper.eq(ActDeCalendar::getDateCode,dateCode);
		//TODO 目前不支持半天
//		wrapper.eq(ActDeCalendar::getHalfDayDesc,"全天");
		wrapper.eq(ActDeCalendar::getHolidaySign,1);
		//是否是法定节假日
		ActDeCalendar sysStatutoryHoliday = baseMapper.selectOne(wrapper);
		if(null!=sysStatutoryHoliday)
		{
			return true;
		}
		//是否是周末
		int day = LunarInfo.getDayOfWeek(datetime);
		if(day==5 || day==6)
		{
			return true;
		}

		return false;
	}

	@Override
	public IPage<List<ActDeCalendar>> queryStatutoryHolidays(Page page, SysStatutoryHolidayVo sysStatutoryHolidayVo)
	{
		IPage<List<ActDeCalendar>> statutoryHolidays = baseMapper.queryStatutoryHolidays(page, sysStatutoryHolidayVo);
		return statutoryHolidays;
	}


	@Override
	public boolean setToStatutoryHoliday(String dateCode, Integer halfDaySign, Integer holidaySign,String holidayDesc, String notes)
	{
		ActDeCalendar sysCalendar = this.queryByDateCode(dateCode);
		if(null!=sysCalendar)
		{
			sysCalendar.setDateCode(dateCode);
			sysCalendar.setHalfDaySign(halfDaySign);
			if(halfDaySign == 0)
			{
				sysCalendar.setHalfDayDesc("全天");
			}
			else {
				sysCalendar.setHalfDayDesc("半天");
			}
			sysCalendar.setHolidaySign(holidaySign);
			sysCalendar.setHolidayDesc(holidayDesc);
			sysCalendar.setHolidayNotes(notes);
			baseMapper.updateById(sysCalendar);
		}
		return true;
	}


	@Override
	public void setStatutoryHolidays(List<String> dateCodes, Integer halfDaySign, Integer holidaySign, String holidayDesc, String notes)
	{
		for(String dateCode:dateCodes)
		{
			ActDeCalendar sysCalendar = this.queryByDateCode(dateCode);
			if(null!=sysCalendar)
			{
				sysCalendar.setDateCode(dateCode);
				sysCalendar.setHalfDaySign(halfDaySign);
				if(halfDaySign == 0)
				{
					sysCalendar.setHalfDayDesc("全天");
				}
				else {
					sysCalendar.setHalfDayDesc("半天");
				}
				sysCalendar.setHolidaySign(holidaySign);

				sysCalendar.setHolidayDesc(holidayDesc);
				sysCalendar.setHolidayNotes(notes);
				baseMapper.updateById(sysCalendar);
			}
		}
	}


	@Override
	public boolean batchDeleteStatutoryHoliday(List<String> dateCodeList)
	{
		baseMapper.batchDeleteStatutoryHoliday(dateCodeList);
		return true;
	}

	@Override
	public boolean deleteCustomHolidayGroup(String groupCode)
	{
		baseMapper.deleteCustomHolidayGroup(groupCode);
		return true;
	}

	@Override
	public boolean deleteCustomHoliday(String groupCode, String dateCode)
	{
		baseMapper.deleteCustomHoliday(groupCode,dateCode);
		return true;
	}

	@Override
	public boolean batchDeleteCustomHoliday(String groupCode, List<String> dateCodeList)
	{
		baseMapper.batchDeleteCustomHoliday(groupCode,dateCodeList);
		return true;
	}

	@Override
	public Integer interval(String startDateCode, String endDateCode)
	{
		return baseMapper.interval(startDateCode,endDateCode);
	}

	@Override
	public Integer totalDay(String startDateCode, String endDateCode)
	{
		return baseMapper.totalDay(startDateCode,endDateCode);
	}
}

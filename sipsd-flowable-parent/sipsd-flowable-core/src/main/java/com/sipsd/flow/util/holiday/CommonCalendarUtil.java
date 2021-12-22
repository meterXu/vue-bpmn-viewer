/*
 * Copyright sipsd All right reserved.
 */
package com.sipsd.flow.util.holiday;


import com.sipsd.flow.vo.flowable.holiday.ActDeCalendar;
import com.sipsd.flow.vo.flowable.holiday.FourthTuple;
import com.sipsd.flow.vo.flowable.holiday.ThreeTuple;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.util.*;

/**
 * @author 高强
 * @title: CommonCalendar
 * @projectName sipsdx
 * @description: 日历
 * @date 2021/9/30下午3:05
 */
public class CommonCalendarUtil
{

	//获取干支
	public static String GetSexagenaryCycle(Date date)
	{
		String[] tg = { "甲", "乙", "丙", "丁", "戊", "己", "庚", "辛", "壬", "癸" };
		String[] dz = { "子", "丑", "寅", "卯", "辰", "巳", "午", "未", "申", "酉", "戌", "亥" };

		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
//		int day = cal.get(Calendar.DAY_OF_YEAR);//一年中的第几天
//		int week = cal.get(Calendar.WEEK_OF_YEAR);//一年中的第几周
//		int month = cal.get(Calendar.MONTH);//第几个月
		int year = cal.get(Calendar.YEAR);//年份数值
		int tgIndex = (year - 4) % 10;
		int dzIndex = (year - 4) % 12;
		return tg[tgIndex].concat(dz[dzIndex]);
	}

	//获取农历
	public  static  String GetLunarDate(Date date)
	{
		String[] years = { "〇", "一", "二", "三", "四", "五", "六", "七", "八", "九" };
		String[] months = { "正", "二", "三", "四", "五", "六", "七", "八", "九", "十", "十一", "腊" };
		String[] days1 = { "初", "十", "廿", "三" };
		String[] days2 = { "一", "二", "三", "四", "五", "六", "七", "八", "九", "十" };

		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		int year = cal.get(Calendar.YEAR);//年份数值
		int month = cal.get(Calendar.MONTH)+1;//第几个月
		int day = cal.get(Calendar.DAY_OF_MONTH);//一年中的第几天
		boolean isleap = false;
		//获取闰月，0则表示没有闰月
		int leapMonth = LunarInfo.leapMonth(year);
		if(leapMonth>0)
		{
			if (leapMonth == month)
			{
				isleap = true;
				month--;
			}
			else if (month > leapMonth)
			{
				month--;
			}
		}

		String yearstr = String.valueOf(year);
		int year1 = Integer.parseInt(yearstr.substring(0,1));
		int year2 = Integer.parseInt(yearstr.substring(1,2));
		int year3 = Integer.parseInt(yearstr.substring(2,3));
		int year4 = Integer.parseInt(yearstr.substring(3,4));

		String daystr = "";
		if (day > 0 && day < 32)
		{
			if (day != 20 && day != 30)
			{
				daystr = days1[(day - 1) / 10].concat(days2[(day - 1) % 10]);
			}
			else
			{
				daystr = days2[(day - 1) / 10].concat(days1[1]);
			}
		}
		String leapStr = isleap==true?"闰":"";
		return ""+years[year1]+years[year2]+years[year3]+years[year4]+"年"+leapStr+months[month - 1]+"月"+daystr+"";
	}

	//获取生肖
	private static String GetChineseZodiac(Date date)
	{
		String[] zodiac = { "鼠", "牛", "虎", "免", "龙", "蛇", "马", "羊", "猴", "鸡", "狗", "猪" };
		int year = LunarInfo.getYear(date);//年份数值
		int index = (year - 4) % 12;
		return zodiac[index];
	}

	//获取节气
	private static String GetSolarTerms(Date date)
	{
		String result = "";

		String[] SolarTerm = new String[] { "小寒", "大寒", "立春", "雨水", "惊蛰", "春分", "清明", "谷雨", "立夏", "小满", "芒种", "夏至", "小暑", "大暑", "立秋", "处暑", "白露", "秋分", "寒露", "霜降", "立冬", "小雪", "大雪", "冬至" };
		int[] sTermInfo = new int[] { 0, 21208, 42467, 63836, 85337, 107014, 128867, 150921, 173149, 195551, 218072, 240693, 263343, 285989, 308563, 331033, 353350, 375494, 397447, 419210, 440795, 462224, 483532, 504758 };

		int year = LunarInfo.getYear(date);//年份数值
		double num;
		Date baseDate = new Date(1900, 1, 6, 2, 5, 0);
		Date newDate;
		for (int day = 1; day <= 24; day++)
		{
			num = 525948.76 * (year - 1900) + sTermInfo[day - 1];
			newDate = new Date(baseDate.getTime() + (int)Math.round(num));//按分钟计算
			if (LunarInfo.getDay(newDate) == LunarInfo.getDay(date)) { result = SolarTerm[day - 1]; break; }
		}
		return result;
	}

	//获取星座
	private static String GetZodiac(Date date)
	{
		String[] zodiac = { "白羊", "金牛", "双子", "巨蟹", "狮子", "处女", "天秤", "天蝎", "射手", "摩羯", "水瓶", "双鱼" };

		int index;
		int m = LunarInfo.getMonth(date);
		int d = LunarInfo.getDay(date);
		int y = m * 100 + d;
		if ((y >= 321) && (y <= 419)) { index = 0; }
		else if ((y >= 420) && (y <= 520)) { index = 1; }
		else if ((y >= 521) && (y <= 620)) { index = 2; }
		else if ((y >= 621) && (y <= 722)) { index = 3; }
		else if ((y >= 723) && (y <= 822)) { index = 4; }
		else if ((y >= 823) && (y <= 922)) { index = 5; }
		else if ((y >= 923) && (y <= 1022)) { index = 6; }
		else if ((y >= 1023) && (y <= 1121)) { index = 7; }
		else if ((y >= 1122) && (y <= 1221)) { index = 8; }
		else if ((y >= 1222) || (y <= 119)) { index = 9; }
		else if ((y >= 120) && (y <= 218)) { index = 10; }
		else if ((y >= 219) && (y <= 320)) { index = 11; }
		else { index = 0; }

		return zodiac[index];
	}

	private static int ConvertDayOfWeek(DayOfWeek dayOfWeek)
	{
		int result = 0;
		switch (dayOfWeek){
			case  SUNDAY:
				result = 1;
				break;
			case  MONDAY:
				result = 2;
				break;
			case  TUESDAY:
				result = 3;
				break;
			case  WEDNESDAY:
				result = 4;
				break;
			case  THURSDAY:
				result = 5;
				break;
			case  FRIDAY:
				result = 6;
				break;
			case  SATURDAY:
				result = 7;
				break;
			default:

		}
		return result;
	}

	private static boolean CompareWeekDayHoliday(Date date, int month, int week, int day)
	{
		boolean res = false;

		if (LunarInfo.getMonth(date) == month) //月份相同
		{
			Calendar c = Calendar.getInstance();
			c.setTime(date);
			if (c.get(Calendar.DAY_OF_WEEK) == day) //星期几相同
			{

				Calendar cal = Calendar.getInstance();
				cal.setTime(date);
				//设置为1号,当前日期既为本月第一天
				cal.set(Calendar.DAY_OF_MONTH,1);
				cal.add(Calendar.MONTH, 0);
				int i  = LunarInfo.getDayOfWeek(cal.getTime());
				//计算第一周剩余天数
				int firWeekDays = 7 -i + 1;

				if (i > day && (week - 1) * 7 + day + firWeekDays == LunarInfo.getDayOfMonth(c.getTime()))
				{
					res = true;
				}
				else if (day + firWeekDays + (week - 2) * 7 == LunarInfo.getDayOfMonth(c.getTime()))
				{
					res = true;
				}
			}
		}

		return res;
	}


	private static int GetChineseMonthDays(int year, int month)
	{
		int[] LunarDateArray = new int[]{
				0x04BD8,0x04AE0,0x0A570,0x054D5,0x0D260,0x0D950,0x16554,0x056A0,0x09AD0,0x055D2,
				0x04AE0,0x0A5B6,0x0A4D0,0x0D250,0x1D255,0x0B540,0x0D6A0,0x0ADA2,0x095B0,0x14977,
				0x04970,0x0A4B0,0x0B4B5,0x06A50,0x06D40,0x1AB54,0x02B60,0x09570,0x052F2,0x04970,
				0x06566,0x0D4A0,0x0EA50,0x06E95,0x05AD0,0x02B60,0x186E3,0x092E0,0x1C8D7,0x0C950,
				0x0D4A0,0x1D8A6,0x0B550,0x056A0,0x1A5B4,0x025D0,0x092D0,0x0D2B2,0x0A950,0x0B557,
				0x06CA0,0x0B550,0x15355,0x04DA0,0x0A5B0,0x14573,0x052B0,0x0A9A8,0x0E950,0x06AA0,
				0x0AEA6,0x0AB50,0x04B60,0x0AAE4,0x0A570,0x05260,0x0F263,0x0D950,0x05B57,0x056A0,
				0x096D0,0x04DD5,0x04AD0,0x0A4D0,0x0D4D4,0x0D250,0x0D558,0x0B540,0x0B6A0,0x195A6,
				0x095B0,0x049B0,0x0A974,0x0A4B0,0x0B27A,0x06A50,0x06D40,0x0AF46,0x0AB60,0x09570,
				0x04AF5,0x04970,0x064B0,0x074A3,0x0EA50,0x06B58,0x055C0,0x0AB60,0x096D5,0x092E0,
				0x0C960,0x0D954,0x0D4A0,0x0DA50,0x07552,0x056A0,0x0ABB7,0x025D0,0x092D0,0x0CAB5,
				0x0A950,0x0B4A0,0x0BAA4,0x0AD50,0x055D9,0x04BA0,0x0A5B0,0x15176,0x052B0,0x0A930,
				0x07954,0x06AA0,0x0AD50,0x05B52,0x04B60,0x0A6E6,0x0A4E0,0x0D260,0x0EA65,0x0D530,
				0x05AA0,0x076A3,0x096D0,0x04BD7,0x04AD0,0x0A4D0,0x1D0B6,0x0D250,0x0D520,0x0DD45,
				0x0B5A0,0x056D0,0x055B2,0x049B0,0x0A577,0x0A4B0,0x0AA50,0x1B255,0x06D20,0x0ADA0,
				0x14B63
		};
		int num = (LunarDateArray[year - 1900] & 0x0000FFFF);
		int bitpostion = 16 - month;
		int bit = 1 << bitpostion;
		if ((num & bit) == 0)
		{
			return 29;
		}
		else
		{
			return 30;
		}
	}

	//获取节日
	private static String GetFestivals(Date date) throws Exception
	{
		List<String> festivals = new ArrayList<>();

		List<ThreeTuple<Integer, Integer, String>> sHolidays = new ArrayList<>();
		sHolidays.add(new ThreeTuple<Integer, Integer, String>(1, 1, "元旦" ));
		sHolidays.add(new ThreeTuple<Integer, Integer, String>(2, 2, "世界湿地日" ));
		sHolidays.add(new ThreeTuple<Integer, Integer, String>(2, 10, "国际气象节" ));
		sHolidays.add(new ThreeTuple<Integer, Integer, String>(2, 14, "情人节" ));
		sHolidays.add(new ThreeTuple<Integer, Integer, String>(3, 8, "妇女节" ));
		sHolidays.add(new ThreeTuple<Integer, Integer, String>(3, 12, "植树节" ));
		sHolidays.add(new ThreeTuple<Integer, Integer, String>(3, 15, "消费者权益日" ));
		sHolidays.add(new ThreeTuple<Integer, Integer, String>(4, 1, "愚人节" ));
		sHolidays.add(new ThreeTuple<Integer, Integer, String>(4, 7, "世界卫生日" ));
		sHolidays.add(new ThreeTuple<Integer, Integer, String>(4, 22, "世界地球日" ));
		sHolidays.add(new ThreeTuple<Integer, Integer, String>(5, 1, "劳动节" ));
		sHolidays.add(new ThreeTuple<Integer, Integer, String>(5, 4, "青年节" ));
		sHolidays.add(new ThreeTuple<Integer, Integer, String>(5, 8, "世界红十字日" ));
		sHolidays.add(new ThreeTuple<Integer, Integer, String>(5, 12, "国际护士节" ));
		sHolidays.add(new ThreeTuple<Integer, Integer, String>(6, 1, "国际儿童节" ));
		sHolidays.add(new ThreeTuple<Integer, Integer, String>(6, 5, "世界环境保护日" ));
		sHolidays.add(new ThreeTuple<Integer, Integer, String>(6, 26, "国际禁毒日" ));
		sHolidays.add(new ThreeTuple<Integer, Integer, String>(7, 1, "建党节" ));
		sHolidays.add(new ThreeTuple<Integer, Integer, String>(8, 1, "建军节" ));
		sHolidays.add(new ThreeTuple<Integer, Integer, String>(8, 15, "抗日战争胜利纪念" ));
		sHolidays.add(new ThreeTuple<Integer, Integer, String>(9, 10, "教师节" ));
		sHolidays.add(new ThreeTuple<Integer, Integer, String>(9, 18, "九·一八事变纪念日" ));
		sHolidays.add(new ThreeTuple<Integer, Integer, String>(9, 20, "国际爱牙日" ));
		sHolidays.add(new ThreeTuple<Integer, Integer, String>(9, 27, "世界旅游日" ));
		sHolidays.add(new ThreeTuple<Integer, Integer, String>(10, 1, "国庆节" ));
		sHolidays.add(new ThreeTuple<Integer, Integer, String>(10, 24, "联合国日" ));
		sHolidays.add(new ThreeTuple<Integer, Integer, String>(11, 10, "世界青年节" ));
		sHolidays.add(new ThreeTuple<Integer, Integer, String>(12, 1, "世界艾滋病日" ));
		sHolidays.add(new ThreeTuple<Integer, Integer, String>(12, 3, "世界残疾人日" ));
		sHolidays.add(new ThreeTuple<Integer, Integer, String>(12, 24, "平安夜" ));
		sHolidays.add(new ThreeTuple<Integer, Integer, String>(12, 25, "圣诞节" ));

		List<ThreeTuple<Integer, Integer, String>> lHolidays = new ArrayList<>();
		lHolidays.add(new ThreeTuple<Integer, Integer, String>(1, 1, "春节"));
		lHolidays.add(new ThreeTuple<Integer, Integer, String>(1, 15, "元宵"));
		lHolidays.add(new ThreeTuple<Integer, Integer, String>(5, 5, "端午"));
		lHolidays.add(new ThreeTuple<Integer, Integer, String>(7, 7, "七夕"));
		lHolidays.add(new ThreeTuple<Integer, Integer, String>(7, 15, "中元"));
		lHolidays.add(new ThreeTuple<Integer, Integer, String>(8, 15, "中秋"));
		lHolidays.add(new ThreeTuple<Integer, Integer, String>(9, 9, "重阳"));
		lHolidays.add(new ThreeTuple<Integer, Integer, String>(12, 8, "腊八"));
		//new LunarHolidayStruct(12, 30, 0, "除夕")

		List<FourthTuple<Integer, Integer,Integer, String>> wHolidays = new ArrayList<>();
		wHolidays.add(new FourthTuple<Integer, Integer,Integer, String>(5, 2, 1, "母亲节"));
		wHolidays.add(new FourthTuple<Integer, Integer,Integer, String>(5, 3, 1, "全国助残日"));
		wHolidays.add(new FourthTuple<Integer, Integer,Integer, String>(6, 3, 1, "父亲节"));
		wHolidays.add(new FourthTuple<Integer, Integer,Integer, String>(9, 3, 3, "国际和平日"));
		wHolidays.add(new FourthTuple<Integer, Integer,Integer, String>(9, 4, 1, "国际聋人节"));
		wHolidays.add(new FourthTuple<Integer, Integer,Integer, String>(10, 1, 2, "国际住房日"));
		wHolidays.add(new FourthTuple<Integer, Integer,Integer, String>(10, 1, 4, "国际减轻自然灾害日"));
		wHolidays.add(new FourthTuple<Integer, Integer,Integer, String>(11, 4, 5, "感恩节"));

		//公历节日
		for (ThreeTuple<Integer, Integer, String> sh  : sHolidays)
		{
			if ((sh.first == LunarInfo.getMonth(date)) && (sh.second == LunarInfo.getDayOfMonth(date))) { festivals.add(sh.third); break; }
		}

		//公历转换农历
		String dateCode = ChinaDate.solarToLunar(LunarInfo.getFormatDate(date,"yyyy-MM-dd"), false);
		Date lunar_date =  LunarInfo.FormatToDate(dateCode,"yyyyMMdd");
		//年份数值
		int lunar_year = LunarInfo.getYear(lunar_date);
		//查询是否农历的该月是否为闰月
		int leapMonth = LunarInfo.leapMonth(lunar_year);
		//年份数值
		int lunar_month = LunarInfo.getMonth(lunar_date);
		int lunar_day = LunarInfo.getDayOfMonth(lunar_date);
		boolean isleap = false;
		if (leapMonth>0 &&   leapMonth == lunar_month)
		{
			isleap = true;
		}
		if (isleap == false)
		{
			for (ThreeTuple<Integer, Integer, String> lh : lHolidays)
			{
				if ((lh.first == lunar_month) && (lh.second == lunar_day)) { festivals.add(lh.third); break; }
			}

			//对除夕进行特别处理
			if (lunar_month == 12 && lunar_day == GetChineseMonthDays(lunar_year, 12))
			{
				festivals.add("除夕");
			}
		}

		//其他节日 TODO 有问题
//		for (FourthTuple<Integer, Integer,Integer, String> wh : wHolidays)
//		{
//			if (CompareWeekDayHoliday(date, wh.first, wh.second, wh.third)) { festivals.add(wh.fourth); break; }
//		}

		return String.join(" ", festivals);
	}

	public static ActDeCalendar CompleteCalendarEntity(String dateCode) throws Exception
	{
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");//注意月份是MM
		SimpleDateFormat simpleDateFormat2 = new SimpleDateFormat("yyyy年MM月dd日", Locale.CHINA);
		SimpleDateFormat simpleDateFormat3 = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
		Calendar cal = Calendar.getInstance();
		ActDeCalendar sysCalendar = new ActDeCalendar();
		try
		{
			Date date = simpleDateFormat.parse(dateCode);
			sysCalendar.setDateCode(dateCode);
			sysCalendar.setDate(date);
			sysCalendar.setSolarDate(simpleDateFormat2.format(date));
			cal.setTime(date);
			sysCalendar.setYearCode(String.valueOf(cal.get(Calendar.YEAR)));
//			sysCalendar.setLunarDate(GetLunarDate(date));
//			sysCalendar.setSexagenaryCycle(GetSexagenaryCycle(date));
//			sysCalendar.setChineseZodiac( GetChineseZodiac(date));
//			sysCalendar.setSolarTerms(GetSolarTerms(date));
//			sysCalendar.setZodiac(GetZodiac(date));
			sysCalendar.setFestivals(GetFestivals(date));
			switch (sysCalendar.getFestivals())
			{
				case "元旦": sysCalendar.setHolidaySign(1); sysCalendar.setHolidayDesc("放假调休"); sysCalendar.setHolidayNotes("元旦"); break;
				case "除夕": sysCalendar.setHolidaySign(1); sysCalendar.setHolidayDesc("放假调休"); sysCalendar.setHolidayNotes("除夕"); break;
				case "春节": sysCalendar.setHolidaySign(1); sysCalendar.setHolidayDesc("放假调休"); sysCalendar.setHolidayNotes("春节"); break;
				case "劳动节": sysCalendar.setHolidaySign(1); sysCalendar.setHolidayDesc("放假调休"); sysCalendar.setHolidayNotes("劳动节"); break;
				case "端午": sysCalendar.setHolidaySign(1); sysCalendar.setHolidayDesc("放假调休"); sysCalendar.setHolidayNotes("端午节"); break;
				case "中秋": sysCalendar.setHolidaySign(1); sysCalendar.setHolidayDesc("放假调休"); sysCalendar.setHolidayNotes("中秋节"); break;
				case "国庆节": sysCalendar.setHolidaySign(1); sysCalendar.setHolidayDesc("放假调休"); sysCalendar.setHolidayNotes("国庆节"); break;
				default:
			}

			switch (cal.get(Calendar.DAY_OF_WEEK))
			{
				case 2: sysCalendar.setIfWeekend(false); sysCalendar.setWeek("星期一"); sysCalendar.setWeekCode("Monday"); break;
				case 3: sysCalendar.setIfWeekend(false); sysCalendar.setWeek("星期二");sysCalendar.setWeekCode("Tuesday"); break;
				case 4: sysCalendar.setIfWeekend(false); sysCalendar.setWeek("星期三");sysCalendar.setWeekCode("Wednesday"); break;
				case 5: sysCalendar.setIfWeekend(false); sysCalendar.setWeek("星期四");sysCalendar.setWeekCode("Thursday"); break;
				case 6: sysCalendar.setIfWeekend(false); sysCalendar.setWeek("星期五");sysCalendar.setWeekCode("Friday"); break;
				case 7: sysCalendar.setIfWeekend(true); sysCalendar.setWeek("星期六");sysCalendar.setWeekCode("Saturday"); break;
				case 1: sysCalendar.setIfWeekend(true); sysCalendar.setWeek("星期日");sysCalendar.setWeekCode("Sunday"); break;
				default:
			}
		}
		catch (ParseException parseException)
		{
			parseException.printStackTrace();
		}
		return sysCalendar;
	}
}
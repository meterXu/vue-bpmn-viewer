package com.sipsd.flow.rest.api;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sipsd.flow.service.flowable.IFlowableCalendarService;
import com.sipsd.flow.util.Result;
import com.sipsd.flow.util.holiday.CommonCalendarUtil;
import com.sipsd.flow.util.holiday.LunarInfo;
import com.sipsd.flow.vo.flowable.holiday.ActDeCalendar;
import com.sipsd.flow.vo.flowable.holiday.SysCalendarVo;
import com.sipsd.flow.vo.flowable.holiday.SysInterval;
import com.sipsd.flow.vo.flowable.holiday.SysStatutoryHolidayVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.*;

/**
* @Description: 日历管理
* @Author: gaoqiang
* @Date:   2021-09-29
* @Version: V1.0
*/
@Slf4j
@Api(tags="日历/节假日管理")
@RestController
@RequestMapping("/rest/calendar")
public class ApiFlowableCalendarResource
{
   @Autowired
   private IFlowableCalendarService flowableCalendarService;

   /**
    * 分页列表查询
    *
    * @param sysCalendar
    * @param page
    * @param sysCalendar
    * @return
    */
   @ApiOperation(value="日历管理-分页列表查询", notes="日历管理-分页列表查询")
   @GetMapping(value = "/page")
   public Result queryPageList(Page page, ActDeCalendar sysCalendar) {
	   return Result.data(flowableCalendarService.page(page, Wrappers.query(sysCalendar)));
   }

   /**
    * 添加
    *
    * @param sysCalendar
    * @return
    */
   @ApiOperation(value="日历管理-添加", notes="日历管理-添加")
   @PostMapping(value = "/add")
   public Result add(@RequestBody ActDeCalendar sysCalendar) {
	   return Result.data(flowableCalendarService.save(sysCalendar));
   }

	/**
	 * 编辑
	 *
	 * @param sysCalendar
	 * @return
	 */
	@ApiOperation(value="日历管理-编辑", notes="日历管理-编辑")
	@PutMapping(value = "/updateById")
	public Result edit(@RequestBody ActDeCalendar sysCalendar) {
		return Result.data(flowableCalendarService.updateById(sysCalendar));
	}

   /**
    * 通过日期编码修改记录
    *
    * @param sysCalendar
    * @return
    */
   @ApiOperation(value="日历管理-通过日期编码修改记录", notes="日历管理-通过日期编码修改记录")
   @PutMapping(value = "/updateByDateCode")
   public Result updateByDateCode(@RequestBody ActDeCalendar sysCalendar) {
	   flowableCalendarService.updateByDateCode(sysCalendar);
	   return Result.ok();
   }

	/**
	 * 通过id删除
	 *
	 * @param id
	 * @return
	 */
	@ApiOperation(value="日历管理-通过id删除", notes="日历管理-通过id删除")
	@DeleteMapping("/{id}" )
	public Result removeById(@PathVariable String id) {
		flowableCalendarService.removeById(id);
		return Result.ok();
	}

   /**
    * 通过日期编码修改记录
    *
    * @param deteCode
    * @return
    */
   @ApiOperation(value="日历管理-通过日期编码删除", notes="日历管理-通过日期编码删除")
   @DeleteMapping("/removeByDateCode" )
   public Result removeByDateCode(@RequestParam(name="dateCode",required=true) String deteCode) {
	   flowableCalendarService.removeByDateCode(deteCode);
	   return Result.ok();
   }

   /**
    * 批量删除
    *
    * @param ids
    * @return
    */
   @ApiOperation(value="日历管理-批量删除", notes="日历管理-批量删除")
   @DeleteMapping(value = "/deleteBatch")
   public Result deleteBatch(@RequestParam(name="ids",required=true) String ids) {
	   return Result.data(this.flowableCalendarService.removeByIds(Arrays.asList(ids.split(","))));
   }

   /**
    * 通过id查询
    *
    * @param id
    * @return
    */
   @ApiOperation(value="日历管理-通过id查询", notes="日历管理-通过id查询")
   @GetMapping("/{id}" )
   public Result getById(@PathVariable("id" ) String id) {
	   return Result.data(flowableCalendarService.getById(id));
   }

	/**
	 * 通过日历编码查询
	 *
	 * @param dateCode
	 * @return
	 */
	@ApiOperation(value="日历管理-通过日历编码查询", notes="日历管理-通过日历编码查询")
	@GetMapping("/getByDateCode" )
	public Result getByDateCode(@PathVariable("dateCode" ) String dateCode) {
		return Result.data(flowableCalendarService.queryByDateCode(dateCode));
	}


	/**
	 * 分页获取日历
	 *
	 * @param page    参数集
	 * @param sysCalendarVo 查询参数列表
	 * @return 日历集合
	 */
	@GetMapping("/getCalendars")
	public Result getCalendars(Page page, SysCalendarVo sysCalendarVo) {
		return Result.ok(flowableCalendarService.QueryCalendars(page, sysCalendarVo));
	}

	/**
	 * 日期编码查询日历
	 *
	 * @param dateCode
	 * @return
	 */
	@ApiOperation(value="日历管理-通过日期编码查询", notes="日历管理-通过日期编码查询")
	@GetMapping(value = "/getCalendar")
	public Result getCalendar(@RequestParam(name="dateCode",required=false) String dateCode) {
		return Result.data(this.flowableCalendarService.queryByDateCode(dateCode));
	}

	/**
	 * 完善日历数据
	 *
	 * @param dateCode
	 * @return
	 */
	@ApiOperation(value="日历管理-完善日历数据", notes="日历管理-完善日历数据")
	@GetMapping(value = "/completeCalendarData")
	public Result completeCalendarData(@RequestParam(name="dateCode",required=false) String dateCode) throws Exception {
		if(StringUtils.isEmpty(dateCode))
		{
			SimpleDateFormat chineseDateFormat = new SimpleDateFormat("yyyyMMdd", Locale.CHINA);
			dateCode = chineseDateFormat.format(new Date());
		}
		ActDeCalendar sysCalendar = CommonCalendarUtil.CompleteCalendarEntity(dateCode);
		flowableCalendarService.save(sysCalendar);
		return Result.ok();
	}

	/**
	 * 完善日历数据
	 *
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	@ApiOperation(value="日历管理-批量完善日历数据", notes="日历管理-批量完善日历数据")
	@GetMapping(value = "/completeCalendarDatas")
	@Transactional(rollbackFor = Exception.class)
	public Result completeCalendarDatas(@RequestParam(name="startDate",required=true) String startDate,
                                        @RequestParam(name="endDate",required=true) String endDate) throws Exception {

		List<ActDeCalendar> calendarList = new ArrayList<>();
		if(Integer.parseInt(startDate) > Integer.parseInt(endDate))
		{
			return Result.failed("开始时间不可大于结束时间!");
		}
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		Date date_start = sdf.parse(startDate);
		Date date_end = sdf.parse(endDate);
		Date date =date_start;
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);//用Calendar 进行日期比较判断
		while (cal.getTime().compareTo(date_end)<=0){
			String dateCode  = sdf.format(cal.getTime());
			log.info(dateCode);
			calendarList.add(CommonCalendarUtil.CompleteCalendarEntity(dateCode));
			cal.add(Calendar.DATE, 1);//增加一天 放入集合
		}
		flowableCalendarService.saveBatch(calendarList);
		return Result.ok();
	}

	/**
	 * 获取节假日
	 *
	 * @param startDate
	 * @param endDate
	 *  @param groupCode
	 * @return
	 */
	@ApiOperation(value="节假日管理-获取所有节假日", notes="节假日管理-获取所有节假日")
	@RequestMapping(value = "/GetAllHolidays",method = {RequestMethod.GET,RequestMethod.POST})
	public Result GetAllHolidays(@RequestParam(name="startData",required=true,defaultValue = "2021-01-01") String startDate,
                                 @RequestParam(name="endData",required=true,defaultValue = "2021-12-31") String endDate,
                                 @RequestParam(name="groupCode",required=false) String groupCode) throws Exception
	{
		return Result.data(flowableCalendarService.getAllHolidays(startDate,endDate,groupCode));
	}

	/**
	 * 是否是节假日
	 *
	 * @param dateTime
	 * @return
	 */
	@ApiOperation(value="节假日管理-是否是节假日", notes="节假日管理-是否是节假日")
	@GetMapping(value = "/isHoliday")
	public Result isHoliday(@RequestParam(name="dateTime",required=true,defaultValue = "2021-10-01 00:00:00") String dateTime) throws Exception {
		Date date = LunarInfo.FormatToDate(dateTime,"yyyy-MM-dd HH:mm:ss");
		return Result.data(flowableCalendarService.isHoliday(date));
	}

	/**
	 * 查询法定节假日
	 *
	 * @param page    参数集
	 * @param sysStatutoryHolidayVo 查询参数列表
	 * @return 日历集合
	 */
	@GetMapping("/getStatutoryHolidays")
	@ApiOperation(value="节假日管理-查询法定节假日", notes="节假日管理-查询法定节假日")
	public Result getStatutoryHolidays(Page page, SysStatutoryHolidayVo sysStatutoryHolidayVo) {
		return Result.ok(flowableCalendarService.queryStatutoryHolidays(page, sysStatutoryHolidayVo));
	}


	/**
	 * 批量设置为法定节假日
	 *
	 * @param dateCode
	 * @param halfDaySign
	 * @param holidaySign
	 * @param holidayDesc
	 * @param notes
	 * @return 日历集合
	 */
	@PostMapping("/setToStatutoryHoliday")
	@ApiOperation(value="节假日管理-设置为节假日/补班", notes="节假日管理-设置为节假日/补班")
	public Result setToStatutoryHoliday(@RequestParam(name="dateCode",required=true) String dateCode,
                                        @RequestParam(name="halfDaySign",required=true) Integer halfDaySign,
                                        @RequestParam(name="holidaySign",required=true) Integer holidaySign,
                                        @RequestParam(name="holidayDesc",required=true) String holidayDesc,
                                        @RequestParam(name="notes",required=true) String notes) {
		flowableCalendarService.setToStatutoryHoliday(dateCode, halfDaySign,holidaySign,holidayDesc,notes);
		return Result.ok();
	}

	/**
	 * 批量设置为法定节假日
	 *
	 * @param dateCode
	 * @param halfDaySign
	 * @param holidaySign
	 * @param holidayDesc
	 * @param notes
	 * @return 日历集合
	 */
	@PostMapping("/batchSetToStatutoryHoliday")
	@ApiOperation(value="节假日管理-批量设置为节假日/补班", notes="节假日管理-批量设置为节假日/补班")
	public Result batchSetToStatutoryHoliday(@RequestParam(name="dateCode",required=true) List<String> dateCode,
                                             @RequestParam(name="halfDaySign",required=true) Integer halfDaySign,
                                             @RequestParam(name="holidaySign",required=true) Integer holidaySign,
                                             @RequestParam(name="holidayDesc",required=true) String holidayDesc,
                                             @RequestParam(name="notes",required=true) String notes) {
		flowableCalendarService.setStatutoryHolidays(dateCode,halfDaySign,holidaySign,holidayDesc,notes);
		return Result.ok();
	}


	/**
	 * 间隔天数
	 *
	 * @param startDateCode  格式yyyyMMdd
	 * @param endDateCode   格式yyyyMMdd
	 * @return 间隔天数
	 */
	@ApiOperation(value="节假日管理-间隔工作天数-格式yyyyMMdd", notes="节假日管理-间隔工作天数-格式yyyyMMdd")
	@GetMapping(value = "/interval")
	public Result isHoliday(@RequestParam(name="startDateCode",required=true) String startDateCode,
                            @RequestParam(name="endDateCode",required=true) String endDateCode) throws Exception {
		Date start = LunarInfo.FormatToDate(startDateCode,"yyyyMMdd");
		Date end = LunarInfo.FormatToDate(endDateCode,"yyyyMMdd");
		if(end.compareTo(start)<0)
		{
			return Result.failed("开始时间不可大于结束时间!");
		}
		LambdaQueryWrapper<ActDeCalendar> wrapper =  new LambdaQueryWrapper<>();
		wrapper.eq(ActDeCalendar::getDateCode,startDateCode);
		ActDeCalendar sysCalendar = flowableCalendarService.getOne(wrapper);
		if(sysCalendar==null)
		{
			return Result.failed("开始日期没有维护!");
		}
		wrapper =  new LambdaQueryWrapper<>();
		wrapper.eq(ActDeCalendar::getDateCode,endDateCode);
		sysCalendar = flowableCalendarService.getOne(wrapper);
		if(sysCalendar==null)
		{
			return Result.failed("结束日期没有维护!");
		}

		Integer total = flowableCalendarService.totalDay(startDateCode,endDateCode);
		Integer holiday = flowableCalendarService.interval(startDateCode,endDateCode);
		SysInterval sysInterval = new SysInterval();
		sysInterval.setTotal(total);
		sysInterval.setHoliday(holiday);
		sysInterval.setWorkday(total-holiday);
		return  Result.ok(sysInterval);
	}
}
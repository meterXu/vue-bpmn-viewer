package com.sipsd.flow.common;


import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

public class DateUtil {
  public static String formatDate(Date date) {
    return formatDateByFormat(date, "yyyy-MM-dd");
  }
  
  public static String formatDateByFormat(Date date, String format) {
    String result = "";
    if (date != null)
      try {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        result = sdf.format(date);
      } catch (Exception ex) {
        ex.printStackTrace();
      }  
    return result;
  }
  
  public static String formatHourTime(Long ms) {
    return formatTime(ms, "1");
  }
  
  public static String formatMinuteTime(Long ms) {
    return formatTime(ms, "2");
  }
  
  public static String formatSecondTime(Long ms) {
    return formatTime(ms, "3");
  }
  
  public static String formatMilliSecondTime(Long ms) {
    return formatTime(ms, "4");
  }
  
  public static String formatTime(Long ms, String type) {
    Integer ss = Integer.valueOf(1000);
    Integer mi = Integer.valueOf(ss.intValue() * 60);
    Integer hh = Integer.valueOf(mi.intValue() * 60);
    Integer dd = Integer.valueOf(hh.intValue() * 24);
    Long day = Long.valueOf(ms.longValue() / dd.intValue());
    Long hour = Long.valueOf((ms.longValue() - day.longValue() * dd.intValue()) / hh.intValue());
    Long minute = Long.valueOf((ms.longValue() - day.longValue() * dd.intValue() - hour.longValue() * hh.intValue()) / mi.intValue());
    Long second = Long.valueOf((ms.longValue() - day.longValue() * dd.intValue() - hour.longValue() * hh.intValue() - minute.longValue() * mi.intValue()) / ss.intValue());
    Long milliSecond = Long.valueOf(ms.longValue() - day.longValue() * dd.intValue() - hour.longValue() * hh.intValue() - minute.longValue() * mi.intValue() - second.longValue() * ss.intValue());
    StringBuffer sb = new StringBuffer();
    switch (type) {
      case "1":
        if (day.longValue() > 0L)
          sb.append(day + "天"); 
        if (hour.longValue() > 0L)
          sb.append(hour + "小时"); 
        return sb.toString();
      case "2":
        if (day.longValue() > 0L)
          sb.append(day + "天"); 
        if (hour.longValue() > 0L)
          sb.append(hour + "小时"); 
        if (minute.longValue() > 0L)
          sb.append(minute + "分"); 
        return sb.toString();
      case "3":
        if (day.longValue() > 0L)
          sb.append(day + "天"); 
        if (hour.longValue() > 0L)
          sb.append(hour + "小时"); 
        if (minute.longValue() > 0L)
          sb.append(minute + "分"); 
        if (second.longValue() > 0L)
          sb.append(second + "秒"); 
        return sb.toString();
      case "4":
          if (day.longValue() > 0L)
              sb.append(day + "天"); 
            if (hour.longValue() > 0L)
              sb.append(hour + "小时"); 
            if (minute.longValue() > 0L)
              sb.append(minute + "分"); 
            if (second.longValue() > 0L)
              sb.append(second + "秒"); 
        if (milliSecond.longValue() > 0L)
          sb.append(milliSecond + "毫秒"); 
        return sb.toString();
    } 
    if (day.longValue() > 0L)
      sb.append(day + "天"); 
    if (hour.longValue() > 0L)
      sb.append(hour + "小时"); 
    if (minute.longValue() > 0L)
      sb.append(minute + "分"); 
    if (second.longValue() > 0L)
      sb.append(second + "秒"); 
    return sb.toString();
  }
  
  public static Date parseDate(Date date) {
    return date;
  }
  
  public static Date parseDate(String date) throws ParseException {
    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
    return df.parse(date);
  }
  
  public static Date parseDate(String date, String format) throws ParseException {
    SimpleDateFormat df = new SimpleDateFormat(format);
    return df.parse(date);
  }
  
  public static Date parseSqlDate(Date date) {
    if (date != null)
      return new Date(date.getTime()); 
    return null;
  }
  
  public static String format(Date date, String format) {
    String result = "";
    try {
      if (date != null) {
        DateFormat df = new SimpleDateFormat(format);
        result = df.format(date);
      } 
    } catch (Exception exception) {}
    return result;
  }
  
  public static List<String> getDaysByWeek(Date date) throws Exception {
    date = getdate(format1(date));
    List<String> days = new ArrayList<>();
    GregorianCalendar gcLast = (GregorianCalendar)Calendar.getInstance();
    gcLast.setTime(date);
    String firstWeek = getFirstWeekDay(date);
    for (int i = 0; i < 7; ) {
      Date firstWeekDate = getdate1(firstWeek);
      firstWeekDate = addDate(firstWeekDate, i);
      int j = diffDate(date, firstWeekDate);
      if (j >= 0) {
        days.add(format1(firstWeekDate));
        i++;
      } 
    } 
    return days;
  }
  
  public static Date getNextWeek(Date date, int count) {
    Calendar strDate = Calendar.getInstance();
    strDate.setTime(date);
    strDate.add(5, count * 7);
    GregorianCalendar currentDate = new GregorianCalendar();
    currentDate.set(strDate.get(1), strDate
        .get(2), strDate.get(5));
    Date day = currentDate.getTime();
    return day;
  }
  
  public static List<String> getDaysByDate(Date date) {
    List<String> days = new ArrayList<>();
    GregorianCalendar gcLast = (GregorianCalendar)Calendar.getInstance();
    gcLast.setTime(date);
    int dayss = getDay(date);
    String monthStr = format(date, "yyyy-MM");
    for (int i = 1; i <= dayss; i++) {
      String day = new String();
      if (i < 10) {
        day = monthStr + "-0" + i;
      } else {
        day = monthStr + "-" + i;
      } 
      days.add(day);
    } 
    return days;
  }
  
  public static String getFirstWeekDay(Date theDate) {
    Calendar calendar = new GregorianCalendar();
    calendar.setTime(theDate);
    calendar.set(calendar.get(1), calendar.get(2), calendar
        .get(5), 0, 0, 0);
    calendar.setFirstDayOfWeek(2);
    calendar.set(7, 2);
    return format(calendar.getTime(), "yyyy-MM-dd") + " 00:00:00";
  }
  
  public static String getLastWeekDay(Date theDate) {
    Calendar calendar = new GregorianCalendar();
    calendar.setTime(theDate);
    calendar.set(calendar.get(1), calendar.get(2), calendar
        .get(5), 0, 0, 0);
    calendar.setFirstDayOfWeek(2);
    calendar.set(7, 2);
    return format(new Date(calendar.getTime().getTime() + 518400000L), "yyyy-MM-dd") + " 23:59:59";
  }
  
  public static String getFirstDay(Date theDate) {
    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
    GregorianCalendar gcLast = (GregorianCalendar)Calendar.getInstance();
    gcLast.setTime(theDate);
    gcLast.set(5, 1);
    String day_first = df.format(gcLast.getTime());
    StringBuffer str = (new StringBuffer()).append(day_first).append(" 00:00:00");
    return str.toString();
  }
  
  public static Date getUpMonth(Date theDate, int month) throws ParseException {
    Calendar c = Calendar.getInstance();
    c.setTime(theDate);
    c.add(2, month);
    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
    String time = format.format(c.getTime());
    return format.parse(time);
  }
  
  public static String getLastDay(Date theDate) {
    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
    GregorianCalendar gcLast = (GregorianCalendar)Calendar.getInstance();
    gcLast.setTime(theDate);
    gcLast.set(5, gcLast
        .getActualMaximum(5));
    String s = df.format(gcLast.getTime());
    StringBuffer str = (new StringBuffer()).append(s).append(" 23:59:59");
    return str.toString();
  }
  
  public static String getMinDay(Date theDate) {
    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
    String s = df.format(theDate);
    StringBuffer str = (new StringBuffer()).append(s).append(" 00:00:00");
    return str.toString();
  }
  
  public static String getMaxDay(Date theDate) {
    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
    String s = df.format(theDate);
    StringBuffer str = (new StringBuffer()).append(s).append(" 23:59:59");
    return str.toString();
  }
  
  public static String format(Date date) {
    return format(date, "yyyy/MM/dd");
  }
  
  public static String format1(Date date) {
    return format(date, "yyyy-MM-dd");
  }
  
  public static int getYear(Date date) {
    Calendar c = Calendar.getInstance();
    c.setTime(date);
    return c.get(1);
  }
  
  public static int getMonth(Date date) {
    Calendar c = Calendar.getInstance();
    c.setTime(date);
    return c.get(2) + 1;
  }
  
  public static int getDay(Date date) {
    Calendar c = Calendar.getInstance();
    c.setTime(date);
    return c.get(5);
  }
  
  public static int getHour(Date date) {
    Calendar c = Calendar.getInstance();
    c.setTime(date);
    return c.get(11);
  }
  
  public static int getMinute(Date date) {
    Calendar c = Calendar.getInstance();
    c.setTime(date);
    return c.get(12);
  }
  
  public static int getSecond(Date date) {
    Calendar c = Calendar.getInstance();
    c.setTime(date);
    return c.get(13);
  }
  
  public static long getMillis(Date date) {
    Calendar c = Calendar.getInstance();
    c.setTime(date);
    return c.getTimeInMillis();
  }
  
  public static int getWeek(Date date) {
    Calendar c = Calendar.getInstance();
    c.setTime(date);
    return c.get(3);
  }
  
  public static String getDate(Date date) {
    return format(date, "yyyy/MM/dd");
  }
  
  public static String getDate(Date date, String formatStr) {
    return format(date, formatStr);
  }
  
  public static String getTime(Date date) {
    return format(date, "HH:mm:ss");
  }
  
  public static String getDateTime(Date date) {
    return format(date, "yyyy-MM-dd HH:mm:ss");
  }
  
  public static Date addDate(Date date, int day) {
    Calendar c = Calendar.getInstance();
    c.setTimeInMillis(getMillis(date) + day * 24L * 3600L * 1000L);
    return c.getTime();
  }
  
  public static int diffDate(Date date, Date date1) {
    return (int)((getMillis(date) - getMillis(date1)) / 86400000L);
  }
  
  public static Long diffDateTime(Date date, Date date1) {
    return Long.valueOf((getMillis(date) - getMillis(date1)) / 1000L);
  }
  
  public static Date getdate(String date) throws Exception {
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    return sdf.parse(date);
  }
  
  public static Date getDate(String date, String format) throws Exception {
    SimpleDateFormat sdf = new SimpleDateFormat(format);
    return sdf.parse(date);
  }
  
  public static Date getJsonDate(String date) throws Exception {
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
    return sdf.parse(date);
  }
  
  public static Date getdate1(String date) throws Exception {
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    return sdf.parse(date);
  }
  
  public static Date getMaxTimeByStringDate(String date) throws Exception {
    String maxTime = date + " 23:59:59";
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    return sdf.parse(maxTime);
  }
  
  public static Date getCurrentDateTime() {
    Date date = new Date();
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    String result = getDateTime(date);
    try {
      return sdf.parse(result);
    } catch (ParseException e) {
      e.printStackTrace();
      return null;
    } 
  }
  
  public static String getCurrentDateTimeToStr() {
    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
    return sdf.format(getCurrentDateTime());
  }
  
  public static String getCurrentDateTimeToStr2() {
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    return sdf.format(getCurrentDateTime());
  }
  
  public static Long getWmsupdateDateTime() {
    Calendar cl = Calendar.getInstance();
    return Long.valueOf(cl.getTimeInMillis());
  }
  
  public static Integer getLeftSeconds(String date) throws Exception {
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    Date condition = sdf.parse(date);
    long n = condition.getTime();
    long s = sdf.parse(getCurrentDateTimeToStr2()).getTime();
    return Integer.valueOf((int)((s - n) / 1000L));
  }
  
  public static List<Date> getDatesBetweenTwoDate(Date beginDate, Date endDate) {
    List<Date> lDate = new ArrayList<>();
    lDate.add(beginDate);
    Calendar cal = Calendar.getInstance();
    cal.setTime(beginDate);
    boolean bContinue = true;
    while (bContinue) {
      cal.add(5, 1);
      if (endDate.after(cal.getTime()))
        lDate.add(cal.getTime()); 
    } 
    lDate.add(endDate);
    return lDate;
  }
  
  public static Date addMonth(Date date, int month) {
    Calendar cal = Calendar.getInstance();
    cal.setTime(date);
    cal.add(2, month);
    return cal.getTime();
  }
  
  public static void main(String[] args) throws Exception {
    System.err.println((new SimpleDateFormat("yyyy-MM-dd mm:HH:ss")).format(addMonth(new Date(), 2)));
    List<Date> days = getDatesBetweenTwoDate(getdate("2013-01-09"), getdate("2013-01-11"));
    System.out.println(days.size());
  }
}

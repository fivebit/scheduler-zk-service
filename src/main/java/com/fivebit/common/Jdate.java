package com.fivebit.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * 基本工具
 * @author huangl
 *
 */
public class Jdate {
	public static String base_day = "";		//基本日期，其他计算都是以这个为基础。该参数需要在程序入口处设置。这个参数只针对没有传日期的函数。
	public static void setLocal(){
		Locale.setDefault(new Locale(Locale.CHINA.toString()));
	}
	public static void setBaseDay(String bday){
		base_day = bday;		
	}
	private static Logger log = LoggerFactory.getLogger(Jdate.class);
	/**
	 * 从字符串中，获取时间戳
	 * @param stime
	 * @return
	 */
	public static String getTimeByStr(String stime){
		return getTimeByStr(stime,"yyyy-MM-dd HH:mm:ss");
		
	}
	public static String getTimeByStr(String stime,String format){
		String re_time = null;  
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		sdf.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));
		Date d;
		try {  
		  
			d = sdf.parse(stime);  
			long l = d.getTime();  
			String str = String.valueOf(l);  
			re_time = str.substring(0, 10);   

		} catch (ParseException e) {  
			log.info("getTimebyStr meet exception"+e.getMessage());
		}
		return re_time; 
	}
	/**
	 * 检测输入是否是特定格式的日期字符串。如20160608 false 2016-09-06 12:01:12 true
	 * @param day
	 * @return
	 */
	public static Boolean checkIsStringDate(String day){
		Date _date = null;
		Boolean ret= true;
		try{
			 SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			 _date = sdf.parse(day);
			 if(day.equals(sdf.format(new Date(_date.getTime()))) == false){
				 ret = false;
			 }
		}catch (ParseException e){
			ret = false;
		}
		return ret;
	}
	/**
	 * 日期时间戳转成date类型。如果day是空的，则使用当前时间。
	 * @param day 20160808
	 * @return date
	 */
	public static Date getDateByString(String day){
		Date ret = null;
		if(day.isEmpty() == true ){
			ret = new Date();
		}else{
			try{
				 SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");  
				 ret = sdf.parse(day);
			}catch (ParseException e){
				log.info("getDateByString meet exception:"+e.getMessage());
			}
		}
		return ret;
	}
	/**
	 * 时间戳转换成字符串
	 * @param ctime
	 * @return
	 */
	public static String getStrTime(String ctime){   
		return getStrTime(ctime,"yyyy/MM/dd");  
	}
	public static String getHourStrTime(String ctime){   
		return getStrTime(ctime,"yyyy/MM/dd hh:mm:ss");  
	}
	public  static String getStrTimeByLong(String ctime){
		String format = "yyyy-MM-dd HH:mm:ss";
		String re_StrTime = null;
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		sdf.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));
		long lcc_time = Long.valueOf(ctime);
		re_StrTime = sdf.format(new Date(lcc_time ));
		return re_StrTime;
	}
	public static String getStrTime(String ctime,String format){
		String re_StrTime = null;  
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		sdf.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));
		long lcc_time = Long.valueOf(ctime);  
		re_StrTime = sdf.format(new Date(lcc_time * 1000L));    
		return re_StrTime;  
	}
	/**
	 * 获取当前时间前N天的时间字符串，例如20150606
	 * n > 0 是当前时候往未来。
	 * n < 0 是当前时候往过去。
	 */
	public static String getNDayString(int n){
		return getNDayString(n,"yyyyMMdd");
	}
	public static String getNDayString(int n,String format){
		int nowtime = getBeforeTime(n*24);
		return getStrTime(String.valueOf(nowtime),format);
	}
	/**
	 * 获取指定时间字符串的前N天
	 */
	public static String getNDayByDayString(String day,int n){
		String t = getTimeByStr(day,"yyyyMMdd");
		int tt = Integer.parseInt(t)+n*24*60*60;
		return getStrTime(String.valueOf(tt),"yyyyMMdd");
	}
	/**
	 * 当天0点时间戳
	 * @return
	 */
	public static int getDayBeginTime(){
		Calendar calendar = Calendar.getInstance(); 
		calendar.setTime(getDateByString(base_day));
		calendar.set(Calendar.HOUR_OF_DAY, 0); 
		calendar.set(Calendar.SECOND, 0); 
		calendar.set(Calendar.MINUTE, 0); 
		calendar.set(Calendar.MILLISECOND, 0); 
		return (int)(calendar.getTimeInMillis()/1000L); 
	}
	/**
	 * 获取指定时间戳的那天的开始时间戳
	 * @param day 时间戳
	 * @return
	 */
	public static int getDayBeginTime(Integer day){
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date(day*1000L));
		calendar.set(Calendar.HOUR_OF_DAY, 0); 
		calendar.set(Calendar.SECOND, 0); 
		calendar.set(Calendar.MINUTE, 0); 
		calendar.set(Calendar.MILLISECOND, 0); 
		return (int)(calendar.getTimeInMillis()/1000L); 
	}
	public static int getDayEndTIme(){
		Calendar calendar = Calendar.getInstance(); 
		calendar.setTime(getDateByString(base_day));
		calendar.set(Calendar.HOUR_OF_DAY, 24); 
		calendar.set(Calendar.SECOND, 0); 
		calendar.set(Calendar.MINUTE, 0); 
		calendar.set(Calendar.MILLISECOND, 0); 
		return (int)(calendar.getTimeInMillis()/1000L); 
	}
	/**
	 * 获取指定时间戳的那天的截至时间戳
	 * @param day
	 * @return
	 */
	public static int getDayEndTIme(Integer day){
		Calendar calendar = Calendar.getInstance(); 
		calendar.setTime(new Date(day*1000L));
		calendar.set(Calendar.HOUR_OF_DAY, 24); 
		calendar.set(Calendar.SECOND, 0); 
		calendar.set(Calendar.MINUTE, 0); 
		calendar.set(Calendar.MILLISECOND, 0); 
		return (int)(calendar.getTimeInMillis()/1000L); 
	}
	/**
	 * 获取当前时间戳
	 * @return
	 */
	public static int getNowTime(){
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(getDateByString(base_day));
		calendar.set(Calendar.MILLISECOND, 0); 
		return (int)(calendar.getTimeInMillis()/1000L); 
	}
	public static String getNowStrTime(){
	    Date date = new Date();
	    long times = date.getTime();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		formatter.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));
		String dateString = formatter.format(date);
		return dateString;
	}

	/**
	 * 获取当前属于的小时
	 * @return
	 */
	public static String getNowHourStr(){
		Date date = new Date();
		long times = date.getTime();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd-HH");
		formatter.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));
		String dateString = formatter.format(date);
		return dateString;
	}
	/**
	 * 获取当前时间N个小时之前的时间戳
	 * @return
	 */
	public static int getBeforeTime(int before){
		Calendar calendar = Calendar.getInstance(); 
		calendar.setTime(getDateByString(base_day));
		calendar.add(Calendar.HOUR_OF_DAY, before); 
		calendar.set(Calendar.MILLISECOND, 0); 
		return (int)(calendar.getTimeInMillis()/1000L); 
	}
	/**
	 * 获取前一天的开始时间戳
	 * @return
	 */
	public static int getLastDayBegin(){
		Calendar calendar = Calendar.getInstance(); 
		calendar.setTime(getDateByString(base_day));
		calendar.add(Calendar.DAY_OF_MONTH, -1);
		calendar.set(Calendar.HOUR_OF_DAY, 0); 
		calendar.set(Calendar.SECOND, 0); 
		calendar.set(Calendar.MINUTE, 0); 
		calendar.set(Calendar.MILLISECOND, 0); 
		return (int)(calendar.getTimeInMillis()/1000L); 
	}
	/**
	 * 获取前一天的结束时间戳
	 * @return
	 */
	public static int getLastDayEnd(){
		return getDayBeginTime();
		
	}
	/**
	 * 获取每周的第一个时间戳
	 * 星期日为一周的第一天	   SUN	MON	TUE	WED	THU	FRI	SAT
	 * DAY_OF_WEEK返回值	1	2	3	4	5	6	7
	 * @return
	 */
	public static int getWeekBeginTime(){
	    Calendar calendar = Calendar.getInstance();// 获取当前日期  
	    calendar.setTime(getDateByString(base_day));
	    calendar.setFirstDayOfWeek(Calendar.MONDAY);
	    calendar.add(Calendar.MONTH, 0);  
	    calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);// 设置为1号,当前日期既为本月第一天  
	    calendar.set(Calendar.HOUR_OF_DAY, 0);  
	    calendar.set(Calendar.MINUTE, 0);  
	    calendar.set(Calendar.SECOND, 0); 
	    return (int)(calendar.getTimeInMillis()/1000L);  
	}
	/**
	 * 获取当前周的最后一个时间戳，其实是下一周的一个时间戳，不需要包括即可
	 * @return
	 */
	public static int getWeekEndTime(){
	    Calendar calendar = Calendar.getInstance();// 获取当前日期  
	    calendar.setTime(getDateByString(base_day));
	    calendar.setFirstDayOfWeek(Calendar.MONDAY);
	    calendar.add(Calendar.WEEK_OF_MONTH, 1);  
	    calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);// 设置为1号,当前日期既为本月第一天  
	    calendar.set(Calendar.HOUR_OF_DAY, 0);  
	    calendar.set(Calendar.MINUTE, 0);  
	    calendar.set(Calendar.SECOND, 0); 
	    return (int)(calendar.getTimeInMillis()/1000L);
	}
	/**
	 * 获取当月的第一个时间戳
	 * @return
	 */
	public static int getMonthBeginTime() {  
	    Calendar calendar = Calendar.getInstance();// 获取当前日期  
	    calendar.setTime(getDateByString(base_day));
	    calendar.add(Calendar.MONTH, 0);  
	    calendar.set(Calendar.DAY_OF_MONTH, 1);// 设置为1号,当前日期既为本月第一天  
	    calendar.set(Calendar.HOUR_OF_DAY, 0);  
	    calendar.set(Calendar.MINUTE, 0);  
	    calendar.set(Calendar.SECOND, 0);    
	    return (int)(calendar.getTimeInMillis()/1000L);  
	}  
	/**
	 * 获取当月的最后一个时间戳
	 * @return
	 */
	public static int getMonthEndTime() {  
	    Calendar calendar = Calendar.getInstance();// 获取当前日期  
	    calendar.setTime(getDateByString(base_day));
	    calendar.add(Calendar.MONTH, 1);  
	    calendar.set(Calendar.DAY_OF_MONTH, 1);// 设置为1号,当前日期既为本月第一天  
	    calendar.set(Calendar.HOUR_OF_DAY, 0);  
	    calendar.set(Calendar.MINUTE, 0);  
	    calendar.set(Calendar.SECOND, 0);    
	    return (int)(calendar.getTimeInMillis()/1000L);  
	} 
	/**
	 * 获取当前时间所在周的第一天字符串
	 * @return
	 */
	public static String getWeekBeginString(){
		int week_begin = getWeekBeginTime();
		return getStrTime(String.valueOf(week_begin),"yyyyMMdd");
	}
	public static String getMonthBeginString(){
		int month_begin = getMonthBeginTime();
		return getStrTime(String.valueOf(month_begin),"yyyyMMdd");
	}
	/*
	 * 获取当前日期的月份的最后一天。
	 */
	public static String getMonthEndString(){
		int month_begin = getMonthEndTime()-1;
		return getStrTime(String.valueOf(month_begin),"yyyyMMdd");	
	}
	/**
	 * 当前时间上一个星期的开始时间。
	 * @return
	 */
	public static String getLastWeekBeginString(){
	    Calendar calendar = Calendar.getInstance();// 获取当前日期  
	    calendar.setTime(getDateByString(base_day));
	    calendar.setFirstDayOfWeek(Calendar.MONDAY);
	    calendar.add(Calendar.MONTH, 0);  
	    calendar.add(Calendar.WEEK_OF_YEAR, -1); 
	    calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);// 设置为1号,当前日期既为本月第一天  
	    calendar.set(Calendar.HOUR_OF_DAY, 0);  
	    calendar.set(Calendar.MINUTE, 0);  
	    calendar.set(Calendar.SECOND, 0); 
	    int week_begin =  (int)(calendar.getTimeInMillis()/1000L); 
		return getStrTime(String.valueOf(week_begin),"yyyyMMdd");
	}
	/**
	 * 当天时间上一个月的开始时间
	 * @return
	 */
	public static String getLastMonthBeginString(){
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(getDateByString(base_day));
		calendar.add(Calendar.MONTH, -1);
		calendar.set(Calendar.DAY_OF_MONTH, 1);// 设置为1号,当前日期既为本月第一天
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		int month_begin = (int) (calendar.getTimeInMillis() / 1000L);
		return getStrTime(String.valueOf(month_begin), "yyyyMMdd");
	}
    /** 
     *  
     * 1 第一季度 2 第二季度 3 第三季度 4 第四季度 
     *  
     * @param date 
     * @return 
     */  
    public static int getSeason(Date date) {  
        int season = 0;  
        Calendar c = Calendar.getInstance();  
        c.setTime(date);  
        int month = c.get(Calendar.MONTH);  
        switch (month) {  
        case Calendar.JANUARY:  
        case Calendar.FEBRUARY:  
        case Calendar.MARCH:  
            season = 1;  
            break;  
        case Calendar.APRIL:  
        case Calendar.MAY:  
        case Calendar.JUNE:  
            season = 2;  
            break;  
        case Calendar.JULY:  
        case Calendar.AUGUST:  
        case Calendar.SEPTEMBER:  
            season = 3;  
            break;  
        case Calendar.OCTOBER:  
        case Calendar.NOVEMBER:  
        case Calendar.DECEMBER:  
            season = 4;  
            break;  
        default:  
            break;  
        }  
        return season;  
    }  
    /** 
     * 取得季度月 
     *  
     * @param date 
     * @return 
     */  
    public static Date[] getSeasonDate(Date date) {  
        Date[] season = new Date[3];  
        Calendar c = Calendar.getInstance();  
        c.setTime(date);  
  
        int nSeason = getSeason(date);  
        if (nSeason == 1) {// 第一季度  
            c.set(Calendar.MONTH, Calendar.JANUARY);  
            season[0] = c.getTime();  
            c.set(Calendar.MONTH, Calendar.FEBRUARY);  
            season[1] = c.getTime();  
            c.set(Calendar.MONTH, Calendar.MARCH);  
            season[2] = c.getTime();  
        } else if (nSeason == 2) {// 第二季度  
            c.set(Calendar.MONTH, Calendar.APRIL);  
            season[0] = c.getTime();  
            c.set(Calendar.MONTH, Calendar.MAY);  
            season[1] = c.getTime();  
            c.set(Calendar.MONTH, Calendar.JUNE);  
            season[2] = c.getTime();  
        } else if (nSeason == 3) {// 第三季度  
            c.set(Calendar.MONTH, Calendar.JULY);  
            season[0] = c.getTime();  
            c.set(Calendar.MONTH, Calendar.AUGUST);  
            season[1] = c.getTime();  
            c.set(Calendar.MONTH, Calendar.SEPTEMBER);  
            season[2] = c.getTime();  
        } else if (nSeason == 4) {// 第四季度  
            c.set(Calendar.MONTH, Calendar.OCTOBER);  
            season[0] = c.getTime();  
            c.set(Calendar.MONTH, Calendar.NOVEMBER);  
            season[1] = c.getTime();  
            c.set(Calendar.MONTH, Calendar.DECEMBER);  
            season[2] = c.getTime();  
        }  
        return season;  
    }  
    /** 
     * 取得季度第一天 
     *  
     * @param date 
     * @return 
     */  
    public static String getFirstDateOfSeason() {
    	Calendar cal=Calendar.getInstance();
    	cal.setTime(getDateByString(base_day));
    	Date date=getSeasonDate(cal.getTime())[0];
    	cal.setTime(date);
    	cal.set(Calendar.DAY_OF_MONTH, 1);
    	int _t = (int)(cal.getTimeInMillis()/1000L);
    	return getStrTime(String.valueOf(_t),"yyyyMMdd");
    } 
    /** 
     * 取得前一个季度第一天 
     *  
     * @param date 
     * @return 
     */  
    public static String getFirstDateOfLastSeason() {
    	Calendar cal=Calendar.getInstance();
    	cal.setTime(getDateByString(base_day));
    	cal.add(Calendar.MONTH, -3);
    	Date date=getSeasonDate(cal.getTime())[0];
    	cal.setTime(date);
    	cal.set(Calendar.DAY_OF_MONTH, 1);
    	int _t = (int)(cal.getTimeInMillis()/1000L);
    	return getStrTime(String.valueOf(_t),"yyyyMMdd");
    }
    /**
     * 计算两个日期字符串相差的天数。
     * 
     */
    public static int diffOfDayString(String day_one,String day_two){
    	String one = getTimeByStr(day_one,"yyyy-MM-dd");
    	String two = getTimeByStr(day_two,"yyyy-MM-dd");
    	int one_int = Integer.parseInt(one);
    	int two_int = Integer.parseInt(two);
    	double div = 0;
    	if(one_int > two_int){
    		div = (double)(one_int-two_int)/(24*60*60);
    		
    	}else{
    		div = (double)(two_int-one_int)/(24*60*60);
    	}
    	return (int)Math.ceil(div);
    }
    /**
     * 在某个字符串上面加add天数之后的日期字符串
     * @param day
     * @param add
     * @return
     */
    public static String addDayString(String day,int add){
    	String two = getTimeByStr(day,"yyyyMMdd");
    	int two_int = Integer.parseInt(two) + add*24*60*60;
    	return getStrTime(String.valueOf(two_int),"yyyyMMdd");
    	
    }
}

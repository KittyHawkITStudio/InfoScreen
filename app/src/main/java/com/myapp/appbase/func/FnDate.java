package com.myapp.appbase.func;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class FnDate {
	/** 以指定格式获取当前时间 */
	public static String getCurrentTime(String format){
    	String result = "";
    	Calendar ca = Calendar.getInstance();  
    	Date time=ca.getTime();  
    	SimpleDateFormat df = new SimpleDateFormat(format, Locale.CHINA);  
    	result = df.format(time);

    	return result;
    }

    /**按小时加减*/
    public static Date dateTimeDiffHour(Date datetime, int diffHour){

		try {
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(datetime);
			calendar.add(Calendar.HOUR, diffHour);
			return calendar.getTime();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}

	}

	/** 得到距某天若干个月的那一天, day为正则往后推，为负则往前推 */
	public static String getDayAway(Date date, int day){
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.set(Calendar.DATE, cal.get(Calendar.DATE) + day);
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
		return df.format(cal.getTime());
	}

	/** 获取一个月的天数 */
	public static int getDaysOfMonth(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		return calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
	}

	public static String getDayOfWeek(Date dt) {
		String[] weekDays = {"星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"};
		Calendar cal = Calendar.getInstance();
		cal.setTime(dt);
		int w = cal.get(Calendar.DAY_OF_WEEK) - 1;
		if (w < 0)
			w = 0;
		return weekDays[w];
	}

	/** 获取下一个整点 */
	public static Date getNextHour(){
		Calendar ca = Calendar.getInstance();
		ca.add(Calendar.HOUR,1);
		ca.set(Calendar.MINUTE,0);
		ca.set(Calendar.SECOND,0);
		ca.set(Calendar.MILLISECOND,0);
		//System.out.println(ca.getTime());
		//for test
		SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss", Locale.CHINA);

		System.out.println("下一个整点：" + df.format(ca.getTime()));
		return ca.getTime();
	}

	/**
	 * 获取指定时间对应的毫秒数
	 * @param time "HH:mm:ss"
	 * @return
	 */
	public  static long getTimeMillis(String time) {
		try {
			DateFormat dateFormat = new SimpleDateFormat("yy-MM-dd HH:mm:ss");
			DateFormat dayFormat = new SimpleDateFormat("yy-MM-dd");
			Date curDate = dateFormat.parse(dayFormat.format(new Date()) + " " + time);
			return curDate.getTime();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return 0;
	}

	/** 获取今天以后或以前的某一天*/
	public static Date getOneDateFromDay(int after){

		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());
		calendar.add(calendar.DATE, after);//把日期往后增加一天.整数往后推,负数往前移动
		Date date =calendar.getTime(); //这个时间就是日期往后推一天的结果

		return date;
	}

	//农历操作***************************************************************



}

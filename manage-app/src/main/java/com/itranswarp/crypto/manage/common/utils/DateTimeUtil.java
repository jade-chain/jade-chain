package com.itranswarp.crypto.manage.common.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * @author biqingguo
 */
public class DateTimeUtil {
	private static final String DATE_FORMAT_NORMAL = "yyyy-MM-dd";

	/**
	 * java实现一个月的最后一个星期几，星期日为1，依次类推
	 *
	 * @param date
	 *            时间
	 * @param day
	 *            周几
	 * @return 当月的日期
	 */
	public static int getDate(Date date, int day) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.MONTH, 1);// 月份+1
		calendar.set(Calendar.DAY_OF_MONTH, 1);// 天设为一个月的第一天
		calendar.add(Calendar.DAY_OF_MONTH, -1);// 本月最后一天
		int a = calendar.get(Calendar.DAY_OF_WEEK);
		calendar.add(Calendar.DAY_OF_MONTH, day - a > 0 ? -a - (7 - day) : day - a);// 根据月末最后一天是星期几，向前偏移至最近的周几
		return calendar.get(Calendar.DAY_OF_MONTH);
	}

	public static Date getBeginDate(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.set(Calendar.DAY_OF_MONTH, 1);// 天设为一个月的第一天
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		return calendar.getTime();
	}

	public static Date getEndDate(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.set(Calendar.DAY_OF_MONTH, -1);// 天设为一个月的第一天
		calendar.set(Calendar.HOUR_OF_DAY, -1);
		calendar.set(Calendar.MINUTE, -1);
		calendar.set(Calendar.SECOND, -1);
		calendar.set(Calendar.MILLISECOND, -1);
		return calendar.getTime();
	}

	/**
	 * @param date
	 *            当前日期
	 * @param day
	 *            周几，1为周日
	 * @return 日期
	 */
	public static int getDateWeek(Date date, int day) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.set(Calendar.DAY_OF_MONTH, 1);// 天设为一个月的第一天
		int a = calendar.get(Calendar.DAY_OF_WEEK);
		calendar.add(Calendar.DAY_OF_MONTH, day - a > 0 ? day - a : 7 - a + day);// 根据月初第一天是星期几，向后偏移至最近的周几
		return calendar.get(Calendar.DAY_OF_MONTH);
	}

	/**
	 * 计算输入日期所在月的最后一天
	 *
	 * @param date
	 *            当前日期
	 * @return 当前日期所在月的最后一天
	 */
	public static int getLastDayOfMonth(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		calendar.add(Calendar.MONTH, 1);
		calendar.add(Calendar.DAY_OF_MONTH, -1);
		return calendar.get(Calendar.DAY_OF_MONTH);
	}

	/**
	 * 取得当月天数
	 *
	 * @param date
	 * @return
	 */
	public static int getThisMonthDays(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.set(Calendar.DATE, 1);// 把日期设置为当月第一天
		calendar.roll(Calendar.DATE, -1);// 日期回滚一天，也就是最后一天
		return calendar.get(Calendar.DATE);
	}

	/**
	 * 取得当前日期最近的工作日
	 *
	 * @param date
	 * @return
	 */
	public static int getNearestDay(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		/**
		 * 拿到当前日期是周几
		 */
		int i = calendar.get(Calendar.DAY_OF_WEEK);
		/**
		 * 如果不是周六日直接返回
		 */
		if (i != 1 && i != 7) {
			return calendar.get(Calendar.DAY_OF_MONTH);
		}
		/**
		 * 如果是周六日则去取最近的工作时间 如果是周六则向前一天取周五，如果当前日期是1号则向后取周一
		 * 如果是周日则向后取周一，入过时月末则向前取周五
		 */
		int day = calendar.get(Calendar.DAY_OF_MONTH); // 获取当前天数
		int daysCount = DateTimeUtil.getThisMonthDays(date);
		switch (i) {
		case 1:
			/**
			 * 如果是周日则向后取周一，如果是月末则向前取周五
			 */
			if (day == daysCount) {
				calendar.roll(Calendar.DATE, -2);
			} else {
				calendar.add(Calendar.DAY_OF_MONTH, 1);
			}
			break;
		case 7:
			if (day == 1) {
				calendar.add(Calendar.DAY_OF_MONTH, 2);
			} else {
				calendar.roll(Calendar.DATE, -1);
			}
			break;

		default:
			break;
		}
		return calendar.get(Calendar.DAY_OF_MONTH);
	}

	public static Date convertAsDateString(String origin) {
		return convertAsDateString(origin, DateTimeUtil.DATE_FORMAT_NORMAL);
	}

	public static Date convertAsDateString(String origin, String format) {
		try {
			return new SimpleDateFormat(format).parse(origin);
		} catch (ParseException e) {
			throw new IllegalArgumentException("Date or Time String is invalid.", e);
		}
	}

	public static String converAsStringDate(Date date) {
		return DateTimeUtil.converAsStringDate(date, DateTimeUtil.DATE_FORMAT_NORMAL);
	}

	public static String converAsStringDate(Date date, String format) {
		if (date != null) {
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
			return simpleDateFormat.format(date);
		}
		return null;
	}

	/**
	 * 取得当前日期后的日期
	 *
	 * @param date
	 * @param day
	 * @return
	 */
	public static Date getMonthAdd(Date date, int day) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.MONTH, day);
		return calendar.getTime();
	}

	/**
	 * 取得当前日期后的日期
	 *
	 * @param date
	 * @param day
	 * @return
	 */
	public static Date getDayAdd(Date date, int day) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.DATE, day);
		return calendar.getTime();
	}

	/**
	 * 取得当前日期后多少分钟的时间
	 *
	 * @param date
	 * @param minute
	 * @return
	 */
	public static Date getMinuteAdd(Date date, int minute) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.MINUTE, minute);
		return calendar.getTime();
	}
	
	public static Date getHourAdd(Date date, int hour) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.HOUR, hour);
		return calendar.getTime();
	}

	/**
	 * 取得当前日期前多少分钟的时间
	 *
	 * @param date
	 * @param minute
	 * @return
	 */
	public static Date getMinutesubtract(Date date, int minute) {
		return DateTimeUtil.getMinuteAdd(date, -minute);
	}

	public static void main(String[] args) {
		// DateTimeUtil.convertAsDateString("2017-5-5");
		// System.out.println(DateTimeUtil.converAsStringDate(new Date(),
		// "yyyy-MM-dd HH:mm:ss SSS"));
		// System.out.println(
		// DateTimeUtil.converAsStringDate(DateTimeUtil.getMonthAdd(new Date(),
		// -5), "yyyy-MM-dd HH:mm:ss SSS"));

		// System.out.println(DateTimeUtil.converAsStringDate(
		// DateTimeUtil.getMinutesubtract(new Date(), 10),
		// "yyyy-MM-dd HH:mm:ss SSS"));
		System.out.println(
				DateTimeUtil.converAsStringDate(DateTimeUtil.getEndDate(new Date()), "yyyy-MM-dd HH:mm:ss SSS"));
	}
}

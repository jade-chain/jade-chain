package com.itranswarp.crypto.manage.common.utils;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.redisson.executor.CronExpression;

import com.itranswarp.crypto.manage.common.utils.DateCheckout.Checkout;

public class CroExpressionAnalysis {
	private DateCheckout dateCheckout;

	public CroExpressionAnalysis(String cronExpressions) throws ParseException {
		String[] value = cronExpressions.split(";");
		this.dateCheckout = new DateCheckout();
		List<Checkout> item = new ArrayList<>();
		DateCheckout.Checkout checkout = null;
		this.dateCheckout.setItem(item);
		for (String element : value) {
			CronExpression cronExpression = new CronExpression(element);
			checkout = new DateCheckout().new Checkout(cronExpression.getExpressionSummary());
			item.add(checkout);
		}
	}

	public Boolean validate(Date date) {
		Boolean flag = false;
		Calendar calendar = new GregorianCalendar();
		calendar.setTime(date);
		int year = calendar.get(Calendar.YEAR); // 获取年
		int month = calendar.get(Calendar.MONTH) + 1; // 获取月份，0表示1月份
		calendar.get(Calendar.DAY_OF_MONTH);
		calendar.getActualMinimum(Calendar.DAY_OF_MONTH);
		calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
		int hours = calendar.get(Calendar.HOUR_OF_DAY); // 获取当前小时
		int min = calendar.get(Calendar.MINUTE); // 获取当前分钟
		int seconds = calendar.get(Calendar.SECOND); // 获取当前秒
		calendar.get(Calendar.DAY_OF_WEEK);
		for (DateCheckout.Checkout checkout : this.dateCheckout.getItem()) {
			if (!this.validateYear(checkout, year)) {
				continue;
			}
			if (!this.validateMonths(checkout, month)) {
				continue;
			}
			if (!this.validateDays(checkout, date)) {
				continue;
			}
			if (!this.validateHours(checkout, hours)) {
				continue;
			}
			if (!this.validateMin(checkout, min)) {
				continue;
			}
			if (!this.validateSeconds(checkout, seconds)) {
				continue;
			} else {
				flag = true;
				break;
			}
		}
		return flag;
	}

	/**
	 * @param checkout
	 * @param year
	 *            验证年
	 * @return
	 */
	public Boolean validateYear(DateCheckout.Checkout checkout, int year) {
		Boolean flag = false;
		if (checkout.getYearsMap().containsKey("*")) {
			return true;
		} else {
			if (checkout.getYearsMap().containsKey(year + "")) {
				return true;
			}
		}
		return flag;
	}

	/**
	 * @param checkout
	 * @param Months
	 *            验证月
	 * @return
	 */
	public Boolean validateMonths(DateCheckout.Checkout checkout, int Months) {
		Boolean flag = false;
		if (checkout.getMonthsMap().containsKey("*")) {
			return true;
		} else {
			if (checkout.getMonthsMap().containsKey(Months + "")) {
				return true;
			}
		}
		return flag;
	}

	/**
	 * @param checkout
	 *            验证规则
	 * @param date
	 *            时间
	 * @return
	 */
	public Boolean validateDays(DateCheckout.Checkout checkout, Date date) {
		Boolean flag = false;
		Calendar calendar = new GregorianCalendar();
		calendar.setTime(date);
		int year = calendar.get(Calendar.YEAR); // 获取年
		int month = calendar.get(Calendar.MONTH) + 1; // 获取月份，0表示1月份
		int day = calendar.get(Calendar.DAY_OF_MONTH); // 获取当前天数
		calendar.getActualMinimum(Calendar.DAY_OF_MONTH);
		calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
		int week = calendar.get(Calendar.DAY_OF_WEEK);// 获取当前周几
		Map<String, String> daysOfMonthMap = checkout.getDaysOfMonthMap();
		Map<String, String> lastdayOfMonthMap = checkout.getLastdayOfMonthMap();
		Map<String, String> lastdayOfWeekMap = checkout.getLastdayOfWeekMap();
		Map<String, String> nearestWeekdayMap = checkout.getNearestWeekdayMap();
		Map<String, String> nthDayOfWeekMap = checkout.getNthDayOfWeekMap();
		Map<String, String> daysOfWeekMap = checkout.getDaysOfWeekMap();
		if (!daysOfMonthMap.isEmpty() && daysOfMonthMap.containsKey("?")) {
			/**
			 * 验证周规则
			 */
			if (!daysOfWeekMap.containsKey(week + "")) {
				return false;
			}
			if (lastdayOfWeekMap.containsKey("true")) {
				/**
				 * 验证每月最后一个周几
				 */
				for (Entry<String, String> entry : daysOfWeekMap.entrySet()) {
					/**
					 * 拿到集合中的最后星期的日期
					 */
					int lastDay = DateTimeUtil.getDate(date, Integer.parseInt(entry.getKey()));
					if (lastDay == day) {
						flag = true;
						break;
					}
				}
			} else {
				/***
				 * 验证不是每月最后一个周几
				 */
				if (nthDayOfWeekMap.containsKey("0")) {
					/**
					 * 验证每周都开始
					 */
					flag = true;
				} else {
					/**
					 * 验证从每月几个周开始
					 */
					Map<Integer, Integer> dayWeekMap = new HashMap<>();
					nthDayOfWeekMap.entrySet();
					int nthDayOfWeek = 0;
					for (Entry<String, String> entry : nthDayOfWeekMap.entrySet()) {
						/**
						 * 拿到从第几个星期开始
						 */
						nthDayOfWeek = Integer.parseInt(entry.getKey());
					}
					for (Entry<String, String> entry : daysOfWeekMap.entrySet()) {
						/**
						 * 拿到集合中的第一个星期的日期
						 */
						int dateWeek = DateTimeUtil.getDateWeek(date, Integer.parseInt(entry.getKey()))
								+ nthDayOfWeek * 7;
						while (dateWeek <= 31) {
							dayWeekMap.put(dateWeek, dateWeek);
							dateWeek += 7;
						}

					}
				}
			}
		} else {
			/**
			 * 验证月规则
			 */
			if (lastdayOfMonthMap.containsKey("true")) {
				/**
				 * 验证每月最后一天的规则
				 */
				int lastDayOfMonth = DateTimeUtil.getLastDayOfMonth(date);
				if (day == lastDayOfMonth) {
					flag = true;
				}
			} else {
				/**
				 * 验证每月第几天规则
				 */
				if (nearestWeekdayMap.containsKey("true")) {
					/**
					 * 验证每月是工作日时的规则
					 */
					Map<Integer, Integer> daysMap = new HashMap<>();
					/**
					 * 拿到当月应该跑的日期集合
					 */
					String yearMonth = year + "-" + month;
					for (Entry<String, String> entry : daysOfMonthMap.entrySet()) {
						int nearestDay = DateTimeUtil
								.getNearestDay(DateTimeUtil.convertAsDateString(yearMonth + "-" + entry.getKey()));
						daysMap.put(nearestDay, nearestDay);
					}
					if (daysMap.containsKey(day)) {
						flag = true;
					}
				} else {
					/**
					 * 验证每月不是工作日
					 */
					if (daysOfMonthMap.containsKey("*") || daysOfMonthMap.containsKey(day + "")) {
						flag = true;
					}
				}
			}
		}
		return flag;
	}

	/**
	 * 验证小时是否可用
	 *
	 * @param checkout
	 *            规则
	 * @param hours
	 *            时间
	 * @return
	 */
	public Boolean validateHours(DateCheckout.Checkout checkout, int hours) {
		Boolean flag = false;
		if (checkout.getHoursMap().containsKey("*")) {
			return true;
		} else {
			if (checkout.getHoursMap().containsKey(hours + "")) {
				return true;
			}
		}
		return flag;
	}

	public Boolean validateMin(DateCheckout.Checkout checkout, int min) {
		Boolean flag = false;
		if (checkout.getMinutesMap().containsKey("*")) {
			return true;
		} else {
			if (checkout.getMinutesMap().containsKey(min + "")) {
				return true;
			}
		}
		return flag;
	}

	public Boolean validateSeconds(DateCheckout.Checkout checkout, int seconds) {
		Boolean flag = false;
		if (checkout.getSecondsMap().containsKey("*")) {
			return true;
		} else {
			if (checkout.getSecondsMap().containsKey(seconds + "")) {
				return true;
			}
		}
		return flag;
	}
}

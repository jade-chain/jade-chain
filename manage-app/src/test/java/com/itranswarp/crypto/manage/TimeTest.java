package com.itranswarp.crypto.manage;

import java.util.Date;

import org.junit.Test;
import org.redisson.executor.CronExpression;

import com.itranswarp.crypto.manage.common.utils.CroExpressionAnalysis;
import com.itranswarp.crypto.manage.common.utils.DateTimeUtil;

public class TimeTest {
	@Test
	public void test() {
		try {
			String cron = "* * * * 2 ? 2018";
			System.out.println(cron);
			CronExpression cronExpression = new CronExpression(cron);
			System.out.println(cronExpression.getExpressionSummary());
			Date parseDate = DateTimeUtil.convertAsDateString("2018-03-28 15:30:01", "yyyy-MM-dd HH:mm:ss");
//			long currentTimeMillis = System.currentTimeMillis();
			CroExpressionAnalysis croExpressionAnalysis = new CroExpressionAnalysis(cron);
			// croExpressionAnalysis.validate(parseDate);
			// long currentTimeMillis2 = System.currentTimeMillis();
			// System.out.println(currentTimeMillis2 - currentTimeMillis);
			System.out.println(croExpressionAnalysis.validate(parseDate));
			// long currentTimeMillis3 = System.currentTimeMillis();
			// System.out.println(currentTimeMillis3 - currentTimeMillis2);
			// System.out.println(currentTimeMillis3 - currentTimeMillis);
			// CronExpression cronExpression = new CronExpression(
			// "0 15 10 ? 1 *L *");
			// System.out.println(cronExpression.getExpressionSummary());
			// CronExpression cronExpression2 = new CronExpression(
			// "0 15 10 8-10W * ?");
			// System.out.println(cronExpression2.getExpressionSummary());

		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}

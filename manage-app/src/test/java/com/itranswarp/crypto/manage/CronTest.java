package com.itranswarp.crypto.manage;

import java.util.Date;

import org.junit.Test;
import org.redisson.executor.CronExpression;

import com.itranswarp.crypto.manage.common.utils.DateTimeUtil;

public class CronTest {
	@Test
	public void cronTest() {
		try {
			String cron = "0 0 0 1 2 ? 2017";
			System.out.println(cron);
			CronExpression cronExpression = new CronExpression(cron);
			Date timeAfter = cronExpression.getTimeAfter(new Date());
			// Date timeAfter1 = cronExpression.getTimeAfter(timeAfter);
			// System.out.println(DateTimeUtil.converAsStringDate(nextInvalidTimeAfter,
			// "yyyy-MM-dd HH:mm:ss"));
			System.out.println(DateTimeUtil.converAsStringDate(timeAfter, "yyyy-MM-dd HH:mm:ss"));
			// System.out.println(DateTimeUtil.converAsStringDate(timeAfter1,
			// "yyyy-MM-dd HH:mm:ss"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

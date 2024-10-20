package com.cissol.core.util;

import java.util.Calendar;
import java.util.Date;

public class DateUtil {
	public static Date addDays(Date d, int days) throws Exception {
		Calendar c = Calendar.getInstance();
		c.setTime(d);
		c.add(Calendar.DATE, days);
		return c.getTime();
	}
}
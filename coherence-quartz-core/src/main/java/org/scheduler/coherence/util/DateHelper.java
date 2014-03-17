package org.scheduler.coherence.util;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

public class DateHelper {

	public static Date parseDateYYYYmmDD(String yyyyMMdd) throws ParseException  {
		try {
			Calendar cal = Calendar.getInstance();
			
			String year = yyyyMMdd.substring(0,4);
			cal.set(Calendar.YEAR, Integer.parseInt(year));
			String month = yyyyMMdd.substring(4, 6);
			cal.set(Calendar.MONTH, Integer.parseInt(month)-1);
			String day = yyyyMMdd.substring(6);
			cal.set(Calendar.DATE, Integer.parseInt(day));
			
			
			return cal.getTime();
		} catch (Exception e) {
			throw new ParseException("ParseException with date "+yyyyMMdd+" : "+e.getMessage(), 0);
		}
		
	}
	
	public static Date parseDateYYYYmmDDNoon(String yyyyMMdd) throws ParseException  {
		try {
			Calendar cal = Calendar.getInstance();
			
			String year = yyyyMMdd.substring(0,4);
			cal.set(Calendar.YEAR, Integer.parseInt(year));
			String month = yyyyMMdd.substring(4, 6);
			cal.set(Calendar.MONTH, Integer.parseInt(month)-1);
			String day = yyyyMMdd.substring(6);
			cal.set(Calendar.DATE, Integer.parseInt(day));
			
			cal.set(Calendar.HOUR_OF_DAY, 12);
			cal.set(Calendar.MINUTE, 0);
			cal.set(Calendar.SECOND, 0);
			
			
			return cal.getTime();
		} catch (Exception e) {
			throw new ParseException("ParseException with date "+yyyyMMdd+" : "+e.getMessage(), 0);
		}
		
	}
	
	public static Integer formatYYYYmmDD(Calendar cal) {
		return cal.get(Calendar.YEAR) * 10000 + (cal.get(Calendar.MONTH) + 1) * 100 + cal.get(Calendar.DAY_OF_MONTH);
	}
	
	
}

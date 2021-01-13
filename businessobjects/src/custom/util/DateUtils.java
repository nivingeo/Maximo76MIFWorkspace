package custom.util;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class DateUtils {
	
	public static Date increaseDate(Date date, int days){
		Calendar cal = new  GregorianCalendar();
		cal.setTime(date);
		cal.add(cal.DAY_OF_MONTH, days);
		return new Date(cal.getTimeInMillis());
		
	}

	public static String getFinancialYearFor(Date d)
	{
		String a = "";
		return a;
	}
}

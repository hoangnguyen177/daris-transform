package transform.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateTimeUtil {

	public static final String DATE_TIME_FORMAT = "dd-MMM-yyyy HH:mm:ss";
	public static final String DATE_FORMAT = "dd-MMM-yyyy";

	public static Date parseDate(String s) throws ParseException {
		return new SimpleDateFormat(DATE_FORMAT).parse(s);
	}

	public static Date parseDateTime(String s) throws ParseException {
		return new SimpleDateFormat(DATE_TIME_FORMAT).parse(s);
	}

	public static String formatDate(Date d) {
		return new SimpleDateFormat(DATE_FORMAT).format(d);
	}
	
	public static String formatDateTime(Date d) {
		return new SimpleDateFormat(DATE_TIME_FORMAT).format(d);
	}
	
}

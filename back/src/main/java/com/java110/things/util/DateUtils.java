package com.java110.things.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateUtils {

	private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss ", Locale.ENGLISH);
	
	public static String getGBFormatDate(Date date){
		return dateFormat.format(date).replace(" ", "T");
	}
}

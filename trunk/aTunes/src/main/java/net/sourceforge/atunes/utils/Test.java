package net.sourceforge.atunes.utils;

import java.util.Locale;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;


public class Test {

	public static void main(String[] args) {
//		DateTimeFormatter fmt = DateTimeFormat.forPattern("EEE, dd MMM yyyy HH:mm:ss ZZ");
//        System.out.println(fmt.withLocale(Locale.US).parseDateTime(s));


		String s = "Tue May 23 00:00:00 CEST 2000";
		DateTimeFormatter fmt = DateTimeFormat.forPattern("EEE MMM dd HH:mm:ss 'CEST' yyyy");
		System.out.println(fmt.withLocale(Locale.US).parseDateTime(s));
        
	}
}

package org.easydarwin.video.beautify.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import android.util.Log;

public class DateUtils {
	/**
	 * 把日期时间转为字符串
	 * 
	 * @param date
	 * @return 返回格式为 'yyyy-MM-dd HH:mm:ss'
	 */
	public final static String formatDate(Date date) {
		if (date == null) {
			return null;
		}
		SimpleDateFormat form = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",Locale.getDefault());
		return form.format(date);
	}

	/**
	 * 把日期时间转为字符串
	 * 
	 * @param date
	 *            格式 yyyy-MM-dd HH:mm:ss
	 * @return 返回格式为 'MM/dd'
	 */
	public final static String formatDateToDay(String date) {
		if (date == null || date.length() < 10) {
			return "";
		}
		String res = null;
		SimpleDateFormat form = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",Locale.getDefault());
		try {
			Date d = form.parse(date);
			res = formatDateToDay(d);
		} catch (ParseException e) {
			Log.e("DateUtil", "parse error", e);
		}
		return res;
	}

	/**
	 * 把日期时间转为字符串
	 * 
	 * @param date
	 * @return 返回格式为 'MM/dd'
	 */
	@SuppressWarnings("deprecation")
	public final static String formatDateToDay(Date date) {
		if (date == null) {
			return "";
		}
		String res = date.getMonth() + "/" + date.getDay();
		return res;
	}

	public final static Date parseDate(String date) {
		if (date == null || date.length() < 10) {
			return null;
		}
		Date res = null;
		SimpleDateFormat form = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",Locale.getDefault());
		try {
			res = form.parse(date);
		} catch (ParseException e) {
			Log.e("DateUtil", "parse error", e);
		}
		return res;
	}

	public final static Date parseDate(String date, DateFormat format) {

		if (date == null || date.length() < 8) {
			return null;
		}

		Date res = null;

		try {
			res = format.parse(date);
		} catch (ParseException e) {
			Log.e("DateUtil", "parse error", e);
		}
		return res;
	}

	public final static int compareDate(Date beg, Date end) {
		int res = 0;
		if (beg != null && end != null) {
			res = (int) (end.getTime() - beg.getTime()) / 1000;
		}
		return res;
	}

	public static String millisToDate(long millis) {
		Date date = new Date(millis);
		GregorianCalendar gc = new GregorianCalendar();
		gc.setTime(date);
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",Locale.getDefault());
		String sb = format.format(gc.getTime());
		return sb;
	}

	/**
	 * 用于将毫秒数转换为00:00 或 00:00:00格式,比如在配乐页面时间显示
	 * 
	 * @param time
	 * @return
	 */
	public static String formateTime(int time, String type) {
		time /= 1000;
		int minute = time / 60;
		int hour = minute / 60;
		int second = time % 60;
		minute %= 60;
		String result = null;
		if (type == null) {
			throw new NullPointerException("type == null");
		}
		if (type.equals("00:00")) {
			result = String.format(Locale.getDefault(),"%02d:%02d", minute, second);
		}
		if (type.equals("00:00:00")) {
			result = String.format(Locale.getDefault(),"%02d:%02d", hour, minute, second);
		}
		return result;
	}

	public static void main(String[] args) {
		System.out.println(formateTime(8667, "00:00"));
	}
}

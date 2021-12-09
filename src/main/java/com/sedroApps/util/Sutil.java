 /*************************************************************************
 * Ledbetter CONFIDENTIAL
 * __________________
 * 
 * [2018] - [2020] Aaron Ledbetter
 * All Rights Reserved.
 * 
 * NOTICE: All information contained herein is, and remains
 * the property of Aaron Ledbetter. The intellectual and technical 
 * concepts contained herein are proprietary to Aaron Ledbetter and 
 * may be covered by U.S. and Foreign Patents, patents in process, 
 * and are protected by trade secret or copyright law. 
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Aaron Ledbetter.
 */

package com.sedroApps.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.nio.ByteBuffer;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.TimeZone;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.Map.Entry;

import org.eclipse.jetty.util.StringUtil;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;



/*
 * Service util class to contain generic Service functionality for use in all service objects.
 * get and del are both implemented here 
 */
public class Sutil {
	static public String no_space [] = {",", ":", ";", ".", "!", "?"};
	   public static final String pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";    
	    public static final String display_pattern_sec = "EEE MMM d h:mm:ss a z yyyy";
	    public static final String display_pattern_msec = "EEE MMM d h:mm:ss.SSS a z yyyy";
	    public static final String display_pattern = "EEE MMM d h:mm a z yyyy";
	    public static final String email_pattern = "yyyy-MM-dd HH:mm:ss";
	    public static final String display_pattern_clock = "h:mm a";
	    public static final String display_pattern_24clock = "HH:mm|EEE";
	    public static final String date_pattern = "M/d/yy";
	    public static final String date_pattern1 = "M/d/yyyy";
	    public static final String date_pattern2 = "EEEEEEEEEEEE, MMMMMMMMMMMMMMM d";
	    public static final String date_pattern3 = "MMMMMMMMMMMMMMM d, yyyy";
	    public static final String dob_pattern = "yyyy-MM";
	    public static final String dob_pattern2 = "yyyy-MM-dd";
	    public static final String dob_pattern4 = "MM/dd/yyyy";
	    public static final String dob_pattern10 = "yyyyMMdd";
	    public static final String dob_pattern5 = "yyyy";
	    public static final String dob_pattern8 = "w"; // week in year
	    public static final String dob_pattern9 = "D"; // day in year
	    public static final String date_pattern_Month = "MMMMMMMMMMMMMMM";
	    public static final String date_pattern_Month3 = "MMM";
	    
	    public static final String ont_time_pattern = "M-d-yyyy-HH:mm:ss.SSS";
	    public static final String ont_date_pattern = "M-d-yyyy";
	    public static final String ont_week_pattern = "'w'w-yyyy";
	    public static final String ont_Month_pattern = "'m'M-yyyy";
	    public static final String ont_year_pattern = "'y'yyyy";
    
    // Get a new GUID (not inteded for printing)
	public final static BigInteger getGUID() {
		UUID uu = UUID.randomUUID();
		ByteBuffer bb = ByteBuffer.wrap(new byte[16]);
		bb.putLong(uu.getMostSignificantBits());
		bb.putLong(uu.getLeastSignificantBits());
		return new BigInteger(1,bb.array());
	}
	public final static String getGUIDString() {
		UUID uu = UUID.randomUUID();
		return uu.toString();
	}
	public final static String getGUIDNoString() {
		UUID uu = UUID.randomUUID();
		String u = uu.toString();
		return StringUtil.replace(u, "-", "");
	}
	

	public static String getSpace(int count) {
		String sp = "";
		for (int i = 0;i<count; i ++) sp += "   ";
		return sp;
	}
	public static String getSpace(int count, char c) {
		String sp = "";
		for (int i = 0;i<count; i ++) sp += c;
		return sp;
	}
	
	// get the current time as a string
	public final static String getCurrentTimeStamp() {
		return formatTimeStamp(Sutil.getUTCTime());
	}
	public final static String getCurrentDate() {
		return formatDateString(Sutil.getUTCTime());
	}
	public final static int getBankSchedHour(String time) {
		int i = time.indexOf(":");
		return Sutil.toInt(time.substring(0, i));		
	}
	public final static int getBankSchedMin(String time) {
		int i = time.indexOf(":");
		int e = time.indexOf("|");
		return Sutil.toInt(time.substring((i+1), e));		
	}
	public static String getBankSchedType(String time) {
		int e = time.indexOf("|");
		return time.substring((e+1), time.length());		
	}
	public static int getBankSchedAltDay(String time) {
		int e = time.indexOf("|");
		String d = time.substring((e+1), time.length());
		if (d.equalsIgnoreCase("sun")) return Calendar.SUNDAY;
		else if (d.equalsIgnoreCase("mon")) return Calendar.MONDAY;
		else if (d.equalsIgnoreCase("tue")) return Calendar.TUESDAY;
		else if (d.equalsIgnoreCase("wed")) return Calendar.WEDNESDAY;
		else if (d.equalsIgnoreCase("thu")) return Calendar.THURSDAY;
		else if (d.equalsIgnoreCase("fri")) return Calendar.FRIDAY;
		else if (d.equalsIgnoreCase("sat")) return Calendar.SATURDAY;
		return 0;			
	}
	public static int getCalendarDay(String day) {
		if (day == null) return -1;
		if (day.equalsIgnoreCase("sunday")) return Calendar.SUNDAY;
		else if (day.equalsIgnoreCase("monday")) return Calendar.MONDAY;
		else if (day.equalsIgnoreCase("tuesday")) return Calendar.TUESDAY;
		else if (day.equalsIgnoreCase("wednesday")) return Calendar.WEDNESDAY;
		else if (day.equalsIgnoreCase("thursday")) return Calendar.THURSDAY;
		else if (day.equalsIgnoreCase("friday")) return Calendar.FRIDAY;
		else if (day.equalsIgnoreCase("saturday")) return Calendar.SATURDAY;
		return -1;			
	}
	public static int getCalendarDayDiff(String day, String end_day) {
		int s = getCalendarDay(day);
		int e = getCalendarDay(end_day);
		if (s < e) return e - s;
		return e - s;
	}
	public static int getPstHour(Calendar time) {
		String pstTime = Sutil.formatTimeString24Clock(time, "PST");
		return getBankSchedHour(pstTime);		
	}
	public static int getPstHourOffset(Calendar time) {
/*		int gmtH = ServiceUtil.getGMTHour(time);
		int pstH = ServiceUtil.getPstHour(time);
	    System.out.println("    getPstHourOffset["+pstH+"]: "+ServiceUtil.formatTimeString24Clock(start, "PST"));

		int off = (gmtH-pstH);
		if (off < 0) return -((gmtH+24)-pstH);
		return off;*/
		return Sutil.getPstHour(time);
	}
	
	public static int getGMTHour(Calendar time) {
		String pstTime = Sutil.formatTimeString24Clock(time);
		return getBankSchedHour(pstTime);		
	}
	public static int getPstHour() {
		return getPstHour(Sutil.getUTCTime());	
	}
	public static int getPstMinute(Calendar time) {
		String pstTime = Sutil.formatTimeString24Clock(time, "PST");
		return getBankSchedMin(pstTime);		
	}
	public static int getPstMinute() {
		return getPstMinute(Sutil.getUTCTime());	
	}
	public static int getPstDayOfWeek(Calendar time) {
		String pstTime = Sutil.formatTimeString24Clock(time, "PST");
		return getBankSchedAltDay(pstTime);		
	}
	public static int getPstDayOfWeek() {
		return getPstDayOfWeek(Sutil.getUTCTime());	
	}
	public static int getPstDayOfMonth(Calendar time) {		
		String pstTime = Sutil.formatDob2String(time, "PST");
		pstTime = pstTime.substring(8, pstTime.length()); //"yyyy-MM-dd"
		return toInt(pstTime);
	}
	public static int getPstDayOfMonth() {
		return getPstDayOfMonth(Sutil.getUTCTime());	
	}
	public static int getPstDayOfYear(Calendar time) {		
    	SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dob_pattern9);
    	simpleDateFormat.setTimeZone(TimeZone.getTimeZone(getTimeZoneStr("PST")));
		return toInt(simpleDateFormat.format(time.getTime()));
	}
	public static int getPstDayOfYear() {
		return getPstDayOfYear(Sutil.getUTCTime());	
	}
	public static int getPstWeekOfYear(Calendar time) {		
    	SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dob_pattern8);
    	simpleDateFormat.setTimeZone(TimeZone.getTimeZone(getTimeZoneStr("PST")));
		return toInt(simpleDateFormat.format(time.getTime()));
	}
	public static int getPstWeekOfYear() {
		return getPstWeekOfYear(Sutil.getUTCTime());	
	}
	public static int getPstMonth(Calendar time) {		
		String pstTime = Sutil.formatDob2String(time, "PST");
		pstTime = pstTime.substring(5, 7); //"yyyy-MM-dd"
		return toInt(pstTime);
	}
	public static int getPstMonth() {
		return getPstMonth(Sutil.getUTCTime());	
	}
	public static int getPstYear(Calendar time) {
		String pstTime = Sutil.formatDob2String(time, "PST");
		pstTime = pstTime.substring(0, 4); //"yyyy-MM-dd"
		return toInt(pstTime);
	}
	public static int getPstYear() {
		return getPstYear(Sutil.getUTCTime());	
	}
	// format the time we want
	public static String formatTimeStamp(Calendar time) {
		if (time == null) {
			return null;
		}
    	SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
    	simpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
    	return simpleDateFormat.format(time.getTime());
	}
	public static String formatTimeStamp(Calendar time, String time_zone) {
		if (time == null) {
			return null;
		}
    	SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
    	simpleDateFormat.setTimeZone(TimeZone.getTimeZone(getTimeZoneStr(time_zone)));
    	return simpleDateFormat.format(time.getTime());
	}
	public static String formatTimeStampNoGMT(Calendar time) {
		if (time == null) {
			return null;
		}
    	SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
    	return simpleDateFormat.format(time.getTime());
	}	
	// format the time for display in templates and such
	public static String formatDateString(Calendar time) {
		return formatDateString(time, "GMT");
	}
	public static String formatDateString(Calendar time, String time_zone) {
		if (time == null) {
			return null;
		}
    	SimpleDateFormat simpleDateFormat = new SimpleDateFormat(date_pattern);
    	simpleDateFormat.setTimeZone(TimeZone.getTimeZone(getTimeZoneStr(time_zone)));
    	return simpleDateFormat.format(time.getTime());
	}
	public static String formatDateFullYear(Calendar time) {
		return formatDateFullYear(time, "GMT");
	}
	public static String formatDateFullYear(Calendar time, String time_zone) {
		if (time == null) {
			return null;
		}
    	SimpleDateFormat simpleDateFormat = new SimpleDateFormat(date_pattern1);
    	simpleDateFormat.setTimeZone(TimeZone.getTimeZone(getTimeZoneStr(time_zone)));
    	return simpleDateFormat.format(time.getTime());
	}
	public static String formatDateDayString(Calendar time) {
		return formatDateDayString(time, "GMT");
	}
	public static String formatDateDayString(Calendar time, String time_zone) {
		if (time == null) {
			return null;
		}
    	SimpleDateFormat simpleDateFormat = new SimpleDateFormat(date_pattern2);
    	simpleDateFormat.setTimeZone(TimeZone.getTimeZone(getTimeZoneStr(time_zone)));
    	return simpleDateFormat.format(time.getTime());
	}
	public static String formatDateLongString(Calendar time) {
		return formatDateLongString(time, "GMT");
	}
	public static String formatDateLongString(Calendar time, String time_zone) {
		if (time == null) {
			return null;
		}
    	SimpleDateFormat simpleDateFormat = new SimpleDateFormat(date_pattern3);
    	simpleDateFormat.setTimeZone(TimeZone.getTimeZone(getTimeZoneStr(time_zone)));
    	return simpleDateFormat.format(time.getTime());
	}	
	public static String formatDatefullYearString(Calendar time) {
		return formatDatefullYearString(time, "GMT");
	}
	public static String formatDatefullYearString(Calendar time, String time_zone) {
		if (time == null) {
			return null;
		}
    	SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dob_pattern4);
    	simpleDateFormat.setTimeZone(TimeZone.getTimeZone(getTimeZoneStr(time_zone)));
    	return simpleDateFormat.format(time.getTime());
	}	
	public static String formatYearString(Calendar time) {
		return formatYearString(time, "GMT");
	}
	public static String formatYearString(Calendar time, String time_zone) {
		if (time == null) {
			return null;
		}
    	SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dob_pattern5);
    	simpleDateFormat.setTimeZone(TimeZone.getTimeZone(getTimeZoneStr(time_zone)));
    	return simpleDateFormat.format(time.getTime());
	}
	
	public static String formatMonth(Calendar time) {
		return formatMonth(time, "GMT");
	}
	public static String formatMonth(Calendar time, String time_zone) {
		if (time == null) {
			return null;
		}
    	SimpleDateFormat simpleDateFormat = new SimpleDateFormat(date_pattern_Month);
    	simpleDateFormat.setTimeZone(TimeZone.getTimeZone(getTimeZoneStr(time_zone)));
    	return simpleDateFormat.format(time.getTime());
	}
	public static String formatMonth3(Calendar time) {
		return formatMonth3(time, "GMT");
	}
	public static String formatMonth3(Calendar time, String time_zone) {
		if (time == null) {
			return null;
		}
    	SimpleDateFormat simpleDateFormat = new SimpleDateFormat(date_pattern_Month3);
    	simpleDateFormat.setTimeZone(TimeZone.getTimeZone(getTimeZoneStr(time_zone)));
    	return simpleDateFormat.format(time.getTime());
	}

	
	// format the time for display in templates and such
	public static String formatDobString(Calendar time) {
		return formatDobString(time, "GMT");
	}
	public static String formatDobString(Calendar time, String time_zone) {
		if (time == null) {
			return null;
		}
    	SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dob_pattern);
    	simpleDateFormat.setTimeZone(TimeZone.getTimeZone(getTimeZoneStr(time_zone)));
    	return simpleDateFormat.format(time.getTime());
	}

	public static String formatDob2String(Calendar time) {
		return formatDob2String(time, "GMT");
	}
	public static String formatDob2String(Calendar time, String time_zone) {
		if (time == null) {
			return null;
		}
    	SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dob_pattern2);
    	simpleDateFormat.setTimeZone(TimeZone.getTimeZone(getTimeZoneStr(time_zone)));
    	return simpleDateFormat.format(time.getTime());
	}

    public static Calendar makeDate(Calendar time) {
		time.set(Calendar.HOUR_OF_DAY, 0);
		time.set(Calendar.MINUTE, 0);
		time.set(Calendar.SECOND, 0);
		time.set(Calendar.MILLISECOND, 0);
		return time;
    }
    // ont range
	public static String fmtRangeName(Calendar start, Calendar end, String label) {
		if (label != null) return Sutil.fmtOntTime(start) + "_" + Sutil.fmtOntTime(end) + "_" + label;
		return Sutil.fmtOntTime(start) + "_" + Sutil.fmtOntTime(end);
	}
	public static Calendar loadRangeStart(String range) {
		if (range == null) return null;
		int idx = range.indexOf("_");
		if (idx < 0) return null;
		return Sutil.loadOntTime(range.substring(0, idx));
	}
	public static Calendar loadRangeEnd(String range) {
		if (range == null) return null;
		int idx = range.indexOf("_");
		if (idx < 0) return null;
		String r = range.substring(idx+1, range.length());
		idx = r.indexOf("_");
		if (idx > 0) r = range.substring(0, idx);
		return Sutil.loadOntTime(r);
	}	
	public static String loadRangeLabel(String range) {
		if (range == null) return null;
		int idx = range.indexOf("_");
		if (idx < 0) return null;
		String r = range.substring(idx+1, range.length());
		idx = r.indexOf("_");
		if (idx <= 0) return null;
		return r.substring(idx+1, r.length());
	}

	
    // ont time
	public static String fmtOntTime(Calendar time) {
		if (time == null) return null;
		int h = time.get(Calendar.HOUR_OF_DAY);
		int m = time.get(Calendar.MINUTE);
		int s = time.get(Calendar.SECOND);
		int ms = time.get(Calendar.MILLISECOND);
		if (h == 0 && m == 0 && s == 0 && ms == 0) {
	    	SimpleDateFormat simpleDateFormat = new SimpleDateFormat(ont_date_pattern);
	    	simpleDateFormat.setTimeZone(TimeZone.getTimeZone(getTimeZoneStr("GMT")));
	    	return simpleDateFormat.format(time.getTime());
		}
    	SimpleDateFormat simpleDateFormat = new SimpleDateFormat(ont_time_pattern);
    	simpleDateFormat.setTimeZone(TimeZone.getTimeZone(getTimeZoneStr("GMT")));
    	return simpleDateFormat.format(time.getTime());
	}
	public static String fmtOntDate(Calendar time) {
		if (time == null) return null;
    	SimpleDateFormat simpleDateFormat = new SimpleDateFormat(ont_date_pattern);
    	simpleDateFormat.setTimeZone(TimeZone.getTimeZone(getTimeZoneStr("GMT")));
    	return simpleDateFormat.format(time.getTime());
	}
	public static String fmtOntWeek(Calendar time) {
		if (time == null) return null;
    	SimpleDateFormat simpleDateFormat = new SimpleDateFormat(ont_week_pattern);
    	simpleDateFormat.setTimeZone(TimeZone.getTimeZone(getTimeZoneStr("GMT")));
    	return simpleDateFormat.format(time.getTime());
	}
	public static Calendar loadOntWeek(String date_string) {
    	try {
	    	SimpleDateFormat dateFormat = new SimpleDateFormat(ont_week_pattern);
	    	dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
	    	dateFormat.parse(date_string.trim());
	    	return dateFormat.getCalendar();
    	} catch (Throwable t) {
    		return null;
    	}
	}
	public static String fmtOntMonth(Calendar time) {
		if (time == null) return null;
    	SimpleDateFormat simpleDateFormat = new SimpleDateFormat(ont_Month_pattern);
    	simpleDateFormat.setTimeZone(TimeZone.getTimeZone(getTimeZoneStr("GMT")));
    	return simpleDateFormat.format(time.getTime());
	}
	public static Calendar loadOntMonth(String date_string) {
    	try {
	    	SimpleDateFormat dateFormat = new SimpleDateFormat(ont_Month_pattern);
	    	dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
	    	dateFormat.parse(date_string.trim());
	    	return dateFormat.getCalendar();
    	} catch (Throwable t) {
    		return null;
    	}
	}
	public static String fmtOntYear(Calendar time) {
		if (time == null) return null;
    	SimpleDateFormat simpleDateFormat = new SimpleDateFormat(ont_year_pattern);
    	simpleDateFormat.setTimeZone(TimeZone.getTimeZone(getTimeZoneStr("GMT")));
    	return simpleDateFormat.format(time.getTime());
	}
	public static Calendar loadOntYear(String date_string) {
    	try {
	    	SimpleDateFormat dateFormat = new SimpleDateFormat(ont_year_pattern);
	    	dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
	    	dateFormat.parse(date_string.trim());
	    	return dateFormat.getCalendar();
    	} catch (Throwable t) {
    		return null;
    	}
	}
	
    public static Calendar loadOntTime(String date_string) {
    	try {
    		if (date_string.contains("_")) return null; // is range
    		if (date_string.contains(":")) {
    	    	SimpleDateFormat dateFormat = new SimpleDateFormat(ont_time_pattern);
    	    	dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
    	    	dateFormat.parse(date_string.trim());
    	    	return dateFormat.getCalendar();   		
    		} else if (date_string.startsWith("y")) {
    			return null; // not yet
    		} else if (date_string.startsWith("m")) {
    			return null; // not yet
    		} else if (date_string.startsWith("w")) {
    			return null; // not yet
    		} else {
    	    	SimpleDateFormat dateFormat = new SimpleDateFormat(ont_date_pattern);
    	    	dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
    	    	dateFormat.parse(date_string.trim());
    	    	return dateFormat.getCalendar();
    		}
    	} catch (Throwable t) {
    		return null;
    	}
    }
    
 

	// format the time for display in templates and such
	public static String formatTimeString(Calendar time) {
    	return formatTimeString(time, "GMT");
	}
	// format the time for display in templates and such
	public static String formatTimeString(Calendar time, String time_zone) {
		if (time == null) {
			return null;
		}
    	SimpleDateFormat simpleDateFormat = new SimpleDateFormat(display_pattern);
    	simpleDateFormat.setTimeZone(TimeZone.getTimeZone(getTimeZoneStr(time_zone)));
    	return simpleDateFormat.format(time.getTime());
	}
	// display with seconds
	public static String formatTimeStringSec(Calendar time) {
    	return formatTimeStringSec(time, "GMT");
	}
	public static String formatTimeStringSec(Calendar time, String time_zone) {
		if (time == null) {
			return null;
		}
    	SimpleDateFormat simpleDateFormat = new SimpleDateFormat(display_pattern_sec);
    	simpleDateFormat.setTimeZone(TimeZone.getTimeZone(getTimeZoneStr(time_zone)));
    	return simpleDateFormat.format(time.getTime());
	}

	// display with milliseconds
	public static String formatTimeStringMSec(Calendar time) {
    	return formatTimeStringMSec(time, "GMT");
	}
	public static String formatTimeStringMSec(Calendar time, String time_zone) {
		if (time == null) {
			return null;
		}
    	SimpleDateFormat simpleDateFormat = new SimpleDateFormat(display_pattern_msec);
    	simpleDateFormat.setTimeZone(TimeZone.getTimeZone(getTimeZoneStr(time_zone)));
    	return simpleDateFormat.format(time.getTime());
	}
    public static Calendar loadTimeStamp(String timeStampString) {
    	if (timeStampString == null || timeStampString.equalsIgnoreCase("(null)") || timeStampString.equalsIgnoreCase("[?]")) {
    		return null;
    	}
    	// hack, we may have the '+' converted to a ' ' by the html gunk
    	timeStampString.trim();
    	timeStampString = timeStampString.replaceAll(" ", "+");
    	try {
	    	SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);

	    	dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));	    	
	    	dateFormat.parse(timeStampString);
	    	Calendar cal = dateFormat.getCalendar();
	    	//cal = ServiceUtil.convertToGmt(cal);
	    	return cal;
    	} catch (Throwable t) {
    		//System.out.println("loadTimeStamp: ERROR: " + timeStampString);
    		return null;
    	}
    }
    public static Calendar loadEmailTimeStamp(String timeStampString) {
    	if (timeStampString == null) return null;
    	timeStampString.trim();
    	try {
	    	SimpleDateFormat dateFormat = new SimpleDateFormat(email_pattern);
	    	dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));	    	
	    	dateFormat.parse(timeStampString);
	    	Calendar cal = dateFormat.getCalendar();
	    	return cal;
    	} catch (Throwable t) {
    		return null;
    	}
    }    
    
    
	public static String formatTimeStringClock(Calendar time) {
    	return formatTimeStringClock(time, "GMT");
	}
	public static String formatTimeStringClock(Calendar time, String time_zone) {
		if (time == null) return null;
		
    	SimpleDateFormat simpleDateFormat = new SimpleDateFormat(display_pattern_clock);
    	simpleDateFormat.setTimeZone(TimeZone.getTimeZone(getTimeZoneStr(time_zone)));
    	return simpleDateFormat.format(time.getTime());
	}  
	public static String formatTimeString24Clock(Calendar time) {
    	return formatTimeString24Clock(time, "GMT");
	}
	public static String formatTimeString24Clock(Calendar time, String time_zone) {
		if (time == null) return null;
		
    	SimpleDateFormat simpleDateFormat = new SimpleDateFormat(display_pattern_24clock);
    	simpleDateFormat.setTimeZone(TimeZone.getTimeZone(getTimeZoneStr(time_zone)));
    	return simpleDateFormat.format(time.getTime());
	}  

     public static Calendar setToTimeZone(Calendar time, String tz) {
		TimeZone z = TimeZone.getTimeZone(getTimeZoneStr(tz));
		time.setTimeZone(z); // set today to corect PST time		
		long currentTime = time.getTimeInMillis();
		long convertedTime = currentTime + z.getOffset(currentTime);		
		time.setTimeInMillis(convertedTime);
		return time;
    }
    
    public static Calendar setCalendarNumberDay(Calendar date, int instance, int day) {
    	if (date == null) return null;
    	date.set(Calendar.DAY_OF_MONTH, 1);
    	int nday = date.get(Calendar.DAY_OF_WEEK); 
    	
    	// days to the first instance
    	int cdays =  nday - day;
    	if (nday > day) {
    		cdays = 7 - (nday - day);
    	} else if (nday == day) {
    		cdays = 0;   		
    	} else if (nday < day) {
    		cdays = day - nday;
    	}
    	cdays++; // DAY_OF_WEEK has different base
    					
    	cdays += (instance-1) * 7; // add the weeks
    	date.set(Calendar.DAY_OF_MONTH, cdays);
    	return date;
    } 
    
    public final static int randInt(int min, int max) {

        // NOTE: This will (intentionally) not run as written so that folks
        // copy-pasting have to think about how to initialize their
        // Random instance.  Initialization of the Random instance is outside
        // the main scope of the question, but some decent options are to have
        // a field that is initialized once and then re-used as needed or to
        // use ThreadLocalRandom (if using at least Java 1.7).
      //  Random rand;
        Random rand = new Random();

        // nextInt is normally exclusive of the top value,
        // so add 1 to make it inclusive
        int randomNum = rand.nextInt((max - min) + 1) + min;

        return randomNum;
    }

	private static final int TT_MAX_ID = 200;
	private static long trac_time[] = null;
	private static long trac_time_cnt[] = null;
	private static long trac_time_total[] = null;
	
	public static void tracTimeStart(int id) {
		if (trac_time == null) tracTimeReset();
		trac_time[id] = System.currentTimeMillis();
	}
	public static void tracTimeSwtich(int last_id, int id) {
		trac_time[id] = tracTimeEnd(last_id);
	}
	public static long tracTimeEnd(int id) {
		long tt = System.currentTimeMillis();
		trac_time_cnt[id]++;
		trac_time_total[id] += (tt - trac_time[id]);
		trac_time[id] = 0;
		return tt;
	}	
	public static void tracTimeShow(int id) {
		if (trac_time_cnt == null || trac_time_cnt[id] <= 0) return;
		long avg = trac_time_total[id] / trac_time_cnt[id];
		long sec = avg / 1000;
		long ms = avg - (sec * 1000);
		System.out.println(" TRAC["+id+"]  avg: " + sec + " sec "+ms+" ms   count: " + trac_time_cnt[id]);
	}		
	public static void tracTimeShow() {
		for (int i=0;i<TT_MAX_ID;i++) tracTimeShow(i);
	}		
	public static void tracTimeReset() {
		for (int i=0;i<TT_MAX_ID;i++) {
			trac_time = new long[TT_MAX_ID];
			trac_time_cnt = new long[TT_MAX_ID];
			trac_time_total = new long[TT_MAX_ID];			
		}
	}
 
    /*
     * Serialize a HashMap<String, HashMap<String, String>>
     */
	  public final static byte [] serializeHashMap(HashMap<String, HashMap<String, String>> map) throws IOException, ClassNotFoundException {
	        ByteArrayOutputStream out = new ByteArrayOutputStream();
	        ObjectOutput objOut = new java.io.ObjectOutputStream(out);
	        objOut.writeObject(map);
	        objOut.close();
	        return out.toByteArray();
	    }
	  
	  @SuppressWarnings("unchecked")
	public static HashMap<String, HashMap<String, String>> deSerializeHashMap(byte [] data_in) throws IOException, ClassNotFoundException {
	        ByteArrayInputStream bosin = new ByteArrayInputStream(data_in);
	        ObjectInput in = new ObjectInputStream(bosin);	       
	        return (HashMap<String, HashMap<String, String>>) in.readObject();
    }
    public final static boolean isValidEmailAddress(String email) {
    	if (email == null || email.trim().length() < 7 || email.indexOf("@") < 2) return false;
    	return true;
	}

    /*
     * make these strings with correct decimals
     */
	public static String toMoney(float amount) {
		return String.format("%.2f", amount);
	}
	public static String toMoney_b(int amount) {
		if (amount == 0) return "0.00";
		if (amount < 0) {
			amount = -amount;
			String res = String.format("%02d", (amount - ((amount/100)*100)));
			return  "-"+(amount/100) + "." + res;			
		}
		String res = String.format("%02d", (amount - ((amount/100)*100)));
		return  ""+(amount/100) + "." + res;
	}
	// xx.xxxx => xx.xx
	public static String toMoney(String amount) {
		if (amount == null || amount.isEmpty()) {
			return "0.00";
		}
		BigDecimal money = new BigDecimal(amount);		
		money = money.setScale(2, BigDecimal.ROUND_DOWN);		
		DecimalFormat df = new DecimalFormat();
		df.setMaximumFractionDigits(2);
		df.setMinimumFractionDigits(2);
		df.setGroupingUsed(false);
		return df.format(money);
	}
	// xx.xx => int
	public static int toMoney_int(String amount) {
		if (amount == null || amount.isEmpty()) {
			return 0;
		}
		try {
		BigDecimal money = new BigDecimal(amount);		
		money = money.setScale(2, BigDecimal.ROUND_DOWN);
		money  = money.multiply(new BigDecimal("100"));
		return money.intValue();
		} catch (Throwable t) {
			return 0;
		}
	}
	// xx.xx => float
	public static float toMoney_float(String amount) {
		if (amount == null || amount.isEmpty()) {
			return 0;
		}
		try {
		BigDecimal money = new BigDecimal(amount);		
		money = money.setScale(2, BigDecimal.ROUND_DOWN);
		return money.floatValue();
		} catch (Throwable t) {
			return 0;
		}
	}
	// xx.xx => float
	public static float toMoneyVal(String amount) {
		if (amount == null || amount.isEmpty()) {
			return (float)0;
		}
		try {
		BigDecimal money = new BigDecimal(amount);		
		money = money.setScale(2, BigDecimal.ROUND_DOWN);
		return money.floatValue();
		} catch (Throwable t) {
			return 0;
		}		
	}
	
	
	// xxxx => int
	public static int toMoneyVal_b(String amount) {
		if (amount == null || amount.isEmpty()) {
			return 0;
		}
		int f = 0;
		try {
			f = Integer.parseInt(amount);
		} catch (Throwable t) {
			return 0;
		}
		return f;
	}
	// xx.xxxx => xxxx
	public static String toMoneyftob(String amount) {
		if (amount == null || amount.isEmpty()) {
			return "0";
		}
		
		BigDecimal money = new BigDecimal(amount);		
		money = money.setScale(2, BigDecimal.ROUND_DOWN);
		money  = money.multiply(new BigDecimal("100"));
		
		DecimalFormat df = new DecimalFormat();
		df.setMaximumFractionDigits(0);
		df.setMinimumFractionDigits(0);
		df.setGroupingUsed(false);

		return df.format(money);
	}
	
	public final static String fmtDouble(double value_d) {
		if (value_d == Double.MAX_VALUE) value_d = 0;
		if (value_d == Double.MIN_VALUE) value_d = 0;
		String r = String.format("%.6f", value_d);
		return r.replaceAll("\\.?0*$", "");
	}
	public final static String fmtDouble2(double value_d) {
		if (value_d == -Double.MAX_VALUE) value_d = 0;
		if (value_d == -Double.MAX_VALUE) value_d = 0;
		String r = String.format("%.2f", value_d);
		return r.replaceAll("\\.?0*$", "");
	}
	
	// xx.xxxxxxxxxx => xx.xxxxx
	public static String toRatio(double amount) {
		try {
		BigDecimal myBase_b = new BigDecimal(""+amount);
		myBase_b = myBase_b.setScale(6, BigDecimal.ROUND_DOWN);
		return myBase_b.toString();
		} catch (Throwable t) {
			return "0.000000";
		}
	}
	// xx.xxxxxxxxxx => xx.xx
	public final static String toRatio2(double amount) {
		try {
		BigDecimal myBase_b = new BigDecimal(""+amount);
		myBase_b = myBase_b.setScale(2, BigDecimal.ROUND_DOWN);
		return myBase_b.toString();
		} catch (Throwable t) {
			return "0.00";
		}
	}
	// xx.xxxxxxxxxx => xx.xx
	public final static double toRatio2dec(double amount) {
		try {
		BigDecimal myBase_b = new BigDecimal(""+amount);
		myBase_b = myBase_b.setScale(2, BigDecimal.ROUND_DOWN);
		return myBase_b.doubleValue();
		} catch (Throwable t) {
			return 0;
		}
	}
	
	public final static double toRatio6(double amount) {
		try {
		BigDecimal myBase_b = new BigDecimal(""+amount);
		myBase_b = myBase_b.setScale(6, BigDecimal.ROUND_DOWN);
		return myBase_b.doubleValue();
		} catch (Throwable t) {
			return 0;
		}
	}

	public final static int toIntHex(String hex_amount) {
		int val = 0;
		try {
		val = Integer.parseInt(hex_amount, 16);
		} catch (Throwable t){}
		return val;
	}
	
	public final static int toInt(String amount) {
		int val = 0;
		try {
		val = Integer.parseInt(amount);
		} catch (Throwable t){}
		return val;
	}
	public final static long toLong(String amount) {
		long val = 0;
		try {
		val = Long.parseLong(amount);
		} catch (Throwable t){}
		return val;
	}
	public final static double toDouble(String amount) {
		try {
		BigDecimal myBase_b = new BigDecimal(""+amount);
		return myBase_b.doubleValue();
		} catch (Throwable t) {
			return 0;
		}
	}
	public final static double toDouble2(String amount) {
		try {
		BigDecimal myBase_b = new BigDecimal(""+amount);
		myBase_b = myBase_b.setScale(2, BigDecimal.ROUND_DOWN);
		return myBase_b.doubleValue();
		} catch (Throwable t) {
			return 0;
		}
	}	
	public final static BigInteger toBigInt(String v) {
		try {
			return new BigInteger(v);
		} catch (Throwable t) {}
		return null;
	}
	public final static boolean compare(String s1, String s2) {
		if (s1 == null && s2 == null) return true;
		if (s1 == null || s2 == null) return false;
		return s1.equals(s2);
	}	
	public final static boolean compareIgnoreCase(String s1, String s2) {
		if (s1 == null && s2 == null) return true;
		if (s1 == null || s2 == null) return false;
		return s1.equalsIgnoreCase(s2);
	}	
	public final static boolean contains(String big, String small) {
		if (big == null || small == null) return false;
		return big.contains(small);
	}		
	

	public static String phoneNormalize(String number) {
		if (number == null) {
			return null;
		}
		number = number.replaceAll("[^\\d]", "");
		return number.trim();
	}
	public static String emailNormalize(String email) {
		if (email == null) {
			return null;
		}
		email = email.toLowerCase();
		return email.trim();
	}
	public final static String fmtProperWord(String word) {
		if (word == null) return null;
		if (word.length() == 1) return word.toUpperCase();
		String w = word.toLowerCase().trim();
		return w.substring(0, 1).toUpperCase() + w.substring(1, w.length());
	}
	public final static String fmtInitial(String word) {
		if (word == null || word.isEmpty()) return null;
		String fi = word.substring(0, 1).toUpperCase();
		if (fi.charAt(fi.length()-1) != '.') fi += ".";
		return fi;
	}
	public final static String fmtInitialNoDot(String word) {
		if (word == null || word.isEmpty()) return null;
		return word.substring(0, 1).toUpperCase();
	}
	
	public final static boolean isValue(String val) {
		if (val == null || val.isEmpty()) return false;
		return true;
	}
	public final static String getValue(String val) {
		if (val.equalsIgnoreCase("(null)")) return null;
		return val;
	}
	public final static boolean getBoolean(String val) {
		if (val.equalsIgnoreCase("true")) return true;
		return false;
	}
	
	/*
	 * calculate a rate
	 */
	public static double calculateRate(int ret, int cost) {
		BigDecimal calc = calculateRateBigD(ret, cost);
		if (calc != null) {
			return calc.doubleValue();
		}
		return 0;
	}
	// (ret / cost) * 100
	public static BigDecimal calculateRateBigD(int ret, int cost) {
		BigDecimal calc = new BigDecimal(ret);
		if (cost > 0) {
			// get day rate
			calc = calc.divide(new BigDecimal(cost), 8, RoundingMode.HALF_UP);
			calc = calc.multiply(new BigDecimal("100"));
			calc = calc.setScale(2, BigDecimal.ROUND_DOWN);
			return calc;
		} else {
			return null;
		}
	}
	public static double calculateAnnualRate(int term, int cost, int fees) {
		BigDecimal calc = calculateAnnualRateBigD(term, cost, fees);
		if (calc != null) {
			return calc.doubleValue();
		}
		return 0;
	}
	public static String calculateAnnualRateStr(int term, int cost, int fees) {
		BigDecimal calc = calculateAnnualRateBigD(term, cost, fees);
		if (calc != null) {
			DecimalFormat df = new DecimalFormat();
			df.setMaximumFractionDigits(2);
			df.setMinimumFractionDigits(2);
			df.setGroupingUsed(false);
			return df.format(calc);
		}
		return "0.00";
	}
	public static BigDecimal calculateAnnualRateBigD(int term, int cost, int fees) {
		if (term < 1) {
			term = 1;
		}
		if (cost > 0 && cost > fees) {
			BigDecimal calc = new BigDecimal(fees);
			// get day rate
			calc = calc.divide(new BigDecimal(cost), 8, RoundingMode.HALF_UP);
			calc = calc.multiply(new BigDecimal("100"));
			calc = calc.divide(new BigDecimal(term), 8, RoundingMode.HALF_UP);		
			calc = calc.multiply(new BigDecimal("365"));
			calc = calc.setScale(2, BigDecimal.ROUND_DOWN);
			return calc;
		} else {
			return null;
		}
	}

	
	// get days between to dates
	private static final int MILLISECONDS_IN_DAY = 1000 * 60 * 60 * 24;
	private static final int MILLISECONDS_IN_MIN = 1000 * 60;
	private static final int MILLISECONDS_IN_SEC = 1000;
	public static int getDaysBetween(Calendar startCal, Calendar endCal, String time_zone){
		if (startCal == null || endCal == null) {
			return 0;
		}
		Calendar sC = (Calendar)startCal.clone();
		Calendar eC = (Calendar)endCal.clone();
		sC = setTimeZone(sC, time_zone);
		eC = setTimeZone(eC, time_zone);
		sC.set(Calendar.HOUR_OF_DAY, 0);
		sC.set(Calendar.MINUTE, 1);
		sC.set(Calendar.SECOND, 0);
		eC.set(Calendar.HOUR_OF_DAY, 0);
		eC.set(Calendar.MINUTE, 1);
		eC.set(Calendar.SECOND, 0);
		
		//System.out.println("getDaysBetween: " + ServiceUtil.formatTimeString(sC) + " to " + ServiceUtil.formatTimeString(eC));
		
		long endTime = eC.getTimeInMillis();
		long startTime = sC.getTimeInMillis();
		return (int) ((endTime - startTime) / MILLISECONDS_IN_DAY);
	}
	public static int getDaysBetween(Calendar startCal, Calendar endCal){
		return getDaysBetween(startCal, endCal, "PST");
	}
	public static int getDaysBetween(long startCal, long endCal){
		return (int) ((endCal - startCal) / MILLISECONDS_IN_DAY);
	}


	
	/*
	 * check for sameDate
	 */
	public static boolean isSameDate(Calendar startCal, Calendar endCal, String time_zone){
		if (startCal == null && endCal == null) {
			return true;
		}
		if (startCal == null || endCal == null) {
			return false;
		}
		
		Calendar sd = (Calendar)startCal.clone();
		Calendar ed = (Calendar)endCal.clone();
		sd = setTimeZone(sd, time_zone);
		ed = setTimeZone(ed, time_zone);
		int sMonth = sd.get(Calendar.MONTH);
		int eMonth = ed.get(Calendar.MONTH);		
		
		// same date and year at 1 min past start of day
		if (eMonth == sMonth && endCal.get(Calendar.DATE) == startCal.get(Calendar.DATE) 
				&& endCal.get(Calendar.YEAR) == startCal.get(Calendar.YEAR)) {
			return true;
		}

		return false;
	}

	public static boolean isSameDate(Calendar startCal, Calendar endCal){
		return isSameDate(startCal, endCal, "PST");
	}
	public static int getHoursBetween(Calendar startCal, Calendar endCal, String time_zone){
		int min = getMinutesBetween(startCal, endCal, time_zone);
		if (min <= 0) {
			return 0;
		}
		return min / 60;
	}
	public static int getHoursBetween(Calendar startCal, Calendar endCal){
		return getHoursBetween(startCal, endCal, "PST");
	}
	public static int getMinutesBetween(Calendar startCal, Calendar endCal, String time_zone){
		if (startCal == null || endCal == null) {
			return 0;
		}
		Calendar sC = (Calendar)startCal.clone();
		Calendar eC = (Calendar)endCal.clone();
		sC = setTimeZone(sC, time_zone);
		eC = setTimeZone(eC, time_zone);
		long endTime = eC.getTimeInMillis();
		long startTime = sC.getTimeInMillis();
		return (int) ((endTime - startTime) / MILLISECONDS_IN_MIN);
	}
	public static int getMinutesBetween(Calendar startCal, Calendar endCal){
		return getMinutesBetween(startCal, endCal, "PST");
	}
	public static int getMinutesBetween(long startCal, long endCal){
		return (int) ((endCal - startCal) / MILLISECONDS_IN_MIN);
	}
	public static int getSecondsBetween(Calendar startCal, Calendar endCal, String time_zone){
		if (startCal == null || endCal == null) {
			return 0;
		}
		Calendar sC = (Calendar)startCal.clone();
		Calendar eC = (Calendar)endCal.clone();
		sC = setTimeZone(sC, time_zone);
		eC = setTimeZone(eC, time_zone);
		long endTime = eC.getTimeInMillis();
		long startTime = sC.getTimeInMillis();
		return (int) ((endTime - startTime) / MILLISECONDS_IN_SEC);
	}
	public static int getSecondsBetween(Calendar startCal, Calendar endCal){
		return getSecondsBetween(startCal, endCal, "PST");
	}
	public static int getSecondsBetween(long startCal, long endCal){
		return (int) ((endCal - startCal) / MILLISECONDS_IN_SEC);
	}
	public static int getMilliSecondsBetween(Calendar startCal, Calendar endCal, String time_zone){
		if (startCal == null || endCal == null) return 0;
		
		Calendar sC = (Calendar)startCal.clone();
		Calendar eC = (Calendar)endCal.clone();
		sC = setTimeZone(sC, time_zone);
		eC = setTimeZone(eC, time_zone);
		long endTime = eC.getTimeInMillis();
		long startTime = sC.getTimeInMillis();
		return (int) (endTime - startTime);
	}
	public static int getMilliSecondsBetween(Calendar startCal, Calendar endCal){
		return getMilliSecondsBetween(startCal, endCal, "PST");
	}
	public static int getMilliSecondsBetween(long startCal, long endCal){
		return (int) (endCal - startCal);
	}
	
	public static boolean isBetween(Calendar date, Calendar start, Calendar end) {
		if (date == null || start == null || start == end) return false;
		
		if (isSameDate(date, start) || isSameDate(date, end)) return true;
		if (date.after(start) && date.before(end)) return true;
		return false;
	}
	public static String getDurration(Calendar startCal, Calendar endCa) {
		long msec = Sutil.getMilliSecondsBetween(startCal, endCa);
		int bmsec = ((int)msec) % 1000;
		int sec = ((int)msec) / 1000;
		int min = sec / 60;
		sec = sec % 60;
		return min + " min " +sec + " sec " + String.format("%03d", bmsec) + " ms";
	}
	public static void printTimeDiff(String lable, Calendar t1, Calendar t2) {
		long msec = Sutil.getMilliSecondsBetween(t1, t2);
		//int sec = Gtil.getSecondsBetween(t1, t2);
		int bmsec = ((int)msec) % 1000;
		int sec = ((int)msec) / 1000;
		int min = sec / 60;
		sec = sec % 60;
		
		if (msec == 0 && sec == 0 && min == 0) return;
		System.out.println("       TIME["+lable+"] " + min + ":"+String.format("%02d", sec)+"."+String.format("%03d", bmsec));
	}
	// set this to the timezone
	public static String getTimeZoneStr(String time_zone) {
		if (time_zone != null && !time_zone.isEmpty()) {
			if (time_zone.equalsIgnoreCase("PST")) {
				time_zone = "America/Los_Angeles";
			} else if (time_zone.equalsIgnoreCase("CST")) {
				time_zone = "US/Central";
			} else if (time_zone.equalsIgnoreCase("MST")) {
				time_zone = "US/Mountain";
			} else if (time_zone.equalsIgnoreCase("EST")) {
				time_zone = "America/New_York";
			}
		}
		return time_zone;
	}
	private static Calendar setTimeZone(Calendar date, String time_zone) {
		if (date == null) {
			return null;
		}
		// set timezone
		if (time_zone != null && !time_zone.isEmpty()) {
			TimeZone tz = TimeZone.getTimeZone(getTimeZoneStr(time_zone));
			if (tz != null) {
				date.setTimeZone(tz);
			}
		}
		return date;
	}
	
	// get Months between to dates
	public static int getMonthsBetween(Calendar start, Calendar end) {
		if (start == null || end == null) return 0;
		
		int syear = start.get(Calendar.YEAR);
		int sMonth = start.get(Calendar.MONTH);
		int eyear = end.get(Calendar.YEAR);
		int eMonth = end.get(Calendar.MONTH);
	//	System.out.println("between: " + sMonth + "/" + syear + " > " + eMonth + "/" + eyear + "  == " + ((eyear - syear) * 12) + " M " + ((eMonth - sMonth) + 1));
		return ((eyear - syear) * 12) + ((eMonth - sMonth));
	}
	// get years between to dates
	public static int getYearsBetween(Calendar start, Calendar end) {
		if (start == null || end == null) {
			return 0;
		}
		int syear = start.get(Calendar.YEAR);
		int sMonth = start.get(Calendar.MONTH);
		int eyear = end.get(Calendar.YEAR);
		int eMonth = end.get(Calendar.MONTH);
		return ((eyear - syear) + ((eMonth - sMonth) + 1) / 12);
	}
	
	// get local time zone
	public static TimeZone getLocalTimeZone() {
		Calendar now = Calendar. getInstance();
		TimeZone timeZone = now.getTimeZone();
		return timeZone;
	}
	public static String getLocalTimeZoneString() {
		Calendar now = Calendar. getInstance();
		TimeZone timeZone = now.getTimeZone();
		return timeZone.getDisplayName();
	}
	public static Calendar getLocalTime() {
		return Calendar. getInstance();
	}
	public static Calendar getTime(String timezone) {
		Calendar c = Calendar.getInstance(TimeZone.getTimeZone(timezone));
		return c;
	}		
	
	/*
	 * getUTCInstance()
	 */
	public static Calendar getUTCTime() {
	   Calendar c = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
	   //System.out.println("current: "+c.getTime());

	    TimeZone z = c.getTimeZone();
	    int offset = z.getRawOffset();
	    if(z.inDaylightTime(new Date())){
	        offset = offset + z.getDSTSavings();
	    }
	    int offsetHrs = offset / 1000 / 60 / 60;
	    int offsetMins = offset / 1000 / 60 % 60;

	    //System.out.println("offset hrs: " + offsetHrs + " offset min: " + offsetMins);

	    c.add(Calendar.HOUR_OF_DAY, (-offsetHrs));
	    c.add(Calendar.MINUTE, (-offsetMins));
	    
	    //System.out.println("GMT Time: "+c.getTime());
	    return c;
	}
	public static Calendar getUTCTimeClear() {
		Calendar t = Sutil.getUTCTime();	
		t.set(Calendar.HOUR, 0);
		t.set(Calendar.MINUTE, 0);
		t.set(Calendar.SECOND, 0);
		t.set(Calendar.MILLISECOND, 0);
		t.set(Calendar.DATE, 1);
		t.set(Calendar.MONTH, 0);
		t.set(Calendar.YEAR, 0);	
		return t;
	}
	
	public static Calendar getUTCTimePlus(int hours) {
		Calendar t = Sutil.getUTCTime();
		t.add(Calendar.HOUR, hours);
		return t;
	}
	public static Calendar getUTCTimePlusSeconds(int seconds) {
		Calendar t = Sutil.getUTCTime();
		t.add(Calendar.SECOND, seconds);
		return t;
	}
	public static Calendar getUTCTimePlusMinutes(int min) {
		Calendar t = Sutil.getUTCTime();
		t.add(Calendar.MINUTE, min);
		return t;
	}
	public static Calendar getUTCTimePlusDay(int days) {
		Calendar t = Sutil.getUTCTime();
		t.add(Calendar.DATE, days);
		return t;
	}
	public static Calendar getUTCTimeMinus(int hours) {
		Calendar t = Sutil.getUTCTime();
		t.add(Calendar.HOUR, -hours);
		return t;
	}
	public static Calendar getUTCTimeMinusMinutes(int min) {
		Calendar t = Sutil.getUTCTime();
		t.add(Calendar.MINUTE, -min);
		return t;
	}
	public static Calendar getUTCTimeMinusDay(int days) {
		Calendar t = Sutil.getUTCTime();
		t.add(Calendar.DATE, -days);
		return t;
	}
	public static Calendar getUTCTimeMinusWeek(int weeks) {
		Calendar t = Sutil.getUTCTime();
		t.add(Calendar.DATE, -(weeks*7));
		return t;
	}
	public static Calendar getUTCTimeMinusMonth(int Months) {
		Calendar t = Sutil.getUTCTime();
		t.add(Calendar.MONTH, -Months);
		return t;
	}
	public static Calendar getUTCTimeMinusYear(int years) {
		Calendar t = Sutil.getUTCTime();
		t.add(Calendar.YEAR, -years);
		return t;
	}
	public static Calendar convertToGmt(Calendar cal) {
		if (cal == null) {
			return null;
		}
		Date date = cal.getTime();
		TimeZone tz = cal.getTimeZone();

		//Returns the number of milliseconds since January 1, 1970, 00:00:00 GMT 
		long msFromEpochGmt = date.getTime();

		//gives you the current offset in ms from GMT at the current date
		int offsetFromUTC = tz.getOffset(msFromEpochGmt);

		//create a new calendar in GMT timezone, set to this date and add the offset
		Calendar gmtCal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
		gmtCal.setTime(date);
		gmtCal.add(Calendar.MILLISECOND, offsetFromUTC);

		return gmtCal;
	}


	public static byte[] getBytes(InputStream is) {
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();

		int nRead;
		byte[] data = new byte[16384];

		try {
			while ((nRead = is.read(data, 0, data.length)) != -1) {
			  buffer.write(data, 0, nRead);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try {
			buffer.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return buffer.toByteArray();		
	}
	public static int indexOfAnyIgnoreCase(String test, String [] list) {
		if (test == null || list == null || list.length < 1) return -1;
		for (int i=0;i<list.length;i++) if (test.equalsIgnoreCase(list[i])) return i;
		return -1;
	}
	public static int indexOf(String test, String [] list) {
		if (test == null || list == null || list.length < 1) return -1;
		for (int i=0;i<list.length;i++) if (test.equals(list[i])) return i;
		return -1;
	}
	public static int indexOfContains(String test, String [] list) {
		if (test == null || list == null || list.length < 1) return -1;
		for (int i=0;i<list.length;i++) if (test.contains(list[i])) return i;
		return -1;
	}
	public static int indexOfStartsWith(String test, String [] list) {
		if (test == null || list == null || list.length < 1) return -1;
		for (int i=0;i<list.length;i++) if (test.startsWith(list[i])) return i;
		return -1;
	}
	public static int indexOfStartsWithIgnoreCase(String test, String [] list) {
		if (test == null || list == null || list.length < 1) return -1;
		String st = test.toLowerCase();
		for (int i=0;i<list.length;i++) if (st.startsWith(list[i])) return i;
		return -1;
	}
	public static int countLetters(String word) {
	    String onlyLetters = word.replaceAll("[^\\p{L}]", "");
	    return onlyLetters.length();
	}
	public static int upperCaseCount(String word) {
		int upper = 0;
		for (int i=0;i<word.length();i++) {
			if (Character.isUpperCase(word.charAt(i))) upper++;
		}
		return upper;
	}
	public static int lowerCaseCount(String word) {
		int upper = 0;
		for (int i=0;i<word.length();i++) {
			if (Character.isLowerCase(i)) upper++;
		}
		return upper;
	}
	public static int letterNumberCount(String word) {
		if (word == null) return 0;
		int upper = 0;
		for (int i=0;i<word.length();i++) {
			if (Character.isLetterOrDigit(word.charAt(i))) upper++;
		}
		return upper;
	}
	public static boolean endsWithIngoreCase(String text, String test) {
		if (test == null || text == null) return false;
		String tst = text;
		tst = tst.toUpperCase();
		String txt = test;
		txt = txt.toUpperCase();
		return tst.endsWith(txt);
	}
	public static boolean containsIngoreCase(String text, String test) {
		if (test == null || text == null) return false;
		String tst = text;
		tst = tst.toUpperCase();
		String txt = test;
		txt = txt.toUpperCase();
		return tst.contains(txt);
	}
	public static boolean startsWithIngoreCase(String text, String test) {
		if (test == null || text == null) return false;
		String tst = text;
		tst = tst.toUpperCase();
		String txt = test;
		txt = txt.toUpperCase();
		return tst.startsWith(txt);
	}
	public static int countSpace(String text) {
		int v = 0;
		for (int i=0;i<text.length();i++) {
			char c = text.charAt(i);
			int ascii = (int)c;
			if (Character.isWhitespace(c)) v++;
			else if (ascii == 160) v++; // not a space space
		}
		return v;
	}

	
	// this should be optimized
	public static boolean isVowel(char c) {
		return isVowel(c, false);
	}
	public static boolean isVowel(char c, boolean why) {
		switch (c) {
	      case 'a':
	      case 'A':
	      case 'e':
	      case 'E':
	      case 'i':
	      case 'I':
	      case 'o':
	      case 'O':
	      case 'u':
	      case 'U':
	        return true;
	      case 'y':	 
	      case 'Y':
	    	  if (why) return true;
	       // if (pos != 0 && isVowel(pos - 1)) return false; // FIXME account for 'y'
	      default:
	        return false;
	    }
	}
	public static int countVowels(String word) {
		  return countVowels(word, false);
	}
	public static int countVowels(String word, boolean why) {
		int cnt = 0;
		for (char c:word.toCharArray()) {
			if (isVowel(c, why)) cnt++;
		}
		return cnt;
	}
	public static boolean isConsonant(char c) {
		if (Character.isLetter(c)) return !isVowel(c);
		return false;
	}
		
		

	
	// not the smartest check, but its simple and fast
	public static boolean isJSONtext(String text) {
		if (text == null || text.isEmpty()) return false;
		if (text.startsWith("{") && text.endsWith("}")) return true;
		return false;
	}
	
	
	// get JSON string for object
	public static String toJSON(Object obj) {
		ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
		try {
			return ow.writeValueAsString(obj);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		return null;
	}

}



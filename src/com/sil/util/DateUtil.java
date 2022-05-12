package com.sil.util;

import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.hibernate.Query;
import org.hibernate.Session;

import com.sil.hbm.D001011;
import com.sil.hbm.D946020;
import com.sil.prop.ConfigurationLoader;
public class DateUtil {
	public	static org.apache.log4j.Logger logger=org.apache.log4j.Logger.getLogger(DateUtil.class);	  
	private static 	SimpleDateFormat dateFormatPDF = new SimpleDateFormat("ddMMMyyyyhhmmssSSS");
	private static SimpleDateFormat dateFormat2 = new SimpleDateFormat("dd/MM/yyyy");
	private static SimpleDateFormat datecurrentDate = new SimpleDateFormat("dd-MMM-yyyy");
	private static DateFormat julianDate = new SimpleDateFormat("yyDDD");
	private static DateFormat julianDate1 = new SimpleDateFormat("DDD");
	private static SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy");
	//    private static SimpleDateFormat sdf = new SimpleDateFormat("dd/MM-yyyy");
	private static SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss a");
	private static SimpleDateFormat dateFormatnew = new SimpleDateFormat("yyyyMMdd");
	private static SimpleDateFormat dateFormat3 = new SimpleDateFormat("dd/MMM/yyyy");
	private static SimpleDateFormat sdf1 = new SimpleDateFormat("hh:mm:ss a");
	private static SimpleDateFormat dateFormatMMYY = new SimpleDateFormat("MM/yy");
	/**
	 * @return date2
	 */
	public static Date getcurrentDate() {
		java.util.Date date = new java.util.Date();
		String dateStr = datecurrentDate.format(date);
		Date date2 = null;
		try {
			date2 = datecurrentDate.parse(dateStr);
		} catch (Exception pe) {
			pe.printStackTrace();
		}
		return date2;
	}
	
	public static Date getCurrentDateNew(Date date) {
		String dateStr = dateFormat2.format(date);
		Date date2 = null;
		try {
			date2 = dateFormat2.parse(dateStr);
		} catch (Exception pe) {
			pe.printStackTrace();
		}
		return date2;
	}
	public static String getcurrentDateString() {
		SimpleDateFormat dateFormat1 = new SimpleDateFormat("dd/MM/yyyy");
		java.util.Date date = new java.util.Date();
		String dateStr = dateFormat1.format(date);
		return dateStr;
	}
	public static String getcurrentDateStringNew() {
		SimpleDateFormat dateFormat1 = new SimpleDateFormat("dd-MON-yyyy");
		java.util.Date date = new java.util.Date();
		String dateStr = dateFormat1.format(date);
		return dateStr;
	}
	public static String getcurrentDateStringDDMONYYYY() {
		SimpleDateFormat dateFormat1 = new SimpleDateFormat("dd-MMM-yyyy");
		java.util.Date date = new java.util.Date();
		String dateStr = dateFormat1.format(date);
		return dateStr;
	}

	public static String getcurrentDateStringDDMMYYYY() {
		SimpleDateFormat dateFormat1 = new SimpleDateFormat("ddMMyyyy");
		java.util.Date date = new java.util.Date();
		String dateStr = dateFormat1.format(date);
		return dateStr;
	}
	/**
	 * 	 * @return formated date
	 */
	public static String getcurrentDateForPDF() {
		java.util.Date date = new java.util.Date();
		String dateStr = dateFormatPDF.format(date);
		return dateStr;
	}



	/**
	 * @param date gives date
	 * @return formated date
	 */

	public static String getString(Date date){
		try
		{
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
			return simpleDateFormat.format(date);
		}
		catch (Exception e) {
			return null;
		}
	}


	public static String getStringDateNew(Date date){
		try
		{
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MMM-yyyy");
			return simpleDateFormat.format(date);
		}
		catch (Exception e) {
			return null;
		}
	}

	/**
	 * @param date gives String date
	 * @return formated String
	 */
	public static String getStringDate(Date date){
		try
		{
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MMM/yyyy");			
			return simpleDateFormat.format(date).toString();
		}
		catch (Exception e) {
			return null;
		}
	}

	/**
	 * @param date gives date
	 * @return convertedDate in yyyyMMdd format
	 */

	public static Date getDateFromString(String date) {
		String dateString = null;
		Date convertedDate = null;
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		try {
			dateString = date;
			convertedDate = dateFormat.parse(dateString);			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return convertedDate;
	}
	public static Date getDateFromStringDate(String date) {
		String dateString = null;
		Date convertedDate = null;
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
		try {
			dateString = date;
			convertedDate = dateFormat.parse(dateString);			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return convertedDate;
	}
	public static Date getDateFromStringNew(String date) {
		String dateString = null;
		Date convertedDate = null;
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
		try {
			dateString = date;
			convertedDate = dateFormat.parse(dateString);			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return convertedDate;
	}

	public static Date getDateFromStringYYYYMMDD(String date) {
		String dateString = null;
		Date convertedDate = null;
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
		try {
			dateString = date;
			convertedDate = dateFormat.parse(dateString);			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return convertedDate;
	}
	/**
	 * @param String gives date
	 * @return String in yyyyMMdd format
	 */
	public static String getStringDateyyyyMMdd(Date date) {
		String dateString = null;
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
		try {
			dateString = dateFormat.format(date);			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return dateString;
	}

	/**
	 * @param date gives date
	 * @return formatedDate 
	 */
	public static Date getDateFromStringinDateFormat(String date) {
		String dateString = null;
		Date convertedDate = null;
		try {
			dateString = date;
			convertedDate = dateFormatnew.parse(dateString);     
		} catch (Exception e) {
			e.printStackTrace();
		}
		return convertedDate;
	}

	/**
	 * @param date gives date
	 * @return formatedDate 
	 */

	public static Date getFormattedDate(Date date){
		try{
			return dateFormat.parse(dateFormat.format(date));
		}catch (Exception e) {
			return null;
		}
	}

	public static Date getFormattedDateNew(Date date){
		try{
			return dateFormat3.parse(dateFormat3.format(date));
		}catch (Exception e) {
			return null;
		}
	}

	/**
	 * @param date gives date
	 * @return formatedDate 
	 */

	public static Date getFormattedDateMonth(Date date){
		try{
			return dateFormat2.parse(dateFormat2.format(date));
		}catch (Exception e) {
			return null;
		}
	}


	/**
	 * @description Returns the date difference between two Calendar dates
	 * @version $Revision$
	 * @param startDate
	 * @param finalendDate
	 * @return long
	 */

	public static long getDateDiff(Calendar startDate, Calendar finalendDate) {

		long ndays = 0;
		int n;
		if (startDate.get(startDate.YEAR) < finalendDate.get(finalendDate.YEAR)) {
			ndays += (366 - startDate.get(startDate.DAY_OF_YEAR));
			for (n = startDate.get(startDate.YEAR) + 1; n <= finalendDate.get(finalendDate.YEAR) - 1; n++) {
				ndays += 365;
			}
		}
		ndays += finalendDate.get(finalendDate.DAY_OF_YEAR);
		if (finalendDate.get(finalendDate.YEAR) == startDate.get(startDate.YEAR)) {
			ndays = finalendDate.get(finalendDate.DAY_OF_YEAR) - startDate.get(startDate.DAY_OF_YEAR);

		}
		return ndays;
	}

	/**
	 * @param dateOfBirth gives date of birth
	 * @param operationalDate gives operational date
	 * @return age 
	 */

	public static int getAge(Date dateofBirth , Date operationalDate){

		Calendar cal1 = null; 
		Calendar cal2 = null;
		if(operationalDate.compareTo(dateofBirth)>0){
			try{
				cal1=Calendar.getInstance(); 
				cal2=Calendar.getInstance(); 
				cal1.setTime(dateofBirth);          
				cal2.setTime(operationalDate);
				int yearDiff  = cal2.get(Calendar.YEAR) - cal1.get(Calendar.YEAR); 
				int monthDiff = yearDiff * 12 + cal2.get(Calendar.MONTH) - cal1.get(Calendar.MONTH);

				int quotientMonths = 0;
				if(monthDiff >= 12){
					quotientMonths = monthDiff / 12;
				}
				if(monthDiff < 12){
					return 1;	
				}else{
					return quotientMonths; 
				}
			}catch (Exception e) {
			}
		}
		return 0;
	} 

	/**
	 * this method returns the date in dd/mm/yyyy format
	 * @param date
	 * @return
	 */
	public static String getUserFormattedDate(Date date){
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
		return dateFormat.format(date);
	}

	/**
	 * this method returns the date in dd/mm/yyyy format
	 * @param date
	 * @return
	 */
	public static String getUserFormattedDateInMonth(Date date){
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/mmm/yyyy");
		return dateFormat.format(date);
	}

	public static String getDiff(Date date, Date operationalDate) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(operationalDate);
		cal.add(Calendar.MONTH, -6);
		if (date.after(cal.getTime()))
			return "success";
		else
			return "failure";
	}
	/**
	 * 
	 *Following method is used to convert the date into the user specified format
	 *@param inputDate receives the date into string format
	 *@param outputDateFormat receives the format into which user wants to format the date
	 *@return
	 */
	public static Date getDateFromString(String inputDate, String outputDateFormat){
		Date convertedDate = null;
		SimpleDateFormat dateFormat = new SimpleDateFormat(outputDateFormat);
		try {
			convertedDate = dateFormat.parse(inputDate);			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return convertedDate;
	}
	/**
	 * 
	 *@description: method is use to convert the time into the the 8 digits long value.		
	 *@return
	 */
	public static long getMakerTime(Date date){
		/*Calendar calendar = Calendar.getInstance();
		long makerTime = Long.parseLong(calendar.get(Calendar.HOUR_OF_DAY)+""+calendar.get(Calendar.MINUTE)+""+calendar.get(Calendar.SECOND)+String.valueOf((calendar.get(Calendar.MILLISECOND))).substring(0, 2));
		return makerTime;
	    Date convertedDate = null;*/
		//long makerTime = 0;		
		SimpleDateFormat dateFormat = new SimpleDateFormat("HHmmssSS");
		String dtString = dateFormat.format(new Date());
		//long currentTime = Long.parseLong(dateFormat.format(new Date()));
		if (dtString.length() > 8) 
			return Long.parseLong(dtString.substring(0, 8));
		else 
			return Long.parseLong(dtString);		       
	}

	public static void main(String [] args){
//		System.out.println(getEffDate("2017-11-09 00:00:00.0"));
//		System.out.println(getcurrentDateStringDDMONYYYY());
		System.out.println(convertDateFormatIVR(new Date()));

//		System.out.println(getStringDateNew(new Date()));
//		System.out.println("compareFromDateToDate::>>"+compareFromDateToDate("20170301", getcurrentDateyyyyMMdd()));
		//    	System.out.println(addDays(-20, new Date()));
		//    System.out.println(compareODExpDate("2016-02-15 00:00:00.0", ""));
		//    	System.out.println(getcurrentDate());
		//    	System.out.println(""+compareTwoDateMMYY("2021-01-04 00:00:00.0", new Date().toString()));
		//    	logger.error(getDateFromString("2013-02-18T00:00:00+05:30"));;
		//    	logger.error("formatted Date:>>>"+getStringDateDDMMYYYY("20160603"));
		//    	logger.error(getDateDDMONYYYY("10/11/2016"));
		//        getMakerTime(new Date());
		/*Calendar calendar = Calendar.getInstance();
    	logger.error(calendar);
		logger.error(calendar.get(Calendar.HOUR_OF_DAY)+""+calendar.get(Calendar.MINUTE)+""+calendar.get(Calendar.SECOND)+String.valueOf((calendar.get(Calendar.MILLISECOND))).substring(0, 2));
		logger.error(calendar.get(Calendar.MINUTE));
		logger.error(calendar.get(Calendar.SECOND));
		logger.error(String.valueOf((calendar.get(Calendar.MILLISECOND))).substring(0, 2));//+" "+Calendar.MINUTE+" "+Calendar.MILLISECOND)));
		 */    	//logger.error(DateUtility.getDateFromString("20100814"));
	} 
	public static int getLastDayOfMonth(int month,int year){
		int lastDay = 0;
		switch(month){
		case 1: lastDay=31; break;
		case 2: lastDay = (year%4 == 0)?29:28; break;
		case 3: lastDay=31; break;
		case 4: lastDay=30; break;
		case 5: lastDay=31; break;
		case 6: lastDay=30; break;
		case 7: lastDay=31; break;
		case 8: lastDay=31; break;
		case 9: lastDay=30; break;
		case 10: lastDay=31; break;
		case 11: lastDay=30; break;
		case 12: lastDay=31; break;
		}

		return lastDay;
	}

	public static Date formatDate(int day,int month, int year){
		try{
			SimpleDateFormat dateFormat1 = new SimpleDateFormat("dd/MM/yyyy");
			try {
				return dateFormat1.parse(day + "/" + month + "/" + year);
			} catch (java.text.ParseException e) {
				e.printStackTrace();
			}
		}catch(Exception ex ){

		}
		return null;
	}

	public static String getDateString(Date date){
		SimpleDateFormat dateFormat1 = new SimpleDateFormat("dd/MM/yyyy");
		return dateFormat1.format(date);		
	}


	/**
	 * @param date gives date
	 * @return formated date
	 */

	public static String getComboString(Date date){
		try
		{
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat("ddMMyyyy");
			return simpleDateFormat.format(date);
		}
		catch (Exception e) {
			return null;
		}
	}
	public static Date convertDateFormatNew(Date date)
	{
		try {
			SimpleDateFormat dateFormat = new SimpleDateFormat("0yyyyMMdd");
			String dateString = dateFormat.format(date);
			return dateFormat.parse(dateString);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}		
	}
	public static Date convertDateFormat(Date date)
	{
		try {
			SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
			String dateString = dateFormat.format(date);
			return dateFormat.parse(dateString);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}		
	}
	
	public static Date getEffDate(String date){
		try
		{
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
			Date convertedDate = simpleDateFormat.parse(date);  
			return convertedDate;
		}
		catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static int getMonthsBetweenDates(Date startDate, Date dateEnddate) {
		int i=0;
		Calendar start = Calendar.getInstance();
		Calendar end = Calendar.getInstance();
		start.setTime(startDate);
		end.setTime(dateEnddate);
		while(!start.after(end)){
			start.add(start.MONTH,1);
			i = i+1;
		}
		return i-1;
	} 

	/**
	 * @date
	 * @param 
	 * @description
	 * @return 
	 * @throws  SQLException
	 * @throws  DataAccessException
	 * @version $Revision$
	 */
	public static Date getEffDateNew(String date){
		try
		{
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEE MMM dd hh:mm:ss zzz yyyy");
			Date convertedDate = simpleDateFormat.parse(date);  
			return convertedDate;
		}
		catch (Exception e) {
			try{
				if(date.length()==18 || date.length()==19){	
					Date newDate=DateUtil.getDateFromString(date.substring(0, 10),"yyyy-MM-dd");
					return newDate;
				}
			}catch(Exception ee){
				e.printStackTrace();
				return null;
			}
			return null;
		}
	}

	/**
	 * @date
	 * @param 
	 * @description
	 * @return 
	 * @throws  
	 * @throws  
	 * @version $Revision$
	 */
	public static Date addDays(long days, Date dt){		
		Calendar c1 = Calendar.getInstance();		
		c1.setTime(dt);
		c1.add(Calendar.DATE, (int) days );
		return c1.getTime();
	}

	/**
	 * @param date
	 * @return
	 */
	public static String geDateForFileGeneration(Date date){
		try
		{
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
			String dateString = simpleDateFormat.format(date);
			return dateString;
		}
		catch (Exception e) {			
			return null;
		}
	}

	/**
	 * @date : July 18, 2012
	 * @param dob
	 * @param operationDate
	 * @return
	 */
	public static double getAgeYears(java.util.Date dob,java.util.Date operationDate) {
		DateFormat dateFormat = new SimpleDateFormat("yyyy");
		int year = Integer.parseInt(dateFormat.format(dob));
		dateFormat = new SimpleDateFormat("MM");
		int month = Integer.parseInt(dateFormat.format(dob));
		dateFormat = new SimpleDateFormat("dd");
		int day = Integer.parseInt(dateFormat.format(dob));
		dateFormat = new SimpleDateFormat("yyyy");
		int curyear = Integer.parseInt(dateFormat.format(operationDate));
		dateFormat = new SimpleDateFormat("MM");
		int curmonth = Integer.parseInt(dateFormat.format(operationDate));
		dateFormat = new SimpleDateFormat("dd");
		int curday = Integer.parseInt(dateFormat.format(operationDate));
		return getAge(year,curyear,month,curmonth,day,curday);
	}

	/**
	 * @date : July 18, 2012
	 * @author : Sachin Mandhare
	 * @param yr
	 * @param tdYr
	 * @param mth
	 * @param tdMth
	 * @param usday
	 * @param tdDay
	 * @return
	 */
	private static double getAge(int yr, int tdYr, int mth, int tdMth, int usday, int tdDay){
		int usrYear, year, usrMonth, month, usrDay, day, outDay=0, outMonth, outYear, tempMth;
		int [] dayInMonth = {31,28,31,30,31,30,31,31,30,31,30,31};
		int tempDyMth; 
		usrYear = yr;
		year = tdYr;
		usrMonth = mth;
		month = tdMth;
		usrDay = usday;
		day = tdDay;
		outDay = day - usrDay;
		tempMth = usrMonth;
		outMonth = (month+1) - usrMonth;
		outYear = year - usrYear;
		if (usrMonth-1<month&&usrDay>day)
		{
			outMonth = outMonth - 1;
			tempDyMth = dayInMonth[usrMonth-1];
			outDay = tempDyMth+outDay;
		}
		if ( outDay<0 )
		{
			outDay = getDaysInMonth(tempMth,usrDay,day);
			outMonth = Math.abs(outMonth);
			outMonth=(12-outMonth)-1;
			outYear=outYear-1;
		}
		if (outMonth<0)
		{	
			outMonth=Math.abs(outMonth); 
			outMonth=12-outMonth;
			outYear=outYear-1;
		}
		String age =  outYear + "." + outMonth;
		double ageDouble = Double.parseDouble(age);
		return ageDouble;
	}

	/**
	 * @date : July 18, 2012
	 * @author : Sachin Mandhare
	 * @param usrMth
	 * @param usrDy
	 * @param tdDay
	 * @return
	 */
	public static int getDaysInMonth(int usrMth, int usrDy, int tdDay)
	{
		int usrMonth, usrDay, daysInMth,day, outDay;
		int [] dayInMonth = {31,28,31,30,31,30,31,31,30,31,30,31};
		usrMonth = usrMth-1;
		usrDay = usrDy;
		day = tdDay;
		daysInMth = dayInMonth[usrMonth];
		outDay = daysInMth - usrDay;
		outDay = outDay + day;
		return outDay;
	}

	public static String getDiffParam(Date date, Date operationalDate, int param) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(operationalDate);
		cal.add(Calendar.MONTH, param);
		if (date.after(cal.getTime()))
			return "success";
		else
			return "failure";
	}	

	/**
	 * 
	 *@date: Sep 04, 2012 
	 *@author: Indrasena
	 *@description: generates date in the form of 1/1/1900 hh:mm:ss a		
	 *@return
	 */
	public static Date getFormattedTime(Date date) {
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss a");
		try {
			int hour, minute, second;
			String time="";
			cal.setTime(date);
			hour = cal.get(Calendar.HOUR_OF_DAY);
			minute = cal.get(Calendar.MINUTE);
			second = cal.get(Calendar.SECOND);
			if(hour<12){
				time =hour + ":" + minute + ":" + second + " AM";
			}else if(hour>12){
				time =(hour-12) + ":" + minute + ":" + second + " PM";
			}else if(hour==12){
				time =hour + ":" + minute + ":" + second + " PM";
			}
			return dateFormat.parse("1/1/1900 " + time);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 
	 *@date: Aug 30, 2012 
	 *@author: sohan chodankar
	 *@description: gives date and time in string format		
	 *@return
	 */
	public static String getStringDateTime(Date date) {
		try{
			DateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm a");
			return simpleDateFormat.format(date).toString();
		}
		catch (Exception e) {
			return null;
		}
	}


	/**
	 *@date: Jan 28, 2016
	 *@author: Amar Dhakad
	 *@description: gives date and time in string format		
	 *@return String
	 */
	public static String getStringDateWithTime(Date date) {
		try{
			DateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHH");
			return simpleDateFormat.format(date).toString();
		}
		catch (Exception e) {
			return null;
		}
	}

	/**
	 * @date: Oct 20, 2012 
	 * @author: Shubhra Nerurkar
	 * method is used to get diff in date for bug 5598
	 * @param date
	 * @param operationalDate
	 * @return
	 */
	public static String getDiffForInstrumentDate(Date date, Date operationalDate) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(operationalDate);
		cal.add(Calendar.MONTH, -3);
		if (date.after(cal.getTime()))
			return "success";
		else
			return "failure";
	}


	/**
	 * @date : Aug 25, 2012
	 * @author Indrasena
	 * @description Returns the date difference between two Calendar dates
	 * @version $Revision$
	 * @param startDate
	 * @param finalendDate
	 * @return long
	 */

	@SuppressWarnings("unused")
	public static long getDateDifference(Calendar startDate, Calendar finalendDate) {

		long ndays = 0;
		int n;
		if (startDate.get(startDate.YEAR) < finalendDate.get(finalendDate.YEAR)) {
			if(startDate.get(startDate.YEAR)%4==0){
				ndays += (366 - startDate.get(startDate.DAY_OF_YEAR));
			}else{
				ndays += (365 - startDate.get(startDate.DAY_OF_YEAR));
			}
			for (n = startDate.get(startDate.YEAR) + 1; n <= finalendDate.get(finalendDate.YEAR) - 1; n++) {
				if(n%4==0){
					ndays += 366;
				}else{
					ndays += 365;
				}
			}
			ndays += finalendDate.get(finalendDate.DAY_OF_YEAR);
		}
		if (finalendDate.get(finalendDate.YEAR) == startDate.get(startDate.YEAR)) {
			ndays = finalendDate.get(finalendDate.DAY_OF_YEAR) - startDate.get(startDate.DAY_OF_YEAR);
		}
		return ndays;
	}
	/**
	 * @param date gives date
	 * @return formatedDate 
	 */
	public static Date getDateFromStringinDateFormat2(String date) {
		String dateString = null;
		Date convertedDate = null;
		try {
			dateString = date;
			convertedDate = dateFormat2.parse(dateString);     
		} catch (Exception e) {
			e.printStackTrace();
		}
		return convertedDate;
	}	
	public static Date getDateFromDateFormat(String date) {
		String OLD_FORMAT = "EEE MMM d HH:mm:ss z yyyy";	
		String NEW_FORMAT = "yyyy-MM-d hh:mm:ss";	
		String newDate = date.substring(0, 19);
		try {	
			SimpleDateFormat sdf = new SimpleDateFormat(OLD_FORMAT);
			Date d = (Date) sdf.parse(newDate);
			sdf.applyPattern(NEW_FORMAT);
			newDate = sdf.format(d);
			logger.error(" new : "+newDate);
		} catch (ParseException e1) {	
			e1.printStackTrace();
		}
		return DateUtil.getDateFromString(newDate.substring(0, 10),"yyyy-MM-dd");
	}
	/**
	 * @date: Aug 07, 2013 
	 * @author: Venu
	 * method is used to get diff in date for bug 8179
	 * @param date
	 * @param operationalDate
	 * @return
	 */
	public static String getDiffInstrumentDate(Date date, Date operationalDate,Long validmonths) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(operationalDate);
		cal.add(Calendar.MONTH, - validmonths.intValue());
		if (date.after(cal.getTime()))
			return "success";
		else
			return "failure";
	}

	public static String getTimeForFileGeneration(Date date){
		try
		{
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm:ss");
			String dateString = simpleDateFormat.format(date);
			return dateString;
		}
		catch (Exception e) {			
			return null;
		}
	}


	/**--To calculate Customer Age 
	 * Rushikesh K. 
	 * @param dateofBirth
	 * @param operationalDate
	 * @return
	 */
	public static int getAgeFor15H(Date dateofBirth , Date operationalDate){

		Calendar cal1 = null; 
		Calendar cal2 = null;
		int monthDiff=0;
		if(operationalDate.compareTo(dateofBirth)>0){
			try{
				cal1=Calendar.getInstance(); 
				cal2=Calendar.getInstance(); 
				cal1.setTime(dateofBirth);          
				cal2.setTime(operationalDate);
				int yearDiff  = cal2.get(Calendar.YEAR) - cal1.get(Calendar.YEAR); 
				int monthDiff1 = cal2.get(Calendar.MONTH) - cal1.get(Calendar.MONTH);
				int dayDiff = cal2.get(Calendar.DAY_OF_MONTH) - cal1.get(Calendar.DAY_OF_MONTH);
				/*if(cal2.get(Calendar.MONTH) - cal1.get(Calendar.MONTH)==0 && (cal2.get(Calendar.DAY_OF_MONTH)<=cal1.get(Calendar.DAY_OF_MONTH))){
	             monthDiff = yearDiff * 12 + cal2.get(Calendar.DAY_OF_MONTH) - cal1.get(Calendar.DAY_OF_MONTH);}
	            else{
	            	 monthDiff = yearDiff * 12 + cal2.get(Calendar.MONTH) - cal1.get(Calendar.MONTH);
	            }*/
				if(yearDiff==60)
				{
					//Modified by Muhammed Waheed on 17/04/2015 for Bug #12130
					int monthDiff2 = cal1.get(Calendar.MONTH) - cal2.get(Calendar.MONTH);
					if(monthDiff2<0){

						if(dayDiff>=0)
						{
							monthDiff = yearDiff * 12;
						}else{
							monthDiff = yearDiff * 12;
							int noofdays=cal2.get(Calendar.DAY_OF_MONTH) - cal1.get(Calendar.DAY_OF_MONTH);
							if(noofdays>=30){
								monthDiff=monthDiff+1;
							}

						}

					}
					//
					else if(monthDiff1>=0)
					{
						if(dayDiff>=0)
						{
							monthDiff = yearDiff * 12;
						}else{
							monthDiff = yearDiff * 12 + cal2.get(Calendar.DAY_OF_MONTH) - cal1.get(Calendar.DAY_OF_MONTH);
						}
					}else{
						monthDiff = yearDiff * 12 + cal2.get(Calendar.MONTH) - cal1.get(Calendar.MONTH) ;
					}
				}else if(yearDiff>60){
					monthDiff = yearDiff * 12 + cal2.get(Calendar.MONTH) - cal1.get(Calendar.MONTH);
				}else if (yearDiff<60){
					monthDiff = yearDiff * 12;
				}

				int quotientMonths = 0;
				if(monthDiff >= 12){
					quotientMonths = monthDiff / 12;
				}
				if(monthDiff < 12){
					return 1;	
				}else{
					return quotientMonths; 
				}
			}catch (Exception e) {
			}
		}
		return 0;
	} 

	/**
	 * @date: May 29, 2015 
	 * @author: Amar Dhakad
	 * @desc method is used to get age in years,month,days 
	 * @param date
	 * @param operationalDate
	 * @returnList<Object>
	 */	
	@SuppressWarnings("unused")
	public static List<Object> getAgeInYearAndMonth(Date birthDate , Date operationalDate){
		List<Object> temp=new ArrayList<Object>();
		if(operationalDate.compareTo(birthDate)>0){
			try{
				int years = 0;
				int months = 0;
				int days = 0;
				//create calendar object for birth day
				Calendar birthDay = Calendar.getInstance();
				birthDay.setTimeInMillis(birthDate.getTime());

				//create calendar object for current day
				Calendar now = Calendar.getInstance();
				now.setTimeInMillis(operationalDate.getTime());

				//Get difference between years
				years = now.get(Calendar.YEAR) - birthDay.get(Calendar.YEAR);
				int currMonth = now.get(Calendar.MONTH) + 1;
				int birthMonth = birthDay.get(Calendar.MONTH) + 1;

				//Get difference between months
				months = currMonth - birthMonth;
				//if month difference is in negative then reduce years by one and calculate the number of months.
				if (months < 0)
				{
					years--;
					months = 12 - birthMonth + currMonth;
					if (now.get(Calendar.DATE) < birthDay.get(Calendar.DATE))
						months--;
				} else if (months == 0 && now.get(Calendar.DATE) < birthDay.get(Calendar.DATE))
				{
					years--;
					months = 11;
				}

				//Calculate the days
				if (now.get(Calendar.DATE) > birthDay.get(Calendar.DATE))
					days = now.get(Calendar.DATE) - birthDay.get(Calendar.DATE);
				else if (now.get(Calendar.DATE) < birthDay.get(Calendar.DATE))
				{
					int today = now.get(Calendar.DAY_OF_MONTH);
					now.add(Calendar.MONTH, -1);
					days = now.getActualMaximum(Calendar.DAY_OF_MONTH) - birthDay.get(Calendar.DAY_OF_MONTH) + today;
				} else {
					days = 0;
					if (months == 12)
					{
						years++;
						months = 0;
					}
				}
				//add years,months,days to object 
				temp.add(years);
				temp.add(months);
				temp.add(days);

			}catch (Exception e) {
				e.printStackTrace();
			}
		}
		return temp;
	}


	/**
	 * @date: May 29, 2015 
	 * @author: Amar Dhakad
	 * @desc method is used to get Expiry date for PMSBY  
	 * @param date
	 * @param operationalDate
	 * @returnList<Object>
	 */	
	@SuppressWarnings("unused")
	public static Date getExpirydatePMSBY(Date operDat){
		SimpleDateFormat dateFormat1 = new SimpleDateFormat("dd/MM/yyyy");
		DateFormat dateFormat = new SimpleDateFormat("yyyy");
		int curyear = Integer.parseInt(dateFormat.format(operDat));
		Date expdt=null;
		try {
			expdt=dateFormat1.parse(31 + "/" + 5 + "/" + curyear);
			if (expdt.after(operDat))
			{
				return expdt;
			}else{
				expdt=dateFormat1.parse(31 + "/" + 5 + "/" + (curyear+1));
				return expdt;
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return expdt;
	}


	/**
	 * @author Mukesh Patidar
	 * @param given
	 *            date
	 * @return input Date in format (yyyyMMdd)
	 */
	public static String getCurrentDate3(Date date) {

		return sdf.format(date);//
	}

	public static String getJulianDateForCurrentDate(Date date) {
		GregorianCalendar calJ = new GregorianCalendar();
		calJ.setGregorianChange(date);
		return julianDate.format(calJ.getGregorianChange());
	}

	/**
	 * @author Mukesh Patidar
	 * @param given
	 *            date
	 * @return current Date in format (dd/MM/yyyy)
	 */
	public static Date getCurrentDate() {
		Date date = new Date();
		String dateStr = dateFormat2.format(date);
		Date date2 = null;
		try {
			date2 = dateFormat2.parse(dateStr);
		} catch (Exception pe) {
			pe.printStackTrace();
		}
		return date2;
	}

	/*
	 * Get Julian Date for the current Date in 'DDD' format
	 */
	public static String getJulianDateForCurrentDate1() {
		GregorianCalendar calJ = new GregorianCalendar();
		calJ.setGregorianChange(new Date());
		return julianDate1.format(calJ.getGregorianChange());
	}

	/**
	 * @description This method used to return the date in dd/MMM/yyyy format.
	 * @author Sudarshan Maheshwari
	 * @version $Revision$
	 * @param date
	 * @return
	 */
	public static String getDateStringFormat(Date date){
		return dateFormat.format(date);
	}
	//Added by Muhammed Waheed for Bug #13487 fix.
	public static java.sql.Date convertUtiltoSqlDate(java.util.Date date){
		java.sql.Date sqlDate = new java.sql.Date(date.getTime());
		return sqlDate;
	}
	public static java.util.Date convertSqltoUtilDate(java.sql.Date date){
		java.util.Date utildate = new java.util.Date(date.getTime());
		return utildate;
	}
	/**
	 * @description This method used to add Seconds/Minutes in time and return.
	 * @author Sudarshan Maheshwari
	 * @date 12/Jan/2016
	 * @version $Revision$
	 * @param date
	 * @param incrementVal
	 * @param dateTimeFlag
	 * @return
	 */
	public static Calendar addingDateTime(Date date, int incrementVal, String dateTimeFlag) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		if("Seconds".equalsIgnoreCase(dateTimeFlag)) {
			cal.add(Calendar.SECOND, incrementVal);
		} else if("Minutes".equalsIgnoreCase(dateTimeFlag)) {
			cal.add(Calendar.MINUTE, incrementVal);
		} else if("Hours".equalsIgnoreCase(dateTimeFlag)) {
			cal.add(Calendar.HOUR, incrementVal);
		} else if("Days".equalsIgnoreCase(dateTimeFlag)) {
			cal.add(Calendar.DAY_OF_WEEK, incrementVal);
		}
		return cal;
	}

	/**
	 * @description This method used to return the date in dd-MMM-yyyy format.
	 * @date 13/Jan/2016
	 * @author Sudarshan Maheshwari
	 * @version $Revision$
	 * @param date
	 * @return
	 */
	public static String getDateFormat_ddMMMyyyy(Date date){
		return sdf.format(date);
	}

	/**
	 * @description This method used to parse the date in dd-MMM-yyyy format and
	 *              return.
	 * @date 15/Feb/2016
	 * @author Sudarshan Maheshwari
	 * @version $Revision$
	 * @param dateStr
	 * @return
	 */
	public static Date getDateParse_ddMMMyyyy(String dateStr){
		try {
			System.out.println("getDateParse_ddMMMyyyy.date::>>>"+dateStr);
			return dateFormat2.parse(dateStr);
		} catch (ParseException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * @description This method used to join date and time.
	 * @date 17/Feb/2016
	 * @author Sudarshan Maheshwari
	 * @version $Revision$
	 * @param dateStr
	 * @return
	 */
	public static Date joinDateAndTime(Date inputDate, Date inputTime) {
		Date date = null;
		try {
			String dateString = dateFormat2.format(inputDate);
			String timeString = sdf1.format(inputTime);
			date = dateFormat.parse(dateString+" "+timeString);
		} catch (ParseException e) {
			e.printStackTrace();
		}

		return date;
	}

	public static String getStringDateDDMMYYYY(String date)
	{
		String yr=date.substring(0,4);
		String month=date.substring(4,6);
		String dd=date.substring(6,8);
		return ""+dd+"/"+month+"/"+yr;
	}
	public static String getDateDDMONYYYY(String date){
		try
		{
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
			Date convertedDate = simpleDateFormat.parse(date);  
			SimpleDateFormat dateFormat1 = new SimpleDateFormat("dd-MMM-yyyy");
			String dateStr = dateFormat1.format(convertedDate);
			return dateStr;
		}
		catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	public static String scheduleDateValidation(int days)
	{
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());
		cal.add(Calendar.DATE, days); //minus number would decrement the days
		return DateUtil.geDateForFileGeneration(cal.getTime());
	}
	public static boolean compareTwoDate(String dt1,String dt2)
	{
		Date date1 = null;
		Date date2 = null;
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
			date1 = sdf.parse(dt1);
			date2 = sdf.parse(dt2);
			if (date1.compareTo(date2) > 0) {
				return true;
			}else
				return false;

		} catch (Exception e) {
			// TODO: handle exception
			return false;

		}
	}
	public static java.util.Date getUtilDate(String date)
	{
		Date fromDate = new Date();

		SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");

		try {
			fromDate = df.parse(date);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return fromDate;

	}

	public static boolean compareTwoDateMMYY(String dt1,String dt2)
	{
		Date date1 = null;
		Date date2 = null;
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.sss");
			date1 = sdf.parse(dt1);
			SimpleDateFormat dateFormat1 = new SimpleDateFormat("yy/MM");
			String dateStr = dateFormat1.format(date1);
			date2 = sdf.parse(dt2);
			if (date1.compareTo(date2) > 0) {
				return true;
			}else
				return false;

		} catch (Exception e) {
			// TODO: handle exception
			return false;

		}
	}

	public static boolean compareODExpDate(String dt1,String dt2)
	{
		Date date1 = null;
		Date date2 = null;
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.sss");
			date1 = sdf.parse(dt1);
			date2 = sdf.parse(getcurrentODDateString());
			date1= getFormattedDateNew(date1);
			date2= getFormattedDateNew(date2);
			if (date1.compareTo(date2) > 0 || date1.compareTo(date2) == 0) {
				return true;
			}else
				return false;
		} catch (Exception e) {
			// TODO: handle exception
			return false;
		}
	}
	public static boolean compareIBnextPwdDate(String dt1,String dt2)
	{
		Date date1 = null;
		Date date2 = null;
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS");
			date1 = sdf.parse(dt1);
			date2 = sdf.parse(getcurrentODDateString());
			if (date1.compareTo(date2) > 0) {
				return true;
			}else
				return false;

		} catch (Exception e) {
			// TODO: handle exception
			return false;

		}
	}
	public static String getcurrentODDateString() {
		SimpleDateFormat dateFormat1 = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.sss");
		java.util.Date date = new java.util.Date();
		String dateStr = dateFormat1.format(date);
		System.out.println("Current date::>>"+dateStr);
		return dateStr;
	}

	public static String getDateFormat_ddMMyyyy(Date date){
		return dateFormat2.format(date);
	}
	public static Long getSINOSEQ() {
		Long lastNo=null;
		String queryString = "";
		Query queryObject = null;
		queryString = "SELECT NEXT VALUE FOR dbo.RTGS_BATCHSQNO ";
		queryObject = HBUtil.getSessionFactory().openSession().createSQLQuery(queryString);
		List list = queryObject.list();
		System.out.println("list.get(0)::>>"+list.get(0));
		if (list!=null && list.size()>0){
			lastNo = Long.valueOf(list.get(0)+"");
		}
		return lastNo;
	}
	public static List<D001011> getRtgsHolidayDetailsList(String stateCode, char rtgsNEFT, Long year, Long month, Long date) {
		long l_start = System.currentTimeMillis();
		String queryString = "from D001011 where id.stateCode='MAH' and id.calYear=? and id.calMonth=? and id.day=? and id.rtgsneft=?"; 
		Query queryObject = HBUtil.getSessionFactory().openSession().createQuery(queryString);
		queryObject.setInteger(0,Short.valueOf(year+""));
		queryObject.setByte(1, Byte.valueOf(month+""));
		queryObject.setByte(2, Byte.valueOf(date+""));
		queryObject.setCharacter(3,  rtgsNEFT);
		List<D001011> list = queryObject.list();
		long l_end = System.currentTimeMillis();
		logger.debug("Instrumentation :<InternetBankingDAOImpl.java>:<getRtgsHolidayDetailsList>: "+ (l_end - l_start));
		return list;
	}
	public static D001011 getRtgsHolidayDetailsObject(String stateCode, char rtgsNEFT, Long year, Long month, Long date) {
		long l_start = System.currentTimeMillis();
		String queryString = "from D001011 where id.stateCode='MAH' and id.calYear=? and id.calMonth=? and id.day=? and id.rtgsneft=?"; 
		Query queryObject = HBUtil.getSessionFactory().openSession().createQuery(queryString);
		queryObject.setInteger(0,Short.valueOf(year+""));
		queryObject.setByte(1, Byte.valueOf(month+""));
		queryObject.setByte(2, Byte.valueOf(date+""));
		queryObject.setCharacter(3,  rtgsNEFT);
		
		List<D001011> list = queryObject.list();
		long l_end = System.currentTimeMillis();
		logger.debug("Instrumentation :<InternetBankingDAOImpl.java>:<getRtgsHolidayDetailsList>: "+ (l_end - l_start));
		if(list.isEmpty() || list==null)
			return null;
		
		 return list.get(0);         //change BY Manish.Commit on 28-Jan-2020
	}               
	public static String getStringDate_ddMMyy(Date date) {
		try {
			DateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yy");
			return simpleDateFormat.format(date).toString();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}
	public static String getStringTime_hhmmss_a(Date date) {
		try {
			return sdf1.format(date).toString();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}
	public static boolean compareFromDateToDate(String dt1,String dt2)
	{
		Date date1 = null;
		Date date2 = null;
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
			date1 = sdf.parse(dt1);
			date2 = sdf.parse(dt2);
			if (date1.compareTo(date2) > 0) {
				return true;
			}else
				return false;

		} catch (Exception e) {
			// TODO: handle exception
			return false;

		}
	}
	public static boolean getDateDiff(Date dt1,Date dt2)
	{
		Date date1 = null;
		Date date2 = null;
		try {
			
			if (date1.compareTo(date2) > 0) {
				return true;
			}else
				return false;

		} catch (Exception e) {
			// TODO: handle exception
			return false;

		}
	}
	public static String getcurrentDateyyyyMMdd() {
		java.util.Date date = new java.util.Date();
		String dateStr = dateFormatnew.format(date);
		Date date2 = null;
		try {
			date2 = dateFormatnew.parse(dateStr);
		} catch (Exception pe) {
			pe.printStackTrace();
		}
		return dateStr;
	}
	public static String convertDateFormatIVR(Date date)
	{
		try {
			SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yy");
			return dateFormat.format(date);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}		
	}
	
	public D946020 getRtgsMessagesForRefNo(int obrcode, String iwowmsg,
			String msgstype, Date msgdate, String refno, Session session) throws SQLException {	
		//String queryString = "from RtgsMessages where id.obrcode=? and id.iwowmsg=? and id.msgdate=? and id.refno like '"+refno+"%' order by id.refno desc";
		String queryString ="";
		if (ConfigurationLoader.getParameters(false).getProperty("DATABASE").equalsIgnoreCase("ORACLE"))
			queryString = "from D946020 where id.obrcode=? and id.iwowmsg=? and id.msgstype=? and id.msgdate=? and substr(id.refno,5,5)=? order by id.refno desc";
		else
			queryString = "from D946020 where id.obrcode=? and id.iwowmsg=? and id.msgstype=? and id.msgdate=? and substring(id.refno,5,5)=? order by id.refno desc";
		Query queryObject = session.createQuery(queryString);
		queryObject.setLong(0, obrcode);
		queryObject.setString(1, iwowmsg);
		queryObject.setString(2, msgstype);
		queryObject.setDate(3, msgdate);	
		queryObject.setString(4, refno);
		List<D946020> rtgsList = queryObject.list();
		if(!rtgsList.isEmpty()){ 
			return rtgsList.get(0);
		}		
		return null;
	}
	
	public static Calendar dateToCalendar(Date date) {

		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		return calendar;

	}
	
	public static long getDateDiffrence(Date firstDate, Date secondDate) {
		long diffInMillies = Math.abs(secondDate.getTime() - firstDate.getTime());
	    long diff = TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);
	    System.out.println("Diffrences:-"+diff);
		return diff;
	}
}

/*
 * (C) Copyright  Saraswat Infotech Limited. All Rights Reserved.
 *
 * This is unpublished, proprietary, confidential source code of Saraswat Infotech Limited.
 *
 * Saraswat Infotech Limited retains all title to and intellectual property rights in these materials.
 */
/*
 * @DateUtility.java
 *
 * author             version        date        change description
 * abc                  0.1        dd/mm/yy       class created
 * 
 */


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


/**
 * 
 * 
 * 
 * 
 * 
 *
 * @version 0.1, dd/mm/yy
 */

@SuppressWarnings({ "unused", "static-access" })
public class DateUtility {
    private static SimpleDateFormat dateFormat1 = new SimpleDateFormat("dd/MMM/yyyy");
    /** 20 Jan 2014 modify by Vikas Gupta (Taking milli second for PDF Generation)   *Start*/
    private static 	SimpleDateFormat dateFormatPDF = new SimpleDateFormat("ddMMMyyyyhhmmssSSS");
    /** 20 Jan 2014 modify by Vikas Gupta (Taking milli second for PDF Generation)   *End*/
    private static SimpleDateFormat dateFormat2 = new SimpleDateFormat("dd/MM/yyyy");
    private static SimpleDateFormat dateFormat3 = new SimpleDateFormat(
	"yyyyMMdd");
    private static DateFormat julianDate = new SimpleDateFormat("yyDDD");
    private static DateFormat julianDate1 = new SimpleDateFormat("DDD");
    private static SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy");
    private static SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss a");
    private static SimpleDateFormat sdf1 = new SimpleDateFormat("hh:mm:ss a");
    private static DateFormat dateFormat4 = new SimpleDateFormat("dd-MM-yyyy");
    private static SimpleDateFormat sdf2 = new SimpleDateFormat("MMyy");
    private static SimpleDateFormat sdfYear = new SimpleDateFormat("yyyy");

    /**
	 * @return date2
	 */
    public static Date getcurrentDate() {
		java.util.Date date = new java.util.Date();
		String dateStr = dateFormat1.format(date);
		Date date2 = null;
		try {
			date2 = dateFormat1.parse(dateStr);
		} catch (Exception pe) {
			pe.printStackTrace();
		}
		return date2;
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
	
	/**
	 * @author Jayendra Agrawal
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
        convertedDate = dateFormat1.parse(dateString);     
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
			return dateFormat1.parse(dateFormat1.format(date));
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
   * @author Mandar Palekar
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
	 *@date :Mar 15, 2011 
	 *@author :tushar.dhotre
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
	 *@date: Apr 7, 2011 
	 *@author: tushar.dhotre
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
        //getMakerTime(new Date());
        //System.out.println(getFormattedTime(new Date()));
        System.out.println(formattedTime(new Date()));
        System.out.println(getPostTime(new Date()));
        
    	/*Calendar calendar = Calendar.getInstance();
    	System.out.println(calendar);
		System.out.println(calendar.get(Calendar.HOUR_OF_DAY)+""+calendar.get(Calendar.MINUTE)+""+calendar.get(Calendar.SECOND)+String.valueOf((calendar.get(Calendar.MILLISECOND))).substring(0, 2));
		System.out.println(calendar.get(Calendar.MINUTE));
		System.out.println(calendar.get(Calendar.SECOND));
		System.out.println(String.valueOf((calendar.get(Calendar.MILLISECOND))).substring(0, 2));//+" "+Calendar.MINUTE+" "+Calendar.MILLISECOND)));
*/    	//System.out.println(DateUtility.getDateFromString("20100814"));
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
	 * @author Sandesh chandane
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
			//e.printStackTrace();
			//Added by Muhammed Waheed on 18/01/2016 for Bug #13487 fix.
			try{
			if(date.length()==18 || date.length()==19){	
			Date newDate=DateUtility.getDateFromString(date.substring(0, 10),"yyyy-MM-dd");
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
	 * @author Sajid Chauhan
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
	 * @date : May 14, 2012
	 * @author : Shubhra Nerurkar
	 * @param date
	 * @return
	 */
	public static String geDateForFileGeneration(Date date){
		try
		{
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat("ddMMMyyyyhhmmss");
			String dateString = simpleDateFormat.format(date);
			return dateString;
		}
		catch (Exception e) {			
			return null;
		}
	}
	
	/**
	 * @date : July 18, 2012
	 * @author : Sachin Mandhare
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
	
	public static String getStringDateTime1(Date date) {
		try{
			DateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss a");
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
			System.out.println(" new : "+newDate);
		} catch (ParseException e1) {	
			e1.printStackTrace();
		}
		return DateUtility.getDateFromString(newDate.substring(0, 10),"yyyy-MM-dd");
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

		return dateFormat3.format(date);
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
		return dateFormat1.format(date);
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
			return sdf.parse(dateStr);
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

	/**
	 * This method used to give date and time with millisecond.
	 * 
	 * @date 20/May/2016
	 * @author Sudarshan Maheshwari
	 * @version $Revision$
	 * @param inputDate
	 * @return
	 */
	public static Date formattedTime(Date inputDate) {
		Date date = null;
		try {
		    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SSS");
			Calendar cal = Calendar.getInstance();
			int hour, minute, second, milliSecond;
			String time = "";
			cal.setTime(inputDate);
			hour = cal.get(Calendar.HOUR_OF_DAY);
			minute = cal.get(Calendar.MINUTE);
			second = cal.get(Calendar.SECOND);
			milliSecond = cal.get(Calendar.MILLISECOND);
			time = hour + ":" + minute + ":" + second + "."+milliSecond;
			date = sdf.parse("1/1/1900 " + time);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		return date;
	}

	/**
	 * This method used to give value of date and time with millisecond.
	 * 
	 * @date 20/May/2016
	 * @author Sudarshan Maheshwari
	 * @version $Revision$
	 * @param inputDate
	 * @return
	 */
	public static Long getPostTime(Date inputDate) {
	    SimpleDateFormat sdf = new SimpleDateFormat("HHmmssSSS");
	    Long postTime = 0l;
		try {
			String time = sdf.format(inputDate);
			postTime = Long.valueOf(time);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		System.out.println(postTime);
		return postTime;
	}
    
    /**
     * This method used to convert date into ddMMyyyy format.
     * 
     * @author Sudarshan Maheshwari
     * @version $Revision$
     * @param date
     * @return
     * Jun 8, 2016
     */
    public static String getDateFormat(Date date) {
		String dateString = null;
		try {
			dateString = dateFormat4.format(date);			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return dateString;
	}

	/**
	 * This method used to convert date into MMyy format.
	 * 
	 * @author Sudarshan Maheshwari
	 * @date Jun 23, 2016
	 * @version $Revision$
	 * @param date
	 * @return
	 */
	public static String getformattedDate_MMyy(Date date) {
		String formattedDateStr = null;
		try {
			formattedDateStr = sdf2.format(date);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return formattedDateStr;
	}
	
	/**
	 * This method used to convert date into MM/dd/yyyy format.
	 * 
	 * @author NITIN WAGH
	 * @date DEC 13, 2016
	 * @version $Revision$
	 * @param date
	 * @return
	 */	
	public static String getStringDateTimeForSQL(Date date) {
		try{
			DateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy");
			return simpleDateFormat.format(date).toString();
		}
		catch (Exception e) {
			return null;
		}
	}
	/**
	 * This method used to convert date into ddMMyy format.
	 * 
	 * @author Sudarshan Maheshwari
	 * @date Aug 29, 2016
	 * @version $Revision$
	 * @param date
	 * @return
	 */
	public static String getDateString_ddMMyy(Date date) {
		try {
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat("ddMMyy");
			return simpleDateFormat.format(date);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	/**
	 * This method used to convert date into MM/dd/yyyy HH:mm:ss format.
	 * 
	 * @author Sudarshan Maheshwari
	 * @date Aug 29, 2016
	 * @version $Revision$
	 * @param date
	 * @return
	 */
	public static String getStringDateTime_MMddyyyyHHmmss(Date date) {
		try{
			DateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
			return simpleDateFormat.format(date).toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	public static Calendar getCalendar(String year) {
		Date date;
		try {
			date = sdfYear.parse(year);
			Calendar cal = Calendar.getInstance();
			cal.setTime(date);
			return cal;
		} catch (ParseException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * This method used to convert date into dd-MM-yy format.
	 * 
	 * @author Sudarshan Maheshwari
	 * @date Dec 2, 2016
	 * @version $Revision$
	 * @param date
	 * @return
	 */
	public static String getStringDate_ddMMyy(Date date) {
		try {
			DateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yy");
			return simpleDateFormat.format(date).toString();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}
	
	/**
	 * This method used to convert time into hh:mm:ss a format.
	 * 
	 * @author Sudarshan Maheshwari
	 * @date Dec 2, 2016
	 * @version $Revision$
	 * @param date
	 * @return
	 */
	public static String getStringTime_hhmmss_a(Date date) {
		try {
			return sdf1.format(date).toString();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}
	
	public static Integer getJulianDay(String date) {

		Integer julianDay = null;
		String dateArr[] = date.split("-");
		int year = Integer.parseInt(dateArr[2]);
		int month = Integer.parseInt(dateArr[1]) - 1;
		int day = Integer.parseInt(dateArr[0]);
		GregorianCalendar gc = new GregorianCalendar();
		gc.set(GregorianCalendar.DAY_OF_MONTH, day);
		gc.set(GregorianCalendar.MONTH, month);
		gc.set(GregorianCalendar.YEAR, year);
		julianDay = gc.get(GregorianCalendar.DAY_OF_YEAR);
		String jdate= dateArr[2].substring(2,4)+julianDay.toString();
		return Integer.parseInt(jdate);
	
	}

	public static String getDateFromDateAsString(Date date, String format) throws ParseException {
		SimpleDateFormat dateFormat = new SimpleDateFormat(format);
		return dateFormat.format(date);
	}
}
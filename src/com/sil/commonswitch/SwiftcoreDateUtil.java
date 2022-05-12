package com.sil.commonswitch;
import java.util.*;

import com.sil.util.DateUtil;
public class SwiftcoreDateUtil {
	
    public static long getDateDiff(Calendar startDateOri, Calendar endDateOri){
        int monthUnit = 31;
        int dateDiff = 0;
        Long dateDiff2 = 0l;
        int count = 0;
        int increment = 0;
        int total = 0;
        
        Calendar startDate = Calendar.getInstance();
        Calendar endDate = Calendar.getInstance();
        
        startDate.setTime(startDateOri.getTime());
        endDate.setTime(endDateOri.getTime());  
        
        startDate.set(Calendar.HOUR_OF_DAY, 0);
        startDate.set(Calendar.MINUTE, 0);
        startDate.set(Calendar.SECOND, 0);
        startDate.set(Calendar.MILLISECOND, 0);

        endDate.set(Calendar.HOUR_OF_DAY, 0);
        endDate.set(Calendar.MINUTE, 0);
        endDate.set(Calendar.SECOND, 0);
        endDate.set(Calendar.MILLISECOND, 0);
        
        Calendar startDate1 = Calendar.getInstance();       
        Calendar startDatePlusMonth = Calendar.getInstance();
        startDatePlusMonth.setTime(startDate.getTime());
        
        int startDay = startDate.get(Calendar.DAY_OF_MONTH);
        int startMonth = startDate.get(Calendar.MONTH);
        int startYear = startDate.get(Calendar.YEAR);
        
        
        int endDay = endDate.get(Calendar.DAY_OF_MONTH);
        int endMonth = endDate.get(Calendar.MONTH);
        int endYear = endDate.get(Calendar.YEAR);
        
        if(startDate.compareTo(endDate)==0){
            return 1;
        }else if(startDate.compareTo(endDate)>0){
            return 0;
        }/*else if(startDate.compareTo(endDate)<0 && startDate.get(Calendar.MONTH)==Calendar.FEBRUARY ){
            Calendar startDateOriginal = Calendar.getInstance();
            startDateOriginal.setTime(startDate.getTime());
            startDate.set(startDate.get(Calendar.YEAR), Calendar.MARCH, 4);
            dateDiff2 = DateUtility.getDateDiff(startDateOriginal, startDate);
            total = total + (dateDiff2.intValue()-1);
            startDay = startDate.get(Calendar.DAY_OF_MONTH);
            
            while(startDate.before(endDate)){
                if((startDate.get(Calendar.MONTH)==endDate.get(Calendar.MONTH))&& ((startDate.get(Calendar.YEAR)) == (endDate.get(Calendar.YEAR)))){
                    dateDiff = endDate.get(Calendar.DAY_OF_MONTH)-startDay;
                    dateDiff = dateDiff + 1;
                }else{
                    dateDiff = startDate.getActualMaximum(Calendar.DAY_OF_MONTH)-startDay;
                    dateDiff = dateDiff + 1;
                }
                System.out.println("DATE: "+startDate.getTime()+"\n");
                total = total + dateDiff;
                System.out.println("DAYS TILL: "+startDate.getActualMaximum(Calendar.DAY_OF_MONTH)+" "+total+"\n");
                startDate.set(startDate.get(Calendar.YEAR), startDate.get(Calendar.MONTH), startDate.getActualMaximum(Calendar.DAY_OF_MONTH));
                startDate.add(Calendar.DAY_OF_MONTH, 1);
                if(startDate.get(Calendar.MONTH)==Calendar.FEBRUARY){
                    total = total + 30;
                    startDate.set(startDate.get(Calendar.YEAR), Calendar.MARCH, 4);
                }
                startDay = startDate.get(Calendar.DAY_OF_MONTH);
                increment = increment + 1;
            }
            return total;
        }*/
        else if(startDate.compareTo(endDate)<0){
            /*if(startMonth==endMonth && startYear==endYear){
                return (endDay - startDay)+1;
            }else { */          
                if (endYear-startYear > 1) {
                    count = endYear-startYear-1;
                    startDate1.set(startDate.get(Calendar.YEAR) + count, startDate.get(Calendar.MONTH), startDate.get(Calendar.DAY_OF_MONTH));
                    total = Integer.parseInt(DateUtil.getDateDiff(startDate,startDate1)+"");
                    if(startDate.get(Calendar.DAY_OF_MONTH)==29 && (((startDate.get(Calendar.YEAR))+count)%4 != 0) && startDate.get(Calendar.MONTH)==Calendar.FEBRUARY){
                        startDate.set(startDate.get(Calendar.YEAR) + count, startDate.get(Calendar.MONTH)+1, 1);
                        startDay = startDate.get(Calendar.DAY_OF_MONTH);
                    }else{
                        startDate.set(startDate.get(Calendar.YEAR) + count, startDate.get(Calendar.MONTH), startDate.get(Calendar.DAY_OF_MONTH));
                    }
                }

                    while(startDate.compareTo(endDate)<=0){
                        if((startDate.get(Calendar.MONTH)==endDate.get(Calendar.MONTH)) && (startDate.get(Calendar.YEAR)==endDate.get(Calendar.YEAR))){
                            dateDiff = endDate.get(Calendar.DAY_OF_MONTH)-startDay;
                            dateDiff = dateDiff + 1;
                        }else{
                            dateDiff = startDate.getActualMaximum(Calendar.DAY_OF_MONTH)-startDay;
                            dateDiff = dateDiff + 1;
                        }
						//System.out.println("DATE: "+startDate.getTime()+"\n");
                        total = total + dateDiff;
						//System.out.println("DAYS TILL: "+startDate.getActualMaximum(Calendar.DAY_OF_MONTH)+" "+total+"\n");
                        long temp = DateUtil.getDateDiff(startDate, endDate);
                        startDate.set(startDate.get(Calendar.YEAR), startDate.get(Calendar.MONTH), startDate.getActualMaximum(Calendar.DAY_OF_MONTH));
                        startDate.add(Calendar.DAY_OF_MONTH, 1);
/*                      if(startDate.get(Calendar.MONTH)==Calendar.FEBRUARY){
                            if(startDate.get(Calendar.MONTH)==endDate.get(Calendar.MONTH)){
                                total = total + (endDate.get(Calendar.DAY_OF_YEAR)-startDate.get(Calendar.DAY_OF_YEAR));
                            }else{
                                if(startDate.get(Calendar.YEAR)%4==0){
                                    total = total + 31;
                                }else{
                                    total = total + 30;
                                }
                            }                           
                            System.out.println("DATE : "+startDate.getTime()+"\n");
                            System.out.println("DAYS TILL: "+startDate.getActualMaximum(Calendar.DAY_OF_MONTH)+" "+total+"\n");
                            startDate.set(startDate.get(Calendar.YEAR), Calendar.MARCH, 4);
                        }*/
                        startDay = startDate.get(Calendar.DAY_OF_MONTH);
                        increment = increment + 1;
                    }
            
                return total;
            
        }
        return 0;
    }

	public  static void main(String args[]) {
		Calendar start = Calendar.getInstance();
		Calendar end = Calendar.getInstance();
		long diff = 0l;
		start.set(2011,10,05);
		end.set(2012,9,16);
		diff = getDateDiff(start,end);
		java.util.Date date = new java.util.Date();
		DateUtil.getDateFormat_ddMMyyyy(date);
		System.out.println("date--: "+DateUtil.getDateFormat_ddMMyyyy(date));
		System.out.println("DIFFERENCE: "+diff);
	}
	
	public static Calendar getFinancialYearStartDateByOperationDate(Calendar operDate) {
		Calendar startDate = Calendar.getInstance();
		if((operDate.get(Calendar.MONTH)== Calendar.JANUARY) || (operDate.get(Calendar.MONTH)== Calendar.FEBRUARY) || (operDate.get(Calendar.MONTH)== Calendar.MARCH)){
			startDate.set(operDate.get(Calendar.YEAR)-1, Calendar.APRIL, 1);
		}else{
			startDate.set(operDate.get(Calendar.YEAR), Calendar.APRIL, 1);
		}
		return startDate;
	}

	
	public static Calendar getFinancialYearEndDateByOperationDate(Calendar operDate) {
		Calendar endDate = Calendar.getInstance();
		if((operDate.get(Calendar.MONTH)== Calendar.JANUARY) || (operDate.get(Calendar.MONTH)== Calendar.FEBRUARY) || (operDate.get(Calendar.MONTH)== Calendar.MARCH)){
			endDate.set(operDate.get(Calendar.YEAR), Calendar.MARCH, 31);
		}else{
			endDate.set(operDate.get(Calendar.YEAR)+1, Calendar.MARCH, 31);
		}
		return endDate;
	}
	
	public static long getDateDiffForTDSProjection(Calendar startDateOri, Calendar endDateOri){

		int monthUnit = 31;
		int dateDiff = 0;
		Long dateDiff2 = 0l;
		int count = 0;
		int increment = 0;
		int total = 0;
		
		Calendar startDate = Calendar.getInstance();
		Calendar endDate = Calendar.getInstance();
		
		startDate.setTime(startDateOri.getTime());
		endDate.setTime(endDateOri.getTime());
		
		startDate.set(Calendar.HOUR_OF_DAY, 0);
		startDate.set(Calendar.MINUTE, 0);
		startDate.set(Calendar.SECOND, 0);
		startDate.set(Calendar.MILLISECOND, 0);

		endDate.set(Calendar.HOUR_OF_DAY, 0);
		endDate.set(Calendar.MINUTE, 0);
		endDate.set(Calendar.SECOND, 0);
		endDate.set(Calendar.MILLISECOND, 0);
		
		Calendar startDate1 = Calendar.getInstance();		
		Calendar startDatePlusMonth = Calendar.getInstance();
		startDatePlusMonth.setTime(startDate.getTime());
		
		int startDay = startDate.get(Calendar.DAY_OF_MONTH);
		int startMonth = startDate.get(Calendar.MONTH);
		int startYear = startDate.get(Calendar.YEAR);
		
		
		int endDay = endDate.get(Calendar.DAY_OF_MONTH);
		int endMonth = endDate.get(Calendar.MONTH);
		int endYear = endDate.get(Calendar.YEAR);
		
		if(startDate.compareTo(endDate)==0){
			return 0;
		}else if(startDate.compareTo(endDate)>0){
			return 0;
		}/*else if(startDate.compareTo(endDate)<0 && startDate.get(Calendar.MONTH)==Calendar.FEBRUARY ){
			Calendar startDateOriginal = Calendar.getInstance();
			startDateOriginal.setTime(startDate.getTime());
			startDate.set(startDate.get(Calendar.YEAR), Calendar.MARCH, 4);
			dateDiff2 = DateUtility.getDateDiff(startDateOriginal, startDate);
			total = total + (dateDiff2.intValue()-1);
			startDay = startDate.get(Calendar.DAY_OF_MONTH);
			
			while(startDate.before(endDate)){
				if((startDate.get(Calendar.MONTH)==endDate.get(Calendar.MONTH))&& ((startDate.get(Calendar.YEAR)) == (endDate.get(Calendar.YEAR)))){
					dateDiff = endDate.get(Calendar.DAY_OF_MONTH)-startDay;
					dateDiff = dateDiff + 1;
				}else{
					dateDiff = startDate.getActualMaximum(Calendar.DAY_OF_MONTH)-startDay;
					dateDiff = dateDiff + 1;
				}
				System.out.println("DATE: "+startDate.getTime()+"\n");
				total = total + dateDiff;
				System.out.println("DAYS TILL: "+startDate.getActualMaximum(Calendar.DAY_OF_MONTH)+" "+total+"\n");
				startDate.set(startDate.get(Calendar.YEAR), startDate.get(Calendar.MONTH), startDate.getActualMaximum(Calendar.DAY_OF_MONTH));
				startDate.add(Calendar.DAY_OF_MONTH, 1);
				if(startDate.get(Calendar.MONTH)==Calendar.FEBRUARY){
					total = total + 30;
					startDate.set(startDate.get(Calendar.YEAR), Calendar.MARCH, 4);
				}
				startDay = startDate.get(Calendar.DAY_OF_MONTH);
				increment = increment + 1;
			}
			return total;
		}*/
		else if(startDate.compareTo(endDate)<0){
/*			if(startMonth==endMonth && startYear==endYear){
				return (endDay - startDay);
			}else {	*/			
				if (endYear-startYear > 1) {
					count = endYear-startYear-1;
					startDate1.set(startDate.get(Calendar.YEAR) + count, startDate.get(Calendar.MONTH), startDate.get(Calendar.DAY_OF_MONTH));
					total = Integer.parseInt(DateUtil.getDateDiff(startDate,startDate1)+"");
					if(startDate.get(Calendar.DAY_OF_MONTH)==29 && (((startDate.get(Calendar.YEAR))+count)%4 != 0) && startDate.get(Calendar.MONTH)==Calendar.FEBRUARY){
						startDate.set(startDate.get(Calendar.YEAR) + count, startDate.get(Calendar.MONTH)+1, 1);
						startDay = startDate.get(Calendar.DAY_OF_MONTH);
					}else{
						startDate.set(startDate.get(Calendar.YEAR) + count, startDate.get(Calendar.MONTH), startDate.get(Calendar.DAY_OF_MONTH));
					}
				}

					while(startDate.compareTo(endDate)<=0){
						if((startDate.get(Calendar.MONTH)==endDate.get(Calendar.MONTH)) && (startDate.get(Calendar.YEAR)==endDate.get(Calendar.YEAR))){
							dateDiff = endDate.get(Calendar.DAY_OF_MONTH)-startDay;
							dateDiff = dateDiff + 1;
						}else{
							dateDiff = startDate.getActualMaximum(Calendar.DAY_OF_MONTH)-startDay;
							dateDiff = dateDiff + 1;
						}
						total = total + dateDiff;
						long temp = DateUtil.getDateDiff(startDate, endDate);
						startDate.set(startDate.get(Calendar.YEAR), startDate.get(Calendar.MONTH), startDate.getActualMaximum(Calendar.DAY_OF_MONTH));
						startDate.add(Calendar.DAY_OF_MONTH, 1);
						startDay = startDate.get(Calendar.DAY_OF_MONTH);
						increment = increment + 1;
					}
				return total;
		}
 		return 0;
	
	}

	public static Calendar clearMilliSeconds(Calendar startDate) {
		startDate.set(Calendar.HOUR_OF_DAY, 0);
		startDate.set(Calendar.MINUTE, 0);
		startDate.set(Calendar.SECOND, 0);
		startDate.set(Calendar.MILLISECOND, 0);
		return startDate;
	}

}
package com.sil.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.Transaction;

import com.sil.commonswitch.DataUtils;
import com.sil.commonswitch.SwiftcoreDateUtil;
import com.sil.constants.SwiftCoreConstants;
import com.sil.dao.TermDepositReceiptsDao;
import com.sil.hbm.CentrelisedBrwiseCustTDSFile;
import com.sil.hbm.D009021;
import com.sil.hbm.D009500;
import com.sil.hbm.D009500Id;
import com.sil.hbm.D010014;
import com.sil.hbm.D010014Id;
import com.sil.hbm.D020002;
import com.sil.hbm.D020004;
import com.sil.hbm.D020118;
import com.sil.hbm.PeriodWiseOffset;
import com.sil.hbm.TDSMaintananceForTD;
import com.sil.util.Customer;
import com.sil.util.DateUtility;
import com.sil.util.HBUtil;

public class TermDepositeServiceImpl {

	public static Logger logger=Logger.getLogger(TermDepositeServiceImpl.class);
	
	public static DecimalFormat twoDecPlaces = new DecimalFormat("#.##");
	public static DecimalFormat fourDecPlaces = new DecimalFormat("#.####");
	public static DecimalFormat sixDecPlaces = new DecimalFormat("0.000000");
	
	public static Double calculateInterestForTDProducts(D020004 termDepositReceipts,D020002 tdParamForeachReceipt, Date startDate1, Date endDate1,long l,Double unitWdAmount,boolean isMaturing,boolean isUnitWD, Long totalDaysInMat, String activityType) {
		Calendar asOfdate = Calendar.getInstance();
		Calendar endDate = Calendar.getInstance();
		Calendar monthEndDate = Calendar.getInstance();
		Calendar certStartDate = Calendar.getInstance();
		Calendar periodEndDate = Calendar.getInstance();
		Calendar originalPeriodEndate = Calendar.getInstance();
		Calendar originalAsoffDate = Calendar.getInstance();
		Calendar matDate = Calendar.getInstance();
		asOfdate.setTime(termDepositReceipts.getAsOffdate());
		asOfdate = SwiftcoreDateUtil.clearMilliSeconds(asOfdate);
		originalAsoffDate.setTime(termDepositReceipts.getAsOffdate());
		matDate.setTime(termDepositReceipts.getMatDate());
		certStartDate.setTime(termDepositReceipts.getAsOffdate());
		if(l==1L){
			originalPeriodEndate.setTime(endDate1);
			endDate.setTime(startDate1);
			endDate.add(Calendar.DAY_OF_MONTH, -1);
			periodEndDate.setTime(startDate1);
			periodEndDate.add(Calendar.DAY_OF_MONTH, -1);
			periodEndDate = SwiftcoreDateUtil.clearMilliSeconds(periodEndDate);			
		}else if(l==3L){
			asOfdate.setTime(termDepositReceipts.getAsOffdate());
			originalPeriodEndate.setTime(endDate1);
			endDate.setTime(endDate1);
			periodEndDate.setTime(endDate1);
			periodEndDate.clear(Calendar.MILLISECOND);
			periodEndDate = SwiftcoreDateUtil.clearMilliSeconds(periodEndDate);		
		}else if(l==5L){
			asOfdate.setTime(termDepositReceipts.getAsOffdate());
			originalPeriodEndate.setTime(endDate1);
			endDate.setTime(endDate1);
			periodEndDate.setTime(endDate1);
			periodEndDate.clear(Calendar.MILLISECOND);
			periodEndDate = SwiftcoreDateUtil.clearMilliSeconds(periodEndDate);
			periodEndDate.add(Calendar.DAY_OF_MONTH, -1);
		}else{
			originalPeriodEndate.setTime(endDate1);
			endDate.setTime(endDate1);
			periodEndDate.setTime(endDate1);
			periodEndDate.clear(Calendar.MILLISECOND);
			periodEndDate = SwiftcoreDateUtil.clearMilliSeconds(periodEndDate);			
			if(l==2L){
				asOfdate.setTime(startDate1);
				asOfdate = SwiftcoreDateUtil.clearMilliSeconds(asOfdate);
				if(isUnitWD){
					periodEndDate.add(Calendar.DAY_OF_MONTH, -1);
				}				
			}if(l==4l){
				asOfdate.setTime(startDate1);
				asOfdate = SwiftcoreDateUtil.clearMilliSeconds(asOfdate);
			}
		}
		Double tdInterestProvided = 0.0;
		Double tdDayInterest = 0.0;
		//boolean hasMatured = false;
		//boolean isHoliday = false;
		Long monthDiff = 0L;
		Long noOfMonths = 0L;
		Long dateDiff = 0L;
		Long totalDaysDiff = 0l;
		//BigDecimal tdFMsum = new BigDecimal(0.0);
		Double tdFQSum = 0.0;
		Double tdQuarterlyInterestKD = 0.0;
		Long remainderRemainingMonths = 0l;
		Long numberOfQuartersFQ = 0l;
		//totalDaysInMat = SwiftcoreDateUtil.getDateDiff(originalAsoffDate, matDate);
		//totalDaysInMat = totalDaysInMat - 1;
		/*******************************************************************************************
		 * the variable totalDaysDiff is maintained to find if the interest must be calculated in
		 * days only
		 ******************************************************************************************/		
		asOfdate.set(Calendar.HOUR_OF_DAY, 0);
		asOfdate.set(Calendar.MINUTE, 0);
		asOfdate.set(Calendar.SECOND, 0);
		asOfdate.set(Calendar.MILLISECOND, 0);

		periodEndDate.set(Calendar.HOUR_OF_DAY, 0);
		periodEndDate.set(Calendar.MINUTE, 0);
		periodEndDate.set(Calendar.SECOND, 0);
		periodEndDate.set(Calendar.MILLISECOND, 0);		
		totalDaysDiff = SwiftcoreDateUtil.getDateDiff(asOfdate, periodEndDate);
		if(isUnitWD){
			totalDaysInMat = totalDaysDiff;
		}
		if((l==1L) && asOfdate.compareTo(periodEndDate)==0){
			totalDaysDiff = totalDaysDiff + 1;
		}
		Double mainbalfcy = 0.0;
		if(termDepositReceipts.getMainBalFcy()==0.0){
			if((l==3L) || (l==4L)){
				mainbalfcy = unitWdAmount;
			}else if(l==1L){
				mainbalfcy = unitWdAmount;
			}else{
				mainbalfcy = termDepositReceipts.getInstOrPrincAmt();
			}			
		}else{
			if((l==4L) || (l==1L)){
				mainbalfcy = unitWdAmount;
			}else{
				mainbalfcy = termDepositReceipts.getMainBalFcy();
			}			
		}
		
		if(l==2L){
			mainbalfcy = unitWdAmount;
		}
		if((l==3L) || (l==4L)){
			l=1L;
		}
		Double tdQuarterlyInterest = 0.0;
		Double periodicInterest = 0.0;
		Double intRate = 0.0;
		BigDecimal periodInterest = new BigDecimal(0.0);
		//ITermDepositReceiptsService receiptsService = (ITermDepositReceiptsService) ServiceFinder.findBean("termDepositReceiptsService");
		if (tdParamForeachReceipt.getIntPaidYn()=='Y') {
			if (tdParamForeachReceipt.getIntFreq()=='Y' && tdParamForeachReceipt.getCumIntYn()=='Y') {
				/***********************************************************************************
				 * Finding the month end of the as of date for calculating the broken days.
				 **********************************************************************************/
				monthEndDate.set(asOfdate.get(Calendar.YEAR), asOfdate.get(Calendar.MONTH), asOfdate.getActualMaximum(Calendar.DAY_OF_MONTH), 0, 0, 0);
				monthEndDate.clear(Calendar.MILLISECOND);
				if (mainbalfcy != 0.0) {
					Double singleMonthInt = 0.0;
					/***********************************************************
					 * To check if the receipt expires before the interest
					 * calculation run date
					 **********************************************************/
					if(mainbalfcy!=0.0){
						if(tdParamForeachReceipt.getClIntCalcType()==6){
							periodicInterest = (termDepositReceipts.getInstOrPrincAmt() * (termDepositReceipts.getIntrateCalc())) / 1200;
							periodInterest = new BigDecimal(periodicInterest).setScale(2, RoundingMode.HALF_UP);
							singleMonthInt = periodInterest.doubleValue();
						}else{
							periodicInterest = (termDepositReceipts.getInstOrPrincAmt() * (termDepositReceipts.getIntrateCalc())) / ((termDepositReceipts.getIntrateCalc()) + 1200);
							periodInterest = new BigDecimal(periodicInterest).setScale(2, RoundingMode.HALF_UP);
							singleMonthInt = periodInterest.doubleValue();
						}
						if (totalDaysInMat < (tdParamForeachReceipt.getShortTermDays() + 1)) {
							tdQuarterlyInterest = (singleMonthInt * (totalDaysDiff)) / 30;
							tdFQSum = tdQuarterlyInterest;
						}else{
								if(l==1){
									if (asOfdate.get(Calendar.DAY_OF_MONTH) == 1) {
										dateDiff = 0L;
										monthDiff = SwiftcoreDateUtil.getDateDiffForTDSProjection(asOfdate, periodEndDate);												
									} else {
										dateDiff = SwiftcoreDateUtil.getDateDiffForTDSProjection(asOfdate, monthEndDate);
										monthDiff = SwiftcoreDateUtil.getDateDiffForTDSProjection(monthEndDate, periodEndDate);
									}							
									noOfMonths = monthDiff / 30;									
									tdQuarterlyInterest = singleMonthInt * noOfMonths;
									if (tdParamForeachReceipt.getClIntCalcType() == 7) {
										tdDayInterest = (singleMonthInt * (dateDiff)) / 30;
									} else {
										tdDayInterest = (mainbalfcy * (termDepositReceipts.getIntrateCalc()) * (dateDiff)) / 36500;
									}			
									tdFQSum = tdQuarterlyInterest + tdDayInterest;									
								}else{
									/*******************************************************
									 * if the asofdate is the start of the quarter and the
									 * quarter end date is the run date we only consider the
									 * complete quarter and not the broken days. Else we
									 * consider both.
									 ******************************************************/
									noOfMonths = totalDaysDiff / 30;
									asOfdate.add(Calendar.MONTH, noOfMonths.intValue());
									asOfdate.add(Calendar.DAY_OF_MONTH, -1);
									while(asOfdate.compareTo(endDate)>0){
										asOfdate.setTime(termDepositReceipts.getAsOffdate());
										noOfMonths = noOfMonths - 1;
										asOfdate.add(Calendar.MONTH, noOfMonths
												.intValue());
									}
									asOfdate.setTime(termDepositReceipts.getAsOffdate());									
									tdQuarterlyInterest = periodicInterest * noOfMonths;
									asOfdate.add(Calendar.MONTH, noOfMonths.intValue());
									//asOfdate.add(Calendar.DAY_OF_MONTH, -1);
									if(asOfdate.compareTo(periodEndDate)>0){
										dateDiff = 0L;
									}else{
										dateDiff = SwiftcoreDateUtil.getDateDiff(asOfdate, periodEndDate);
									}
									/*if(asOfdate.compareTo(periodEndDate)==0){
										dateDiff = dateDiff + 1;
									}*/
									if (tdParamForeachReceipt.getClIntCalcType() == 7) {
										tdDayInterest = (singleMonthInt * (dateDiff)) / 30;
									} else {
										tdDayInterest = (mainbalfcy * (termDepositReceipts.getIntrateCalc()) * (dateDiff)) / 36500;
									}			
									tdFQSum = tdQuarterlyInterest + tdDayInterest;
								}
							}												
							/*******************************************************
							 * Update the Interest Provided LCY and FCY amounts in
							 * the termDepositReceipts table
							 ******************************************************/
							BigDecimal bigInterest = new BigDecimal(tdFQSum).setScale(2, RoundingMode.HALF_UP);
							tdInterestProvided = bigInterest.doubleValue();						
					}else{
						tdInterestProvided = 0.0;
					}
				} else {
					tdInterestProvided = 0.0;
				}

				/***********************************************************************************
				 * Interest calculated for a month is then multipled to the number of completed
				 * months
				 **********************************************************************************/
				if (tdInterestProvided < 0) {
					tdInterestProvided = 0.0;
				}
			}else if(tdParamForeachReceipt.getIntFreq()=='Y' && tdParamForeachReceipt.getCumIntYn()=='Y'){
				Double singleQuarterInt = 0.0;
				/***********************************************************
				 * To check if the receipt expires before the interest
				 * calculation run date
				 **********************************************************/
				if(mainbalfcy!=0.0){
						if(totalDaysInMat<(tdParamForeachReceipt.getShortTermDays()+1)){
							if(isUnitWD){
								intRate = getInterestRateAsPerPeriod(termDepositReceipts,tdParamForeachReceipt,asOfdate,originalPeriodEndate,activityType);
								tdQuarterlyInterest = (((mainbalfcy * (intRate)) * (totalDaysDiff)) / 36500);	
							}else{
								tdQuarterlyInterest = (mainbalfcy * (termDepositReceipts.getIntrateCalc()) * (totalDaysDiff))/36500;
							}
							tdInterestProvided = Math.round(tdQuarterlyInterest)+0.0;
						}else{
							if(l==1L){
								Calendar quarterStartDate = Calendar.getInstance();
								Calendar quarterEndDate = Calendar.getInstance();
								singleQuarterInt = ((mainbalfcy * (termDepositReceipts.getIntrateCalc())) / (1200 / 3));
								if (asOfdate.get(Calendar.MONTH) == Calendar.JANUARY || asOfdate.get(Calendar.MONTH) == Calendar.FEBRUARY || asOfdate.get(Calendar.MONTH) == Calendar.MARCH) {
									quarterEndDate.set(asOfdate.get(Calendar.YEAR), Calendar.MARCH, 31);
									quarterStartDate.set(asOfdate.get(Calendar.YEAR), Calendar.JANUARY, 1);
								} else if (asOfdate.get(Calendar.MONTH) == Calendar.APRIL || asOfdate.get(Calendar.MONTH) == Calendar.MAY || asOfdate.get(Calendar.MONTH) == Calendar.JUNE) {
									quarterEndDate.set(asOfdate.get(Calendar.YEAR), Calendar.JUNE, 30);
									quarterStartDate.set(asOfdate.get(Calendar.YEAR), Calendar.APRIL, 1);
								} else if (asOfdate.get(Calendar.MONTH) == Calendar.JULY || asOfdate.get(Calendar.MONTH) == Calendar.AUGUST || asOfdate.get(Calendar.MONTH) == Calendar.SEPTEMBER) {
									quarterEndDate.set(asOfdate.get(Calendar.YEAR), Calendar.SEPTEMBER, 30);
									quarterStartDate.set(asOfdate.get(Calendar.YEAR), Calendar.JULY, 1);
								} else {
									quarterEndDate.set(asOfdate.get(Calendar.YEAR), Calendar.DECEMBER, 31);
									quarterStartDate.set(asOfdate.get(Calendar.YEAR), Calendar.OCTOBER, 1);
								}
								if (DateUtility.getFormattedDate(asOfdate.getTime()).compareTo(DateUtility.getFormattedDate(quarterStartDate.getTime())) == 0) {
									monthDiff = SwiftcoreDateUtil.getDateDiffForTDSProjection(asOfdate, periodEndDate);
									dateDiff = 0l;												
								} else if(DateUtility.getFormattedDate(asOfdate.getTime()).compareTo(DateUtility.getFormattedDate(quarterEndDate.getTime())) == 0){
									quarterEndDate.add(Calendar.DAY_OF_MONTH, 1);
									monthDiff = SwiftcoreDateUtil.getDateDiffForTDSProjection(quarterEndDate, periodEndDate);
									dateDiff = 1L;
								}else {
									dateDiff = SwiftcoreDateUtil.getDateDiffForTDSProjection(asOfdate, quarterEndDate);
									monthDiff = SwiftcoreDateUtil.getDateDiffForTDSProjection(quarterEndDate, periodEndDate);
								}
								noOfMonths = (monthDiff + 1) / 30;
								asOfdate.add(Calendar.MONTH, noOfMonths.intValue());
								asOfdate.add(Calendar.DAY_OF_MONTH, -1);
								while(asOfdate.compareTo(periodEndDate)>0){
									asOfdate.setTime(termDepositReceipts.getAsOffdate());
									noOfMonths = noOfMonths - 1;
									asOfdate.add(Calendar.MONTH, noOfMonths
											.intValue());
								}
								asOfdate.setTime(termDepositReceipts.getAsOffdate());
								remainderRemainingMonths = noOfMonths % 3;
								if (remainderRemainingMonths > 0) {
									noOfMonths = noOfMonths - remainderRemainingMonths;
								}
								numberOfQuartersFQ = noOfMonths / 3;
								tdQuarterlyInterest = singleQuarterInt.doubleValue() * numberOfQuartersFQ;			
								tdDayInterest = (((mainbalfcy * (termDepositReceipts.getIntrateCalc())) * (dateDiff)) / 36500);
								tdFQSum = tdQuarterlyInterest + tdDayInterest;							
							} else {
							/***********************************************************************
							 * if the asofdate is the start of the quarter and the quarter end date
							 * is the run date we only consider the complete quarter and not the
							 * broken days. Else we consider both.
							 **********************************************************************/
							noOfMonths = totalDaysDiff / 30;
							asOfdate.add(Calendar.MONTH, noOfMonths.intValue());
							asOfdate.add(Calendar.DAY_OF_MONTH, -1);
							while (asOfdate.compareTo(endDate) > 0) {
								asOfdate.setTime(termDepositReceipts.getAsOffdate());
								noOfMonths = noOfMonths - 1;
								asOfdate.add(Calendar.MONTH, noOfMonths.intValue());
							}
							if (l == 2L) {
								asOfdate.setTime(startDate1);
							} else {
								asOfdate.setTime(termDepositReceipts.getAsOffdate());
							}
							remainderRemainingMonths = noOfMonths % 3;
							if (remainderRemainingMonths > 0) {
								noOfMonths = noOfMonths - remainderRemainingMonths;
							}
							numberOfQuartersFQ = noOfMonths / 3;
							singleQuarterInt = ((mainbalfcy * (termDepositReceipts.getIntrateCalc())) / (1200 / 3));
							asOfdate.add(Calendar.MONTH, noOfMonths.intValue());							
							tdQuarterlyInterest = numberOfQuartersFQ * singleQuarterInt;
							if (asOfdate.compareTo(periodEndDate) > 0) {
								dateDiff = 0L;
							} else {
								dateDiff = SwiftcoreDateUtil.getDateDiff(asOfdate, periodEndDate);
							}
							if (isUnitWD) {
								intRate = getInterestRateAsPerPeriod(termDepositReceipts, tdParamForeachReceipt, asOfdate, originalPeriodEndate, activityType);
								tdDayInterest = (((mainbalfcy * (intRate)) * (dateDiff)) / 36500);
							} else {
								tdDayInterest = (((mainbalfcy * (termDepositReceipts.getIntrateCalc())) * (dateDiff)) / 36500);
							}							
							tdFQSum = tdQuarterlyInterest + tdDayInterest;
						}												
							/***********************************************************************
							 * Update the Interest Provided LCY and FCY amounts in the
							 * termDepositReceipts table
							 **********************************************************************/
							tdInterestProvided = Math.round(tdFQSum)+0.0;						
						}							
				}else{
					tdInterestProvided = 0.0;
				}
			
			}else if(tdParamForeachReceipt.getIntFreq()=='H' && tdParamForeachReceipt.getCumIntYn()=='N'){
				Double singleQuarterInt = 0.0;
				/***********************************************************
				 * To check if the receipt expires before the interest
				 * calculation run date
				 **********************************************************/
				if(mainbalfcy!=0.0){
						if(totalDaysInMat<(tdParamForeachReceipt.getShortTermDays()+1)){
							tdQuarterlyInterest = (mainbalfcy * termDepositReceipts.getIntrateCalc() * (totalDaysDiff))/36500;
							tdFQSum = tdQuarterlyInterest;
						}else{							
							/*******************************************************
							 * if the asofdate is the start of the quarter and the
							 * quarter end date is the run date we only consider the
							 * complete quarter and not the broken days. Else we
							 * consider both.
							 ******************************************************/
							noOfMonths = totalDaysDiff / 30;
							asOfdate.add(Calendar.MONTH, noOfMonths.intValue());
							asOfdate.add(Calendar.DAY_OF_MONTH, -1);
							while(asOfdate.compareTo(endDate)>0){
								asOfdate.setTime(termDepositReceipts.getAsOffdate());
								noOfMonths = noOfMonths - 1;
								asOfdate.add(Calendar.MONTH, noOfMonths
										.intValue());
							}
							asOfdate.setTime(termDepositReceipts.getAsOffdate());
							remainderRemainingMonths = noOfMonths % 3;
							if (remainderRemainingMonths > 0) {
								noOfMonths = noOfMonths - remainderRemainingMonths;
							}
							numberOfQuartersFQ = noOfMonths / 3;
							singleQuarterInt = ((mainbalfcy * termDepositReceipts.getIntrateCalc()) / (1200 / 3));
							asOfdate.add(Calendar.MONTH, noOfMonths.intValue());
							//asOfdate.add(Calendar.DAY_OF_MONTH, -1);
							//Long roundedQuartlerlyInt = Math.round(singleQuarterInt);
							tdQuarterlyInterest = numberOfQuartersFQ * singleQuarterInt;
							if(asOfdate.compareTo(periodEndDate)>0){
								dateDiff = 0L;
							}else{
								dateDiff = SwiftcoreDateUtil.getDateDiff(asOfdate, periodEndDate);
							}							
							tdDayInterest = (((mainbalfcy * termDepositReceipts.getIntrateCalc()) * (dateDiff)) / 36500);							
							tdFQSum = tdQuarterlyInterest + tdDayInterest;
						}												
						/*******************************************************
						 * Update the Interest Provided LCY and FCY amounts in
						 * the termDepositReceipts table
						 ******************************************************/
						tdInterestProvided = Math.round(tdFQSum)+0.0;
					
				}else{
					tdInterestProvided = 0.0;
				}			
			}
		}else {
			/*******************************************************************
			 * if the interest is calculated on principal amount, it must be a
			 * KD product
			 ******************************************************************/
			if(tdParamForeachReceipt.getInstOrPrinc()=='P'){
				Double tdsEffect = 0.0;
				if (tdParamForeachReceipt.getIntFreq()=='Q' && tdParamForeachReceipt.getCumIntYn()=='Y') {
					if(mainbalfcy!=0.0){
						if (termDepositReceipts.getMatDate().before(certStartDate.getTime()) && termDepositReceipts.getMatDate().before(periodEndDate.getTime())) {
							tdInterestProvided = 0.0;
							//hasMatured = true;
							tdFQSum = 0.0;
						} else {
							if(totalDaysInMat<(tdParamForeachReceipt.getShortTermDays()+1)){
								tdQuarterlyInterest = (mainbalfcy * (termDepositReceipts.getIntrateCalc()) * (totalDaysDiff))/36500;
								tdFQSum = tdQuarterlyInterest;
							}else{
								Double power = 0.0;
								int divisor = 0;
								long daysDivisor = 0L;
								noOfMonths = (totalDaysDiff)/30;
								asOfdate.add(Calendar.MONTH, noOfMonths.intValue());
								asOfdate.add(Calendar.DAY_OF_MONTH, -1);
								while(asOfdate.after(endDate)){
									asOfdate.setTime(termDepositReceipts.getAsOffdate());
									noOfMonths = noOfMonths - 1;
									asOfdate.add(Calendar.MONTH, noOfMonths
											.intValue());
									asOfdate.add(Calendar.DAY_OF_MONTH, -1);
								}
								asOfdate.setTime(termDepositReceipts.getAsOffdate());
								if(tdParamForeachReceipt.getIntFreq()=='Q'){
									remainderRemainingMonths = noOfMonths % 3;
									if (remainderRemainingMonths > 0) {
										noOfMonths = noOfMonths - remainderRemainingMonths;
									}
									numberOfQuartersFQ = noOfMonths / 3;
									divisor = 400;
									daysDivisor = 36500;
									Double baseValue = 100.00;
									/** new addition * */
									//Double originalBaseValue = 100.0;
									/** new addition * */
									for (int i = 1; i <= numberOfQuartersFQ.intValue(); i++) {
										power = (1 + ((termDepositReceipts.getIntrateCalc()) / divisor));
										tdQuarterlyInterest = baseValue * power;
										baseValue = tdQuarterlyInterest;
									}

									tdQuarterlyInterestKD = (Double.valueOf(fourDecPlaces.format(baseValue))) * mainbalfcy / 100;
									tdQuarterlyInterestKD = tdQuarterlyInterestKD - mainbalfcy;
									asOfdate.add(Calendar.MONTH, noOfMonths.intValue());
									if((asOfdate.get(Calendar.MONTH)==Calendar.FEBRUARY)){
										if(originalAsoffDate.get(Calendar.DAY_OF_MONTH)==originalAsoffDate.getActualMaximum(Calendar.DAY_OF_MONTH)){
											asOfdate.set(Calendar.DAY_OF_MONTH, asOfdate.getActualMaximum(Calendar.DAY_OF_MONTH));
										}
									}
									/*if((asOfdate.get(Calendar.MONTH)==Calendar.FEBRUARY) && (asOfdate.get(Calendar.DAY_OF_MONTH)==asOfdate.getActualMaximum(Calendar.DAY_OF_MONTH))){
										asOfdate.add(Calendar.DAY_OF_MONTH, 1);
									}*/
									if(asOfdate.compareTo(periodEndDate)>0){
										dateDiff = 0L;
									}else{
										dateDiff = SwiftcoreDateUtil.getDateDiff(asOfdate, periodEndDate);
									}
									tdDayInterest = (((baseValue * mainbalfcy * (termDepositReceipts.getIntrateCalc()) / 100) * (dateDiff)) / daysDivisor);
									/** new addition* */
									tdFQSum = Math.round((tdQuarterlyInterestKD + Double.valueOf(fourDecPlaces.format(tdDayInterest)))) + 0.0;
								}else{
									Long numberOf180DaySlots = (totalDaysDiff)/180;
									asOfdate.add(Calendar.DAY_OF_MONTH, (numberOf180DaySlots.intValue() * 180));
									dateDiff = SwiftcoreDateUtil.getDateDiff(asOfdate, periodEndDate);
									//ITermDepositReceiptsService receiptsService = (ITermDepositReceiptsService) context.getBean("termDepositReceiptsService");
									tdFQSum = calculateInterestForNRKDProducts(numberOf180DaySlots,dateDiff,termDepositReceipts,tdParamForeachReceipt,mainbalfcy);
								}
								
							}
							tdsEffect = calculateTDSeffect(termDepositReceipts.getId().getLbrCode(), termDepositReceipts, tdInterestProvided, termDepositReceipts.getAsOffdate(), endDate1, tdParamForeachReceipt);
							if ((tdsEffect != null && tdsEffect != 0) && tdFQSum > 0) {
								tdInterestProvided = tdFQSum - tdsEffect;
							}else{
								tdInterestProvided = tdFQSum.doubleValue();
							}
						}					
					}else{
						tdInterestProvided = 0.0;
					}
				}else if(tdParamForeachReceipt.getIntFreq()=='H' && tdParamForeachReceipt.getCumIntYn()=='N'){
					Double singleQuarterInt = 0.0;
					/***********************************************************
					 * To check if the receipt expires before the interest
					 * calculation run date
					 **********************************************************/
					if(mainbalfcy!=0.0){
							if(totalDaysInMat<(tdParamForeachReceipt.getShortTermDays()+1)){
								tdQuarterlyInterest = (mainbalfcy * termDepositReceipts.getIntrateCalc() * (totalDaysDiff))/36500;
								tdFQSum = tdQuarterlyInterest;
							}else{							
								/*******************************************************
								 * if the asofdate is the start of the quarter and the
								 * quarter end date is the run date we only consider the
								 * complete quarter and not the broken days. Else we
								 * consider both.
								 ******************************************************/
								noOfMonths = totalDaysDiff / 30;
								asOfdate.add(Calendar.MONTH, noOfMonths.intValue());
								asOfdate.add(Calendar.DAY_OF_MONTH, -1);
								while(asOfdate.compareTo(endDate)>0){
									asOfdate.setTime(termDepositReceipts.getAsOffdate());
									noOfMonths = noOfMonths - 1;
									asOfdate.add(Calendar.MONTH, noOfMonths
											.intValue());
								}
								asOfdate.setTime(termDepositReceipts.getAsOffdate());
								remainderRemainingMonths = noOfMonths % 3;
								if (remainderRemainingMonths > 0) {
									noOfMonths = noOfMonths - remainderRemainingMonths;
								}
								numberOfQuartersFQ = noOfMonths / 3;
								singleQuarterInt = ((mainbalfcy * termDepositReceipts.getIntrateCalc()) / (1200 / 3));
								asOfdate.add(Calendar.MONTH, noOfMonths.intValue());
								//asOfdate.add(Calendar.DAY_OF_MONTH, -1);
								//Long roundedQuartlerlyInt = Math.round(singleQuarterInt);
								tdQuarterlyInterest = numberOfQuartersFQ * singleQuarterInt;
								if(asOfdate.compareTo(periodEndDate)>0){
									dateDiff = 0L;
								}else{
									dateDiff = SwiftcoreDateUtil.getDateDiff(asOfdate, periodEndDate);
								}							
								tdDayInterest = (((mainbalfcy * termDepositReceipts.getIntrateCalc()) * (dateDiff)) / 36500);							
								tdFQSum = tdQuarterlyInterest + tdDayInterest;
							}												
							/*******************************************************
							 * Update the Interest Provided LCY and FCY amounts in
							 * the termDepositReceipts table
							 ******************************************************/
							tdInterestProvided = Math.round(tdFQSum)+0.0;
						
					}else{
						tdInterestProvided = 0.0;
					}
				}
			}else{
				endDate.add(Calendar.DAY_OF_MONTH, 1);				
				if (mainbalfcy != 0.0) {
					if (totalDaysInMat < (tdParamForeachReceipt.getShortTermDays() + 1)) {
						Double interestRD = (termDepositReceipts.getInstOrPrincAmt() * termDepositReceipts.getIntrateCalc() * totalDaysInMat) / 36500;
						Long interestForDays = Math.round(Double.valueOf(twoDecPlaces.format(interestRD)));
						tdInterestProvided = Double.valueOf(interestForDays);
					} else {
						Double rateOfInterest = 0.0;
						//Double interestEarnedAmt = 0.0;
						Double e0;// factor based on the delphi code
						Double e1;// factor based on the delphi code
						Double e2;
						Double e3 = 0.0;
						Double e4 = 0.0;
						Double e5 = 0.0;
						Double e6 = 0.0;
						Double e7 = 0.0;
						Double maturityAmount = 0.0;
						Double interestOnE0 = 0.0;
						Long noOfQuarters = 0L;
						Long noOfQuartersForInstall = 0L;
						Long noOfQuartersForCalculation = 0l;
						Long remainderInstall = 0l;
						Long remainderRemainingQuarters = 0l;
						Long remainingMonthsForRemainingInstall = 0L;
						Double mainBal = new Double(Double.valueOf(twoDecPlaces.format(termDepositReceipts.getMainBalFcy())));
						if(termDepositReceipts.getMainBalFcy()==0.0){
							mainBal = termDepositReceipts.getInstOrPrincAmt();
						}
						Double installAmount = new Double(Double.valueOf(twoDecPlaces.format(termDepositReceipts.getInstOrPrincAmt())));
						Double installAmt = termDepositReceipts.getInstOrPrincAmt();
						Double InstPaid = Double.valueOf(twoDecPlaces.format(mainBal / installAmount));// mainbalance/installment
																										// amount
						Long numberOfInstPaid = InstPaid.longValue();
						//Long excessInstall = 0l;
						Long originalNoOfinstall = numberOfInstPaid;
						/*******************************************************
						 * adding the offset to the receipt rate of interest
						 ******************************************************/
						rateOfInterest = (termDepositReceipts.getIntRate() + termDepositReceipts.getOffSetRate());
						noOfMonths = totalDaysDiff / 30;
						asOfdate.add(Calendar.MONTH, noOfMonths.intValue());
						while (asOfdate.compareTo(endDate) > 0) {
							asOfdate.setTime(termDepositReceipts.getAsOffdate());
							noOfMonths = noOfMonths - 1;
							asOfdate.add(Calendar.MONTH, noOfMonths.intValue());
						}
						dateDiff = SwiftcoreDateUtil.getDateDiff(asOfdate, periodEndDate);
						if(asOfdate.compareTo(periodEndDate)==0){
							dateDiff = dateDiff + 1;
						}
						if (numberOfInstPaid > noOfMonths) {
							//excessInstall = numberOfInstPaid - noOfMonths;
							numberOfInstPaid = noOfMonths;
						}
						noOfQuartersForInstall = numberOfInstPaid / 3;
						noOfQuarters = noOfMonths / 3;
						if(noOfMonths<originalNoOfinstall){
							noOfQuartersForCalculation = noOfQuarters;
							if(noOfQuarters>0){
								remainderRemainingMonths = noOfMonths - (noOfQuarters * 3);
								remainderInstall = originalNoOfinstall - noOfMonths;
							}else{
								remainderRemainingMonths = 0l;
								remainderInstall = originalNoOfinstall - (noOfQuartersForCalculation * 3);
							}
						}else{
							noOfQuartersForCalculation = noOfQuartersForInstall;
							if(noOfQuarters>0){
								remainderRemainingMonths = noOfMonths - (noOfQuarters * 3);
								remainderRemainingQuarters = noOfQuarters - noOfQuartersForInstall;
								remainingMonthsForRemainingInstall = noOfMonths - (noOfQuartersForInstall * 3);
								remainderInstall = originalNoOfinstall - (noOfQuartersForInstall * 3);
								//remainingQuartersForRemainingInstall = remainingMonthsForRemainingInstall / 3;
								//remainderRemainingMonthsForRemainingInstall = remainingMonthsForRemainingInstall % 3;
							}else{
								remainderRemainingMonths = 0l;
								remainderInstall = originalNoOfinstall - (noOfQuartersForCalculation * 3);
							}
						}
						e0 = 0.0;
						e1 = 0.0;
						e2 = 0.0;
						if(noOfQuartersForCalculation !=0){
							e0 = (5 * rateOfInterest * 4) / 1200;
							e0 = e0 + ((5 * rateOfInterest * 2) / (1200 + rateOfInterest));
							e1 = e0;
							/***********************************************************************************
							 * code to calculate the interest for completed quarters
							 **********************************************************************************/
							for (int j = 1; j <= noOfQuartersForCalculation.intValue() - 1; j++) {
								e1 = e1 + e0 + (((((3 * 5 * j) + e1) * rateOfInterest * 3) / 1200));
							}
							e0 = (termDepositReceipts.getInstOrPrincAmt() / 5) * ((5 * noOfQuartersForCalculation * 3) + e1);
						}
						//interestEarnedAmt = e0;
						asOfdate.add(Calendar.MONTH, noOfMonths.intValue());
						/*******************************************************
						 * code to calculate interest for broken months in incomplete quarters
						 ******************************************************/
						if(noOfMonths>=originalNoOfinstall){
							if (remainderInstall > 0) {
								if (remainderInstall == 1) {
									if((remainingMonthsForRemainingInstall>0) && ((remainingMonthsForRemainingInstall % 3)==0)){
										Long noOfnewQuarters = remainingMonthsForRemainingInstall/3;
										e2 = new Double(e0 * Math.pow((1 + (rateOfInterest / 400)), noOfnewQuarters));
										e2 = e2 + ((e2 * rateOfInterest * dateDiff) / 36500);
										e3 = new Double(installAmt * Math.pow((1 + (rateOfInterest / 400)), noOfnewQuarters));
										e2 = e2 + e3;
									}else if((remainingMonthsForRemainingInstall>0) && ((remainingMonthsForRemainingInstall % 3)!=0)){
										Long noOfnewQuarters = remainingMonthsForRemainingInstall/3;
										Long remainderNewMonths = remainingMonthsForRemainingInstall%3;
										e1 = new Double(e0 * Math.pow((1 + (rateOfInterest / 400)), noOfnewQuarters));
										e2 = ((e1 * rateOfInterest/1200) * remainderNewMonths);
										e1 = e1 + ((e1 * rateOfInterest * dateDiff) / 36500);
										for(int k=0;k<remainderInstall;k++){
											e4 = new Double(installAmt * Math.pow((1 + (rateOfInterest / 400)), noOfnewQuarters));
											e5 = ((e4 * rateOfInterest/1200) * (remainderNewMonths-k)); //+ ((e7 * rateOfInterest/1200) * (remainderNewMonths-k));											
											if(e0==0.0){
												e4 = e4 + ((e4 * rateOfInterest * dateDiff) / 36500);
											}
											e6 = e6 + e4 + e5;
										}
										e2 = e1 + e2 + e6;													
									}						
									e0 = e2;	
								} else {
									if((remainingMonthsForRemainingInstall>0) && ((remainingMonthsForRemainingInstall % 3)==0)){
										Long noOfnewQuarters = remainingMonthsForRemainingInstall/3;
										Long remainderNewMonths = 0l;
										e2 = new Double(e0 * Math.pow((1 + (rateOfInterest / 400)), noOfnewQuarters));
										e2 = e2 + ((e2 * rateOfInterest * dateDiff) / 36500);
										for(int k=0;k<remainderInstall;k++){
											noOfnewQuarters = remainingMonthsForRemainingInstall/3;
											remainderNewMonths = remainingMonthsForRemainingInstall%3;
										e3 = new Double(installAmt * Math.pow((1 + (rateOfInterest / 400)), noOfnewQuarters));
											e4 = (e3 * rateOfInterest/1200) * (remainderNewMonths);
											remainingMonthsForRemainingInstall = remainingMonthsForRemainingInstall -1;
											e6 = e6 + e3 + e4;
										}
										e2 = e2 + e6;
									}else if((remainingMonthsForRemainingInstall>0) && ((remainingMonthsForRemainingInstall % 3)!=0)){
										Long noOfnewQuarters = remainingMonthsForRemainingInstall/3;
										Long remainderNewMonths = remainingMonthsForRemainingInstall%3;
										e7 = new Double(e0 * Math.pow((1 + (rateOfInterest / 400)), noOfnewQuarters));
										e2 = e7 + ((e7 * rateOfInterest * dateDiff) / 36500);
										
										for(int k=0;k<remainderInstall;k++){
											e4 = new Double(installAmt * Math.pow((1 + (rateOfInterest / 400)), noOfnewQuarters));
											e5 = ((e4 * rateOfInterest/1200) * (remainderNewMonths-k)); //+ ((e7 * rateOfInterest/1200) * (remainderNewMonths-k));											
											if(e0==0.0){
												e4 = e4 + ((e4 * rateOfInterest * dateDiff) / 36500);
											}
											e6 = e6 + e4 + e5;
										}
										e2 = e2 + e6 + ((e7 * rateOfInterest/1200) * (remainderNewMonths));
									}
									e0 = e2;
								}
							} else {
								if (remainderRemainingQuarters > 0) {
									e0 = e0 * Math.pow((1 + (rateOfInterest / 400)), remainderRemainingQuarters);
								}
								if (remainderRemainingMonths > 0) {
									e0 = e0 + (e0 * rateOfInterest * remainderRemainingMonths / 1200) + ((e0 * rateOfInterest * dateDiff) / 36500);
								} else {
									e0 = e0 + ((e0 * rateOfInterest * dateDiff) / 36500);
								}
							}						
						}else{
							if (remainderRemainingMonths > 0) {
								interestOnE0 = ((e0 * rateOfInterest / 1200)) * remainderRemainingMonths;
								e0 = e0 + interestOnE0;
								for (int m = 1; m <= remainderRemainingMonths; m++) {
								    Double interestOnInstall = 0.0;
								    interestOnInstall = ((installAmt * rateOfInterest)/1200)* m;
								    e0 = e0 + installAmt + interestOnInstall;		
								}
								e0 = new Double(Math.round(e0));
							}
							/***********************************************************************************
							 * need to change this to handle excess installments...TO DO
							 **********************************************************************************/
							if (remainderInstall > 0) {
								for (int k = 1; k <= remainderInstall.intValue(); k++) {
									e3 = e3 + installAmt;
								}
								if(e0>0){
									e0 = e0 + ((maturityAmount * rateOfInterest * (dateDiff)) / 36500);
									e0 = e0 + e3;
								}else{					
									e0 = e0 + ((installAmt * rateOfInterest * (dateDiff)) / 36500);
									e0 = e0 + e3;												
								}
							} else {
								e0 = e0 + ((installAmt * rateOfInterest * (dateDiff)) / 36500);
							}
						}
						if(termDepositReceipts.getMainBalFcy()==0.0){
							tdInterestProvided = e0 - mainBal;
						}else{
							tdInterestProvided = e0 - termDepositReceipts.getMainBalFcy();
						}						
						tdInterestProvided = Double.valueOf(Math.round(tdInterestProvided));
					}
				}			
			}
		}
		return tdInterestProvided;
	}
	
	public static Double getInterestRateAsPerPeriod(D020004 termDepositReceipts, D020002 tdParamForeachReceipt, Calendar asOfdate, Calendar periodEndDate,String activityType) {
		Long days = 0L;
		D020118 tdProdInt = new D020118();
		Double intRate = 0.0;
		days = SwiftcoreDateUtil.getDateDiff(asOfdate, periodEndDate);
		if(days>=tdParamForeachReceipt.getMinIntDays()){
			tdProdInt = selectIntRate(tdParamForeachReceipt.getId().getPrdCd(),0L,days,asOfdate.getTime(),termDepositReceipts.getCurCd(),periodEndDate.getTime(),asOfdate.getTime());
		}
		if(tdProdInt !=null){
			if(activityType.equals(SwiftCoreConstants.TDCLOSE)){
				intRate = tdProdInt.getIntRate() - tdProdInt.getPenalIntRate();
			}else{
				intRate = tdProdInt.getIntRate();
			}
			return intRate;
		}else{
			return 0.0;
		}		
	}
	
	public static D020118 selectIntRate(String prdcd, Long months, Long days, Date asoffdate, String curcd,Date matDate,Date interestDate) {
		try {
			int i=0;
			List<D020118> newList=new ArrayList<D020118>();
			newList= selectIntRate(prdcd, months, days, interestDate, curcd);
			if(newList!=null){
				for(D020118 intTable : newList){
				//for (Iterator iterator = newList.iterator(); iterator.hasNext();) {
					//TDProductIntTable intTable = (TDProductIntTable) iterator.next();

					Calendar cal = Calendar.getInstance();					 
					cal.setTime(asoffdate);
					cal.add(Calendar.MONTH, intTable.getMonths());
					cal.add(Calendar.DAY_OF_MONTH, (intTable.getDays()));
					intTable.setToMatDate(cal.getTime());
					if(matDate.compareTo(intTable.getToMatDate())==0){
						return intTable;
					}
					if(matDate.before(intTable.getToMatDate())){
						if(i==0){
							return null;// by sandesh 30 dec for int not comming correct
						}else{
							return newList.get(i-1);
						}

					}
					++i;
					if(newList.size()==i){
						if(matDate.after(intTable.getToMatDate())){
							return intTable;
						}
					}


				}
			}


		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
		}
		return null;
	}
	
	public static List<D020118> selectIntRate(String prdcd, Long months, Long days, Date asoffdate, String curcd)
			throws SQLException {
		        long l_start = System.currentTimeMillis();
		        List<D020118> list=new ArrayList<D020118>();
		        Session session = HBUtil.getSessionFactory().openSession();
		    	Transaction tx = session.beginTransaction();
				String queryString = "from D020118 where id.prdCd=? and id.curCd=? and id.intEffDt in ((select max(id.intEffDt) from TDProdWiseInterest where id.prdCd=? and id.intEffDt<=?)) Order By id.prdCd Asc, id.curCd Asc, id.intEffDt Asc";//modified on 22/10/2016
				list = session.createQuery(queryString).setParameter(0, prdcd.trim()).setParameter(1, curcd.trim())
						.setParameter(2, prdcd.trim()).setParameter(3, asoffdate).getResultList();
		        // queryObject.setLong(4, noofdays);
		        
				if(list!=null && list.size()!=0)
				{
					tx.commit();
					session.close();
		            return list;
				}else{
		            return null;
		        }
		    }
	
	public static Double calculateInterestForNRKDProducts(Long numberOf180DaySlots, Long dateDiff, D020004 termDepositReceipts, D020002 tdParamForeachReceipt,Double mainbalfcy) {
		Double matValforNRKDProduct = 0.0;
		Double brokenDayInterest = 0.0;
		Double mainBal = new Double(mainbalfcy);
		Double dayInterestfor180 = 0.0;
		BigDecimal roundedInterestfor180days = new BigDecimal(0.0);
		BigDecimal roundedInterestforBrokenDays = new BigDecimal(0.0);
		for(int i=1;i<=numberOf180DaySlots.intValue();i++){
			dayInterestfor180 = (mainBal * termDepositReceipts.getIntrateCalc() * 180)/36000;
			roundedInterestfor180days = new BigDecimal(dayInterestfor180).setScale(2, RoundingMode.HALF_UP);
			mainBal = mainBal + roundedInterestfor180days.doubleValue();
		}
		if(dateDiff>0){
			brokenDayInterest = (mainBal * termDepositReceipts.getIntrateCalc() * dateDiff)/36000;
			roundedInterestforBrokenDays = new BigDecimal(brokenDayInterest).setScale(2, RoundingMode.HALF_UP);
		}
		matValforNRKDProduct = mainBal + roundedInterestforBrokenDays.doubleValue();
		matValforNRKDProduct = matValforNRKDProduct - mainbalfcy;
		return matValforNRKDProduct;
	}
	
	private static Double calculateTDSeffect(int lbrcode,D020004 termDepositReceipts, Double tdInterestProvided,Date asoffdate, Date dateEnddate, D020002 tdParamForeachReceipt) {
		Double interestLessTDS = 0.0;
		Calendar asOfdate = Calendar.getInstance();
		Calendar lastDebitdate = Calendar.getInstance();
		Calendar periodEndDate = Calendar.getInstance();
		Long dateDiff = 0L;
		Long monthDiff = 0L;
		Long numberOfQuarters = 0L;
		Double tdQuarterlyInterest = 0.0;
		Double tdQuarterlyInterestTrAmt = 0.0;
		Double tdQuarterlyInterestBaseTDS = 0.0;
		Double tdDayInterest = 0.0;
		Double sumOldTDS = 0.0;
		Double sumNewTDS = 0.0;
		Double tdSum = 0.0;
		Long noOfMonths = 0L;
		Double power = 0.0;
		Long remainderRemainingMonths = 0l;
		boolean isMaturing = false;
		if (termDepositReceipts.getMatDate().compareTo(dateEnddate) <= 0) {
			periodEndDate.setTime(termDepositReceipts.getMatDate());
			periodEndDate.add(Calendar.DAY_OF_MONTH, -1);
			isMaturing = true;
		} else {
			periodEndDate.setTime(dateEnddate);
			periodEndDate.add(Calendar.DAY_OF_MONTH, 1);
		}
		try {
			termDepositReceipts.setIntrateCalc(termDepositReceipts.getIntRate() + termDepositReceipts.getOffSetRate());
			List<TDSMaintananceForTD> listOfTDSDeductedDebit = new ArrayList<TDSMaintananceForTD>();
			//List<TermDepositTDSMaintananceProgram> listOfTDSDeductedCredit = new ArrayList<TermDepositTDSMaintananceProgram>();
			listOfTDSDeductedDebit = TermDepositReceiptsDao.getAllTDSDeducted(lbrcode, termDepositReceipts.getId().getPrdAcctId(), termDepositReceipts.getAsOffdate(), periodEndDate.getTime());
			//listOfTDSDeductedCredit = termDepositReceiptsDao.getAllTDSDeductedCredit(lbrcode, termDepositReceipts.getPrdacctid(), termDepositReceipts.getAsoffdate(), periodEndDate.getTime());
			if (listOfTDSDeductedDebit.size() == 0) {
				interestLessTDS = 0.0;
			} else {
				//Date entryDate = new Date();
				//Date valueDate = new Date();
					for (TDSMaintananceForTD maintananceProgram : listOfTDSDeductedDebit) {
						if (maintananceProgram.getId().getBatchCd().equals(SwiftCoreConstants.INTEREST_TRANSFER)) {
							tdQuarterlyInterestTrAmt = maintananceProgram.getTrnAmount();
							//entryDate = maintananceProgram.getId().getEntryDate();
							sumOldTDS = sumOldTDS + maintananceProgram.getTrnAmount();
							asOfdate.setTime(maintananceProgram.getValueDate());
							lastDebitdate.setTime(maintananceProgram.getValueDate());
							//valueDate = maintananceProgram.getValueDate();
						} else {
							tdQuarterlyInterestTrAmt = maintananceProgram.getTrnAmount();
							//entryDate = maintananceProgram.getId().getEntryDate();
							sumOldTDS = sumOldTDS - maintananceProgram.getTrnAmount();
							asOfdate.setTime(maintananceProgram.getValueDate());
							lastDebitdate.setTime(maintananceProgram.getValueDate());
							//valueDate = maintananceProgram.getValueDate();
						}

						if (tdQuarterlyInterestTrAmt > 0) {
							monthDiff = SwiftcoreDateUtil.getDateDiff(asOfdate, periodEndDate);
							if(!isMaturing){
								monthDiff = monthDiff-1;
							}							
							noOfMonths = (monthDiff + 1) / 30;
							remainderRemainingMonths = noOfMonths % 3;
							if (remainderRemainingMonths > 0) {
								noOfMonths = noOfMonths - remainderRemainingMonths;
							}
							numberOfQuarters = noOfMonths / 3;
							if (numberOfQuarters > 0 && monthDiff >= tdParamForeachReceipt.getShortTermDays() + 1) {
								Double baseValue = 100.00;
								/** new addition * */
								Double originalBaseValue = 100.0;
								/** new addition * */
								for (int i = 1; i <= numberOfQuarters; i++) {
									power = (1 + (termDepositReceipts.getIntrateCalc() / 400));
									tdQuarterlyInterest = baseValue * power;
									baseValue = tdQuarterlyInterest;
								}
								baseValue = Double.valueOf(fourDecPlaces.format(tdQuarterlyInterest));
								tdQuarterlyInterestBaseTDS = (baseValue - originalBaseValue) * tdQuarterlyInterestTrAmt / 100;								
									asOfdate.add(Calendar.MONTH, noOfMonths.intValue());									
									asOfdate.add(Calendar.DAY_OF_MONTH, 1);
									dateDiff = SwiftcoreDateUtil.getDateDiff(asOfdate, periodEndDate);
									tdDayInterest = (((baseValue * tdQuarterlyInterestTrAmt * termDepositReceipts.getIntrateCalc() / 100) * (dateDiff)) / 36500);
								
							} else {
								tdQuarterlyInterestBaseTDS = 0.0;
								tdDayInterest = (((tdQuarterlyInterestTrAmt * termDepositReceipts.getIntrateCalc()) * (monthDiff)) / 36500);
							}

							tdSum = tdQuarterlyInterestBaseTDS + Double.valueOf(fourDecPlaces.format(tdDayInterest));
							if (maintananceProgram.getCashFlow().equals(SwiftCoreConstants.TDTDSDR)) {
								sumNewTDS = sumNewTDS + Math.round(tdSum);
							} else {
								sumNewTDS = sumNewTDS - Math.round(tdSum);
							}
							System.out.println("ABAL amt: " + tdQuarterlyInterestTrAmt + "End Result: " + Math.round(tdSum));
							tdQuarterlyInterestTrAmt = 0.0;
						} else {
							sumNewTDS = sumNewTDS + 0.0;
						}
					}
				interestLessTDS = sumNewTDS;
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return interestLessTDS;
	}	
	
	
	public static String getIntOffsetRateForTD(Date matDate, String crAccountType, Long noOfMonths, Long noOfDays, 
			Long drLbrCode, Date drOperationDate, String curCd, String drProductNo, Long acctType) {
		Calendar asOfDateCal = Calendar.getInstance();
		Calendar opDateCal = Calendar.getInstance();
		Calendar matDateCal = Calendar.getInstance();
		asOfDateCal.setTime(drOperationDate);
		opDateCal.setTime(drOperationDate);
		matDateCal.setTime(DateUtility.convertDateFormat(matDate));
		D020118 productIntTable = selectIntRate(drProductNo, noOfMonths, noOfDays,
				drOperationDate, curCd, DateUtility.convertDateFormat(matDate), drOperationDate);
		Double intRate = 0d;
		Double clIntRate = 0d;
		if (productIntTable != null) {
			intRate = productIntTable.getIntRate();
			clIntRate = productIntTable.getIntRate();
		} else {
			logger.error("Record no found in TDProductIntTable.");
			return "Record no found in TDProductIntTable.";
		}
		
		PeriodWiseOffset periodWiseOffset = selectPeriodOffset(drLbrCode, crAccountType,
				drOperationDate, 1l, DateUtility.convertDateFormat(matDate),
				acctType);
		Double offSetRate = 0d;
		if (periodWiseOffset != null) {
			offSetRate = periodWiseOffset.getOffSetIntRate();
			clIntRate = intRate + periodWiseOffset.getOffSetIntRate();
		}
		
		return intRate+"~"+offSetRate+"~"+clIntRate;
	}
	
	
	
	
	
	public static PeriodWiseOffset selectPeriodOffset(Long lbrcode,String prdcd,Date asoffdate, Long pricAmt,Date matDate,Long accType) {
		try {
			int i=0;
			int j=0;
			List<PeriodWiseOffset> offsetList=new ArrayList<PeriodWiseOffset>();
			offsetList= getPeriodOffsetList(lbrcode, prdcd, asoffdate, pricAmt, matDate, accType);
			if(offsetList!=null){
				for (PeriodWiseOffset periodWiseOffset : offsetList){
				/*for (Iterator iterator = offsetList.iterator(); iterator.hasNext();) {
					PeriodWiseOffset periodWiseOffset = (PeriodWiseOffset) iterator.next();
*/
					Calendar cal = Calendar.getInstance();
					Calendar cal1 = Calendar.getInstance();
					cal.setTime(asoffdate);
					cal.add(Calendar.MONTH, periodWiseOffset.getMonths());
					cal.add(Calendar.DAY_OF_MONTH, (periodWiseOffset.getDays()));
					periodWiseOffset.setToMatDate(cal.getTime());

					if(i<(offsetList.size()-1)){
						cal1.setTime(asoffdate);
						cal1.add(Calendar.MONTH, offsetList.get(i+1).getMonths());
						cal1.add(Calendar.DAY_OF_MONTH, (offsetList.get(i+1).getDays()));
						offsetList.get(i+1).setToMatDate(cal1.getTime());
					}					

					if(matDate.compareTo(periodWiseOffset.getToMatDate())==0 ||
							matDate.after(periodWiseOffset.getToMatDate())){
						if(i==offsetList.size()-1){
							if(pricAmt.longValue()>=offsetList.get(i).getUptoAmt()){
								return periodWiseOffset;
							}else{
								for(j=i;j>=0;j--){
									if(pricAmt.longValue()<offsetList.get(j).getUptoAmt()){
										continue;
									}else
										return offsetList.get(j);
								}
							}
						}
						if(offsetList.size()!=1 && matDate.before(offsetList.get(i+1).getToMatDate())){
							if(offsetList.get(i).getUptoAmt()>0){
								if(pricAmt.longValue()>=offsetList.get(i).getUptoAmt()){
									return periodWiseOffset;
								}else{
									for(j=i;j>=0;j--){
										if(pricAmt.longValue()<offsetList.get(j).getUptoAmt()){
											continue;
										}else
											return offsetList.get(j);
									}

								}
							}else{
								return offsetList.get(i);
							}							
						}


					}else{
						return null;
					}

					if(offsetList.size()==i){
						if(matDate.after(offsetList.get(i-1).getToMatDate())){
							return offsetList.get(i-1);
						}
					}
					++i;

				}
			}


		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
		}
		return null;
	}
	
	public static List<PeriodWiseOffset> getPeriodOffsetList(Long lbrcode,String prdcd,Date asoffdate, Long pricAmt,Date matDate,Long accType)
			throws SQLException {
		        long l_start = System.currentTimeMillis();
		        List<PeriodWiseOffset> list=new ArrayList<PeriodWiseOffset>();
		        Session session = HBUtil.getSessionFactory().openSession();
		    	//Transaction tx = session.beginTransaction();
				String queryString = "from PeriodWiseOffset where id.LBrCode=? and id.prdCd=? and id.acctType =? and id.effDate in ((select max(id.effDate) FROM OffsetIntTD WHERE id.LBrCode =? and id.prdCd=? and id.acctType= ? and  id.effDate <=? )) order by months asc,days asc,uptoAmt asc";//modified on 22/10/2016
				list = session.createQuery(queryString).setParameter(0, lbrcode.intValue()).setParameter(1, prdcd.trim())
		        		.setParameter(2, accType.shortValue()).setParameter(3, lbrcode.intValue()).setParameter(4, prdcd.trim())
		        		.setParameter(5, accType.shortValue()).setParameter(6, asoffdate).getResultList();
		        
				if(list!=null && list.size()!=0)
				{
					session.close();
		            return list;
				}else{
		            return null;
		        }
		    }
	
	public static String getInterestFreq(Long lbrcode, String productNo)
			throws SQLException {
				long l_start = System.currentTimeMillis();
				Session session = HBUtil.getSessionFactory().openSession();
				String queryString = "select intFreq from D020002 where id.lbrCode=? and id.prdCd=? ";
				char queryObject = (char) session.createQuery(queryString).setParameter(0, lbrcode.intValue()).setParameter(1, productNo)
						.getSingleResult();
				long l_end = System.currentTimeMillis();
				session.close();
				return queryObject+"";
			}
	
	public static CentrelisedBrwiseCustTDSFile getCentrelisedBrwiseCustTDSFileForCust(Customer customer,boolean isCreation) {
		CentrelisedBrwiseCustTDSFile brwiseCustTDSFile = new CentrelisedBrwiseCustTDSFile();
		Session session = HBUtil.getSessionFactory().openSession();
		try{
			if(isCreation){
				
				brwiseCustTDSFile = session.get(CentrelisedBrwiseCustTDSFile.class, Integer.parseInt(customer.getCustno()));
				if(brwiseCustTDSFile==null){
					CentrelisedBrwiseCustTDSFile centrelisedBrwiseCustTDSFile = new CentrelisedBrwiseCustTDSFile();
					List<Object[]> listBrwiseCustTDSFile = new ArrayList<Object[]>();
					listBrwiseCustTDSFile = getGroupedBrwiseCustTDSFile(Integer.parseInt(customer.getCustno()));
					Long mainCustNo = new Long(0L);
					Double interestProjected = new Double(0.0);
					Double tdsProjected = new Double(0.0);
					Double interestProvided = new Double(0.0);
					Double tdsProvided = new Double(0.0);
					if(listBrwiseCustTDSFile!=null && listBrwiseCustTDSFile.size()>0){
						for (Object obj[] : listBrwiseCustTDSFile) {							
							mainCustNo = (Long) obj[0];
							interestProjected = (Double) obj[1];
							tdsProjected = (Double) obj[2];
							interestProvided = (Double) obj[3];
							tdsProvided = (Double) obj[4];				
							centrelisedBrwiseCustTDSFile.setMainCustNo(mainCustNo.intValue());
							centrelisedBrwiseCustTDSFile.setIntProjected(interestProjected);
							centrelisedBrwiseCustTDSFile.setTdsProjected(tdsProjected);
							centrelisedBrwiseCustTDSFile.setIntProvision(interestProvided);
							centrelisedBrwiseCustTDSFile.setTdsProvision(tdsProvided);
						}
					}else{
						centrelisedBrwiseCustTDSFile.setMainCustNo(Integer.parseInt(customer.getCustno()));
						centrelisedBrwiseCustTDSFile.setIntProjected(interestProjected);
						centrelisedBrwiseCustTDSFile.setTdsProjected(tdsProjected);
						centrelisedBrwiseCustTDSFile.setIntProvision(interestProvided);
						centrelisedBrwiseCustTDSFile.setTdsProvision(tdsProvided);
					}
					return centrelisedBrwiseCustTDSFile;
				}else{
					return brwiseCustTDSFile;
				}
			}else{
				brwiseCustTDSFile = session.get(CentrelisedBrwiseCustTDSFile.class, Integer.parseInt(customer.getCustno()));
				List<Object[]> listBrwiseCustTDSFile = new ArrayList<Object[]>();
				listBrwiseCustTDSFile = getGroupedBrwiseCustTDSFile(Integer.parseInt(customer.getCustno()));
				Long mainCustNo = new Long(0L);
				Double interestProjected = new Double(0.0);
				Double tdsProjected = new Double(0.0);
				Double interestProvided = new Double(0.0);
				Double tdsProvided = new Double(0.0);
				if(listBrwiseCustTDSFile!=null && listBrwiseCustTDSFile.size()>0){
					for (Object obj[] : listBrwiseCustTDSFile) {						
						mainCustNo = (Long) obj[0];
						interestProjected = (Double) obj[1];
						tdsProjected = (Double) obj[2];
						interestProvided = (Double) obj[3];
						tdsProvided = (Double) obj[4];
						if(brwiseCustTDSFile==null){
							brwiseCustTDSFile = new CentrelisedBrwiseCustTDSFile();
							brwiseCustTDSFile.setMainCustNo(mainCustNo.intValue());
							brwiseCustTDSFile.setIntProjected(interestProjected);
							brwiseCustTDSFile.setTdsProjected(tdsProjected);
							brwiseCustTDSFile.setIntProvision(interestProvided);
							brwiseCustTDSFile.setTdsProvision(tdsProvided);
						}else{
							brwiseCustTDSFile.setIntProjected(interestProjected);
							brwiseCustTDSFile.setTdsProjected(tdsProjected);
							brwiseCustTDSFile.setIntProvision(interestProvided);
							brwiseCustTDSFile.setTdsProvision(tdsProvided);
						}					
					}
				}else{
					brwiseCustTDSFile = new CentrelisedBrwiseCustTDSFile();
					brwiseCustTDSFile.setMainCustNo(Integer.parseInt(customer.getCustno()));
					brwiseCustTDSFile.setIntProjected(interestProjected);
					brwiseCustTDSFile.setTdsProjected(tdsProjected);
					brwiseCustTDSFile.setIntProvision(interestProvided);
					brwiseCustTDSFile.setTdsProvision(tdsProvided);
				}
				return brwiseCustTDSFile;
			}			
		}catch(Exception e){
			e.printStackTrace();
			logger.error(e);
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public static List<Object[]> getGroupedBrwiseCustTDSFile(int customerNumber)throws SQLException {
		String queryString = "";
		Session session = HBUtil.getSessionFactory().openSession();
		List<Object[]> returnList = null;
		String queryStr = "select id.mainCustNo, sum(intProjected), sum(tdsProjected), sum(intProvision), sum(tdsProvision) from D009500 ";
		try {
		StringBuffer strBuff = new StringBuffer();
	    strBuff.append(queryStr);
	    
	    	strBuff.append("where id.mainCustNo =:mainCustNo ");
	    
	    strBuff.append("group by id.mainCustNo order by id.mainCustNo");
	    returnList = session.createQuery(strBuff.toString()).setParameter("mainCustNo", customerNumber).getResultList();
		}catch(NullPointerException ne) {
			
		}
		
		return returnList;

	}
	
	
	
	public static D009500 runProjectionForSingleReceipt(D020004 termDepositReceipts, D020002 tdParamForeachReceipt,Customer customer) {
		try{
			Double tdFMsum = 0.0;
			Double interestProjected = 0.0;
			Double tdsOnEachReceipt = 0.0;
			Double tdsPercentage = 0.0;
			Double vouchersOnOpdate = 0.0;
			Date startDate1 = new Date();
			Date endDate1 = new Date();
			Calendar asoffDate = Calendar.getInstance();
			Calendar startDate = Calendar.getInstance();
			Calendar origStartDate = Calendar.getInstance();
			Date operationDate = new Date();
			Calendar endDate = Calendar.getInstance();
			Calendar operDate = Calendar.getInstance();
			operDate.setTime(new Date());
			List<D020004> todisplay = new ArrayList<D020004>();
			
			startDate = SwiftcoreDateUtil.getFinancialYearStartDateByOperationDate(operDate);
			endDate = SwiftcoreDateUtil.getFinancialYearEndDateByOperationDate(operDate);
			startDate1 = startDate.getTime();
			endDate1 = endDate.getTime();
			tdsPercentage = customer.getTdsPercentage();
			
			Calendar originalAsoffDate = Calendar.getInstance();
			Calendar originalMatDate = Calendar.getInstance();
			Calendar oriMaturityDate = Calendar.getInstance();
			Calendar matDate = Calendar.getInstance();
			Calendar lastInterestRunDate = Calendar.getInstance();
			Calendar cbldate = Calendar.getInstance();
			Calendar quarterStartDate = Calendar.getInstance();
			Calendar quarterEndDate = Calendar.getInstance();
			Calendar ogDate = Calendar.getInstance();
			startDate.setTime(startDate1);
			ogDate.setTime(startDate1);
			origStartDate.setTime(startDate1);
			lastInterestRunDate.set(startDate.get(Calendar.YEAR), Calendar.MARCH, 31);
			//ITermDepositReceiptsService receiptsService = (ITermDepositReceiptsService) ServiceFinder.findBean("termDepositReceiptsService");
			/***************************************************************************************
			 * iterating through the list of accounts to find all receipts associated with each
			 * customer account
			 **************************************************************************************/

			try {
				D009500 brwiseCustTDSFile = null;
				D010014 closingBalance = new D010014();
				D010014 closingBalanceForPremature = new D010014();
				D010014 closingBalanceOnOprDate = new D010014();
				Double amtForTDUNITWD = 0.0;
				Double lastInterestAmt = 0.0;
				Calendar dateOfTDUNITWD = Calendar.getInstance();
				Date maxCblDateBeforeOpdate = new Date();
				endDate.setTime(endDate1);
				termDepositReceipts.setIntrateCalc(termDepositReceipts.getIntRate() + termDepositReceipts.getOffSetRate());
				asoffDate.setTime(termDepositReceipts.getAsOffdate());
				originalAsoffDate.setTime(termDepositReceipts.getAsOffdate());
				originalMatDate.setTime(termDepositReceipts.getMatDate());
				oriMaturityDate.setTime(termDepositReceipts.getMatDate());
				originalMatDate.add(Calendar.DAY_OF_MONTH, -1);
				Long daysDiffForReceipt = SwiftcoreDateUtil.getDateDiff(originalAsoffDate, originalMatDate);						
				boolean newReceipt = false;
				boolean premature = false;
				boolean isMaturing = false;// to check if the receipt matures in the
				boolean isKdProduct = false;
				boolean isRdProduct = false;
				boolean isClosingBalanceNull = false;
				Double remainingInterestForMDS = 0.0;
				Double interestBeforeFinYear = 0.0;
				Double periodicInterest = 0.0;
				BigDecimal periodInterest = new BigDecimal(0.0);
				Double kdInterest = 0.0;
				boolean isKDreceiptCblNull = false;
				// financial year
				if (asoffDate.before(startDate)) {
					if (asoffDate.getTime().before(termDepositReceipts.getCertDate()) && termDepositReceipts.getCertDate().after(startDate.getTime())) {
						origStartDate.setTime(termDepositReceipts.getCertDate());
						newReceipt = true;
						startDate.setTime(asoffDate.getTime());
					} else {
						newReceipt = false;
					}
					/***********************************************************************
					 * Get the interest Voucher before startdate
					 **********************************************************************/
					List<Object[]> lastInterestDetails = TermDepositReceiptsDao.getLastInterestVoucherDateAndAmt(termDepositReceipts.getId().getLbrCode(), termDepositReceipts.getId().getPrdAcctId(), startDate1);
					if (lastInterestDetails.size() == 0) {
						interestBeforeFinYear = calculateInterestForTDProducts(termDepositReceipts, tdParamForeachReceipt, startDate1,endDate1,1L,termDepositReceipts.getInstOrPrincAmt(),false, false, daysDiffForReceipt,"");
					}
				} else if (asoffDate.after(startDate)) {
					startDate.setTime(asoffDate.getTime());
					origStartDate.setTime(asoffDate.getTime());
					newReceipt = true;
				}
				/***************************************************************************
				 * Condition to check if the receipt matures before the anual start date
				 **************************************************************************/

				if (termDepositReceipts.getMatDate().before(startDate.getTime())) {
					
				} else {
					long noOfQuarters = 0l;
					Long noOfMonths = 0l;
					Long daysDiff = 0l;
					Long monthDiff = 0l;
					Long daysDiffKd = 0l;
					double interestForKDLastFincal = 0.0;
					long daysDiffForUnitwd = 0l;
					long remainderRemainingMonths = 0l;
					/***********************************************************************
					 * Condition to check if the receipt matures before the anual end date
					 **********************************************************************/
					if(termDepositReceipts.getClosedDate() != null){
						if ((!(DateUtility.getFormattedDate(termDepositReceipts.getClosedDate()).equals(DateUtility.getDateFromString("19000101")))) && (termDepositReceipts.getClosedDate().compareTo(termDepositReceipts.getMatDate()) < 0)) {
							if (termDepositReceipts.getClosedDate().compareTo(startDate.getTime()) >= 0 && termDepositReceipts.getClosedDate().before(endDate.getTime())) {
								termDepositReceipts.setMatDate(termDepositReceipts.getClosedDate());
								endDate.setTime(termDepositReceipts.getClosedDate());
								premature = true;
								if (tdParamForeachReceipt.getUnitsAllowYn()=='Y') {
									closingBalanceForPremature = TermDepositReceiptsDao.getClosingBalanceAfterClosure(termDepositReceipts.getId().getLbrCode(), termDepositReceipts.getId().getPrdAcctId(), termDepositReceipts.getClosedDate(), premature);
								}
							}
						}
					}
					
					closingBalanceOnOprDate = TermDepositReceiptsDao.getClosingBalanceForLastInterestRunDateNew(termDepositReceipts, operDate, newReceipt);
					if (tdParamForeachReceipt.getUnitsAllowYn()=='Y') {
						closingBalance = TermDepositReceiptsDao.getClosingBalanceForLastInterestRunDate(termDepositReceipts, origStartDate, newReceipt);
					}else {
						closingBalance = TermDepositReceiptsDao.getClosingBalanceForLastInterestRunDate(termDepositReceipts, origStartDate, newReceipt);
					}

					if (closingBalance == null) {
						closingBalance = new D010014();
						D010014Id id=new D010014Id();
						closingBalance.setId(id);
						closingBalance.setBalance1(0.0);
						closingBalance.setBalance2(0.0);
						closingBalance.setBalance3(0.0);
						closingBalance.setBalance4(0.0);
						isClosingBalanceNull = true;
						if (termDepositReceipts.getMatDate().before(endDate.getTime()) && newReceipt == false) {
							startDate.setTime(startDate1);
							closingBalance.getId().setCblDate(startDate1);
						} else if (termDepositReceipts.getMatDate().before(endDate.getTime()) && newReceipt == true) {
							startDate.setTime(asoffDate.getTime());
							closingBalance.getId().setCblDate(asoffDate.getTime());
						} else {
							closingBalance.setBalance1(termDepositReceipts.getInstOrPrincAmt());
							startDate.setTime(asoffDate.getTime());
							closingBalance.getId().setCblDate(asoffDate.getTime());
						}
					} else {
						cbldate.setTime(closingBalance.getId().getCblDate());
					}
					if (closingBalanceOnOprDate == null) {
						closingBalanceOnOprDate = new D010014();
						D010014Id id=new D010014Id();
						closingBalanceOnOprDate.setId(id);
						closingBalanceOnOprDate.setBalance1(0.0);
						closingBalanceOnOprDate.setBalance2(0.0);
						closingBalanceOnOprDate.setBalance3(0.0);
						closingBalanceOnOprDate.setBalance4(0.0);
						closingBalanceOnOprDate.getId().setCblDate(termDepositReceipts.getAsOffdate());
					}

					/***********************************************************************
					 * if the receipt is both opened and closed in the same period, the
					 * interest projected is the diff in the mat value and the principal
					 * amount
					 **********************************************************************/
					startDate.set(Calendar.HOUR_OF_DAY, 0);
					startDate.set(Calendar.MINUTE, 0);
					startDate.set(Calendar.SECOND, 0);
					startDate.set(Calendar.MILLISECOND, 0);

					endDate.set(Calendar.HOUR_OF_DAY, 0);
					endDate.set(Calendar.MINUTE, 0);
					endDate.set(Calendar.SECOND, 0);
					endDate.set(Calendar.MILLISECOND, 0);

					if (!premature) {
						if((termDepositReceipts.getMatDate().before(endDate.getTime())) && (termDepositReceipts.getMatDate().before(operationDate))){
							if (newReceipt == true && (termDepositReceipts.getMatDate().before(endDate.getTime()))) {
								premature = true;
								isMaturing = true;
							} else if (newReceipt == false && (termDepositReceipts.getMatDate().compareTo(endDate.getTime()) <= 0)) {
								endDate.setTime(termDepositReceipts.getMatDate());
								if (tdParamForeachReceipt.getUnitsAllowYn()=='Y') {
									closingBalanceForPremature = TermDepositReceiptsDao.getClosingBalanceAfterClosure(termDepositReceipts.getId().getLbrCode(), termDepositReceipts.getId().getPrdAcctId(), termDepositReceipts.getMatDate(), premature);
								}
								premature = true;
								isMaturing = true;
							} 
						} else {
							if(termDepositReceipts.getMatDate().compareTo(endDate.getTime())<=0){
								endDate.setTime(termDepositReceipts.getMatDate());
								endDate.add(Calendar.DAY_OF_MONTH, -1);
								isMaturing = true;
							}
							if (!isMaturing) {
								originalMatDate.setTime(endDate.getTime());
								//endDate.add(Calendar.DAY_OF_MONTH, -1);
								if(startDate.compareTo(ogDate)<0){
									startDate.setTime(ogDate.getTime());
								}
								if (tdParamForeachReceipt.getIntFreq()=='M' && tdParamForeachReceipt.getIntPaidYn()=='Y') {
									quarterEndDate.setTime(startDate.getTime());
									quarterEndDate.set(Calendar.DAY_OF_MONTH, startDate.getActualMaximum(Calendar.DAY_OF_MONTH));
									if (startDate.get(Calendar.DAY_OF_MONTH) == 1) {
										daysDiff = 0L;
										monthDiff = SwiftcoreDateUtil.getDateDiffForTDSProjection(startDate, endDate);											
									} else {
										daysDiff = SwiftcoreDateUtil.getDateDiffForTDSProjection(startDate, quarterEndDate);
										monthDiff = SwiftcoreDateUtil.getDateDiffForTDSProjection(quarterEndDate, endDate);
									}
									noOfMonths = (monthDiff + 1) / 30;
									periodicInterest = (termDepositReceipts.getInstOrPrincAmt() * termDepositReceipts.getIntrateCalc())/(termDepositReceipts.getIntrateCalc() + 1200);
									periodInterest = new BigDecimal(periodicInterest).setScale(2, RoundingMode.HALF_UP);
								} else if (tdParamForeachReceipt.getIntFreq()=='Q' && tdParamForeachReceipt.getIntPaidYn()=='Y') {
									periodicInterest = ((termDepositReceipts.getInstOrPrincAmt() * termDepositReceipts.getIntrateCalc()) / (1200 / 3));
									periodInterest = BigDecimal.valueOf(Math.round(periodicInterest));
									/*******************************************************
									 * To find the quarter start date and quarter end date
									 * based on the start date
									 ******************************************************/
									if (startDate.get(Calendar.MONTH) == Calendar.JANUARY || startDate.get(Calendar.MONTH) == Calendar.FEBRUARY || startDate.get(Calendar.MONTH) == Calendar.MARCH) {
										quarterEndDate.set(startDate.get(Calendar.YEAR), Calendar.MARCH, 31);
										quarterStartDate.set(startDate.get(Calendar.YEAR), Calendar.JANUARY, 1);
									} else if (startDate.get(Calendar.MONTH) == Calendar.APRIL || startDate.get(Calendar.MONTH) == Calendar.MAY || startDate.get(Calendar.MONTH) == Calendar.JUNE) {
										quarterEndDate.set(startDate.get(Calendar.YEAR), Calendar.JUNE, 30);
										quarterStartDate.set(startDate.get(Calendar.YEAR), Calendar.APRIL, 1);
									} else if (startDate.get(Calendar.MONTH) == Calendar.JULY || startDate.get(Calendar.MONTH) == Calendar.AUGUST || startDate.get(Calendar.MONTH) == Calendar.SEPTEMBER) {
										quarterEndDate.set(startDate.get(Calendar.YEAR), Calendar.SEPTEMBER, 30);
										quarterStartDate.set(startDate.get(Calendar.YEAR), Calendar.JULY, 1);
									} else {
										quarterEndDate.set(startDate.get(Calendar.YEAR), Calendar.DECEMBER, 31);
										quarterStartDate.set(startDate.get(Calendar.YEAR), Calendar.OCTOBER, 1);
									}
									if (DateUtility.getFormattedDate(startDate.getTime()).compareTo(DateUtility.getFormattedDate(quarterStartDate.getTime())) == 0) {
										monthDiff = SwiftcoreDateUtil.getDateDiffForTDSProjection(startDate, endDate);
										daysDiff = 0l;												
									} else {
										daysDiff = SwiftcoreDateUtil.getDateDiffForTDSProjection(startDate, quarterEndDate);
										monthDiff = SwiftcoreDateUtil.getDateDiffForTDSProjection(quarterEndDate, endDate);
									}
									noOfMonths = (monthDiff + 1) / 30;
									if (tdParamForeachReceipt.getUnitsAllowYn()=='Y' && amtForTDUNITWD > 0) {
										daysDiffForUnitwd = SwiftcoreDateUtil.getDateDiffForTDSProjection(startDate, dateOfTDUNITWD);
									}
								} else if (tdParamForeachReceipt.getIntFreq()=='H' && tdParamForeachReceipt.getIntPaidYn()=='N') {
									if (tdParamForeachReceipt.getCumIntYn()=='Y') {
										monthDiff = SwiftcoreDateUtil.getDateDiffForTDSProjection(startDate, endDate);													
										isKdProduct = true;
									} else {
										periodicInterest = ((termDepositReceipts.getInstOrPrincAmt() * termDepositReceipts.getIntrateCalc()) / (1200 / 3));
										periodInterest = BigDecimal.valueOf(periodicInterest);
										periodInterest = periodInterest.multiply(new BigDecimal(2.0));
										periodInterest = BigDecimal.valueOf(Math.round(periodInterest.doubleValue()));
										if (startDate.get(Calendar.MONTH) == Calendar.JANUARY || startDate.get(Calendar.MONTH) == Calendar.FEBRUARY || startDate.get(Calendar.MONTH) == Calendar.MARCH) {
											quarterEndDate.set(startDate.get(Calendar.YEAR), Calendar.MARCH, 31);
											quarterStartDate.set(startDate.get(Calendar.YEAR), Calendar.JANUARY, 1);
										} else if (startDate.get(Calendar.MONTH) == Calendar.APRIL || startDate.get(Calendar.MONTH) == Calendar.MAY || startDate.get(Calendar.MONTH) == Calendar.JUNE) {
											quarterEndDate.set(startDate.get(Calendar.YEAR), Calendar.JUNE, 30);
											quarterStartDate.set(startDate.get(Calendar.YEAR), Calendar.APRIL, 1);
										} else if (startDate.get(Calendar.MONTH) == Calendar.JULY || startDate.get(Calendar.MONTH) == Calendar.AUGUST || startDate.get(Calendar.MONTH) == Calendar.SEPTEMBER) {
											quarterEndDate.set(startDate.get(Calendar.YEAR), Calendar.SEPTEMBER, 30);
											quarterStartDate.set(startDate.get(Calendar.YEAR), Calendar.JULY, 1);
										} else {
											quarterEndDate.set(startDate.get(Calendar.YEAR), Calendar.DECEMBER, 31);
											quarterStartDate.set(startDate.get(Calendar.YEAR), Calendar.OCTOBER, 1);
										}
										if (DateUtility.getFormattedDate(startDate.getTime()).compareTo(DateUtility.getFormattedDate(quarterStartDate.getTime())) == 0) {
											monthDiff = SwiftcoreDateUtil.getDateDiffForTDSProjection(startDate, endDate);
											daysDiff = 0l;
										} else {
											daysDiff = SwiftcoreDateUtil.getDateDiffForTDSProjection(startDate, quarterEndDate);
											monthDiff = SwiftcoreDateUtil.getDateDiffForTDSProjection(quarterEndDate, endDate);
										}
										noOfMonths = (monthDiff + 1) / 30;
									}
								} else if((tdParamForeachReceipt.getIntFreq()=='Q' && tdParamForeachReceipt.getCumIntYn()=='Y') && (tdParamForeachReceipt.getInstOrPrinc()=='P')) {
									monthDiff = SwiftcoreDateUtil.getDateDiffForTDSProjection(startDate, endDate);
									noOfMonths = (monthDiff + 1) / 30;
									isKdProduct = true;
									if(newReceipt==true){
										if((asoffDate.compareTo(startDate)<=0) && (interestBeforeFinYear>0)){
											interestForKDLastFincal = interestBeforeFinYear;
										}
									}else{
										if((asoffDate.compareTo(startDate)<0) && (interestBeforeFinYear>0)){
											interestForKDLastFincal = interestBeforeFinYear;
										}
									}
								}else if((tdParamForeachReceipt.getIntFreq()=='Q' && tdParamForeachReceipt.getCumIntYn()=='Y') && (tdParamForeachReceipt.getInstOrPrinc()=='I')){
									monthDiff = SwiftcoreDateUtil.getDateDiffForTDSProjection(startDate, endDate);
									noOfMonths = (monthDiff + 1) / 30;
									isRdProduct = true;
									if(newReceipt==true){
										if((asoffDate.compareTo(startDate)<=0) && (interestBeforeFinYear>0)){
											interestForKDLastFincal = interestBeforeFinYear;
										}
									}else{
										if((asoffDate.compareTo(startDate)<0) && (interestBeforeFinYear>0)){
											interestForKDLastFincal = interestBeforeFinYear;
										}
									}
								}

								quarterEndDate.set(Calendar.HOUR_OF_DAY, 0);
								quarterEndDate.set(Calendar.MINUTE, 0);
								quarterEndDate.set(Calendar.SECOND, 0);
								quarterEndDate.set(Calendar.MILLISECOND, 0);

								if (startDate.compareTo(quarterEndDate) == 0 && tdParamForeachReceipt.getCumIntYn()=='N') {
									daysDiff = daysDiff + 1;
								}
							}
						}
					} else {

					}
					if (premature == false) {
						if(!isMaturing){
							Long daysIntheYear = SwiftcoreDateUtil.getDateDiff(startDate, endDate);
							if(newReceipt==false || isKdProduct==true || isRdProduct==true){
								startDate.add(Calendar.MONTH, noOfMonths.intValue());
								startDate.add(Calendar.DAY_OF_MONTH, -1);
								startDate = SwiftcoreDateUtil.clearMilliSeconds(startDate);
								while(startDate.compareTo(endDate)>0){
									if(newReceipt==false){
										startDate.setTime(ogDate.getTime());
									}else{
										startDate.setTime(termDepositReceipts.getAsOffdate());
									}
									noOfMonths = noOfMonths - 1;
									startDate.add(Calendar.MONTH, noOfMonths
											.intValue());
									startDate = SwiftcoreDateUtil.clearMilliSeconds(startDate);
								}
								if(newReceipt==false){
									startDate.setTime(ogDate.getTime());
								}else{
									startDate.setTime(termDepositReceipts.getAsOffdate());
								}
							}								
							if (tdParamForeachReceipt.getIntFreq()=='Q') {
								remainderRemainingMonths = noOfMonths % 3;	
								if (remainderRemainingMonths > 0 && isKdProduct==false) {
									noOfMonths = noOfMonths - remainderRemainingMonths;
								}										
								if (isKdProduct==true || isRdProduct == true) {
									if((asoffDate.compareTo(startDate)<=0) && (interestBeforeFinYear>0)){
										kdInterest = calculateInterestForTDProducts(termDepositReceipts, tdParamForeachReceipt, ogDate.getTime(), endDate.getTime(),0L,0.0,isMaturing,false,daysDiffForReceipt,"");
										isKDreceiptCblNull=true;
									}else if((asoffDate.compareTo(startDate)<=0) && (interestBeforeFinYear==0)){
										kdInterest = calculateInterestForTDProducts(termDepositReceipts, tdParamForeachReceipt, ogDate.getTime(), endDate.getTime(),0L,0.0,isMaturing,false,daysDiffForReceipt,"");
									}else{
										kdInterest = calculateInterestForTDProducts(termDepositReceipts, tdParamForeachReceipt, termDepositReceipts.getAsOffdate(), endDate.getTime(),0L,0.0,isMaturing,false,daysDiffForReceipt,"");
									}
								}else{
									if(tdParamForeachReceipt.getUnitsAllowYn()=='Y'){
										if(!newReceipt){
											noOfQuarters = noOfMonths / 3;
											startDate.add(Calendar.MONTH, noOfMonths.intValue());
											daysDiff = SwiftcoreDateUtil.getDateDiff(startDate, endDate);
										}else{
											noOfQuarters = noOfMonths / 3;
										}
									}else{
										if(!newReceipt){
											noOfQuarters = noOfMonths / 3;
											startDate.add(Calendar.MONTH, noOfMonths.intValue());
											daysDiff = SwiftcoreDateUtil.getDateDiff(startDate, endDate);
										}else{
											noOfQuarters = noOfMonths / 3;
										}
									}
								}
							} else if (tdParamForeachReceipt.getIntFreq()=='H' && tdParamForeachReceipt.getCumIntYn()=='N') {
								remainderRemainingMonths = noOfMonths % 3;
								if (remainderRemainingMonths > 0) {
									noOfMonths = noOfMonths - remainderRemainingMonths;
								}
								noOfQuarters = noOfMonths / 3;
							}
							if (tdParamForeachReceipt.getIntPaidYn()=='Y') {
								if (tdParamForeachReceipt.getIntFreq()=='M' && tdParamForeachReceipt.getCumIntYn()=='N') {
									if (daysDiffForReceipt < (tdParamForeachReceipt.getShortTermDays() + 1)) {
										interestProjected = ((daysIntheYear) * periodInterest.doubleValue()) / 30;
									} else {
										tdFMsum = (noOfMonths * periodInterest.doubleValue()) + ((daysDiff * periodInterest.doubleValue()) / 30);
										interestProjected = tdFMsum;
									}
									interestProjected = Double.valueOf(twoDecPlaces.format(interestProjected)) + interestBeforeFinYear;
								} else {
									if (tdParamForeachReceipt.getIntFreq()=='Q'&& tdParamForeachReceipt.getCumIntYn()=='N') {
										if (daysDiffForReceipt < (tdParamForeachReceipt.getShortTermDays() + 1)) {
											tdFMsum = ((daysIntheYear) * termDepositReceipts.getIntrateCalc() * termDepositReceipts.getInstOrPrincAmt()) / 36500;
											interestProjected = Double.valueOf(Math.round(tdFMsum));
											if (tdParamForeachReceipt.getUnitsAllowYn()=='Y') {
												interestProjected = interestProjected + amtForTDUNITWD;
											}
										} else {
											if (tdParamForeachReceipt.getUnitsAllowYn()=='Y') {
												tdFMsum = (noOfQuarters * ((termDepositReceipts.getInstOrPrincAmt() * termDepositReceipts.getIntrateCalc()) / (1200 / 3))) + ((daysDiff * termDepositReceipts.getIntrateCalc() * termDepositReceipts.getInstOrPrincAmt()) / 36500);
												if (amtForTDUNITWD != 0) {
													tdFMsum = tdFMsum + amtForTDUNITWD;
												}																												
											} else {
												tdFMsum = (noOfQuarters * ((termDepositReceipts.getInstOrPrincAmt() * termDepositReceipts.getIntrateCalc()) / (1200 / 3))) + ((daysDiff * termDepositReceipts.getIntrateCalc() * termDepositReceipts.getInstOrPrincAmt()) / 36500);
												//tdFMsum = (noOfMonths * (periodInterest.doubleValue() / 3)) + ((daysDiff * termDepositReceipts.getIntrateCalc() * closingBalance.getBalance1()) / 36500);
											}
										}
										if (noOfMonths == 0) {
											tdFMsum = tdFMsum + ((monthDiff * termDepositReceipts.getIntrateCalc() * closingBalance.getBalance1()) / 36500);
										}
										interestProjected = Double.valueOf(Math.round(tdFMsum)) + interestBeforeFinYear;
									}else if(tdParamForeachReceipt.getIntFreq()=='H' && tdParamForeachReceipt.getCumIntYn()=='N'){

										if(isKdProduct){
											kdInterest = calculateInterestForTDProducts(termDepositReceipts, tdParamForeachReceipt, ogDate.getTime(), endDate.getTime(),0L,0.0,isMaturing,false,daysDiffForReceipt,"");
											interestProjected = kdInterest - closingBalance.getBalance2();
										}else{
											if (daysDiffForReceipt < (tdParamForeachReceipt.getShortTermDays() + 1)) {
												tdFMsum = ((daysIntheYear) * termDepositReceipts.getIntrateCalc() * termDepositReceipts.getInstOrPrincAmt()) / 36500;
												interestProjected = Double.valueOf(Math.round(tdFMsum)) + interestBeforeFinYear;
											} else {
												tdFMsum = (((termDepositReceipts.getInstOrPrincAmt() * termDepositReceipts.getIntrateCalc()) / 1200) * noOfMonths) + ((daysDiff * termDepositReceipts.getIntrateCalc() * termDepositReceipts.getInstOrPrincAmt()) / 36500) + 0.0;
												if (noOfMonths == 0) {
													tdFMsum = tdFMsum + ((monthDiff * termDepositReceipts.getIntrateCalc() * termDepositReceipts.getInstOrPrincAmt()) / 36500);
												}
												if (isClosingBalanceNull == false) {
													if (closingBalance.getId().getCblDate().compareTo(origStartDate.getTime()) >= 0 && closingBalance.getId().getCblDate().compareTo(endDate.getTime()) <= 0) {
														interestProjected = Double.valueOf(Math.round(tdFMsum)) + interestBeforeFinYear;
													} else {
														interestProjected = Double.valueOf(Math.round(tdFMsum)) + interestBeforeFinYear;
													}
												} else {
													interestProjected = Double.valueOf(Math.round(tdFMsum)) + interestBeforeFinYear;
												}
											}
										}										
									}
								}									
								
							} else if ((tdParamForeachReceipt.getIntPaidYn()=='N') && (tdParamForeachReceipt.getInstOrPrinc()=='P') && (tdParamForeachReceipt.getIntFreq()=='Q')) {
								if (daysDiffForReceipt < (tdParamForeachReceipt.getShortTermDays() + 1)) {
									tdFMsum = ((daysIntheYear) * termDepositReceipts.getIntrateCalc() * (closingBalance.getBalance1() + closingBalance.getBalance2() + interestForKDLastFincal)) / 36500;
									interestProjected = Double.valueOf(Math.round(tdFMsum)) + interestBeforeFinYear;
								} else {
										interestProjected = kdInterest - closingBalance.getBalance2();											
								}
							}// end KD condition
							else if((tdParamForeachReceipt.getIntPaidYn()=='N') && (tdParamForeachReceipt.getInstOrPrinc()=='I') && (tdParamForeachReceipt.getIntFreq()=='Q')){
								if (daysDiffForReceipt < (tdParamForeachReceipt.getShortTermDays() + 1)) {
									Double interestRD = (termDepositReceipts.getInstOrPrincAmt() * termDepositReceipts.getIntrateCalc() * daysDiffForReceipt)/36500;
									Long interestForDays = Math.round(Double.valueOf(twoDecPlaces.format(interestRD)));
									interestProjected = Double.valueOf(interestForDays) + interestBeforeFinYear;
								} else {
									interestProjected = kdInterest - closingBalance.getBalance2();											
								}																		
							}//end RD condition
							else if ((tdParamForeachReceipt.getIntPaidYn()=='N') && (tdParamForeachReceipt.getIntFreq()=='H')) {
								if(isKdProduct){
									kdInterest = calculateInterestForTDProducts(termDepositReceipts, tdParamForeachReceipt, ogDate.getTime(), endDate.getTime(),0L,0.0,isMaturing,false,daysDiffForReceipt,"");
									interestProjected = kdInterest - closingBalance.getBalance2();
								}else{
									if (daysDiffForReceipt < (tdParamForeachReceipt.getShortTermDays() + 1)) {
										tdFMsum = ((daysIntheYear) * termDepositReceipts.getIntrateCalc() * termDepositReceipts.getInstOrPrincAmt()) / 36500;
										interestProjected = Double.valueOf(Math.round(tdFMsum)) + interestBeforeFinYear;
									} else {
										tdFMsum = (((termDepositReceipts.getInstOrPrincAmt() * termDepositReceipts.getIntrateCalc()) / 1200) * noOfMonths) + ((daysDiff * termDepositReceipts.getIntrateCalc() * termDepositReceipts.getInstOrPrincAmt()) / 36500) + 0.0;
										if (noOfMonths == 0) {
											tdFMsum = tdFMsum + ((monthDiff * termDepositReceipts.getIntrateCalc() * termDepositReceipts.getInstOrPrincAmt()) / 36500);
										}
										if (isClosingBalanceNull == false) {
											if (closingBalance.getId().getCblDate().compareTo(origStartDate.getTime()) >= 0 && closingBalance.getId().getCblDate().compareTo(endDate.getTime()) <= 0) {
												interestProjected = Double.valueOf(Math.round(tdFMsum)) + interestBeforeFinYear;
											} else {
												interestProjected = Double.valueOf(Math.round(tdFMsum)) + interestBeforeFinYear;
											}
										} else {
											interestProjected = Double.valueOf(Math.round(tdFMsum)) + interestBeforeFinYear;
										}
									}
								}

							}// end FR condition. FR now treated as a non pay out product.
						}else{									
								if(newReceipt == true){
									if(tdParamForeachReceipt.getUnitsAllowYn()=='Y'){
										if(amtForTDUNITWD!=0){
											interestProjected = calculateInterestForTDProducts(termDepositReceipts, tdParamForeachReceipt, startDate.getTime(), endDate.getTime(),2L,closingBalanceOnOprDate.getBalance1(),isMaturing,false,daysDiffForReceipt,"");
											interestProjected = interestProjected  + amtForTDUNITWD;
										}else{
											interestProjected = termDepositReceipts.getMatVal() - ((closingBalance.getBalance1() + closingBalance.getBalance2()));
										}
									}else{
										interestProjected =  calculateInterestForTDProducts(termDepositReceipts, tdParamForeachReceipt, termDepositReceipts.getAsOffdate(), endDate.getTime(),0L,0.0,isMaturing,false,daysDiffForReceipt,"");
									}											
								}else{
									if(tdParamForeachReceipt.getUnitsAllowYn()=='Y'){
										/*interestProjected = closingBalanceOnOprDate.getBalance2() - closingBalance.getBalance2();												
										remainingInterestForMDS = calculateRemainderInterestForMDS(closingBalanceOnOprDate,termDepositReceipts,endDate);
										interestProjected = interestProjected + remainingInterestForMDS;*/
										if(amtForTDUNITWD!=0){
											interestProjected =  calculateInterestForTDProducts(termDepositReceipts, tdParamForeachReceipt, termDepositReceipts.getAsOffdate(), endDate.getTime(),2L,closingBalanceOnOprDate.getBalance1(),isMaturing,false,daysDiffForReceipt,"");													
											if(termDepositReceipts.getReceiptStatus()==99){
												interestProjected = amtForTDUNITWD;
											}else{
												interestProjected = amtForTDUNITWD + (termDepositReceipts.getMatVal()- termDepositReceipts.getInstOrPrincAmt()) - termDepositReceipts.getIntPrvdAmtFcy();
											}													
										}else{
											interestProjected =  calculateInterestForTDProducts(termDepositReceipts, tdParamForeachReceipt, termDepositReceipts.getAsOffdate(), endDate.getTime(),2L,closingBalanceOnOprDate.getBalance1(),isMaturing,false,daysDiffForReceipt,"");													
											interestProjected = (termDepositReceipts.getMatVal()- termDepositReceipts.getInstOrPrincAmt()) - closingBalance.getBalance2();
										}
									}else{												
										interestProjected =  calculateInterestForTDProducts(termDepositReceipts, tdParamForeachReceipt, termDepositReceipts.getAsOffdate(), endDate.getTime(),0L,0.0,isMaturing,false,daysDiffForReceipt,"");
										interestProjected = interestProjected - closingBalance.getBalance2();
									}
								}									
																	
						}
					} else if (premature == true && isMaturing == true) {
						if (isClosingBalanceNull == false) {
							if (tdParamForeachReceipt.getUnitsAllowYn()=='Y') {
								if (closingBalanceForPremature.getBalance1() == 0) {
									interestProjected = closingBalanceForPremature.getBalance2() - closingBalance.getBalance2();
								} else {
									interestProjected = termDepositReceipts.getMatVal() - (closingBalance.getBalance1() + closingBalance.getBalance2());
								}
							} else {
								/***********************************************************
								 * changed to facilitate proj for receipts which mature
								 * before the financial year but are older receipts
								 **********************************************************/
								interestProjected = termDepositReceipts.getMatVal() - (closingBalance.getBalance1() + closingBalance.getBalance2());//closingBalanceOnOprDate.getBalance2() - closingBalance.getBalance2();
							}
						} else {
							if (termDepositReceipts.getInstOrPrincAmt() > 0) {
								interestProjected = termDepositReceipts.getMatVal() - termDepositReceipts.getInstOrPrincAmt();
							} else {
								interestProjected = termDepositReceipts.getMatVal() - termDepositReceipts.getInstOrPrincAmt();
							}
						}
					} else if (premature == true && isMaturing == false) {
						if (premature == true && newReceipt == true) {
							/***************************************************************
							 * if the receipt is and older receipt and it prematurely closes
							 * in this calender year
							 **************************************************************/									
							if (operationDate.compareTo(termDepositReceipts.getClosedDate()) == 0) {
								interestProjected = termDepositReceipts.getIntPrvdAmtFcy() - closingBalance.getBalance2();
							} else {
								if (tdParamForeachReceipt.getUnitsAllowYn()=='Y') {
									interestProjected = closingBalanceForPremature.getBalance2();
								} else {
									interestProjected = closingBalanceOnOprDate.getBalance2();
								}
							}

						} else if (premature == true && newReceipt == false) {
							if (operationDate.compareTo(termDepositReceipts.getClosedDate()) == 0) {
								interestProjected = termDepositReceipts.getIntPrvdAmtFcy() - closingBalance.getBalance2();
							} else {
								if (tdParamForeachReceipt.getUnitsAllowYn()=='Y') {
									interestProjected = closingBalanceForPremature.getBalance2() - closingBalance.getBalance2();
								} else {
									interestProjected = closingBalanceOnOprDate.getBalance2() - closingBalance.getBalance2();
								}
							}

						}
					}

				}

				if (interestProjected == null) {
					interestProjected = 0.0;
				}
				if (tdsPercentage==null || tdsPercentage == 0) {
					tdsPercentage = tdParamForeachReceipt.getIntTaxPer();
				}
				brwiseCustTDSFile= new D009500();
				brwiseCustTDSFile.setId(new D009500Id());
				brwiseCustTDSFile.getId().setLbrCode(termDepositReceipts.getId().getLbrCode());
				brwiseCustTDSFile.getId().setMainCustNo(Integer.parseInt(customer.getCustno()));
				Session session = HBUtil.getSessionFactory().openSession();
				brwiseCustTDSFile = session.get(D009500.class, brwiseCustTDSFile.getId());
				session.clear();
				/***************************************************************************
				 * if no branch wise details found, create new details else set int proj,
				 * int prov, tds proj and tds prov to 0
				 **************************************************************************/
				if (brwiseCustTDSFile == null) {
					brwiseCustTDSFile = new D009500();
					brwiseCustTDSFile.setId(new D009500Id());
					brwiseCustTDSFile.getId().setLbrCode(termDepositReceipts.getId().getLbrCode());
					brwiseCustTDSFile.getId().setMainCustNo(Integer.parseInt(customer.getCustno()));
				} else {
					// termDepositReceiptsDao.initializeTdsDetails(brwiseCustTDSFile);
				}

				/***************************************************************************
				 * calculating the interest projection and the tds projection for the
				 * current financial year
				 **************************************************************************/
				if (tdParamForeachReceipt.getTaxProjection()=='Y') {
					tdsOnEachReceipt = (interestProjected * tdsPercentage) / 100;
					/*brwiseCustTDSFile.setTdsprojected(brwiseCustTDSFile.getTdsprojected() + Double.valueOf(Math.round(tdsOnEachReceipt)));
					brwiseCustTDSFile.setIntprojected(brwiseCustTDSFile.getIntprojected() + Double.valueOf(twoDecPlaces.format(interestProjected)));*/
					brwiseCustTDSFile.setTdsProjected(Double.valueOf(Math.round(tdsOnEachReceipt)));
					brwiseCustTDSFile.setIntProjected(Double.valueOf(twoDecPlaces.format(interestProjected)));
					if(isMaturing){
						brwiseCustTDSFile.setFincalEndDate(oriMaturityDate.getTime());
					}else{
						brwiseCustTDSFile.setFincalEndDate(originalMatDate.getTime());
					}							
				} else {
					brwiseCustTDSFile.setIntProjected(Double.valueOf(twoDecPlaces.format(interestProjected)));
				}					
		return brwiseCustTDSFile;
		} catch (Exception e) {
				e.printStackTrace();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static void calculateMaturityValueForTDProducts(D020004 termDepositReceipts, Long noOfDays,D020002 parameter,String cumintyn,String instorprinc,String intfreq, Calendar asOfDate, Calendar originalMatDate, Calendar matDate) throws Exception {
		Long remainderRemainingMonths = 0l;
		Long noOfCompletedMonths = 0l;
		Long noOfMonths = 0l;
		Double intAmountDouble = 0.0;
		Double allQuarter = 0.0;
		Double oneQuarter = 0.0;
		//Double forMonth = 0.0;
		Double forDay = 0.0;
		Long noOfQuarter = 0l;
		long dateDiff = 0;
		/***************************************************************
		 * FOR FQ and FR RECIEPTS
		 **************************************************************/
		Session session = HBUtil.getSessionFactory().openSession();
		D009021 product = DataUtils.getProductMaster(termDepositReceipts.getId().getLbrCode()+"", parameter.getId().getPrdCd());
		if(noOfDays < parameter.getShortTermDays()+1 && !("M".equals(intfreq))){
			intAmountDouble = (termDepositReceipts.getInstOrPrincAmt() * (termDepositReceipts.getIntRate() + termDepositReceipts.getOffSetRate()) * noOfDays)/36500;
			if ("N".equalsIgnoreCase(cumintyn)
					&& "P".equalsIgnoreCase(instorprinc)){
				termDepositReceipts.setIntEarnedAmount(Math.round(intAmountDouble) + 0.0);
				noOfMonths = noOfDays /30;
				remainderRemainingMonths = noOfMonths % 3;
				if(remainderRemainingMonths>0){
					noOfCompletedMonths = noOfMonths - remainderRemainingMonths;
					noOfQuarter = noOfCompletedMonths/3;
					allQuarter = (termDepositReceipts.getInstOrPrincAmt()
							* (termDepositReceipts.getIntRate() + termDepositReceipts.getOffSetRate()) * noOfCompletedMonths) / 1200;
				}else{
					noOfQuarter = noOfMonths/3;
					allQuarter = (termDepositReceipts.getInstOrPrincAmt()
							* (termDepositReceipts.getIntRate() + termDepositReceipts.getOffSetRate()) * noOfMonths) / 1200;
				}
				oneQuarter = allQuarter / noOfQuarter;
				if("H".equals(intfreq)){
					termDepositReceipts.setPeriodicIntAmt(Math.round(oneQuarter * 2.0) + 0.0);
				}else{
					termDepositReceipts.setPeriodicIntAmt(Math.round(oneQuarter)+0.0);
				}
				Double matvalDouble =  termDepositReceipts.getIntEarnedAmount()	+ termDepositReceipts.getInstOrPrincAmt();
				
				/*** Added and Commented by Aniket Desai to RoundOff the Maturity Amount on Sep 22, 2016 : --Start-- ***/
				//termDepositReceipts.setMatval(matvalDouble);
				termDepositReceipts.setMatVal(Math.round(matvalDouble)*1D);
				/*** Added and Commented by Aniket Desai to RoundOff the Maturity Amount : --End-- ***/
				
		
			}else{
				Double matvalDouble = termDepositReceipts.getInstOrPrincAmt() + Math.round(intAmountDouble);
				termDepositReceipts.setIntEarnedAmount(Math.round(intAmountDouble)+0.0);
				/*** Added and Commented by Aniket Desai to RoundOff the Maturity Amount on Sep 22, 2016 : --Start-- ***/
				//termDepositReceipts.setMatval(matvalDouble);
				termDepositReceipts.setMatVal(Math.round(matvalDouble)*1D);
				/*** Added and Commented by Aniket Desai to RoundOff the Maturity Amount : --End-- ***/
			}
		}else{
			if ("N".equalsIgnoreCase(cumintyn)
					&& "P".equalsIgnoreCase(instorprinc)) {
				if("M".equals(intfreq)){
					Double periodicInterest = 0.0;
					Double allMonthsInterest = 0.0;
					Double interestEarned = 0.0;
					if(noOfDays < parameter.getShortTermDays()+1){
						//Double discountIntrate = (this.getIntRate() + this.getOffSetRate())/(1 + ((this.getIntRate() + this.getOffSetRate())/36600));
						if(parameter.getClIntCalcType()==6){
							periodicInterest = ((termDepositReceipts.getInstOrPrincAmt() * (termDepositReceipts.getIntRate() + termDepositReceipts.getOffSetRate())) /1200);
							interestEarned = (termDepositReceipts.getInstOrPrincAmt() * (termDepositReceipts.getIntRate() + termDepositReceipts.getOffSetRate()) * noOfDays)/36500;
						}else{
							periodicInterest = (termDepositReceipts.getInstOrPrincAmt() * (termDepositReceipts.getIntRate() + termDepositReceipts.getOffSetRate())) / ((termDepositReceipts.getIntRate() + termDepositReceipts.getOffSetRate()) + 1200);
							interestEarned = (periodicInterest) * noOfDays / 30;
						}
						//interestEarned = (noOfDays * this.getInstOrPrincAmt() * (this.getIntRate() + this.getOffSetRate()))/36500;
						//commented above line and added belo for calc as discussed with sanjay sir on 17 mar 2012
						termDepositReceipts.setIntEarnedAmount(Double.valueOf(twoDecPlaces.format(interestEarned)));
						termDepositReceipts.setPeriodicIntAmt(Double.valueOf(twoDecPlaces.format(periodicInterest)));
						termDepositReceipts.setMatVal(Double.valueOf(twoDecPlaces.format(termDepositReceipts.getIntEarnedAmount() + termDepositReceipts.getInstOrPrincAmt())));							
					}else{
						if(parameter.getClIntCalcType()==6){
							periodicInterest = (termDepositReceipts.getInstOrPrincAmt() * (termDepositReceipts.getIntRate() + termDepositReceipts.getOffSetRate())) / 1200;
						}else{
							periodicInterest = (termDepositReceipts.getInstOrPrincAmt() * (termDepositReceipts.getIntRate() + termDepositReceipts.getOffSetRate())) / ((termDepositReceipts.getIntRate() + termDepositReceipts.getOffSetRate()) + 1200);
						}
						if(termDepositReceipts.getNoOfMonths() > 0 && termDepositReceipts.getNoOfDays()==0){						
							allMonthsInterest = periodicInterest * termDepositReceipts.getNoOfMonths();
							forDay = 0.0;
						}else {
							noOfMonths = noOfDays / 30;
							asOfDate.add(Calendar.MONTH, noOfMonths.intValue());
							while(asOfDate.after(originalMatDate)){
								asOfDate.setTime(termDepositReceipts.getAsOffdate());
								noOfMonths = noOfMonths - 1;
								asOfDate.add(Calendar.MONTH, noOfMonths
										.intValue());
							}
							asOfDate.setTime(termDepositReceipts.getAsOffdate());
							allMonthsInterest = periodicInterest * noOfMonths;
							asOfDate.add(Calendar.MONTH, noOfMonths.intValue());
							//asOfDate.add(Calendar.DAY_OF_MONTH, -1);
							if(asOfDate.after(matDate)){
								dateDiff = 0;
							}else{
								dateDiff = DateUtility.getDateDiff(asOfDate, matDate);
							}
							if(asOfDate.compareTo(matDate)==0){
								dateDiff = 1;
							}
							if(parameter.getClIntCalcType()==6){
								forDay = (termDepositReceipts.getInstOrPrincAmt() * (termDepositReceipts.getIntRate() + termDepositReceipts.getOffSetRate()) * dateDiff)/36500;
							}else{
							forDay = (periodicInterest * dateDiff)/30;
							}								
						  }
						interestEarned = allMonthsInterest + forDay;
						
						termDepositReceipts.setIntEarnedAmount((Double.valueOf(twoDecPlaces.format(interestEarned)))*1D);
						termDepositReceipts.setPeriodicIntAmt(Double.valueOf(twoDecPlaces
								.format(periodicInterest)));
						// Maturity Value
						/*** Added and Commented by Aniket Desai to RoundOff the Maturity Amount on Sep 22, 2016 : --Start-- ***/
						//termDepositReceipts.setMatval(Double.valueOf(twoDecPlaces.format(termDepositReceipts.getIntEarnedAmount()+ termDepositReceipts.getInstOrPrincAmt())));
						termDepositReceipts.setMatVal(Math.round(Double.valueOf(twoDecPlaces.format(termDepositReceipts.getIntEarnedAmount()+ termDepositReceipts.getInstOrPrincAmt())))*1D);
						/*** Added and Commented by Aniket Desai to RoundOff the Maturity Amount : --End-- ***/
						}						
					
				}else{
					noOfCompletedMonths = noOfDays/30;
					asOfDate.add(Calendar.MONTH, noOfCompletedMonths
							.intValue());
					while(asOfDate.after(originalMatDate)){
						asOfDate.setTime(termDepositReceipts.getAsOffdate());
						noOfCompletedMonths = noOfCompletedMonths - 1;
						asOfDate.add(Calendar.MONTH, noOfCompletedMonths
								.intValue());
					}
					asOfDate.setTime(termDepositReceipts.getAsOffdate());						
					remainderRemainingMonths = noOfCompletedMonths % 3;
					if (remainderRemainingMonths > 0) {
						noOfCompletedMonths = noOfCompletedMonths - remainderRemainingMonths;
					}
					noOfQuarter = noOfCompletedMonths / 3;
					allQuarter = (termDepositReceipts.getInstOrPrincAmt()
							* (termDepositReceipts.getIntRate() + termDepositReceipts.getOffSetRate()) * noOfCompletedMonths) / 1200;
					oneQuarter = allQuarter / noOfQuarter;
					asOfDate.add(Calendar.MONTH, noOfCompletedMonths
							.intValue());
						//asOfDate.add(Calendar.DAY_OF_MONTH, -1);
						if(asOfDate.after(matDate)){
							dateDiff = 0;
						}else{
							dateDiff = DateUtility.getDateDiff(asOfDate, matDate);
						}
						if(asOfDate.compareTo(matDate)==0){
							dateDiff = 1;
						}
						forDay = (((termDepositReceipts.getInstOrPrincAmt() * (termDepositReceipts.getIntRate() + termDepositReceipts.getOffSetRate())) * (dateDiff)) / 36500);	
						intAmountDouble = allQuarter + forDay;
						Long fqSum = Math.round(intAmountDouble);
						Long periodicInt = 0L;
						termDepositReceipts.setIntEarnedAmount(fqSum.doubleValue());
					if("H".equals(intfreq)){
						if(product.getCurCd().equalsIgnoreCase("INR")){
							Double periodicAmount = oneQuarter * 2;
							periodicInt = Math.round(periodicAmount);
							termDepositReceipts.setPeriodicIntAmt(periodicInt.doubleValue());
						}else{
							/***************************************************************
							 * FOR NRFUS
							 **************************************************************/
							asOfDate.setTime(termDepositReceipts.getAsOffdate());
							dateDiff = DateUtility.getDateDiff(asOfDate, matDate);
							forDay = (((termDepositReceipts.getInstOrPrincAmt() * (termDepositReceipts.getIntRate() + termDepositReceipts.getOffSetRate())) * (dateDiff)) / (parameter.getNoOfDaysInYear() * 100));
							termDepositReceipts.setIntEarnedAmount(new BigDecimal(forDay).setScale(2, RoundingMode.HALF_UP).doubleValue());
							termDepositReceipts.setPeriodicIntAmt(0.0);
						}						
					}else{
						periodicInt = Math.round(oneQuarter);
						termDepositReceipts.setPeriodicIntAmt(periodicInt.doubleValue());
					}
					
					// Maturity Value
					Double matval = termDepositReceipts.getIntEarnedAmount()
							+ termDepositReceipts.getInstOrPrincAmt();
					Double matvalDouble = matval.doubleValue();
					/*** Added and Commented by Aniket Desai to RoundOff the Maturity Amount on Sep 22, 2016 : --Start-- ***/
					//termDepositReceipts.setMatval(matvalDouble);
					termDepositReceipts.setMatVal(Math.round(matvalDouble)*1D);
					/*** Added and Commented by Aniket Desai to RoundOff the Maturity Amount : --End-- ***/
				}				
			}else if("Y".equalsIgnoreCase(cumintyn) && "P".equalsIgnoreCase(instorprinc)){
				Double power = 0.0;
				
				noOfCompletedMonths = noOfDays/30;
				asOfDate.add(Calendar.MONTH, noOfCompletedMonths.intValue());
				while(asOfDate.after(originalMatDate)){
					asOfDate.setTime(termDepositReceipts.getAsOffdate());
					noOfCompletedMonths = noOfCompletedMonths - 1;
					asOfDate.add(Calendar.MONTH, noOfCompletedMonths
							.intValue());
				}
				asOfDate.setTime(termDepositReceipts.getAsOffdate());						
				remainderRemainingMonths = noOfCompletedMonths % 3;	
				if (remainderRemainingMonths > 0) {
						noOfCompletedMonths = noOfCompletedMonths - remainderRemainingMonths;
				}
				noOfQuarter = noOfCompletedMonths / 3;
				power = Math.pow((1+((termDepositReceipts.getIntRate() + termDepositReceipts.getOffSetRate())/400)), noOfQuarter);
				allQuarter = termDepositReceipts.getInstOrPrincAmt() * power;
				asOfDate.add(Calendar.MONTH, noOfCompletedMonths.intValue());
										
				if(asOfDate.after(matDate)){
					dateDiff = 0;
				}else{
					dateDiff = DateUtility.getDateDiff(asOfDate, matDate);
				}
				if(asOfDate.compareTo(matDate)==0){
					dateDiff = 1;
				}
				forDay = (((allQuarter * (termDepositReceipts.getIntRate() + termDepositReceipts.getOffSetRate())) * (dateDiff)) / 36500);
				
				intAmountDouble = allQuarter + forDay;
				//Long fqSum = Math.round(intAmountDouble);
				termDepositReceipts.setIntEarnedAmount(Math.round(intAmountDouble - termDepositReceipts.getInstOrPrincAmt())*1D);
				
				if("H".equals(intfreq)){
					if(!product.getCurCd().equalsIgnoreCase("INR")){
						/***************************************************************
						 ***********FOR NRKUS PRODUCT Added on 5 May 2016 ********Start*
						 ***************************************************************/
						Long days = 0L;
						Long noofhfyr = 0L;
						Double hfyrIntrate = 0.0;
						Double noofhfyrInt = 0.0;
						asOfDate.setTime(termDepositReceipts.getAsOffdate());
						dateDiff = DateUtility.getDateDiff(asOfDate, matDate);
						noofhfyr=dateDiff/180;
						days=dateDiff%180;
						
						hfyrIntrate=(termDepositReceipts.getIntRate()+termDepositReceipts.getOffSetRate())/2;
						power = Math.pow((1+((hfyrIntrate)/100)),noofhfyr);
						noofhfyrInt=termDepositReceipts.getInstOrPrincAmt()*power;
						forDay = (noofhfyrInt*days*(termDepositReceipts.getIntRate()+termDepositReceipts.getOffSetRate()))/(parameter.getNoOfDaysInYear()*100);
						noofhfyrInt=noofhfyrInt+forDay;
						termDepositReceipts.setIntEarnedAmount(new BigDecimal(noofhfyrInt-termDepositReceipts.getInstOrPrincAmt()).setScale(2, RoundingMode.HALF_UP).doubleValue());
						termDepositReceipts.setPeriodicIntAmt(0.0);
						/***************************************************************
						 ***********FOR NRKUS PRODUCT Added on 5 May 2016 ********End***
						 ***************************************************************/
					}						
				}
				
//				this.setMatval(fqSum.doubleValue());
				/*** Added and Commented by Aniket Desai to RoundOff the Maturity Amount on Sep 22, 2016 : --Start-- ***/
				double amountMatval= termDepositReceipts.getIntEarnedAmount()+termDepositReceipts.getInstOrPrincAmt();
				double roundOff = Math.round(amountMatval);
				termDepositReceipts.setMatVal(roundOff);
				//termDepositReceipts.setMatval(termDepositReceipts.getIntEarnedAmount()+termDepositReceipts.getInstOrPrincAmt());
				/*** Added by Aniket Desai to RoundOff the Maturity Amount : --End-- ***/
			} /***************************************************************
			   * RD CODE FOR RECEIPT CREATION
			   **************************************************************/
			else if ("Y".equalsIgnoreCase(cumintyn)
					&& "I".equalsIgnoreCase(instorprinc)) {
				Double rateOfInterest = 0.0;
				Double factor1 = 0.0;
				Double factor2 = 0.0;
				Double interestEarnedAmt = 0.0;

				/*******************************************************
				 * adding the offset to the receipt rate of interest
				 ******************************************************/
				rateOfInterest = (termDepositReceipts.getIntRate() + termDepositReceipts.getOffSetRate());
				noOfCompletedMonths = noOfDays/30;
				asOfDate.add(Calendar.MONTH, noOfCompletedMonths
						.intValue());
				while(asOfDate.after(originalMatDate)){
					asOfDate.setTime(termDepositReceipts.getAsOffdate());
					noOfCompletedMonths = noOfCompletedMonths - 1;
					asOfDate.add(Calendar.MONTH, noOfCompletedMonths
							.intValue());
				}
				
				asOfDate.setTime(termDepositReceipts.getAsOffdate());		
				remainderRemainingMonths = noOfCompletedMonths % 3;
				if (remainderRemainingMonths > 0) {
					noOfCompletedMonths = noOfCompletedMonths - remainderRemainingMonths;
				}
				noOfQuarter = noOfCompletedMonths/3;
				factor1 = (5 * rateOfInterest * 4)/1200;
				factor1 = factor1 + ((5 * rateOfInterest * 2)/(1200 + rateOfInterest));
				factor2 = factor1;
				
				/*******************************************************
				 * code to calculate the interest for completed quarters
				 ******************************************************/
				for(int i=1;i<=noOfQuarter.intValue()-1;i++){
					factor2 = factor2 + factor1 + ((((3 * 5 * i) + factor2) * rateOfInterest * 3)/1200);
				}
				factor1 = (termDepositReceipts.getInstOrPrincAmt()/5) * ((5 * noOfQuarter * 3) + factor2);
				interestEarnedAmt = factor1;
				/*******************************************************
				 * code to calculate interest for broken months in
				 * incomplete quarters
				 ******************************************************/
				if (remainderRemainingMonths > 0) {
					/***************************************************
					 * looping through remainder months and compunding
					 * the interest
					 **************************************************/
					for(int j=1;j<=remainderRemainingMonths.intValue();j++){
						factor1 = factor1 + termDepositReceipts.getInstOrPrincAmt();
						interestEarnedAmt = interestEarnedAmt + termDepositReceipts.getInstOrPrincAmt() + ((factor1 * rateOfInterest)/1200);
						}
				}
				termDepositReceipts.setMatVal(Double.valueOf(twoDecPlaces.format(interestEarnedAmt)));
				if(termDepositReceipts.getNoOfMonths()>0){
					termDepositReceipts.setIntEarnedAmount(Double.valueOf(Math.round(termDepositReceipts.getMatVal() - (termDepositReceipts.getNoOfMonths() * termDepositReceipts.getInstOrPrincAmt()))));
					/*** Added and Commented by Aniket Desai to RoundOff the Maturity Amount on Sep 22, 2016 : --Start-- ***/
					//termDepositReceipts.setMatval(termDepositReceipts.getIntEarnedAmount() + (termDepositReceipts.getNoOfMonths() * termDepositReceipts.getInstOrPrincAmt()));
					termDepositReceipts.setMatVal(Math.round(termDepositReceipts.getIntEarnedAmount() + (termDepositReceipts.getNoOfMonths() * termDepositReceipts.getInstOrPrincAmt()))*1D);
					/*** Added and Commented by Aniket Desai to RoundOff the Maturity Amount : --End-- ***/
				}
			}
		}
		
	}
}

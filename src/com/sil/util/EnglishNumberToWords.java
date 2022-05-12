package com.sil.util;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Currency;
import java.util.StringTokenizer;

import com.sil.constants.SwiftCoreConstants;

public class EnglishNumberToWords {

	private static final String[] tensNames = { "", " Ten", " Twenty",
			" Thirty", " Forty", " Fifty", " Sixty", " Seventy", " Eighty",
			" Ninety" };

	private static final String[] numNames = { "", " One", " Two", " Three",
			" Four", " Five", " Six", " Seven", " Eight", " Nine", " Ten",
			" Eleven", " Twelve", " Thirteen", " Fourteen", " Fifteen",
			" Sixteen", " Seventeen", " Eighteen", " Nineteen" };

	private static String convertLessThanOneThousand(int number) {
		String soFar;

		if (number % 100 < 20) {
			soFar = numNames[number % 100];
			number /= 100;
		} else {
			soFar = numNames[number % 10];
			number /= 10;

			soFar = tensNames[number % 10] + soFar;
			number /= 10;
			
			if(number>19) {
				soFar =numNames[number % 10] + " Hundred" + soFar;
				number /= 10;
				
				if(number != 0) {
					soFar =numNames[number] + " Thousand" + soFar;
					return soFar;
				}
					
			}
		}
		if (number == 0)
			return soFar;
		return numNames[number] + " Hundred" + soFar;
	}

	public static String convert(long number) {
		// 0 to 999 999 999 999
		if (number == 0) {
			return "zero";
		}

		String snumber = Long.toString(number);

		// pad with "0"
		String mask = "000000000000";
		DecimalFormat df = new DecimalFormat(mask);
		snumber = df.format(number);

		// XXXXXnnnnnnn
		int crores = Integer.parseInt(snumber.substring(0, 5));
		// nnnnnXXnnnnn
		int lakhs = Integer.parseInt(snumber.substring(5, 7));
		// nnnnnnnXXnnn
		int hundredThousands = Integer.parseInt(snumber.substring(7, 9));
		// nnnnnnnnnXXX
		int thousands = Integer.parseInt(snumber.substring(9, 12));

		String tradBillions;
		switch (crores) {
		case 0:
			tradBillions = "";
			break;
		case 1:
			tradBillions = convertLessThanOneThousand(crores) + " Crores ";
			break;
		default:
			tradBillions = convertLessThanOneThousand(crores) + " Crores ";
		}
		String result = tradBillions;

		String tradMillions;
		switch (lakhs) {
		case 0:
			tradMillions = "";
			break;
		case 1:
			tradMillions = convertLessThanOneThousand(lakhs) + " Lakhs ";
			break;
		default:
			tradMillions = convertLessThanOneThousand(lakhs) + " Lakhs ";
		}
		result = result + tradMillions;

		String tradHundredThousands;
		switch (hundredThousands) {
		case 0:
			tradHundredThousands = "";
			break;
		case 1:
			tradHundredThousands = "One Thousand ";
			break;
		default:
			tradHundredThousands = convertLessThanOneThousand(hundredThousands)
					+ " Thousand ";
		}
		result = result + tradHundredThousands;

		String tradThousand;
		tradThousand = convertLessThanOneThousand(thousands);
		result = result + tradThousand;

		// remove extra spaces!
		return result.replaceAll("^\\s+", "").replaceAll("\\b\\s{2,}\\b", " ");
	}
	
	
	public static String convertNumberToWords(String number){
		if(number == null){
			number = "0";
		}
		String finalWord = "";
		if(number != null){
			if(number.contains(".")){
				number = number.substring(0, (number.indexOf(".")));
			}
			StringTokenizer st = new StringTokenizer(number, ",");
			StringBuffer sb = new StringBuffer();
			while(st.hasMoreTokens()){
				sb.append(st.nextToken());
			}
			
			number = sb.toString();
			
			if (number.contains(".")) {
				finalWord = "Invalid Amount";
			} else {
				finalWord = convert(Long.parseLong(number));
			}
		}
		return finalWord;
	}
	
	public static String convertDouble(Double number) {
		// 0 to 999 999 999 999
		if (number == 0) {
			return "zero";
		}

		String snumber = (new BigDecimal(number)).toString();

		// pad with "0"
		String mask = "000000000000.00";
		DecimalFormat df = new DecimalFormat(mask);
		snumber = df.format(number);

		// XXXXXnnnnnnn
		int crores = Integer.parseInt(snumber.substring(0, 5));
		// nnnnnXXnnnnn
		int lakhs = Integer.parseInt(snumber.substring(5, 7));
		// nnnnnnnXXnnn
		int hundredThousands = Integer.parseInt(snumber.substring(7, 9));
		// nnnnnnnnnXXX
		int thousands = Integer.parseInt(snumber.substring(9, 12));
		
		int fraction = Integer.parseInt(snumber.substring(13, 15));

		String tradBillions;
		switch (crores) {
		case 0:
			tradBillions = "";
			break;
		case 1://Modified by Waheed Muhammed on 12/02/2015 for 12134
			tradBillions = convertLessThanOneThousand(crores) + " Crore ";
			break;
		default:
			tradBillions = convertLessThanOneThousand(crores) + " Crore ";
		}
		String result = tradBillions;

		String tradMillions;
		switch (lakhs) {
		case 0:
			tradMillions = "";
			break;
		case 1:
			tradMillions = convertLessThanOneThousand(lakhs) + " Lac ";
			break;
		default:
			tradMillions = convertLessThanOneThousand(lakhs) + " Lac ";
		}
		result = result + tradMillions;

		String tradHundredThousands;
		switch (hundredThousands) {
		case 0:
			tradHundredThousands = "";
			break;
		case 1:
			tradHundredThousands = "One Thousand ";
			break;
		default:
			tradHundredThousands = convertLessThanOneThousand(hundredThousands)
					+ " Thousand ";
		}
		result = result + tradHundredThousands;

		String tradThousand;
		tradThousand = convertLessThanOneThousand(thousands);
		
		result = result + tradThousand;
		
		String tradFraction;
		if(fraction>0){
			tradFraction = convertLessThanOneThousand(fraction);
			result = result +" & Paise" + tradFraction;
		}

		// remove extra spaces!
		return result.replaceAll("^\\s+", "").replaceAll("\\b\\s{2,}\\b", " ");
	}
	
	public static String amountConvertDouble(Double number) {
		
		boolean isNegative=false;
		if(number<0){
			isNegative=true;
			number = number*-1;
		}
		
		// 0 to 999 999 999 999
		if (number == 0) {
			return "0.00";
		}

		String snumber = Double.toString(number);
		// pad with "0"
		String mask = "000000000000.00";
		DecimalFormat df = new DecimalFormat(mask);
		snumber = df.format(number);

		// XXXXXnnnnnnn
		int crores = Integer.parseInt(snumber.substring(0, 5));
		// nnnnnXXnnnnn
		int lakhs = Integer.parseInt(snumber.substring(5, 7));
		// nnnnnnnXXnnn
		int hundredThousands = Integer.parseInt(snumber.substring(7, 9));
		// nnnnnnnnnXXX
		int thousands = Integer.parseInt(snumber.substring(9, 12));
		
		int fraction = Integer.parseInt(snumber.substring(13, 15));

		String result= SwiftCoreConstants.BLANK_STRING;
		
		if(crores>0){
			result = new Integer(crores).toString()+",";
		}
		
		if(SwiftCoreConstants.BLANK_STRING.equalsIgnoreCase(result.trim())){
			if(lakhs>0){
				result = new Integer(lakhs).toString()+",";
			}
		}else{
			if(lakhs>0){
				result = result+snumber.substring(5, 7)+",";
			}else{
				result = result+"00,";
			}
		}
		
		if(SwiftCoreConstants.BLANK_STRING.equalsIgnoreCase(result.trim())){
			if(hundredThousands>0){
				result = new Integer(hundredThousands).toString()+",";
			}
		}else{
			if(hundredThousands>0){
				result = result+snumber.substring(7, 9)+",";
			}else{
				result = result+"00,";
			}
		}
		
		if(SwiftCoreConstants.BLANK_STRING.equalsIgnoreCase(result.trim())){
			if(thousands>0){
				result = new Integer(thousands).toString()+".";
			}else{
				result = "0.";
			}
		}else{
			if(thousands>0){
				result = result+snumber.substring(9, 12)+".";
			}else{
				result = result+"000.";
			}
		}
		
//		if(fraction == 0){
//			result = result+"00";
//		}else if(fraction > 9){
//			result = result+snumber.substring(13, 15);
//		}else{
//			result = result+"0"+snumber.substring(13, 15);
//		}
		result = result+snumber.substring(13, 15);
		
		
		if(isNegative==true){
			result = result+"-";
		}
		
		return result;
	}

	//saurabh
	public static String amountConvertDoubleWithComma(Double number) {
		boolean flagNegative = false;
		
		/***Added By Aniket Desai on 28th Dec, 2016: --Start-- ***/
		String currencyCode = SwiftCoreConstants.CURRENCY;
		BigDecimal bigAmount = new BigDecimal(number);        
        if (currencyCode.equalsIgnoreCase("INR")) {
        	/***Added By Aniket Desai on 28th Dec, 2016: --End-- ***/   
         	
		
		if (number == 0) {
			return "0.00";
		}
		if (number<0){
			number = number*(-1);
			flagNegative = true;			
		}

		String snumber = Double.toString(number);

		// pad with "0"
		String mask = "000000000000.00";
		DecimalFormat df = new DecimalFormat(mask);
		snumber = df.format(number);

		// XXXXXnnnnnnn
		int crores = Integer.parseInt(snumber.substring(0, 5));
		// nnnnnXXnnnnn
		int lakhs = Integer.parseInt(snumber.substring(5, 7));
		// nnnnnnnXXnnn
		int hundredThousands = Integer.parseInt(snumber.substring(7, 9));
		// nnnnnnnnnXXX
		int thousands = Integer.parseInt(snumber.substring(9, 12));
		
		int fraction = Integer.parseInt(snumber.substring(13, 15));

		String result= SwiftCoreConstants.BLANK_STRING;
		
		if(crores>0){
			result = new Integer(crores).toString()+",";
		}
		
		if(SwiftCoreConstants.BLANK_STRING.equalsIgnoreCase(result.trim())){
			if(lakhs>0){
				if(lakhs<10){
					result = "0"+result+new Integer(lakhs).toString()+",";
				}else{
					result = result+new Integer(lakhs).toString()+",";
				}
			}
		}else{
			if(lakhs>0){
				if(lakhs<10){
					result = result+"0"+new Integer(lakhs).toString()+",";
				}else{
					result = result+new Integer(lakhs).toString()+",";
				}
			}else{
				result = result+"00,";
			}
		}
		
		if(SwiftCoreConstants.BLANK_STRING.equalsIgnoreCase(result.trim())){
			if(hundredThousands>0){
				if(hundredThousands<10){
					result = "0"+new Integer(hundredThousands).toString()+",";
				}else{
					result = new Integer(hundredThousands).toString()+",";
				}
			}
		}else{
			if(hundredThousands>0){
				if(hundredThousands<10){
					result = result+"0"+new Integer(hundredThousands).toString()+",";
				}else{
					result = result+new Integer(hundredThousands).toString()+",";
				}
			}else{
				result = result+"00,";
			}
		}
		
		if(SwiftCoreConstants.BLANK_STRING.equalsIgnoreCase(result.trim())){
			if(thousands>0){
				if(thousands<10){
					result = "00"+new Integer(thousands).toString()+".";
				}else if(thousands<100){
					result = "0"+new Integer(thousands).toString()+".";
				}else{
					result = new Integer(thousands).toString()+".";
				}
			}else{
				result = "0.";
			}
		}else{
			if(thousands>0){
				if(thousands<10){
					result = result+"00"+new Integer(thousands).toString()+".";
				}else if(thousands<100){
					result = result+"0"+new Integer(thousands).toString()+".";
				}else{
					result = result+new Integer(thousands).toString()+".";
				}
			}else{
				result = result+"000.";
			}
		}
		
		if(fraction == 0){
			result = result+"00";
		}else if(fraction > 9){
			result = result+new Integer(fraction).toString();
		}else{
			result = result+"0"+new Integer(fraction).toString();
		}
		
		if(result.startsWith("0")){
			result = result.substring(1, result.length());
		}
		if(result.startsWith("0")){
			result = result.substring(1, result.length());
		}
		if(flagNegative){
			result = "-"+result;
		}
		return result;
        }
        /***Added By Aniket Desai on 28th Dec, 2016: --Start-- ***/
        else {
        	String formattedString = null;
            // Foreign currency
            NumberFormat format = NumberFormat.getInstance();
            format.setMaximumFractionDigits(2);
            format.setMinimumFractionDigits(2);
            Currency currency = null;
            currency = Currency.getInstance(currencyCode.toUpperCase());
            format.setCurrency(currency);
            formattedString = format.format(bigAmount);
            return formattedString;
        }
        /***Added By Aniket Desai on 28th Dec, 2016: --End-- ***/
		
		
	}
	//Vishal Kanungo
	public static String amountConvertDoubleWithCommaNoDecimal(Double number) {
		boolean flagNegative = false;
		if (number == 0) {
			return "0.00";
		}
		if (number<0){
			number = number*(-1);
			flagNegative = true;			
		}

		String snumber = Double.toString(number);
		
		// pad with "0"
		String mask = "000000000000.00";
		DecimalFormat df = new DecimalFormat(mask);
		snumber = df.format(number);

		// XXXXXnnnnnnn
		int crores = Integer.parseInt(snumber.substring(0, 5));
		// nnnnnXXnnnnn
		int lakhs = Integer.parseInt(snumber.substring(5, 7));
		// nnnnnnnXXnnn
		int hundredThousands = Integer.parseInt(snumber.substring(7, 9));
		// nnnnnnnnnXXX
		int thousands = Integer.parseInt(snumber.substring(9, 12));
		
		//int fraction = Integer.parseInt(snumber.substring(13, 15));

		String result= SwiftCoreConstants.BLANK_STRING;
		
		if(crores>0){
			result = new Integer(crores).toString()+",";
		}
		
		if(SwiftCoreConstants.BLANK_STRING.equalsIgnoreCase(result.trim())){
			if(lakhs>0){
				if(lakhs<10){
					result = "0"+result+new Integer(lakhs).toString()+",";
				}else{
					result = result+new Integer(lakhs).toString()+",";
				}
			}
		}else{
			if(lakhs>0){
				if(lakhs<10){
					result = result+"0"+new Integer(lakhs).toString()+",";
				}else{
					result = result+new Integer(lakhs).toString()+",";
				}
			}else{
				result = result+"00,";
			}
		}
		
		if(SwiftCoreConstants.BLANK_STRING.equalsIgnoreCase(result.trim())){
			if(hundredThousands>0){
				if(hundredThousands<10){
					result = "0"+new Integer(hundredThousands).toString()+",";
				}else{
					result = new Integer(hundredThousands).toString()+",";
				}
			}
		}else{
			if(hundredThousands>0){
				if(hundredThousands<10){
					result = result+"0"+new Integer(hundredThousands).toString()+",";
				}else{
					result = result+new Integer(hundredThousands).toString()+",";
				}
			}else{
				result = result+"00,";
			}
		}
		
		if(SwiftCoreConstants.BLANK_STRING.equalsIgnoreCase(result.trim())){
			if(thousands>0){
				if(thousands<10){
					result = "00"+new Integer(thousands).toString()+".";
				}else if(thousands<100){
					result = "0"+new Integer(thousands).toString()+".";
				}else{
					result = new Integer(thousands).toString()+".";
				}
			}else{
				result = "0.";
			}
		}else{
			if(thousands>0){
				if(thousands<10){
					result = result+"00"+new Integer(thousands).toString()+".";
				}else if(thousands<100){
					result = result+"0"+new Integer(thousands).toString()+".";
				}else{
					result = result+new Integer(thousands).toString()+".";
				}
			}else{
				result = result+"000.";
			}
		}
		
		/*if(fraction == 0){
			result = result+"00";
		}else if(fraction > 9){
			result = result+new Integer(fraction).toString();
		}else{
			result = result+"0"+new Integer(fraction).toString();
		}*/
		
		if(result.startsWith("0")){
			result = result.substring(1, result.length());
		}
		if(result.startsWith("0")){
			result = result.substring(1, result.length());
		}
		if(flagNegative){
			result = "-"+result;
		}
		result = removeLastChar(result);
		return result;
	}
	public static String amountConvertDoubleWithCommaForPasbook(Double number) {
		boolean flagNegative = false;
		if (number == 0) {
			return "0.00";
		}
		if (number<0){
			number = number*(-1);
			flagNegative = true;			
		}

		String snumber = Double.toString(number);

		// pad with "0"
		String mask = "000000000000.00";
		DecimalFormat df = new DecimalFormat(mask);
		snumber = df.format(number);

		// XXXXXnnnnnnn
		int crores = Integer.parseInt(snumber.substring(0, 5));
		// nnnnnXXnnnnn
		int lakhs = Integer.parseInt(snumber.substring(5, 7));
		// nnnnnnnXXnnn
		int hundredThousands = Integer.parseInt(snumber.substring(7, 9));
		// nnnnnnnnnXXX
		int thousands = Integer.parseInt(snumber.substring(9, 12));
		
		int fraction = Integer.parseInt(snumber.substring(13, 15));

		String result= SwiftCoreConstants.BLANK_STRING;
		
		if(crores>0){
			result = new Integer(crores).toString()+",";
		}
		
		if(SwiftCoreConstants.BLANK_STRING.equalsIgnoreCase(result.trim())){
			if(lakhs>0){
				if(lakhs<10){
					result = "0"+result+new Integer(lakhs).toString()+",";
				}else{
					result = result+new Integer(lakhs).toString()+",";
				}
			}
		}else{
			if(lakhs>0){
				if(lakhs<10){
					result = result+"0"+new Integer(lakhs).toString()+",";
				}else{
					result = result+new Integer(lakhs).toString()+",";
				}
			}else{
				result = result+"00,";
			}
		}
		
		if(SwiftCoreConstants.BLANK_STRING.equalsIgnoreCase(result.trim())){
			if(hundredThousands>0){
				if(hundredThousands<10){
					result = "0"+new Integer(hundredThousands).toString()+",";
				}else{
					result = new Integer(hundredThousands).toString()+",";
				}
			}
		}else{
			if(hundredThousands>0){
				if(hundredThousands<10){
					result = result+"0"+new Integer(hundredThousands).toString()+",";
				}else{
					result = result+new Integer(hundredThousands).toString()+",";
				}
			}else{
				result = result+"00,";
			}
		}
		
		if(SwiftCoreConstants.BLANK_STRING.equalsIgnoreCase(result.trim())){
			if(thousands>0){
				if(thousands<10){
					result = "00"+new Integer(thousands).toString()+".";
				}else if(thousands<100){
					result = "0"+new Integer(thousands).toString()+".";
				}else{
					result = new Integer(thousands).toString()+".";
				}
			}else{
				result = "0.";
			}
		}else{
			if(thousands>0){
				if(thousands<10){
					result = result+"00"+new Integer(thousands).toString()+".";
				}else if(thousands<100){
					result = result+"0"+new Integer(thousands).toString()+".";
				}else{
					result = result+new Integer(thousands).toString()+".";
				}
			}else{
				result = result+"000.";
			}
		}
		
		if(fraction == 0){
			result = result+"00";
		}else if(fraction > 9){
			result = result+new Integer(fraction).toString();
		}else{
			result = result+"0"+new Integer(fraction).toString();
		}
		if(result.startsWith("0") && !result.startsWith("0.")){
			result = result.substring(1, result.length());
		}
		if(result.startsWith("0")&& !result.startsWith("0.")){
			result = result.substring(1, result.length());
		}
		
		/*if(flagNegative){
			result = "-"+result;
		}*/
		
		return result;
	}
	
	private static String removeLastChar(String str) {
        return str.substring(0,str.length()-1);
    }
	
}

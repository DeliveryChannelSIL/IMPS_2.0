package com.sil.dao;

public class SwiftCoreUtil {
	public static String appendZeroPadding(String number,Long length){
		for (int i = number.length(); i < length; i++) {
			number = "0" + number;
		}
		return number;
	}
	
	public static String splitAccountNumber(String accountNumber) {
		if (accountNumber != null && accountNumber.trim().length() == 15) {
			accountNumber = Long.valueOf(accountNumber.substring(0, 3)) + "/"
					+ Long.valueOf(accountNumber.substring(3, 7)) + "/"
					+ Long.valueOf(accountNumber.substring(7, 15));
			return accountNumber;
		} else
			return null;
	}
	public static String getAccountNumber(String prodCode,String accountNo,String subaccNo){
		if(accountNo == null || accountNo.trim().equalsIgnoreCase("")){
			accountNo = "00000000";
		}
		if(subaccNo == null || subaccNo.trim().equalsIgnoreCase("")){
			subaccNo = "00000000";
		}
		return 	prependZeroPadding(prodCode.trim()) + "00000000" + appendZeroPadding(accountNo.trim()) + appendZeroPadding(subaccNo.trim());
	}
	
	public static String prependZeroPadding(String number){
		for (int i = number.length(); i < 8; i++) {
			number = number + " ";
		}
		return number;
	}
	public static String appendZeroPadding(String number){
		for (int i = number.length(); i < 8; i++) {
			number = "0" + number;
		}
		return number;
	}
	public static String[] tokenizAccountNumber1(String drAccountNo) {
		// TODO Auto-generated method stub
		return null;
	}
	
	/**
	 * Following method will return the 8 digit product number in string format
	 * @param productNo
	 * @return productNo
	 */
	public static String getProductNumber(String productNo) {
		if(productNo!=null && productNo.length()>=8){
			productNo = productNo.substring(0, 8).trim();
		}
		return productNo;
	}

	/*
	
	public static DecimalFormat twoDecPlaces = new DecimalFormat("0.00");
	public static final int ORDERING_ASC=1;
	public static final int ORDERING_DSC=2;
	
	*//**
	 * Following method will return the 32 digits account number in string format
	 * @param prodCode
	 * @param accountNo
	 * @param subaccNo
	 * @return AccountNumber
	 *//*
	public static String getAccountNumber(String prodCode,String accountNo,String subaccNo){
		if(accountNo == null || accountNo.trim().equalsIgnoreCase("")){
			accountNo = "00000000";
		}
		if(subaccNo == null || subaccNo.trim().equalsIgnoreCase("")){
			subaccNo = "00000000";
		}
		return 	prependZeroPadding(prodCode) + "00000000" + appendZeroPadding(accountNo.trim()) + appendZeroPadding(subaccNo.trim());
	}
	
	*//**
	 * 
	 * @param number
	 * @return number
	 *//*
	public static String appendZeroPadding(String number){
		for (int i = number.length(); i < 8; i++) {
			number = "0" + number;
		}
		return number;
	}
	
	*//**
	 * 
	 * @param number
	 * @return
	 *//*
	public static String prependZeroPadding(String number){
		for (int i = number.length(); i < 8; i++) {
			number = number + " ";
		}
		return number;
	}
	
	*//**
	 * Following method will return the product and account number in productNo/accountNo format
	 * @param accountNo
	 * @return ProductAccountNumber
	 *//*
	public static String getProductAccountNumber(String productAccountNo){
		if(productAccountNo!=null && productAccountNo.trim().length()==32){
		    String productNo="";
		    String accountNo="";
		    productNo = productAccountNo.substring(0, 8).trim();
			accountNo = String.valueOf(Integer.parseInt(productAccountNo.substring(16, 24)));
			if(!accountNo.equalsIgnoreCase("0")){
			    productNo = productNo+"/ "+accountNo;
	            if(Integer.parseInt(productAccountNo.substring(24, 32))!=0){
	                productNo = productNo+"/ "+String.valueOf(Integer.parseInt(productAccountNo.substring(24, 32)));
	            }
			}
			//return productCode+"/"+accountId;
			return productNo;
		}
		return null;
	}
	
	
	
	*//**
	 * Following method will return the 8 digits account number in string format
	 * @param accountNo
	 * @return accountNo
	 *//*
	public static String getAccountNumber(String accountNo) {
		if(accountNo!=null && accountNo.length()>8){
			accountNo = accountNo.substring(16, 24);
			return String.valueOf(Long.parseLong(accountNo.trim()));
		}
		return accountNo;
	}
	
	*//**
	 * Following method will return the 8 digits sub account number in string format
	 * @param accountNo
	 * @return sub accountNo
	 *//*
	public static String getSubAccountNumber(String accountNo) {
		if(accountNo!=null && accountNo.length()>16){
			accountNo = accountNo.substring(24,32);
			return String.valueOf(Long.parseLong(accountNo.trim()));
		}
		return accountNo;
	}
	
	*//**
   * This method returns the minimum balance between Shadow Clear,Shadow Total,
   * Actual Clear and Actual Total Balances.
   * 
   * @author Mandar Palekar
   * @version $Revision$
   * @param Account
   * @return Double Double Mar 10, 2011
   *//*
  public static Double getMinBalanceFromAllAccountBalances(D009022 account) {
    Double minBal = account.getShdClrBalFcy();
    if (account.getShdTotBalFcy() < minBal) {
      minBal = account.getShdTotBalFcy();
    }
    if (account.getActClrBalFcy() < minBal) {
      minBal = account.getActClrBalFcy();
    }
    if (account.getActTotBalFcy() < minBal) {
      minBal = account.getActTotBalFcy();
    }
    if (account.getActTotBalLcy() < minBal) {
      minBal = account.getActTotBalLcy();
    }
    return minBal;
  }
	
	public static String prependZeroInsNo(String number){
		for (int i =number.length(); i < 12; i++) {
			number = "0" + number;
		}

		return number;
	}
	public static String appendZeroInstruNo(String number){
		for (int i = number.length(); i < 12; i++) {
			number = "0" + number;
		}
		return number;
	}
	 Account status
	public static String getAccStatus(Long id)
	{
		Map<Long, String> hmStatus=	setAccStatus();
		return hmStatus.get(id) ;
	}

	public static Map<Long, String> setAccStatus()
	{
		
		Map<Long, String> hmStatus = new HashMap<Long, String>();
		hmStatus.put(new Long(1),"ANY OPERATIONS ALLOWED");
		hmStatus.put(new Long(2),"NEW");
		hmStatus.put(new Long(3),"TRANSFERRED / CLOSED");
		hmStatus.put(new Long(4),"INOPERATIVE");
		hmStatus.put(new Long(5),"DORMANT");
		hmStatus.put(new Long(6),"BLOCKED / FREEZE");
		hmStatus.put(new Long(7),"LIEN FOR BANK'S LIABILITY");
		hmStatus.put(new Long(8),"LIQUIDATION / INSOLVENCY");
		hmStatus.put(new Long(9),"CALLED BACK ACCOUNT");
		hmStatus.put(new Long(10),"SUIT FILED");
		hmStatus.put(new Long(11),"NPA ACCOUNTS");
		hmStatus.put(new Long(12),"INTEREST SUSPENDED");
		hmStatus.put(new Long(13),"GARNISHEE ORDER");
		hmStatus.put(new Long(14),"DEATH / INSANITY");
		hmStatus.put(new Long(15),"INCOME TAX ATTACHMENT");
		hmStatus.put(new Long(16),"SALES TAX ATTACHMENT");
		hmStatus.put(new Long(17),"WEALTH TAX ATTACHMENT");
		hmStatus.put(new Long(18),"AWARDED BY GOVT.");
		hmStatus.put(new Long(19),"AWARDED BY COURT");
		hmStatus.put(new Long(20),"AWARDED-REMOTE CHANCES OF RECOVERY");
		return hmStatus;
	}
	*//**
	 * 
	 * @date
	 * @author Shubhra Nerurkar
	 * @param number (String)
	 * @description gives number with zeros appended
	 * @return number 
	 * @version $Revision$
	 *//*
	public static String appendZeroRemitt(String number){
		if(number!=null)
		{
			for (int i =number.length(); i < 28; i++) 
			{
				for(int j=number.length(); j < 8; j++)
				{
					number = number + " "  ;
				}
				number = number + "0";
			}
		}else{
			return number;
		}

		return number;
	}
	*//**
	 * 
	 * @date Apr 18, 2011
	 * @author Shubhra Nerurkar
	 * @param account no
	 * @description method gives receipt number from account number
	 * @return receipt number
	 * @version $Revision$
	 *//*
	public static String getReceiptNumber(String accountNo){
		if(accountNo!=null && accountNo.trim().length()==32){
			String receiptNo = String.valueOf(Long.parseLong(accountNo.substring(24,32).trim()));
			return receiptNo;
		}
		return null;
	}
	
	*//**
	 * 
	 * @date Apr 20,2011
	 * @author Shubhra Nerurkar
	 * @param prodCode,accountNo,subaccNo
	 * @description method gives acoount number for TD
	 * @return 	TDAccountNumber
	 * @version $Revision$
	 *//*
	public static String getTDAccountNumber(String prodCode,String accountNo,String subaccNo){
		
		if(accountNo == null || accountNo.trim().equalsIgnoreCase("")){
			accountNo = "00000000";
		}
		if(subaccNo == null || subaccNo.trim().equalsIgnoreCase("")){
			subaccNo = "00000000";
		}
		return 	prependZeroPadding(prodCode) + "00000000" + appendZeroPadding(accountNo) + appendZeroPadding(subaccNo);
		
	}
	
	public static void main(String [] args){
		System.out.println(SwiftCoreUtil.getProductAccountNumber("MDS     000000000000000300000030"));
	}
	
	
	public static String appendZeroPadding(String number,Long length){
		for (int i = number.length(); i < length; i++) {
			number = "0" + number;
		}
		return number;
	}
	
	*//**
	 * 
	 * @param number
	 * @return
	 *//*
	public static String prependZeroPadding(String number,Long length){
		for (int i = number.length(); i < length; i++) {
			number = number + "0";
		}
		return number;
	}
	*//**
	 * 
	 * @date: Nov 10,2011
	 * @author: sonali.pise
	 * @param 
	 * @description : Some of the status description is different for few account status validation.
	 * 				  so a new static Map<Long, String> setNewAccStatus() is created to handle the account status
	 * 				  for account closure validations.	
	 * @return 
	 * @throws  SQLException
	 * @throws  DataAccessException
	 * @version $Revision$
	 *//*
	public static Map<Long, String> setNewAccStatus()
	{		
		Map<Long, String> hmStatus = new HashMap<Long, String>();
		hmStatus.put(new Long(1),"ANY OPERATIONS ALLOWED");
		hmStatus.put(new Long(2),"OPERATION IN NEW ACCOUNT");
		hmStatus.put(new Long(3),"Account is closed ");
		hmStatus.put(new Long(4),"Inoperative Account");
		hmStatus.put(new Long(5),"Dormant Account");
		hmStatus.put(new Long(6),"Operation in freezed /blocked acct");
		hmStatus.put(new Long(7),"Oprn. In lien for banks liability");
		hmStatus.put(new Long(8),"Operation in LIQUIDATION / INSOLVENCY");
		hmStatus.put(new Long(9),"OPEARTION IN CALLED BACK ACCOUNTS");
		hmStatus.put(new Long(10),"Suit filed account");
		hmStatus.put(new Long(11),"Operation in NPA Account");
		hmStatus.put(new Long(12),"Interest Suspended account");
		hmStatus.put(new Long(13),"Garnishee Account");
		hmStatus.put(new Long(14),"ACCOUNT IS HALF CLOSED");
		hmStatus.put(new Long(15),"OPERATION IN INCOME TAX ATTACHMENT");
		hmStatus.put(new Long(16),"OPERATION IN SALES TAX ATTACHMENT");
		hmStatus.put(new Long(17),"OPRN IN WEALTH TAX ATTACHMENT");
		hmStatus.put(new Long(18),"Operation in state/Central Govt. Order");
		hmStatus.put(new Long(19),"OPERTION IN COURT ORDER");
		hmStatus.put(new Long(20),"OPRN IN AWARD CHANCE OF RECOVERY");
		return hmStatus;
	}
	*//**
	 * 
	 * @date: Nov 10,2011
	 * @author: sonali.pise
	 * @param 
	 * @description 	
	 * @return 
	 * @throws  SQLException
	 * @throws  DataAccessException
	 * @version $Revision$
	 *//*
	public static String getNewAccStatus(Long id){
		Map<Long, String> hmStatus=	setNewAccStatus();
		return hmStatus.get(id) ;
	}
	
	*//**
	 * 
	 * @param number
	 * @return
	 *//*
	public static String prependSpacePadding(String number,Long length){
		for (int i = number.length(); i < length; i++) {
			number = number + " ";
		}
		return number;
	}
	
	*//**
	 * @date : Feb 23, 2012
	 * @author : Shubhra Nerurkar
	 * method is used to get amount with * appended
	 *//*
	public static String appendStar(Integer number)
	{
		String value = "";
		for (int i = number; i < 14; i++) {
			value = "*" + value;
		}
		return value;
	}
	*//**
	 * 
	 * @param str
	 * @param size
	 * @return
	 *//*
	public static String prependSpaces(String str, int size){
		for (int i = str.length(); i < size; i++) {		
			str = " " + str;
		}
		return str;
	}
		*//**
	 * @date Mar 1, 2012
	 * @author Amit Shrivastava
	 * @param 
	 * @description 
	 * @return 
	 * @throws  SQLException
	 * @throws  DataAccessException
	 * @version $Revision$
	*//*
	public static String getActualPath(ServletContext context){
	//	String path=context.getRealPath("/") + "documents/";
		return SwiftCoreProperties.getProperty("FILEPATH");
		// return path;
	}
	*//**
	 * @date Mar 1, 2012
	 * @author Amit Shrivastava
	 * @param 
	 * @description 
	 * @return 
	 * @throws  SQLException
	 * @throws  DataAccessException
	 * @version $Revision$
	*//*
	public static String getDisplayURL(FacesContext facesContext ){
		  return SwiftCoreProperties.getProperty("DOCSERVER")+"tmp/";
		
		}
		
		*//**
		 * @date Apr 25, 2012
		 * @author Jayendra Agrawal
		 * @param 
		 * @description 
		 * @return 
		 * @throws  SQLException
		 * @throws  DataAccessException
		 * @version $Revision$
		*//*
	public static String getBatchDisplayURL(FacesContext facesContext ){
	  return SwiftCoreProperties.getProperty("DOCSERVER");
	}
	
	*//**
	 * @date July 24, 2012
	 * @author Jayendra Agrawal
	 * @param 
	 * @description 
	 * @return 
	 * @throws  SQLException
	 * @throws  DataAccessException
	 * @version $Revision$
	*//*
	public static String getDocumentPath(){
	  return SwiftCoreProperties.getProperty("DOCSERVERPATH");
	}
	
	*//**
	 * @date Mar 31, 2012
	 * @author Saurabh Tiwari
	 * @param prdAcctId(RD      000000000000176600000001)
	 * @description 
	 * @return prdAcctId(RD/1766/1)
	*/
	public static String getAccountNoFormatted(String prdAcctId){
		
		String str1[] = new String[3];
		
		str1[0] = prdAcctId.substring(0, 8);
		str1[1] = prdAcctId.substring(16, 24);
		str1[2] = prdAcctId.substring(24, 32);
		
		String prdAcctNo = "";
		String prodNo = str1[0].trim();
		Long acctNo = Long.parseLong(str1[1]);
		Long subAcctNo = Long.parseLong(str1[2]);
			// Modified by Vishal Knungo for Bug #6210
		if(subAcctNo == 0L){
			prdAcctNo = prodNo+"/"+acctNo;
		}else{
			prdAcctNo = prodNo+"/"+acctNo+"/"+subAcctNo;
		}
		
		return prdAcctNo;
	}
	/**
	 * sonali.pise
	 * @param productAccountNo
	 * @return
	 *//*
	public static String getProductAccountNumberWithoutSubAcct(String productAccountNo){
		if(productAccountNo!=null && productAccountNo.trim().length()==32){
		    String productNo="";
		    String accountNo="";
		    productNo = productAccountNo.substring(0, 8).trim();
			accountNo = String.valueOf(Integer.parseInt(productAccountNo.substring(16, 24)));
			if(!accountNo.equalsIgnoreCase("0")){
			    productNo = productNo+"/"+accountNo;	            
			}
			return productNo;
		}
		return null;
	}

	public static double round2Dec (double a){



		double result = a * 100;
		result = Math.round(result);
		result = result / 100;
		return result;

	}

	public static String twoDecString (Double a){
		return twoDecPlaces.format(a);
	}
	
	public static String roundedDouble(Double value)
	{
		return String.format("%.2g%n",value);
	}
	
	*//**
	 * @date : May 14, 2012
	 * @author : Shubhra Nerurkar
	 * @param lbrcode
	 * @param productOrActivity
	 * @param date
	 * @return
	 *//*
	public static String getFileName(Long lbrcode, String productOrActivity, Date date)
	{
		String fileName = "";
		String formattedDate = DateUtility.geDateForFileGeneration(date);
		fileName = lbrcode.toString()+"_"+productOrActivity.toString()+"_"+formattedDate;
		return fileName;
	}
	
	public static List<TermDepositReceipts> sortListTDList(List<TermDepositReceipts> tdrecipts) {
		Collections.sort(tdrecipts, new Comparator<Object>() {
			public int compare(Object o1, Object o2) {
				TermDepositReceipts p1 = (TermDepositReceipts) o1;
				TermDepositReceipts p2 = (TermDepositReceipts) o2;
				return p1.getId().getPrdacctid().compareTo(p2.getId().getPrdacctid());
			}
		});
		return tdrecipts;
	}
	
	*//**
	 * @date July 25, 2012
	 * @author Jayendra Agrawal
	 * @param 
	 * @description 
	 * @return 
	 * @throws  SQLException
	 * @throws  DataAccessException
	 * @version $Revision$
	*//*
	public static boolean fileOpenOrSaveOption(String completeReportPath){
		
		boolean retuenStatus = false;
		if(completeReportPath != null){
			
			InputStream inputStream = null;
		    try {
		    	inputStream = FileUtils.openInputStream(new File(completeReportPath));
		    	
		    	FacesContext facesContext = FacesContext.getCurrentInstance();
			    ExternalContext externalContext = facesContext.getExternalContext();
			    HttpServletResponse response = (HttpServletResponse) externalContext.getResponse();

			    // Get File Name and File Format to set
			    String tempFileFormat[] = completeReportPath.split("\\.");
			    String tempFileName[] = completeReportPath.split("\\/");
			    String fileName = completeReportPath;
			    String fileFormat = "pdf";
			    if(tempFileName != null && tempFileName.length>0){
			    	fileName = tempFileName[tempFileName.length-1];
			    }
			    if(tempFileFormat != null && tempFileFormat.length>0){
			    	fileFormat = tempFileFormat[tempFileFormat.length-1];
			    }
			    response.reset();
			    response.setContentType("application/"+fileFormat);
			    response.setHeader("Content-disposition", "attachment; filename="+fileName);

			    BufferedInputStream input = null;
			    BufferedOutputStream output = null;
			    try {
			    	inputStream = FileUtils.openInputStream(new File(completeReportPath)); 
			        input = new BufferedInputStream(inputStream);
			        output = new BufferedOutputStream(response.getOutputStream());

			        byte[] buffer = new byte[10240];
			        for (int length; (length = input.read(buffer)) > 0;) {
			            output.write(buffer, 0, length);
			        }
			        retuenStatus = true;
			    }catch (Exception e) {
					retuenStatus = false;
				} 
			    finally {
			        try{
			        	output.close();
			        	input.close();
			        	output= null;
			        	input = null;
			        }catch (Exception e) {
			        	retuenStatus = false;
					}
			    }
			    facesContext.responseComplete();
			    
		    }catch(FileNotFoundException e) {
		    	System.out.println("File do not present in specified path.");
		    	retuenStatus = false;
			}catch (Exception e) {
				retuenStatus = false;
			} finally {
		        try{
		        	inputStream.close();
		        	inputStream=null;
		        }catch (Exception e) {
		        	retuenStatus = false;
				}
		    }			
		}
        return retuenStatus;
	}
	
	public static List<DPMaster> sortList(List<DPMaster> dailyDp) {
		Collections.sort(dailyDp, new Comparator<Object>() {
			public int compare(Object o1, Object o2) {
				DPMaster p1 = (DPMaster) o1;
				DPMaster p2 = (DPMaster) o2;
				return p1.getId().getDpdate().compareTo(p2.getId().getDpdate());
			}
		});
		return dailyDp;
	}
	
	public String roundOff2Dec(String temp){
		DecimalFormat twoDecPlaces = new DecimalFormat("#.##");
		String str=twoDecPlaces.format(temp);
		return str;
	}
	public static double round2DecBoth(double a){
		double temp;
		if(a<0){
			temp=-a;	
		}else{
			temp=a;
		}
		double result = temp * 100;
		result = Math.round(result);
		result = result / 100;
		if(a<0){
			result=-result;
		}			
		return result;


	}
	
	
	public static DefaultTransactionDefinition getDefaultTxnDefinition(){
		DefaultTransactionDefinition defaultTransactionDefinition = new DefaultTransactionDefinition(TransactionDefinition.PROPAGATION_REQUIRED);
		defaultTransactionDefinition.setIsolationLevel(TransactionDefinition.ISOLATION_DEFAULT);
		return defaultTransactionDefinition;
	}
	
	*//**
	 * @date June 10, 2013
	 * @author Mukesh Patidar
	 * @param accountNumber(062203100002547)
	 * @description 
	 * @return prdAcctId(62/2031/2547)
	*//*
	public static String splitAccountNumber(String accountNumber) {
		if (accountNumber != null && accountNumber.trim().length() == 15) {
			accountNumber = Long.valueOf(accountNumber.substring(0, 3)) + "/"
					+ Long.valueOf(accountNumber.substring(3, 7)) + "/"
					+ Long.valueOf(accountNumber.substring(7, 15));
			return accountNumber;
		} else
			return null;
	}
	public static String getUploadPath(){
		  return SwiftCoreProperties.getProperty("FILEPATH");
		}
	
	*//**
	 * @date March 06, 2013
	 * @author Jayendra Agrawal
	 * @param 
	 * @description 
	 * @return Round value as per input
	 * @throws  SQLException
	 * @throws  DataAccessException
	 * @version $Revision$
	*//*
	public static double round(double value, int places) {
	    if (places < 0) throw new IllegalArgumentException();

	    long factor = (long) Math.pow(10, places);
	    value = value * factor;
	    long tmp = Math.round(value);
	    return (double) tmp / factor;
	}
	//added by waheed mohd for Shares to get memrefno.eg:RL000000000000000002
	public static String getMemRefNo(String prodCode,String accountNo){
		if(accountNo == null || accountNo.trim().equalsIgnoreCase("")){
			accountNo = "000000000000000000";
		}
		
		return 	prodCode + appendZeroPadding2(accountNo.trim());
	} 
	 public static String appendZeroPadding2(String number){
			for (int i = number.length(); i < 18; i++) {
				number = "0" + number;
			}
			return number;
	}
  //added by waheed Mohd for shares RL
  public static String getSharesSubAcctNo(String accountNo) {
		if(accountNo!=null && accountNo.length()>8){
				accountNo = accountNo.substring(10,20);
				return String.valueOf(Long.parseLong(accountNo.trim()));
			}
			return accountNo;
		}
  
  public static String appendZeroPaddingfoLookUp(String number){
		for (int i = number.length(); i < 9; i++) {
			number = "0" + number;
		}
		return number;
	}
  
  *//**
	 * @date Novemebr 21, 2013
	 * @author Vikas Gupta
	 * @param 
	 * @description 
	 * @return Logo For Advice
	*//*
  
  public static String getDisplayLOGOURL(FacesContext facesContext ){
//	  return SwiftCoreProperties.getProperty("LOGOSERVER")+"swiftcore/skins/default/images/logo-bank.gif";
	  return SwiftCoreProperties.getProperty("LOGOSERVER");
	
	}
  
  public static String appendZeroPaddingForSystem(String number){
	  String temp="";
	  for (int i = 0; i <Integer.parseInt(number); i++) {
		  temp = temp + "0";
	  }
	  return temp.trim();
  }
  *//**
	 * @date Dec 11, 2013
	 * @author Venu
	 * @param 
	 * @description For show Exception in error.jsp
	 * @throws  SQLException
	 * @throws  DataAccessException
	 * @version $Revision$
	*//*
  public static void showExceptions(Exception e) {
	  Error error = new Error();			
	  error.setStackTrace(e.getClass() + ": " +  e.getMessage() + ": " + e.getCause() + "\n" +  e.getStackTrace().toString());
	  FacesContext.getCurrentInstance().getApplication().getNavigationHandler().handleNavigation(FacesContext.getCurrentInstance(), null, "failure");
	  FacesContext.getCurrentInstance().getExternalContext().getRequestMap().put("Error", error);
  }
  
  public static List<Voucher> sortVoucherList(List<Voucher> vouchersIntAPP) {
		Collections.sort(vouchersIntAPP, new Comparator<Object>() {
			public int compare(Object o1, Object o2) {
				Voucher p1 = (Voucher) o1;
				Voucher p2 = (Voucher) o2;
				return p1.getValuedate().compareTo(p2.getValuedate());
			}
		});
		return vouchersIntAPP;
	}
  *//**
   * 
   *//*
  public static String getFloatinFormatNumber(int precision, Double numberToBeFormat){
	  BigDecimal bigDecimal = BigDecimal.valueOf(numberToBeFormat);
	  return String.format("%,."+precision+"f", bigDecimal);
  }

  public static Double format(Double serviceTaxTotAmt, String taxroffopt) {
	  if(taxroffopt.equalsIgnoreCase("F")){//nearest 1.00
		  serviceTaxTotAmt = BigDecimal.valueOf(Math.round(serviceTaxTotAmt)).doubleValue();  
	  }
	  return serviceTaxTotAmt;
  }
  
   *//**
   * For Random Number delay
   *//*
  public static int randInt(int min, int max) {
      Random rand = new Random();
      int randomNum = rand.nextInt((max - min) + 1) + min;
      return randomNum;
  }
  *//**
   * 
   * @date June 24, 2015
   * @author Venu
   * @param 
   * @description : Create all PDF & Image files.
   * @return 
   * @version $Revision$
   *//*
  public static String getCreateAdviceDocumentPath(){
	  return SwiftCoreProperties.getProperty("ADVICEPATH");
  }
  *//**
   * 
   * @date June 24, 2015
   * @author Venu
   * @param 
   * @description : Open all PDF & Image files.
   * @return 
   * @version $Revision$
   *//*
  public static String getOpenAdviceURL(FacesContext facesContext ){
	  return SwiftCoreProperties.getProperty("ADVICESERVER");
  }
  
  *//**
   * @date June 24, 2015
   * @author Venu
   * @param 
   * @description : Open all PDF & Image files.
   * @return 
   * @version $Revision$
   *//*
  public static String getOpenCTSURL(FacesContext facesContext ){
	  return SwiftCoreProperties.getProperty("CTSSERVER");
  }
  
  *//**
   * @date Dec 11, 2015
   * @author Amar dhakad
   * @param 
   * @description : Open Sign/Photo
   * @return 
   * @version $Revision$
   *//*
  public static String getCKYCSignPhotoURL(FacesContext facesContext ){
	  return SwiftCoreProperties.getProperty("DOCSERVER");
	}
  
  
  public static Map<String,String> getProdCurrency(){
	  
	  Map<String,String> curr_map=new HashMap<String, String>();
	  curr_map.put("EBUSD","USD");
	  curr_map.put("EBGBP","GBP");
	  curr_map.put("EBEUR","EUR");
	  return curr_map;
	  
  }
  
  *//**
	 * @date June 10, 2015
	 * @author Sandeep Upadhyay
	 * @param  Map<K,V>
	 * @description Sort The Map Obj on key basic
	 * @return Map<K,V>
	*//*
	public static <K extends Comparable,V extends Comparable> Map<K,V> sortByKeys(Map<K,V> map,int ordering_asc_dsc){
		List<K> keys = new LinkedList<K>(map.keySet());
		if(ordering_asc_dsc==SwiftCoreUtil.ORDERING_ASC){
			Collections.sort(keys);
		}else if(ordering_asc_dsc==SwiftCoreUtil.ORDERING_DSC){
			Collections.sort(keys,new Comparator<K>(){
				public int compare(K o1,K o2){
					return o2.compareTo(o1);
				}
			});
		}
		
		//LinkedHashMap will keep the keys in the order they are inserted
		//which is currently sorted on natural ordering
		Map<K,V> sortedMap = new LinkedHashMap<K,V>();
		for(K key: keys){
			sortedMap.put(key, map.get(key));
		}

		return sortedMap;
	}
  
	public static Double round2DecPlaces(Double value){
		  BigDecimal bigDecimal = new BigDecimal(value);
	      BigDecimal roundedWithScale = bigDecimal.setScale(3,RoundingMode.HALF_EVEN);
	      Double result= Math.round(roundedWithScale.multiply(new BigDecimal(100.00)).doubleValue())/100.00;
	      return result;	     
	}
	
	public static String appendZeroPaddingLocal(String number){
		for (int i = number.length(); i < 9; i++) {
			number = "0" + number;
		}
		return number;
	}
	public static String signatureUploadPath(){
		return SwiftCoreProperties.getProperty("SIGNATUREUPLOAD");
	}
	
	public static String[] tokenizAccountNumber1(String accountNumber){
		StringTokenizer st2 = null;
		String[] str = new String[3];
		try {
			st2 = new StringTokenizer(accountNumber, "/");
			while (st2.hasMoreElements()) {
				str[0] = st2.nextToken();
				str[1] = st2.nextToken();
				str[2] = st2.nextToken();
				return str;
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return str;
	}
*/

	
}
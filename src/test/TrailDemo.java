package test;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Random;
import java.util.TimeZone;

import org.apache.log4j.Logger;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
public class TrailDemo {
	private static Logger logger=Logger.getLogger(TrailDemo.class);
	public static void main(String[] args) {
		
		  if (1!=1) {
	
			  System.out.println("Transaction Reverted Successfully. Remitter account was credited back. 1");
	} else if (2==2) {
		System.out.println("Transaction Reverted Successfully. Remitter account was credited back. 2");
	} else if (3==3) {
		System.out.println("Transaction Reverted Successfully. Remitter account was credited back. 3");
	} else if (4==4) {
		System.out.println("Transaction Reverted Successfully. Remitter account was credited back. 4");
	} else {
		System.out.println("Transaction Reverted Successfully. Remitter account was credited back. 5");
	}
		
			System.exit(0);
		
		int j=0;
		for(int i=0; i<100000;i++){
		int a=111111+ new Random().nextInt(999999);
			if(a>999999 || a<111111){
			System.out.println("TrailDemo.main()erroe************"+a);
			j++;
			}
			
		}
		System.out.println("TrailDemo.main() j-- "+j++);
		System.exit(0);
		Calendar cal= Calendar.getInstance(Locale.getDefault());
		System.out.println("TrailDemo.main()"+cal.get(Calendar.HOUR_OF_DAY));
		System.out.println("TrailDemo.main()"+cal.get(Calendar.ZONE_OFFSET));
		DateFormat dateTime = new SimpleDateFormat("MMddHHmmss");
		dateTime.setTimeZone(TimeZone.getTimeZone("GMT"));
		Date date = new Date();
		String field7Value = dateTime.format(date).toString();
		System.out.println("TrailDemo.main()"+field7Value);
		System.exit(0);
		
		String toAccount="";
		
		String [] toAccArr=toAccount.split("\\|");
		if (toAccArr.length>2) {
			System.out.println("TrailDemo.main()3"+toAccArr[2]);
		}
		if(toAccArr.length>1){
			System.out.println("TrailDemo.main()2"+toAccArr[1]);
		}
		if(toAccArr.length>0){
			System.out.println("TrailDemo.main()1"+toAccArr[0]);
		}
//		System.out.println("TrailDemo.main()"+xyz.trim().length());
//		String abc=xyz.substring(0,6)+"XXXXXX"+xyz.substring(12);
//		System.out.println("ActOpenAction.searchAccLoan()"+abc);
		System.exit(0);
		
	for (int i=0;i<999999;i++){
		logger.error("TrailDemo.main() i::>>> " +i);
	try {
		
/*		HttpResponse<String> response = Unirest.post("http://10.100.10.221:8088/SarPOSControllerNEW/DepositPGM")
				  .header("content-type", "application/x-www-form-urlencoded")
				  .header("cache-control", "no-cache")
				  .header("postman-token", "e24ccf66-2529-a45e-6fc5-40a693c4b5ee")
				  .body("amt=1&srno=916047036&brcode=3&acctno=PGM%20%20%20%20%20000000000000000100000000")
				  .asString();

		HttpResponse<String> response1 = Unirest.post("http://10.100.9.78:8890/SarPOSControllerNEW/login")
				  .header("content-type", "application/x-www-form-urlencoded")
				  .header("cache-control", "no-cache")
				  .header("postman-token", "9a0da415-140f-b843-50fe-a77cb300ca8d")
				  .body("username=008005&password=Sil%401234&imei=861112038342920")
				  .asString();
	
		
		HttpResponse<String> response = Unirest.post("http://10.100.9.78:8890/SarPOSControllerNEW/DepositPGM")
				  .header("content-type", "application/x-www-form-urlencoded")
				  .header("cache-control", "no-cache")
				  .header("postman-token", "e24ccf66-2529-a45e-6fc5-40a693c4b5ee")
				  .body("amt=1&srno=008005&brcode=3&acctno=PGM%20%20%20%20%20000000000000000100000000")
				  .asString();
	
		HttpResponse<String> response2 = Unirest.post("http://10.100.9.78:8890/SarPOSControllerNEW/LogOut")
				  .header("content-type", "application/x-www-form-urlencoded")
				  .header("cache-control", "no-cache")
				  .header("postman-token", "bdb134b0-a354-1d2c-7441-8e0463ee1cf3")
				  .body("username=008005")
				  .asString();*/
	
		
//		http://10.100.5.238:9093/AccountOpening/transaction/mposCashWithDrawal?param1=004000000000000&param2=003001000006707&param3=BR&param4=1
//		http://10.100.5.238:9093/AccountOpening/transaction/mposCashDeposit?param1=004000000000000&param2=003001000007511&param3=BR&param4=1
		HttpResponse<String> response = Unirest.post("http://10.100.5.238:9093/AccountOpening/transaction/mposCashDeposit?param1=003001000006707&param2=003001000001782&param3=PGM&param4=1")
				  .header("content-type", "application/xml")
				  .header("cache-control", "no-cache")
				  .header("postman-token", "9a0da415-140f-b843-50fe-a77cb300ca8d")
				  .asString();

			HttpResponse<String> response1 = Unirest.post("http://10.100.5.238:9093/AccountOpening/request/searchPigMeAcc?param1=003001000006707&param2=&param3=99")
				  .header("content-type", "application/xml")
				  .header("cache-control", "no-cache")
				  .header("postman-token", "9a0da415-140f-b843-50fe-a77cb300ca8d")
				  
//				  .body("param1=003000000000000&param2=003001000006707&param3=PGM&param4=1")
				  .asString();
			System.out.println("response::>>"+response.getStatusText());
			System.out.println("response::>>"+response1.getStatusText()); 
	} catch (UnirestException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	}
	}
}

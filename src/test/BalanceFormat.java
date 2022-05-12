package test;

import java.math.BigDecimal;

public class BalanceFormat {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		Double amt=12902.440000000000509317032992839813232421875;
		
		System.out.println(String.valueOf(
				new BigDecimal(amt).setScale(2, BigDecimal.ROUND_HALF_UP)));
	}

}

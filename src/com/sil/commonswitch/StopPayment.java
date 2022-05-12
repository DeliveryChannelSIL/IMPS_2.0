package com.sil.commonswitch;

import java.util.Date;

import org.hibernate.Session;
import org.hibernate.Transaction;

import com.sil.hbm.D009044;
import com.sil.hbm.D010010;
import com.sil.hbm.D010010Id;
import com.sil.util.HBUtil;

public class StopPayment
{
	
	public static String getStopPayment(int lbr, String accNo, String chequeNo)
	{
		D009044 d009044 = ChequeStatus.getChequeStatus(lbr, accNo, chequeNo);
		Session session = HBUtil.getSessionFactory().openSession();
		if(d009044 == null)
		{
			return "11";
		}
		else{
			Transaction tx = session.beginTransaction();
			int status = d009044.getStatus();
			if(status==0)
			{
				d009044.setStatus((byte) 4);// 2 already used,0 unused 4 already stopped
				session.update(d009044);
				tx.commit();
				return String.valueOf("00");
			}else if(status==2)
				return String.valueOf(status);
			else if(status==4)
				return String.valueOf(status);
			
			String IssueTo = ""+d009044.getId().getIssuedTo();
			short InsType = d009044.getId().getInsType();

			tx =  session.beginTransaction();
			D010010 d010010 = new D010010();
			D010010Id d010010Id = new D010010Id();
			d010010Id.setLbrCode(lbr);
			d010010Id.setIssuedTo(IssueTo);
			d010010Id.setInsType(InsType);
			d010010Id.setInsNo(chequeNo);
			d010010.setId(d010010Id);
			d010010.setInstrDate(new Date());
			d010010.setStopDate(new Date());
			d010010.setStopTime(new Date());
			d010010.setRequestDate(new Date());
			d010010.setPayeeName("PAYMENT ANEM");
			d010010.setStopAmt(90000000);
			d010010.setRemarks("REMARK");
			d010010.setRevokeFlag((byte) 0);
			d010010.setRevokeDate(new Date());
			d010010.setDbtrAddMk(164);
			d010010.setDbtrAddMb(1);
			d010010.setDbtrAddMs((short) 1);
			d010010.setDbtrAddMd(new Date());
			d010010.setDbtrAddMt(new Date());
			d010010.setDbtrAddCk(0);
			d010010.setDbtrAddCb(0);
			d010010.setDbtrAddCs((short) 1);
			d010010.setDbtrAddCd(new Date());
			d010010.setDbtrAddCt(new Date());
			d010010.setDbtrLupdMk(1);
			d010010.setDbtrLupdMb(1);
			d010010.setDbtrLupdMs((short) 1);
			d010010.setDbtrLupdMd(new Date());
			d010010.setDbtrLupdMt(new Date());
			d010010.setDbtrLupdCk(1);
			d010010.setDbtrLupdCb(1);
			d010010.setDbtrLupdCd(new Date());
			d010010.setDbtrLupdCs((short) 0);
			d010010.setDbtrLupdCt(new Date());
			d010010.setDbtrTauthDone((short) 0);
			d010010.setDbtrRecStat((byte)0);
			d010010.setDbtrAuthDone((byte) 1);
			d010010.setDbtrAuthNeeded((byte) 0);
			d010010.setDbtrUpdtChkId((byte)0);
			d010010.setDbtrLhisTrnNo(0);
			session.save(d010010);
			tx.commit();
			
			return "00";
			
			
		}
	}
	
	public static void main(String[] args) 
	{
		
		String d009044 =	StopPayment.getStopPayment(1,"001200100000194","000000102564");
		
	}

}

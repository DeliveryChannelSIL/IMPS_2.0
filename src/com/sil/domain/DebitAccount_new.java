package com.sil.domain;             
public class DebitAccount_new
{
	private String FreeText2;
	private String FreeText1;
	private String TxnStatus;
	private String BankTxnId;
	private String FreeText3;
	private String BankCode;
	private String AccountNo;
	private String Version;
	private String TxnAmount;
	private String TxnId;
	private String TxnDesc;

	public String getFreeText2 ()
	{
		return FreeText2;
	}
	public void setFreeText2 (String FreeText2)
	{
		this.FreeText2 = FreeText2;
	}
	public String getFreeText1 ()
	{
		return FreeText1;
	}
	public void setFreeText1 (String FreeText1)
	{
		this.FreeText1 = FreeText1;
	}
	public String getTxnStatus ()
	{
		return TxnStatus;
	}
	public void setTxnStatus (String TxnStatus)
	{
		this.TxnStatus = TxnStatus;
	}
	public String getBankTxnId ()
	{
		return BankTxnId;
	}
	public void setBankTxnId (String BankTxnId)
	{
		this.BankTxnId = BankTxnId;
	}
	public String getFreeText3 ()
	{
		return FreeText3;
	}
	public void setFreeText3 (String FreeText3)
	{
		this.FreeText3 = FreeText3;
	}
	public String getBankCode ()
	{
		return BankCode;
	}
	public void setBankCode (String BankCode)
	{
		this.BankCode = BankCode;
	}
	public String getAccountNo ()
	{
		return AccountNo;
	}
	public void setAccountNo (String AccountNo)
	{
		this.AccountNo = AccountNo;
	}
	public String getVersion ()
	{
		return Version;
	}
	public void setVersion (String Version)
	{
		this.Version = Version;
	}
	public String getTxnAmount ()
	{
		return TxnAmount;
	}
	public void setTxnAmount (String TxnAmount)
	{
		this.TxnAmount = TxnAmount;
	}
	public String getTxnId ()
	{
		return TxnId;
	}
	public void setTxnId (String TxnId)
	{
		this.TxnId = TxnId;
	}

	public String getTxnDesc ()
	{
		return TxnDesc;
	}

	public void setTxnDesc (String TxnDesc)
	{
		this.TxnDesc = TxnDesc;
	}

	@Override
	public String toString()
	{
		return "ClassPojo [FreeText2 = "+FreeText2+", FreeText1 = "+FreeText1+", TxnStatus = "+TxnStatus+", BankTxnId = "+BankTxnId+", FreeText3 = "+FreeText3+", BankCode = "+BankCode+", AccountNo = "+AccountNo+", Version = "+Version+", TxnAmount = "+TxnAmount+", TxnId = "+TxnId+", TxnDesc = "+TxnDesc+"]";
	}
}
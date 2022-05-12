package com.sil.domain;             
public class DebitAccountResponse
{
	private DebitAccount DebitAccount;

	public DebitAccount getDebitAccount ()
	{
		return DebitAccount;
	}

	public void setDebitAccount (DebitAccount DebitAccount)
	{
		this.DebitAccount = DebitAccount;
	}

	@Override
	public String toString()
	{
		return "ClassPojo [DebitAccount = "+DebitAccount+"]";
	}
}

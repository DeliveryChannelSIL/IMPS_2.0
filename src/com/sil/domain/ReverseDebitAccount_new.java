package com.sil.domain;                       
public class ReverseDebitAccount_new 
{
    private ReverseDebitAccount_new ReverseDebitAccount;

    public ReverseDebitAccount_new getReverseDebitAccount ()
    {
        return ReverseDebitAccount;
    }

    public void setReverseDebitAccount (ReverseDebitAccount_new ReverseDebitAccount)
    {
        this.ReverseDebitAccount = ReverseDebitAccount;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [ReverseDebitAccount = "+ReverseDebitAccount+"]";
    }
}
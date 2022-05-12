package com.sil.domain;            
public class Header
{
    private String sessiontoken;

    public String getSessiontoken ()
    {
        return sessiontoken;
    }

    public void setSessiontoken (String sessiontoken)
    {
        this.sessiontoken = sessiontoken;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [sessiontoken = "+sessiontoken+"]";
    }
}
package com.sil.domain;
import java.io.Serializable;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class IMPSTransactionResponse  implements Serializable
{
  private static final long serialVersionUID = 1L;
  private String response = "";
  private String errorMessage = null;
  private String errorCode = null;
  private String rrnNo = " ";
  private String stan = " ";
  private String nickNameDebit = " ";
  private String nickNameCredit = " ";
  private String category = " ";
  private String URL = " ";
  private boolean isValid = false;
  private String nBin;
  private String bankName;
  private String refNo;
  private String custNo = "0";
  private Long lbrCode = Long.valueOf(0L);
  private String mobileNo = " ";
  private String accountNo = " ";
  
  
  public String getResponse()
  {
    return this.response;
  }
  
  public void setResponse(String response)
  {
    this.response = response;
  }
  
  
  public String getErrorMessage()
  {
    return this.errorMessage;
  }
  
  public void setErrorMessage(String errorMessage)
  {
    this.errorMessage = errorMessage;
  }
  
  
  public String getErrorCode()
  {
    return this.errorCode;
  }
  
  public void setErrorCode(String errorCode)
  {
    this.errorCode = errorCode;
  }
  
  
  public String getRrnNo()
  {
    return this.rrnNo;
  }
  
  public void setRrnNo(String rrnNo)
  {
    this.rrnNo = rrnNo;
  }
  
  
  public String getStan()
  {
    return this.stan;
  }
  
  public void setStan(String stan)
  {
    this.stan = stan;
  }
  
  
  public String getNickNameDebit()
  {
    return this.nickNameDebit;
  }
  
  public void setNickNameDebit(String nickNameDebit)
  {
    this.nickNameDebit = nickNameDebit;
  }
  
  
  public String getNickNameCredit()
  {
    return this.nickNameCredit;
  }
  
  public void setNickNameCredit(String nickNameCredit)
  {
    this.nickNameCredit = nickNameCredit;
  }
  
  
  public String getCategory()
  {
    return this.category;
  }
  
  public void setCategory(String category)
  {
    this.category = category;
  }
  
  
  public String getURL()
  {
    return this.URL;
  }
  
  public void setURL(String url)
  {
    this.URL = url;
  }
  
  
  public boolean isValid()
  {
    return this.isValid;
  }
  
  public void setValid(boolean isValid)
  {
    this.isValid = isValid;
  }
  
  
  public String getNBin()
  {
    return this.nBin;
  }
  
  public void setNBin(String bin)
  {
    this.nBin = bin;
  }
  
  
  public String getBankName()
  {
    return this.bankName;
  }
  
  public void setBankName(String bankName)
  {
    this.bankName = bankName;
  }
  
  
  public String getRefNo()
  {
    return this.refNo;
  }
  
  public void setRefNo(String refNo)
  {
    this.refNo = refNo;
  }
  
  
  public String getCustNo()
  {
    return this.custNo;
  }
  
  public void setCustNo(String custNo)
  {
    this.custNo = custNo;
  }
  
  
  public Long getLbrCode()
  {
    return this.lbrCode;
  }
  
  public void setLbrCode(Long lbrCode)
  {
    this.lbrCode = lbrCode;
  }
  
  
  public String getMobileNo()
  {
    return this.mobileNo;
  }
  
  public void setMobileNo(String mobileNo)
  {
    this.mobileNo = mobileNo;
  }
  
  
  public String getAccountNo()
  {
    return this.accountNo;
  }
  
  public void setAccountNo(String accountNo)
  {
    this.accountNo = accountNo;
  }

@Override
public String toString() {
	return "IMPSTransactionResponse [response=" + response + ", errorMessage=" + errorMessage + ", errorCode="
			+ errorCode + ", rrnNo=" + rrnNo + ", stan=" + stan + ", nickNameDebit=" + nickNameDebit
			+ ", nickNameCredit=" + nickNameCredit + ", category=" + category + ", URL=" + URL + ", isValid=" + isValid
			+ ", nBin=" + nBin + ", bankName=" + bankName + ", refNo=" + refNo + ", custNo=" + custNo + ", lbrCode="
			+ lbrCode + ", mobileNo=" + mobileNo + ", accountNo=" + accountNo + "]";
}
  
}

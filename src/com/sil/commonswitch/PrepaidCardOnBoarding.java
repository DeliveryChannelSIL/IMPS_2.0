package com.sil.commonswitch;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

@Entity
@Table(name = "PREPAID_CARD_ONBOARDING")
@DynamicInsert
@DynamicUpdate
public class PrepaidCardOnBoarding implements Serializable{
	private static final long serialVersionUID = 4193560463331314250L;
	
	private String lpincode;
	private String lcountry;
	private String AnnualIncome;
	private String state;
	private String resaddress1;
	private String lastname;
	private String rescountry;
	private String dateofbirth;
	private String resaddress2;
	private String PoliticallyExposedPerson;
	private String CustomerStatus;
	private String city;
	private String emailaddress;
	private String mobilenumber;
	private String mothermaidenname;
	private String gender;
	private String CustomerType;
	private String occupation;
	private String middlename;
	private String lcity;
	private String firstname;
	private String laddress1;
//	private Header header;
	private String laddress2;
	private String pincode;
	private String product;
	private String nationality;
	private String fatcadecl;
	private String cardAlias;
	private String SourceIncomeType;
	private String bcagent;
	private String lstate;
	private int branchno;
	private String accno;

	@Column(name = "lpincode")
	public String getLpincode() {
		return lpincode;
	}
	public void setLpincode(String lpincode) {
		this.lpincode = lpincode;
	}
	@Column(name = "lcountry")
	public String getLcountry() {
		return lcountry;
	}
	public void setLcountry(String lcountry) {
		this.lcountry = lcountry;
	}
	@Column(name = "AnnualIncome")
	public String getAnnualIncome() {
		return AnnualIncome;
	}
	public void setAnnualIncome(String AnnualIncome) {
		this.AnnualIncome = AnnualIncome;
	}
	
	@Column(name = "state")
	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}
	
	@Column(name = "resaddress1")
	public String getResaddress1() {
		return resaddress1;
	}

	public void setResaddress1(String resaddress1) {
		this.resaddress1 = resaddress1;
	}

	@Column(name = "lastname")
	public String getLastname() {
		return lastname;
	}

	public void setLastname(String lastname) {
		this.lastname = lastname;
	}
	@Column(name = "rescountry")
	public String getRescountry() {
		return rescountry;
	}

	public void setRescountry(String rescountry) {
		this.rescountry = rescountry;
	}
	@Column(name = "dateofbirth")
	public String getDateofbirth() {
		return dateofbirth;
	}

	public void setDateofbirth(String dateofbirth) {
		this.dateofbirth = dateofbirth;
	}
	@Column(name = "resaddress2")
	public String getResaddress2() {
		return resaddress2;
	}

	public void setResaddress2(String resaddress2) {
		this.resaddress2 = resaddress2;
	}
	@Column(name = "PoliticallyExposedPerson")
	public String getPoliticallyExposedPerson() {
		return PoliticallyExposedPerson;
	}

	public void setPoliticallyExposedPerson(String PoliticallyExposedPerson) {
		this.PoliticallyExposedPerson = PoliticallyExposedPerson;
	}
	
	@Column(name = "CustomerStatus")
	public String getCustomerStatus() {
		return CustomerStatus;
	}

	public void setCustomerStatus(String CustomerStatus) {
		this.CustomerStatus = CustomerStatus;
	}
	@Column(name = "city")
	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}
	@Column(name = "emailaddress")
	public String getEmailaddress() {
		return emailaddress;
	}

	public void setEmailaddress(String emailaddress) {
		this.emailaddress = emailaddress;
	}
	@Id
	@Column(name = "mobilenumber", unique = true, nullable = false)
	public String getMobilenumber() {
		return mobilenumber;
	}

	public void setMobilenumber(String mobilenumber) {
		this.mobilenumber = mobilenumber;
	}
	@Column(name = "mothermaidenname")
	public String getMothermaidenname() {
		return mothermaidenname;
	}

	public void setMothermaidenname(String mothermaidenname) {
		this.mothermaidenname = mothermaidenname;
	}
	@Column(name = "gender")
	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}
	@Column(name = "CustomerType")
	public String getCustomerType() {
		return CustomerType;
	}

	public void setCustomerType(String CustomerType) {
		this.CustomerType = CustomerType;
	}
	@Column(name = "occupation")
	public String getOccupation() {
		return occupation;
	}

	public void setOccupation(String occupation) {
		this.occupation = occupation;
	}
	
	@Column(name = "middlename")
	public String getMiddlename() {
		return middlename;
	}

	public void setMiddlename(String middlename) {
		this.middlename = middlename;
	}

	@Column(name = "lcity")
	public String getLcity() {
		return lcity;
	}

	public void setLcity(String lcity) {
		this.lcity = lcity;
	}
	@Column(name = "firstname")
	public String getFirstname() {
		return firstname;
	}

	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}
	@Column(name = "laddress1")
	public String getLaddress1() {
		return laddress1;
	}

	public void setLaddress1(String laddress1) {
		this.laddress1 = laddress1;
	}
	/*@Deprecated
	public Header getHeader() {
		return header;
	}

	public void setHeader(Header header) {
		this.header = header;
	}*/
	@Column(name = "laddress2")
	public String getLaddress2() {
		return laddress2;
	}

	public void setLaddress2(String laddress2) {
		this.laddress2 = laddress2;
	}
	@Column(name = "pincode")
	public String getPincode() {
		return pincode;
	}

	public void setPincode(String pincode) {
		this.pincode = pincode;
	}
	@Column(name = "product")
	public String getProduct() {
		return product;
	}

	public void setProduct(String product) {
		this.product = product;
	}
	@Column(name = "nationality")
	public String getNationality() {
		return nationality;
	}

	public void setNationality(String nationality) {
		this.nationality = nationality;
	}
	@Column(name = "fatcadecl")
	public String getFatcadecl() {
		return fatcadecl;
	}

	public void setFatcadecl(String fatcadecl) {
		this.fatcadecl = fatcadecl;
	}
	@Column(name = "cardAlias")
	public String getCardAlias() {
		return cardAlias;
	}

	public void setCardAlias(String cardAlias) {
		this.cardAlias = cardAlias;
	}
	@Column(name = "SourceIncomeType")
	public String getSourceIncomeType() {
		return SourceIncomeType;
	}

	public void setSourceIncomeType(String SourceIncomeType) {
		this.SourceIncomeType = SourceIncomeType;
	}
	@Column(name = "bcagent")
	public String getBcagent() {
		return bcagent;
	}

	public void setBcagent(String bcagent) {
		this.bcagent = bcagent;
	}
	@Column(name = "lstate")
	public String getLstate() {
		return lstate;
	}
	public void setLstate(String lstate) {
		this.lstate = lstate;
	}
	@Column(name = "BRANCHNO")	
	public int getBranchno() {
		return branchno;
	}
	public void setBranchno(int branchno) {
		this.branchno = branchno;
	}
	@Column(name = "ACCNO")
	public String getAccno() {
		return accno;
	}
	public void setAccno(String accno) {
		this.accno = accno;
	}
	@Override
	public String toString() {
		return "PrepaidCardOnBoarding [lpincode=" + lpincode + ", lcountry=" + lcountry + ", AnnualIncome="
				+ AnnualIncome + ", state=" + state + ", resaddress1=" + resaddress1 + ", lastname=" + lastname
				+ ", rescountry=" + rescountry + ", dateofbirth=" + dateofbirth + ", resaddress2=" + resaddress2
				+ ", PoliticallyExposedPerson=" + PoliticallyExposedPerson + ", CustomerStatus=" + CustomerStatus
				+ ", city=" + city + ", emailaddress=" + emailaddress + ", mobilenumber=" + mobilenumber
				+ ", mothermaidenname=" + mothermaidenname + ", gender=" + gender + ", CustomerType=" + CustomerType
				+ ", occupation=" + occupation + ", middlename=" + middlename + ", lcity=" + lcity + ", firstname="
				+ firstname + ", laddress1=" + laddress1 + ", laddress2=" + laddress2 + ", pincode=" + pincode
				+ ", product=" + product + ", nationality=" + nationality + ", fatcadecl=" + fatcadecl + ", cardAlias="
				+ cardAlias + ", SourceIncomeType=" + SourceIncomeType + ", bcagent=" + bcagent + ", lstate=" + lstate
				+ ", branchno=" + branchno + ", accno=" + accno + "]";
	}

}
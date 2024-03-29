package com.sil.hbm;
// Generated Feb 26, 2018 2:36:26 PM by Hibernate Tools 5.2.0.Beta1

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * PrepaidCardOnboarding generated by hbm2java
 */
@Entity
@Table(name = "D390077")
public class PrepaidCardOnboarding implements java.io.Serializable {

	private static final long serialVersionUID = -598286279439633028L;
	private PrepaidCardOnboardingId id;
	private String firstname;
	private String middlename;
	private String lastname;
	private String emailaddress;
	private String dateofbirth;
	private String mothermaidenname;
	private String gender;
	private int customerType;
	private String occupation;
	private String politicallyExposedPerson;
	private int annualIncome;
	private String city;
	private String state;
	private String rescountry;
	private String resaddress1;
	private String resaddress2;
	private String pincode;
	private String lcity;
	private String lstate;
	private String lcountry;
	private String laddress1;
	private String laddress2;
	private String lpincode;
	private String customerStatus;
	private String product;
	private String nationality;
	private int fatcadecl;
	private Integer sourceIncomeType;
	private String bcagent;
	private int branchno;
	private String accno;
	private String kycflag;
	private String onboardflag;
	private String rblCustId;
	private String rblAccNo;

	public PrepaidCardOnboarding() {
	}

	public PrepaidCardOnboarding(PrepaidCardOnboardingId id, String firstname, String emailaddress,
			String mothermaidenname, String gender, int customerType, String politicallyExposedPerson, int annualIncome,
			int fatcadecl, int branchno, String accno, String kycflag, String onboardflag, String rblCustId,
			String rblAccNo) {
		this.id = id;
		this.firstname = firstname;
		this.emailaddress = emailaddress;
		this.mothermaidenname = mothermaidenname;
		this.gender = gender;
		this.customerType = customerType;
		this.politicallyExposedPerson = politicallyExposedPerson;
		this.annualIncome = annualIncome;
		this.fatcadecl = fatcadecl;
		this.branchno = branchno;
		this.accno = accno;
		this.kycflag = kycflag;
		this.onboardflag = onboardflag;
		this.rblCustId = rblCustId;
		this.rblAccNo = rblAccNo;
	}

	public PrepaidCardOnboarding(PrepaidCardOnboardingId id, String firstname, String middlename, String lastname,
			String emailaddress, String dateofbirth, String mothermaidenname, String gender, int customerType,
			String occupation, String politicallyExposedPerson, int annualIncome, String city, String state,
			String rescountry, String resaddress1, String resaddress2, String pincode, String lcity, String lstate,
			String lcountry, String laddress1, String laddress2, String lpincode, String customerStatus, String product,
			String nationality, int fatcadecl, Integer sourceIncomeType, String bcagent, int branchno, String accno,
			String kycflag, String onboardflag, String rblCustId, String rblAccNo) {
		this.id = id;
		this.firstname = firstname;
		this.middlename = middlename;
		this.lastname = lastname;
		this.emailaddress = emailaddress;
		this.dateofbirth = dateofbirth;
		this.mothermaidenname = mothermaidenname;
		this.gender = gender;
		this.customerType = customerType;
		this.occupation = occupation;
		this.politicallyExposedPerson = politicallyExposedPerson;
		this.annualIncome = annualIncome;
		this.city = city;
		this.state = state;
		this.rescountry = rescountry;
		this.resaddress1 = resaddress1;
		this.resaddress2 = resaddress2;
		this.pincode = pincode;
		this.lcity = lcity;
		this.lstate = lstate;
		this.lcountry = lcountry;
		this.laddress1 = laddress1;
		this.laddress2 = laddress2;
		this.lpincode = lpincode;
		this.customerStatus = customerStatus;
		this.product = product;
		this.nationality = nationality;
		this.fatcadecl = fatcadecl;
		this.sourceIncomeType = sourceIncomeType;
		this.bcagent = bcagent;
		this.branchno = branchno;
		this.accno = accno;
		this.kycflag = kycflag;
		this.onboardflag = onboardflag;
		this.rblCustId = rblCustId;
		this.rblAccNo = rblAccNo;
	}

	@EmbeddedId

	@AttributeOverrides({
			@AttributeOverride(name = "mobilenumber", column = @Column(name = "mobilenumber", nullable = false, length = 13)),
			@AttributeOverride(name = "cardAlias", column = @Column(name = "cardAlias", nullable = false, length = 50)) })
	public PrepaidCardOnboardingId getId() {
		return this.id;
	}

	public void setId(PrepaidCardOnboardingId id) {
		this.id = id;
	}

	@Column(name = "firstname", nullable = false, length = 50)
	public String getFirstname() {
		return this.firstname;
	}

	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}

	@Column(name = "middlename", length = 50)
	public String getMiddlename() {
		return this.middlename;
	}

	public void setMiddlename(String middlename) {
		this.middlename = middlename;
	}

	@Column(name = "lastname", length = 50)
	public String getLastname() {
		return this.lastname;
	}

	public void setLastname(String lastname) {
		this.lastname = lastname;
	}

	@Column(name = "emailaddress", nullable = false, length = 100)
	public String getEmailaddress() {
		return this.emailaddress;
	}

	public void setEmailaddress(String emailaddress) {
		this.emailaddress = emailaddress;
	}

	@Column(name = "dateofbirth", length = 100)
	public String getDateofbirth() {
		return this.dateofbirth;
	}

	public void setDateofbirth(String dateofbirth) {
		this.dateofbirth = dateofbirth;
	}

	@Column(name = "mothermaidenname", nullable = false, length = 50)
	public String getMothermaidenname() {
		return this.mothermaidenname;
	}

	public void setMothermaidenname(String mothermaidenname) {
		this.mothermaidenname = mothermaidenname;
	}

	@Column(name = "gender", nullable = false, length = 50)
	public String getGender() {
		return this.gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	@Column(name = "CustomerType", nullable = false)
	public int getCustomerType() {
		return this.customerType;
	}

	public void setCustomerType(int customerType) {
		this.customerType = customerType;
	}

	@Column(name = "occupation", length = 100)
	public String getOccupation() {
		return this.occupation;
	}

	public void setOccupation(String occupation) {
		this.occupation = occupation;
	}

	@Column(name = "PoliticallyExposedPerson", nullable = false, length = 2)
	public String getPoliticallyExposedPerson() {
		return this.politicallyExposedPerson;
	}

	public void setPoliticallyExposedPerson(String politicallyExposedPerson) {
		this.politicallyExposedPerson = politicallyExposedPerson;
	}

	@Column(name = "AnnualIncome", nullable = false)
	public int getAnnualIncome() {
		return this.annualIncome;
	}

	public void setAnnualIncome(int annualIncome) {
		this.annualIncome = annualIncome;
	}

	@Column(name = "city", length = 100)
	public String getCity() {
		return this.city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	@Column(name = "state", length = 100)
	public String getState() {
		return this.state;
	}

	public void setState(String state) {
		this.state = state;
	}

	@Column(name = "rescountry", length = 100)
	public String getRescountry() {
		return this.rescountry;
	}

	public void setRescountry(String rescountry) {
		this.rescountry = rescountry;
	}

	@Column(name = "resaddress1", length = 100)
	public String getResaddress1() {
		return this.resaddress1;
	}

	public void setResaddress1(String resaddress1) {
		this.resaddress1 = resaddress1;
	}

	@Column(name = "resaddress2", length = 100)
	public String getResaddress2() {
		return this.resaddress2;
	}

	public void setResaddress2(String resaddress2) {
		this.resaddress2 = resaddress2;
	}

	@Column(name = "pincode", length = 6)
	public String getPincode() {
		return this.pincode;
	}

	public void setPincode(String pincode) {
		this.pincode = pincode;
	}

	@Column(name = "lcity", length = 100)
	public String getLcity() {
		return this.lcity;
	}

	public void setLcity(String lcity) {
		this.lcity = lcity;
	}

	@Column(name = "lstate", length = 100)
	public String getLstate() {
		return this.lstate;
	}

	public void setLstate(String lstate) {
		this.lstate = lstate;
	}

	@Column(name = "lcountry", length = 100)
	public String getLcountry() {
		return this.lcountry;
	}

	public void setLcountry(String lcountry) {
		this.lcountry = lcountry;
	}

	@Column(name = "laddress1", length = 100)
	public String getLaddress1() {
		return this.laddress1;
	}

	public void setLaddress1(String laddress1) {
		this.laddress1 = laddress1;
	}

	@Column(name = "laddress2", length = 100)
	public String getLaddress2() {
		return this.laddress2;
	}

	public void setLaddress2(String laddress2) {
		this.laddress2 = laddress2;
	}

	@Column(name = "lpincode", length = 6)
	public String getLpincode() {
		return this.lpincode;
	}

	public void setLpincode(String lpincode) {
		this.lpincode = lpincode;
	}

	@Column(name = "CustomerStatus", length = 20)
	public String getCustomerStatus() {
		return this.customerStatus;
	}

	public void setCustomerStatus(String customerStatus) {
		this.customerStatus = customerStatus;
	}

	@Column(name = "product", length = 50)
	public String getProduct() {
		return this.product;
	}

	public void setProduct(String product) {
		this.product = product;
	}

	@Column(name = "nationality", length = 50)
	public String getNationality() {
		return this.nationality;
	}

	public void setNationality(String nationality) {
		this.nationality = nationality;
	}

	@Column(name = "fatcadecl", nullable = false)
	public int getFatcadecl() {
		return this.fatcadecl;
	}

	public void setFatcadecl(int fatcadecl) {
		this.fatcadecl = fatcadecl;
	}

	@Column(name = "SourceIncomeType")
	public Integer getSourceIncomeType() {
		return this.sourceIncomeType;
	}

	public void setSourceIncomeType(Integer sourceIncomeType) {
		this.sourceIncomeType = sourceIncomeType;
	}

	@Column(name = "bcagent", length = 50)
	public String getBcagent() {
		return this.bcagent;
	}

	public void setBcagent(String bcagent) {
		this.bcagent = bcagent;
	}

	@Column(name = "BRANCHNO", nullable = false)
	public int getBranchno() {
		return this.branchno;
	}

	public void setBranchno(int branchno) {
		this.branchno = branchno;
	}

	@Column(name = "ACCNO", nullable = false, length = 32)
	public String getAccno() {
		return this.accno;
	}

	public void setAccno(String accno) {
		this.accno = accno;
	}

	@Column(name = "KYCFLAG", nullable = false, length = 2)
	public String getKycflag() {
		return this.kycflag;
	}

	public void setKycflag(String kycflag) {
		this.kycflag = kycflag;
	}

	@Column(name = "ONBOARDFLAG", nullable = false, length = 2)
	public String getOnboardflag() {
		return this.onboardflag;
	}

	public void setOnboardflag(String onboardflag) {
		this.onboardflag = onboardflag;
	}

	@Column(name = "RBL_CUST_ID", nullable = false, length = 20)
	public String getRblCustId() {
		return this.rblCustId;
	}

	public void setRblCustId(String rblCustId) {
		this.rblCustId = rblCustId;
	}

	@Column(name = "RBL_ACC_NO", nullable = false, length = 20)
	public String getRblAccNo() {
		return this.rblAccNo;
	}

	public void setRblAccNo(String rblAccNo) {
		this.rblAccNo = rblAccNo;
	}

}

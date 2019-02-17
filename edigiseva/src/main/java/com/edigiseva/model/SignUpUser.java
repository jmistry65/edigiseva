package com.edigiseva.model;

import java.sql.Timestamp;
import java.util.Date;


public class SignUpUser {
	
	private String clientid;
	private String uid;
	private String dob;
	private String gender;
	private String mobileNo;
	private String verification = "Y";
	private String demoauth = "N";
	private String consent = "Y";
	private Timestamp ts = new Timestamp(new Date().getTime());
	private String hmac;
	public String getDemoauth() {
		return demoauth;
	}
	public void setDemoauth(String demoauth) {
		this.demoauth = demoauth;
	}
	public String getClientid() {
		return clientid;
	}
	public void setClientid(String clientid) {
		this.clientid = clientid;
	}
	public String getUid() {
		return uid;
	}
	public void setUid(String uid) {
		this.uid = uid;
	}
	public String getDob() {
		return dob;
	}
	public void setDob(String dob) {
		this.dob = dob;
	}
	public String getGender() {
		return gender;
	}
	public void setGender(String gender) {
		this.gender = gender;
	}
	public String getMobileNo() {
		return mobileNo;
	}
	public void setMobileNo(String mobileNo) {
		this.mobileNo = mobileNo;
	}
	public String getVerification() {
		return verification;
	}
	public void setVerification(String verification) {
		this.verification = verification;
	}
	public String getConsent() {
		return consent;
	}
	public void setConsent(String consent) {
		this.consent = consent;
	}
	public Timestamp getTs() {
		return ts;
	}
	public void setTs(Timestamp ts) {
		this.ts = ts;
	}
	public SignUpUser(String clientid, String uid, String dob, String gender, String mobileNo, String verification, String demoauth,
			String consent, Timestamp ts, String hmac) {
		super();
		this.clientid = clientid;
		this.uid = uid;
		this.dob = dob;
		this.gender = gender;
		this.mobileNo = mobileNo;
		this.verification = verification;
		this.demoauth = demoauth;
		this.consent = consent;
		this.ts = ts;
		this.hmac = hmac;
	}
	
	public String getHmac() {
		return hmac;
	}
	public void setHmac(String hmac) {
		this.hmac = hmac;
	}
	
	
}

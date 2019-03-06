package com.edigiseva.model;

import java.math.BigDecimal;
import java.sql.Timestamp;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

@Entity
@Table(name="wallet")
public class Wallet{

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	private Long id;
	private boolean isActive = true;
	private Timestamp created = new Timestamp(System.currentTimeMillis());
	private Timestamp updated = new Timestamp(System.currentTimeMillis());
	
	@OneToOne
	@JsonIgnore
	@JsonProperty(value = "user")
	private Users user;
	
	private String mPin;
	
	@OneToOne
	@JsonIgnore
	@JsonProperty(value = "bank")
	private Bank bank;
	
	private BigDecimal amount = BigDecimal.ZERO;

	public Users getUser() {
		return user;
	}

	public void setUser(Users user) {
		this.user = user;
	}

	public String getmPin() {
		return mPin;
	}

	public void setmPin(String mPin) {
		this.mPin = mPin;
	}

	public Bank getBank() {
		return bank;
	}

	public void setBank(Bank bank) {
		this.bank = bank;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}
	public boolean isActive() {
		return isActive;
	}
	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}
	public Timestamp getCreated() {
		return created;
	}
	public void setCreated(Timestamp created) {
		this.created = created;
	}
	public Timestamp getUpdated() {
		return updated;
	}
	public void setUpdated(Timestamp updated) {
		this.updated = updated;
	}
}

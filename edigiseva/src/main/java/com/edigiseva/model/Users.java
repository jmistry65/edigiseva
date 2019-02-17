package com.edigiseva.model;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

@Entity
@Table(name = "users", uniqueConstraints = { @UniqueConstraint(columnNames = { "uuid" }) })
public class Users{

	@Id
	@GeneratedValue(strategy = javax.persistence.GenerationType.IDENTITY)
	private Long id;
	@NotNull
	private BigDecimal uuid;
	
	@NotBlank
	@Size(max = 50)
	private String name;
	
	@NotBlank
	@Size(max = 100)
	private String email;
	
	@NotNull
	private BigDecimal mobileNo;
	
	@NotBlank
	@Size(max = 6)
	private String gender;
	
	@NotNull
	private Date dob;
	
	@NotBlank
	@Size(min=6, max = 100) 
	private String password;
	
	@OneToMany(fetch = FetchType.LAZY)
	@Cascade(value=CascadeType.ALL)
	@JoinTable(name = "address", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "address_id"))
	private List<Address> address;
	
	
	@OneToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "role_id"))
	private Set<Role> roles = new HashSet<>();


	public BigDecimal getUuid() {
		return uuid;
	}


	public void setUuid(BigDecimal uuid) {
		this.uuid = uuid;
	}


	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}


	public String getEmail() {
		return email;
	}


	public void setEmail(String email) {
		this.email = email;
	}


	public BigDecimal getMobileNo() {
		return mobileNo;
	}


	public void setMobileNo(BigDecimal mobileNo) {
		this.mobileNo = mobileNo;
	}


	public String getGender() {
		return gender;
	}


	public void setGender(String gender) {
		this.gender = gender;
	}


	public Date getDob() {
		return dob;
	}


	public void setDob(Date dob) {
		this.dob = dob;
	}


	public String getPassword() {
		return password;
	}


	public void setPassword(String password) {
		this.password = password;
	}


	public List<Address> getAddress() {
		return address;
	}


	public void setAddress(List<Address> address) {
		this.address = address;
	}


	public Set<Role> getRoles() {
		return roles;
	}


	public void setRoles(Set<Role> roles) {
		this.roles = roles;
	}


	public Users() {
		super();
	}


	public Users(@NotBlank @Size(max = 10) BigDecimal uuid, @NotBlank @Size(max = 50) String name,
			@NotBlank @Size(max = 100) String email, @NotBlank @Size(max = 12) BigDecimal mobileNo,
			@NotBlank @Size(max = 6) String gender, @NotBlank Date dob,
			@NotBlank @Size(min = 6, max = 100) String password, @NotBlank List<Address> address, Set<Role> roles) {
		super();
		this.uuid = uuid;
		this.name = name;
		this.email = email;
		this.mobileNo = mobileNo;
		this.gender = gender;
		this.dob = dob;
		this.password = password;
		this.address = address;
		this.roles = roles;
	}

	
}

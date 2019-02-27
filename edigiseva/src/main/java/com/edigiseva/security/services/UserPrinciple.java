package com.edigiseva.security.services;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.edigiseva.model.Address;
import com.edigiseva.model.Users;
import com.fasterxml.jackson.annotation.JsonIgnore;

public class UserPrinciple implements UserDetails {
	private static final long serialVersionUID = 1L;

	private Long id;
    private String name;
    private String username;
    private String email;
    @JsonIgnore
    private String password;
    private Collection<? extends GrantedAuthority> authorities;
	private BigDecimal uuid;
	private BigDecimal mobileNo;
	private String gender;
	private Date dob;
	private Address address;


    public UserPrinciple(BigDecimal uuid, String name, String email, BigDecimal mobileNo, String gender,
			Date dob, String password,List<GrantedAuthority> authorities) {
    	this.uuid = uuid;
		this.name = name;
		this.email = email;
		this.mobileNo = mobileNo;
		this.gender = gender;
		this.dob = dob;
		this.password = password;
		this.authorities = authorities;
	}

	public static UserPrinciple build(Users user) {
        List<GrantedAuthority> authorities = user.getRoles().stream().map(role ->
                new SimpleGrantedAuthority(role.getName().name())
        ).collect(Collectors.toList());

        return new UserPrinciple(
        		user.getUuid(),
        		user.getName(),
        		user.getEmail(),
        		user.getMobileNo(),
        		user.getGender(),
        		user.getDob(),
        		user.getPassword(),
                authorities
        );
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    
    public BigDecimal getUuid() {
		return uuid;
	}

	public void setUuid(BigDecimal uuid) {
		this.uuid = uuid;
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

	public BigDecimal getMobileNo() {
		return mobileNo;
	}

	public void setMobileNo(BigDecimal mobileNo) {
		this.mobileNo = mobileNo;
	}


	public Address getAddress() {
		return address;
	}

	public void setAddress(Address address) {
		this.address = address;
	}

	@Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        
        UserPrinciple user = (UserPrinciple) o;
        return Objects.equals(id, user.id);
    }
}
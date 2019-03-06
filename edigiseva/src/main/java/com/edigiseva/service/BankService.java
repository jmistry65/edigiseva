package com.edigiseva.service;

import java.util.List;

import javax.validation.Valid;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.edigiseva.message.request.BankRequest;
import com.edigiseva.model.Bank;
import com.edigiseva.model.Users;

public interface BankService {

	Bank setUserBank(@Valid BankRequest request, Users users);

	@Query("SELECT b FROM Bank b WHERE b.isActive = :true and b.user.id:=user.id")
	public List<Bank> findByUser(@Param("user") Users user);

}

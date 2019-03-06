package com.edigiseva.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.edigiseva.model.Bank;
import com.edigiseva.model.Users;

public interface BankRepository extends JpaRepository<Bank, Long>{
	List<Bank> findByUser(Users user);
}

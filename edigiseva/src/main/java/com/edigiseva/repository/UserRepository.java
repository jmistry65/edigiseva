package com.edigiseva.repository;

import java.math.BigDecimal;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.edigiseva.model.Users;


@Repository
public interface UserRepository extends JpaRepository<Users, Long> {
	Optional<Users> findByEmail(String email);
    Optional<Users> findByUuid(BigDecimal uuid);
    Optional<Users> findByMobileNo(BigDecimal mobileNo);
}
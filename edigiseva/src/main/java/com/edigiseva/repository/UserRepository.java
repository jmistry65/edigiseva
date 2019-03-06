package com.edigiseva.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.edigiseva.model.Users;


@Repository
public interface UserRepository extends JpaRepository<Users, Long> {
	Optional<Users> findByEmail(String email);
    Optional<Users> findByUuid(Long uuid);
    Optional<Users> findByMobileNo(Long mobileNo);
}
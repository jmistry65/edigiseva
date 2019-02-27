package com.edigiseva.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.edigiseva.model.Users;


@Repository
public interface UserRepository extends JpaRepository<Users, Long> {
   // Boolean existsByEmail(String email);
    //Optional<Users> findByUuid(BigDecimal uuid);
}
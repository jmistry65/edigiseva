package com.edigiseva.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.edigiseva.model.Address;

public interface AddressRepository extends JpaRepository<Address, Long>{

}

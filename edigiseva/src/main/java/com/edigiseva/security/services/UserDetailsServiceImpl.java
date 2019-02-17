package com.edigiseva.security.services;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.edigiseva.model.Users;
import com.edigiseva.repository.UserRepository;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

	@Autowired
	UserRepository userRepository;

	public UserDetails loadUserByUuid(BigDecimal uuid) throws UsernameNotFoundException {

		
		Users user = userRepository.findByUuid(uuid)
				.orElseThrow(() -> new UsernameNotFoundException("User Not Found with -> Adhaar or email : " + uuid));
		return UserPrinciple.build(user);
		 
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		// TODO Auto-generated method stub
		return null;
	}
}
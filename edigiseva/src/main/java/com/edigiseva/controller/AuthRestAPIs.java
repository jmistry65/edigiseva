package com.edigiseva.controller;

import java.util.HashSet;
import java.util.Set;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.edigiseva.message.request.LoginForm;
import com.edigiseva.message.response.JwtResponse;
import com.edigiseva.model.Role;
import com.edigiseva.model.RoleName;
import com.edigiseva.model.Users;
import com.edigiseva.repository.RoleRepository;
import com.edigiseva.repository.UserRepository;
import com.edigiseva.security.jwt.JwtProvider;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class AuthRestAPIs {

	@Autowired
	AuthenticationManager authenticationManager;

	@Autowired
	UserRepository userRepository;

	@Autowired
	RoleRepository roleRepository;

	@Autowired
	PasswordEncoder encoder;

	@Autowired
	JwtProvider jwtProvider;


	@PostMapping("/signin")
	public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginForm loginRequest) {

		Authentication authentication = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

		SecurityContextHolder.getContext().setAuthentication(authentication);

		String jwt = jwtProvider.generateJwtToken(authentication);
		return ResponseEntity.ok(new JwtResponse(jwt));
	}

	@PostMapping("/signup")
	public ResponseEntity<String> registerUser(@Valid @RequestBody Users signUpRequest) {
		/*
		 * if (userRepository.findByUuid(signUpRequest.getUuid()).isPresent()) { return
		 * new ResponseEntity<String>("Fail -> Adhar is already exist!",
		 * HttpStatus.BAD_REQUEST); }
		 */

		// Creating user's account
		Users user = new Users(signUpRequest.getUuid(), signUpRequest.getName(), signUpRequest.getEmail(),
				signUpRequest.getMobileNo(), signUpRequest.getGender(), signUpRequest.getDob(),
				signUpRequest.getPassword(), /* signUpRequest.getAddress(), */ signUpRequest.getRoles());

	//	user.setAddress(signUpRequest.getAddress());
		Set<Role> strRoles = signUpRequest.getRoles();
		Set<Role> roles = new HashSet<>();

		strRoles.forEach(role -> {
			switch (role.toString()) {
			case "admin":
				Role adminRole = roleRepository.findByName(RoleName.ROLE_ADMIN)
						.orElseThrow(() -> new RuntimeException("Fail! -> Cause: User Role not find."));
				roles.add(adminRole);
				break;
			default:
				Role userRole = roleRepository.findByName(RoleName.ROLE_USER)
						.orElseThrow(() -> new RuntimeException("Fail! -> Cause: User Role not find."));
				roles.add(userRole);
			}
		});

		user.setRoles(roles);
		userRepository.save(user);

		return ResponseEntity.ok().body("User registered successfully!");
	}

}
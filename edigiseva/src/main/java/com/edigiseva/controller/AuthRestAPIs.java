package com.edigiseva.controller;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.mail.MessagingException;
import javax.validation.Valid;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.edigiseva.message.request.DigiSevaResponseEntity;
import com.edigiseva.message.request.LoginForm;
import com.edigiseva.message.response.JwtResponse;
import com.edigiseva.model.Users;
import com.edigiseva.repository.RoleRepository;
import com.edigiseva.repository.UserRepository;
import com.edigiseva.security.jwt.JwtProvider;
import com.edigiseva.service.UserService;
import com.edigiseva.utils.Utilities;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class AuthRestAPIs {

	@Autowired
	AuthenticationManager authenticationManager;

	@Autowired
	RoleRepository roleRepository;

	@Autowired
	PasswordEncoder encoder;

	@Autowired
	JwtProvider jwtProvider;

	@Autowired
	private UserService userService;
	
	@Autowired
	private UserRepository userRepo;
	
	@PostMapping("/signin")
	public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginForm loginRequest) {

		
//		  Authentication authentication = authenticationManager.authenticate( new
//		  UsernamePasswordAuthenticationToken(loginRequest.getUsername(),
//		  loginRequest.getPassword()));
//		  SecurityContextHolder.getContext().setAuthentication(authentication);
		 
		
		String jwt = jwtProvider.generateJwtToken(loginRequest.getUsername());
		return ResponseEntity.ok(new JwtResponse(jwt));
	}
	
	@PostMapping("/updatepassword")
	public ResponseEntity<DigiSevaResponseEntity> updatePassword(@RequestBody String request){
		boolean isPsswordUpdated = userService.updatePassword(request);
		if(isPsswordUpdated) {
			return Utilities.createResponse(false, "Password Updated successfully", HttpStatus.OK, "");
		}
		return Utilities.createResponse(true, "Password Could Not Updated", HttpStatus.OK, "");
	}
	
	@PostMapping("/forgotpassword")
	public ResponseEntity<DigiSevaResponseEntity> forgotPassword(@RequestBody String request) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeySpecException, IllegalBlockSizeException, BadPaddingException, UnsupportedEncodingException{
		
		JSONObject reqObject = new JSONObject(request);
		String email = reqObject.getString("email");
		Users user = userService.findByEmailId(email);
		if(null == user) {
			return Utilities.createResponse(true, "No user exist with this email", HttpStatus.CONFLICT, "");
		}
		try {
			String newPassword = Utilities.generateRendomPassword();
			Utilities.sendmail(email,newPassword);
			user.setPassword(newPassword);
			userRepo.save(user);
		} catch (MessagingException | IOException e) {
			e.printStackTrace();
		}
		return Utilities.createResponse(false, "Mail sent", HttpStatus.OK, "");
	}
	
}
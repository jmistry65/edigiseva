package com.edigiseva.controller;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.edigiseva.model.Address;
import com.edigiseva.model.Role;
import com.edigiseva.model.RoleName;
import com.edigiseva.model.UserJson;
import com.edigiseva.model.Users;
import com.edigiseva.repository.AddressRepository;
import com.edigiseva.repository.RoleRepository;
import com.edigiseva.repository.UserRepository;
import com.edigiseva.utils.Utilities;
import com.google.common.collect.Lists;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth/digilocker")
public class DigilockerAPIs {

	@Autowired
	UserRepository userRepository;

	@Autowired
	AddressRepository addressRepo;

	@Autowired
	RoleRepository roleRepository;

	@Value("${edigiseva.app.clientsecret}")
	private String CLIENT_SECRET;

	@Value("${edigiseva.app.clientid}")
	private String CLIENT_ID;

	@PostMapping("/signup")
	public @ResponseBody ResponseEntity<String> getDocumentList(@RequestBody String signupRequest) throws URISyntaxException {
		 
		
		JSONObject reqObject = new JSONObject(signupRequest);
		String tok = reqObject.getString("token");
		BigDecimal udid = reqObject.getBigDecimal("udid");
		BigDecimal mobileNo = reqObject.getBigDecimal("mobileNo");
		String email = reqObject.getString("email");
		String password = reqObject.getString("password");
		if (userRepository.findByUuid(udid).isPresent()) { 
			return new ResponseEntity<String>("Fail -> Adhar is already exist!",
				  HttpStatus.BAD_REQUEST); 
		}
		if (userRepository.findByMobileNo(mobileNo).isPresent()) { 
			return new ResponseEntity<String>("Fail -> Mobile No is already exist!",
				  HttpStatus.BAD_REQUEST); 
		}
		if (userRepository.findByEmail(email).isPresent()) { 
			return new ResponseEntity<String>("Fail -> Email is already exist!",
				  HttpStatus.BAD_REQUEST); 
		}
		
		RestTemplate restTemplate = new RestTemplate();
		String URL = "https://developers.digitallocker.gov.in/public/oauth2/1/files/issued";
		HttpHeaders headers = new HttpHeaders();
		headers.set("Authorization", "Bearer " + tok);
		HttpEntity<String> entity = new HttpEntity<String>(headers);

		String result = restTemplate.exchange(URL, HttpMethod.GET, entity, String.class).getBody();
		System.out.println("result =" + result);
		JSONObject resObject = new JSONObject(result);
		JSONArray items = resObject.getJSONArray("items");
		String uri = "";
		for (int i = 0; i < items.length(); i++) {
			uri = (String) new JSONObject(items.get(i).toString()).get("uri");
		}
		getPdfFile(uri, tok, udid, mobileNo, email, password);
		return new ResponseEntity<String>("User Created", HttpStatus.OK);
	}

	private void getPdfFile(String uri, String tok, BigDecimal udid, BigDecimal mobileNo, String email, String password) {
		RestTemplate restTemplate = new RestTemplate();
		String URL = "https://developers.digitallocker.gov.in/public/oauth2/1/file/" + uri;
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Arrays.asList(MediaType.ALL));
		headers.set("Authorization", "Bearer " + tok);
		HttpEntity<String> entity = new HttpEntity<String>(headers);
		byte[] response = restTemplate.exchange(URL, HttpMethod.GET, entity, byte[].class).getBody();
		try {
			Utilities.writePdfFile(response);
			String jsonObjetct = Utilities.xmlToJson(UserJson.class);
			UserJson user = (UserJson) Utilities.jsonToObject(jsonObjetct, UserJson.class);
		
			Set<Role> roles = new HashSet<>();
			Role userRole = roleRepository.findByName(RoleName.ROLE_USER)
					.orElseThrow(() -> new RuntimeException("Fail! -> Cause: User Role not find."));
			roles.add(userRole);
			
			Users saveUser = new Users(udid, user.getName(), email,
					mobileNo, user.getGender(),
					new SimpleDateFormat("dd-MM-yyyy").parse(user.getDateOfBirth()), password,  roles);
			Address addr = new Address();
			addr.setHouseNo(user.getBuilding());
			addr.setAddress1(user.getStreet());
			addr.setAddress2(user.getLocality());
			addr.setCity(user.getVtcName());
			addr.setPincode(Integer.parseInt(user.getPincode()));
			addr.setState(user.getStateName());
			
			Users u = userRepository.save(saveUser);
			addr.setUser(u);
			addressRepo.save(addr);
			
		} catch (IOException | ParseException e) {
			e.printStackTrace();
		}
	}

	@GetMapping("/digilockerauth")
	private ResponseEntity<?> getDigiLocaker() throws URISyntaxException {

		final String baseUrl = "https://developers.digitallocker.gov.in/public/oauth2/1/authorize?response_type=token&client_id=VTDRIDOX&state=gujarat&redirect_uri=https://www.google.com";

		RestTemplate restTemplate = new RestTemplate();
		URI uri = new URI(baseUrl);
		HttpHeaders headers = new HttpHeaders();
		Charset utf8 = Charset.forName("UTF-8");
		MediaType mediaType = new MediaType("text", "html", utf8);
		headers.setContentType(mediaType);
		HttpEntity<String> entity = new HttpEntity<String>("parameters", headers);
		HttpMessageConverter stringHttpMessageConverter = new StringHttpMessageConverter(Charset.forName("UTF-8"));
		List<HttpMessageConverter<?>> httpMessageConverter = Lists.newArrayList();
		httpMessageConverter.add(stringHttpMessageConverter);
		restTemplate.setMessageConverters(httpMessageConverter);
		ResponseEntity<String> responseEntity = restTemplate.exchange(uri, HttpMethod.GET, entity, String.class);
		System.out.println("Result  " + responseEntity.getBody());
		return responseEntity;
	}

	@GetMapping("/getlistofissuer")
	public @ResponseBody String getListOfIssuer() throws URISyntaxException {
		RestTemplate restTemplate = new RestTemplate();
		long ts = new Timestamp(System.currentTimeMillis()).getTime();
		String hmac = Utilities.stringToSh556(CLIENT_SECRET + CLIENT_ID + ts);
		String URL = "https://developers.digitallocker.gov.in/public/oauth2/1/pull/issuers?" + "clientid=" + CLIENT_ID
				+ "&ts=" + ts + "&hmac=" + hmac;
		ResponseEntity<String> result = restTemplate.getForEntity(URL, String.class);
		return result.getBody();
	}
}

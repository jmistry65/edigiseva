package com.edigiseva.controller;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.Arrays;
import java.util.List;

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

import com.edigiseva.message.request.DigiSevaResponseEntity;
import com.edigiseva.model.UserJson;
import com.edigiseva.service.UserService;
import com.edigiseva.utils.Utilities;
import com.google.common.collect.Lists;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth/digilocker")
public class DigilockerAPIs {

		
	@Autowired
	private UserService userService;

	@Value("${edigiseva.app.clientsecret}")
	private String CLIENT_SECRET;

	@Value("${edigiseva.app.clientid}")
	private String CLIENT_ID;

	@PostMapping("/signup")
	public @ResponseBody ResponseEntity<DigiSevaResponseEntity> getDocumentList(@RequestBody String signupRequest)
			throws URISyntaxException {

		JSONObject reqObject = new JSONObject(signupRequest);
		String tok = reqObject.getString("token");
		Long udid = reqObject.getLong("udid");
		Long mobileNo = reqObject.getLong("mobileNo");
		String email = reqObject.getString("email");
		String password = reqObject.getString("password");
		
		RestTemplate restTemplate = new RestTemplate();
		String URL = "https://developers.digitallocker.gov.in/public/oauth2/1/files/issued";
		HttpHeaders headers = new HttpHeaders();
		headers.set("Authorization", "Bearer " + tok);
		HttpEntity<String> entity = new HttpEntity<String>(headers);

		String result = restTemplate.exchange(URL, HttpMethod.GET, entity, String.class).getBody();
		JSONObject resObject = new JSONObject(result);
		JSONArray items = resObject.getJSONArray("items");
		String uri = "";
		for (int i = 0; i < items.length(); i++) {
			uri = (String) new JSONObject(items.get(i).toString()).get("uri");
		}
		return getPdfFile(uri, tok, udid, mobileNo, email, password);
	}

	private ResponseEntity<DigiSevaResponseEntity> getPdfFile(String uri, String tok, Long udid, Long mobileNo, String email,
			String password) {
		RestTemplate restTemplate = new RestTemplate();
		String URL = "https://developers.digitallocker.gov.in/public/oauth2/1/file/" + uri;
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Arrays.asList(MediaType.ALL));
		headers.set("Authorization", "Bearer " + tok);
		HttpEntity<String> entity = new HttpEntity<String>(headers);
		byte[] response = restTemplate.exchange(URL, HttpMethod.GET, entity, byte[].class).getBody();
		try {
			Utilities.writePdfFile(udid,response);
			String jsonObjetct = Utilities.xmlToJson(UserJson.class,udid);
			UserJson user = (UserJson) Utilities.jsonToObject(jsonObjetct, UserJson.class);
			return userService.createNewUser(user,udid,email,mobileNo,password);
		} catch (IOException e) {
			e.printStackTrace();
			return Utilities.createResponse(true, "User Not Created, Please try again", HttpStatus.CONFLICT, ""); 
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
		HttpMessageConverter<?> stringHttpMessageConverter = new StringHttpMessageConverter(Charset.forName("UTF-8"));
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

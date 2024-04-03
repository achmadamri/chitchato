package com.api.cct.backend.util;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.Random;

import org.springframework.http.HttpEntity;
import org.springframework.web.client.RestTemplate;

import com.api.cct.backend.model.RequestModel;
import com.api.cct.backend.model.token.TokenSyncRequestModel;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

public class TokenUtil {

	public static HashMap<String, String> keyMap;

	public TokenUtil() {
		keyMap = new HashMap<String, String>();
	}

	private String generateString(int length) {
		String characters = "abcdefghijklmnopqrstuvwxyz1234567890";
		Random rnd = new Random();
		char[] text = new char[length];
		for (int i = 0; i < length; i++) {
			text[i] = characters.charAt(rnd.nextInt(characters.length()));
		}
		return new String(text);
	}

	public String generate(String email, String urls[]) {
		LocalDateTime expiration = LocalDateTime.now();
		expiration = expiration.plusDays(365);
		
		// Allow single login at the time
		// keyMap.put(email, generateString(36));

		String tokenSalt = keyMap.get(email);
		
		TokenSyncRequestModel tokenSyncRequestModel = new TokenSyncRequestModel();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS000");
		tokenSyncRequestModel.setRequestDate(sdf.format(new Date()));
		tokenSyncRequestModel.setRequestId(generateString(10));
		tokenSyncRequestModel.setEmail(email);
		tokenSyncRequestModel.setSalt(tokenSalt);
		
		HttpEntity<TokenSyncRequestModel> request = new HttpEntity<>(tokenSyncRequestModel);
		RestTemplate restTemplate = new RestTemplate();
		
		for (String url : urls) {
			restTemplate.postForEntity(url + "token/postsync", request, String.class);
		}
		
		String token = Jwts.builder().setIssuer("mt-auth-api").setSubject("token").claim("name", email).claim("scope", "admins").setIssuedAt(new Date()).setExpiration(Date.from(expiration.atZone(ZoneId.systemDefault()).toInstant())).signWith(SignatureAlgorithm.HS256, keyMap.get(email)).compact();

		return token;
	}

	public void invalidate(String email, String urls[]) {
		String salt = generateString(36);
		keyMap.put(email, salt);
		
		TokenSyncRequestModel tokenSyncRequestModel = new TokenSyncRequestModel();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS000");
		tokenSyncRequestModel.setRequestDate(sdf.format(new Date()));
		tokenSyncRequestModel.setRequestId(generateString(10));
		tokenSyncRequestModel.setEmail(email);
		tokenSyncRequestModel.setSalt(keyMap.get(email));
		
		HttpEntity<TokenSyncRequestModel> request = new HttpEntity<>(tokenSyncRequestModel);
		RestTemplate restTemplate = new RestTemplate();
		
		for (String url : urls) {
			restTemplate.postForEntity(url + "token/postsync", request, String.class);
		}
	}

	public Claims claims(RequestModel requestModel) throws Exception {
		return Jwts.parser().setSigningKey(keyMap.get(requestModel.getEmail())).parseClaimsJws(requestModel.getToken()).getBody();
	}
}

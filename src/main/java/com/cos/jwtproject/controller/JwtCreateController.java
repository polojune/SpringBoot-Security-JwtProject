package com.cos.jwtproject.controller;

import java.util.Date;
import java.util.Map;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.cos.jwtproject.config.jwt.JwtProperties;
import com.cos.jwtproject.config.oauth.provider.GoogleUser;
import com.cos.jwtproject.config.oauth.provider.OAuthUserInfo;
import com.cos.jwtproject.model.User;
import com.cos.jwtproject.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class JwtCreateController {
	private final UserRepository userRepository;
	private final BCryptPasswordEncoder bCryptPasswordEncoder;
	
	@PostMapping("/oauth/jwt/google")
	public String jwtCreate(@RequestBody Map<String, Object> data) {
		System.out.println("jwtCreate 실행됨");
		System.out.println(data.get("profileObj"));
		OAuthUserInfo googleUser = 
				new GoogleUser((Map<String, Object>)data.get("profileObj"));
		
		User userEntity = 
				userRepository.findByUsername(googleUser.getProvider()+"_"+googleUser.getProviderId());
		
		if(userEntity == null) {
			User userRequest = User.builder()
					.username(googleUser.getProvider()+"_"+googleUser.getProviderId())
					.password(bCryptPasswordEncoder.encode("겟인데어"))
					.email(googleUser.getEmail())
					.provider(googleUser.getProvider())
					.providerId(googleUser.getProviderId())
					.roles("ROLE_USER")
					.build();
			
			userEntity = userRepository.save(userRequest);
		}
		
		String jwtToken = JWT.create()
				.withSubject(userEntity.getUsername())
				.withExpiresAt(new Date(System.currentTimeMillis()+JwtProperties.EXPIRATION_TIME))
				.withClaim("id", userEntity.getId())
				.withClaim("username", userEntity.getUsername())
				.sign(Algorithm.HMAC512(JwtProperties.SECRET));
		
		return jwtToken;
	}
}

package com.jts.user.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.jts.user.entity.Users;

import lombok.Data;

import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class CommonDTO {
	private int statusCode;

	private String error;
	
	private String message;
	
	private String token;
	
	private String refreshToken;
	
	private String expirationTime;
	
	private String name;
	
	private String city;
	
	private String role;
	
	private String email;
	
	private String password;
	
	private Users user;
	
	private List<Users> users;
}

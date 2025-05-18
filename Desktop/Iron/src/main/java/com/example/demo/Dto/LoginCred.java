package com.example.demo.Dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class LoginCred {

	@NotBlank(message="email cannot be null")
	@Email(message = "Please provide a valid email address")
	private String email;
	@NotBlank(message="password cannot be null")
	private String password;
	
	
	public LoginCred() {
	
	}
	public LoginCred(
			@NotBlank(message = "email cannot be null") @Email(message = "Please provide a valid email address") String email,
			@NotBlank(message = "password cannot be null") String password) {
		super();
		this.email = email;
		this.password = password;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	
}

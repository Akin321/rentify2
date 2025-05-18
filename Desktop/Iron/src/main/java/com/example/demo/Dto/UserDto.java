package com.example.demo.Dto;




import java.time.LocalDateTime;


import com.example.demo.model.Gender;
import com.example.demo.model.Role;

import jakarta.mail.Multipart;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;


public class UserDto {
	@NotBlank(message="name field cannot be null")
	private String name;
	@NotBlank(message="email cannot be null")
	@Email(message = "Please provide a valid email address")
	private String email;
	@NotBlank(message="password cannot be null")
	private String password;
	@NotBlank(message="phone number cannot be null")
	@Pattern(regexp = "\\d{10}", message = "Phone number must contain only digits and must be exactly 10 characters")
	private String phone;
	@NotNull(message="please select a gender")
	private Gender gender;
	
	private String refToken;

	private String otp;
	private LocalDateTime otpGeneratedTime;
	
	
	
	
	public UserDto(@NotBlank(message = "name field cannot be null") String name,
			@NotBlank(message = "email cannot be null") @Email(message = "Please provide a valid email address") String email,
			@NotBlank(message = "password cannot be null") String password,
			@NotBlank(message = "phone number cannot be null") @Pattern(regexp = "\\d{10}", message = "Phone number must contain only digits and must be exactly 10 characters") String phone,
			@NotNull(message = "please select a gender") Gender gender, String refToken, String otp,
			LocalDateTime otpGeneratedTime) {
		super();
		this.name = name;
		this.email = email;
		this.password = password;
		this.phone = phone;
		this.gender = gender;
		this.refToken = refToken;
		this.otp = otp;
		this.otpGeneratedTime = otpGeneratedTime;
	}

	public UserDto() {
	
	}
	
	public Gender getGender() {
		return gender;
	}
	public void setGender(Gender gender) {
		this.gender = gender;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
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
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getOtp() {
		return otp;
	}
	public void setOtp(String otp) {
		this.otp = otp;
	}
	public LocalDateTime getOtpGeneratedTime() {
		return otpGeneratedTime;
	}
	public void setOtpGeneratedTime(LocalDateTime otpGeneratedTime) {
		this.otpGeneratedTime = otpGeneratedTime;
	}

	public String getRefToken() {
		return refToken;
	}

	public void setRefToken(String refToken) {
		this.refToken = refToken;
	}
	

}

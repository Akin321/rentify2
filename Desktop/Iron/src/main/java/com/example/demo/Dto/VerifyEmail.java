package com.example.demo.Dto;

public class VerifyEmail {
	private String email;
	private String otp;
	public VerifyEmail(String email, String otp) {
		super();
		this.email = email;
		this.otp = otp;
	}
	public VerifyEmail() {
		
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getOtp() {
		return otp;
	}
	public void setOtp(String otp) {
		this.otp = otp;
	}
	
	
}

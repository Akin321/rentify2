package com.example.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
	
	@Autowired 
	JavaMailSender mailSender;
	
	public void SendotpMail(String email,String otp) {
		SimpleMailMessage message=new SimpleMailMessage();
		message.setTo(email);
		message.setSubject("Your OTP code for registration");
		message.setText("Your Otp for Iron Registration is "+otp+" valid for 5 minutes.");
		mailSender.send(message);
	}
	
	public void forgetPasswordOtpMail(String email,String otp) {
		SimpleMailMessage message=new SimpleMailMessage();
		message.setTo(email);
		message.setSubject("Your OTP code for Password Recovery");
		message.setText("Your Otp for Password Recovery is "+otp+" valid for 5 minutes.");
		mailSender.send(message);
	}
	
	public void ChangeEmailOtp(String email,String otp) {
		SimpleMailMessage message=new SimpleMailMessage();
		message.setTo(email);
		message.setSubject("Your OTP code for Changing Password");
		message.setText("Your Otp for Changing email is "+otp+" ");
		mailSender.send(message);
	}
}

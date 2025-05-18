package com.example.demo.controller.util;

import java.util.Random;

public class OtpUtil {
	public static String  generateOtp() {
		int otp=100000+new Random().nextInt(900000);
		return String.valueOf(otp);
	}
	
}

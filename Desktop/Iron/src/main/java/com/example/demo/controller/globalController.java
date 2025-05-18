package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.example.demo.model.NewUserModel;
import com.example.demo.service.UserService;

@ControllerAdvice
public class globalController {
	@Autowired
	UserService userService;
	
	 @ModelAttribute("user")
	    public NewUserModel addUserToModel(@AuthenticationPrincipal UserDetails userDetails) {
	        if (userDetails != null) {
	        	String email=userDetails.getUsername();
				NewUserModel user=userService.findUser(email);
				return user;
	        }
	        return new NewUserModel();  // Return "Guest" if the user is not logged in
	    }
}

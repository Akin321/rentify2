package com.example.demo.service;

import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.demo.Repository.UserRepo;
import com.example.demo.model.NewUserModel;

@Service
public class CustomUserDetailService implements UserDetailsService {
	@Autowired
	UserRepo userRepo;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		
		NewUserModel user=userRepo.findByEmail(username).orElseThrow(()->new UsernameNotFoundException("email not found"));
		  if (!user.isIs_active()) {
		        throw new UsernameNotFoundException("User is blocked");
		    }
		return new User(
				user.getEmail(),
				user.getPassword(),
				Collections.singletonList(new SimpleGrantedAuthority(user.getRole().name())));
	}
	
	
}

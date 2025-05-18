package com.example.demo.Configuration;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.example.demo.service.JwtService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;


@Component
public class JwtFilter extends OncePerRequestFilter{
	@Autowired
	JwtService jwtService;
	
	@Autowired
	UserDetailsService userDetailsService;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		HttpSession session=request.getSession(false);
		String token=null;
		String username=null;
		if (session!=null) {
			token=(String) session.getAttribute("jwttoken");
			
		}
		if(token==null) {
		
		   String authHeader=request.getHeader("Authorization");
		   if(authHeader!=null && authHeader.startsWith("Bearer")) {
			   token=authHeader.substring(7);
		   }	
		}
		if(token!=null) {
			username=jwtService.extractUserName(token);
		}
	
		if (username!=null&&SecurityContextHolder.getContext().getAuthentication()==null) {
			UserDetails userDetails=userDetailsService.loadUserByUsername(username);
			if(jwtService.validateToken(token, userDetails)) {
				 UsernamePasswordAuthenticationToken authToken =new UsernamePasswordAuthenticationToken(userDetails,null,userDetails.getAuthorities());
				 authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
	                SecurityContextHolder.getContext().setAuthentication(authToken);
			}
		}
		
        filterChain.doFilter(request, response);

		
	}

}

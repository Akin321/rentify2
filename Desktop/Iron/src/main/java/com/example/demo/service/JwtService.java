package com.example.demo.service;

import java.util.Date;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.crypto.SecretKey;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {
	 private static final String SECRET_KEY = "aG1hY1NoYTI1NmlzU2VjdXJlU2VjcmV0S2V5MTIzNDU";

	    //  Generate Token
	 private SecretKey getKey() {
	        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(SECRET_KEY));
	    }

	    public String generateToken(UserDetails userDetails) {
	    	   String roles = userDetails.getAuthorities().stream()
	                   .map(GrantedAuthority::getAuthority)
	                   .collect(Collectors.joining(","));
	        return Jwts.builder()
	        		.subject(userDetails.getUsername())
	                .claim("role", roles)
	                .issuedAt(new Date(System.currentTimeMillis()))
	                .expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 60)) // 10 hours
	                .signWith(getKey())	                
	                .compact();
	    }
	    
	    // Extract username from JWT token
		public String extractUserName(String token) {
	        // extract the username from jwt token
	        return extractClaim(token, Claims::getSubject);
	    }

	    private <T> T extractClaim(String token, Function<Claims, T> claimResolver) {
	        final Claims claims = extractAllClaims(token);
	        return claimResolver.apply(claims);
	    }

	    private Claims extractAllClaims(String token) {
	        return Jwts.parser()
	                .verifyWith(getKey())
	                .build()
	                .parseSignedClaims(token)
	                .getPayload();
	    }


	    public boolean validateToken(String token, UserDetails userDetails) {
	        final String userName = extractUserName(token);
	        return (userName.equals(userDetails.getUsername()) && !isTokenExpired(token));
	    }

	    private boolean isTokenExpired(String token) {
	        return extractExpiration(token).before(new Date());
	    }

	    private Date extractExpiration(String token) {
	        return extractClaim(token, Claims::getExpiration);
	    }

}

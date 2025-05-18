package com.example.demo.Dto;

public class EmailRequestDto {
	 private String email;

	 
	 
	    public EmailRequestDto() {
	
	}

		public EmailRequestDto(String email) {
		super();
		this.email = email;
	}

		public String getEmail() {
	        return email;
	    }

	    public void setEmail(String email) {
	        this.email = email;
	    }
}

package com.example.demo.Dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class PasswordForm {
	
	
	   @NotBlank(message = "Current password is required")
	    private String currentPassword;

	    @Size(min = 4, message = "Size should be minimum 4")
	    private String newPassword;

	    @Size(min = 4, message = "Size should be minimum 4")
	    private String confirmNewPassword;
	    
	    


	public PasswordForm() {
			
		}
	public PasswordForm(@NotBlank(message = "Current password is required") String currentPassword,
				@Size(min = 4, message = "Size should be minimum 4") String newPassword,
				@Size(min = 4, message = "Size should be minimum 4") String confirmNewPassword) {
			super();
			this.currentPassword = currentPassword;
			this.newPassword = newPassword;
			this.confirmNewPassword = confirmNewPassword;
		}
	public String getCurrentPassword() {
		return currentPassword;
	}
	public void setCurrentPassword(String currentPassword) {
		this.currentPassword = currentPassword;
	}
	public String getNewPassword() {
		return newPassword;
	}
	public void setNewPassword(String newPassword) {
		this.newPassword = newPassword;
	}
	public String getConfirmNewPassword() {
		return confirmNewPassword;
	}
	public void setConfirmNewPassword(String confirmNewPassword) {
		this.confirmNewPassword = confirmNewPassword;
	}
	
	
	
}

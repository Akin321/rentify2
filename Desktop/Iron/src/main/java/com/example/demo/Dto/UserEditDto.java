package com.example.demo.Dto;

import org.springframework.web.multipart.MultipartFile;

import com.example.demo.model.Gender;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;



public class UserEditDto {
	private int id;
	@NotBlank(message="name field cannot be null")
	private String name;
	@NotBlank(message = "Email cannot be blank")
	@Email(message = "Please provide a valid email address")
	private String email;

	@NotBlank(message="phone number cannot be null")
	@Pattern(regexp = "\\d{10}", message = "Phone number must contain only digits and must be exactly 10 characters")
	private String phone;
	@NotNull(message="please select a gender")
	private Gender gender;
	 private MultipartFile image;
	private String otp;
	
	public UserEditDto(int id, @NotBlank(message = "name field cannot be null") String name,
			@NotBlank(message = "Email cannot be blank") @Email(message = "Please provide a valid email address") String email,
			@NotBlank(message = "phone number cannot be null") @Pattern(regexp = "\\d{10}", message = "Phone number must contain only digits and must be exactly 10 characters") String phone,
			@NotNull(message = "please select a gender") Gender gender, MultipartFile image, String otp) {
		super();
		this.id = id;
		this.name = name;
		this.email = email;
		this.phone = phone;
		this.gender = gender;
		this.image = image;
		this.otp = otp;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public UserEditDto() {
	
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

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public Gender getGender() {
		return gender;
	}

	public void setGender(Gender gender) {
		this.gender = gender;
	}

	public MultipartFile getImage() {
		return image;
	}

	public void setImage(MultipartFile image) {
		this.image = image;
	}

	public String getOtp() {
		return otp;
	}

	public void setOtp(String otp) {
		this.otp = otp;
	}
	
	
	
}


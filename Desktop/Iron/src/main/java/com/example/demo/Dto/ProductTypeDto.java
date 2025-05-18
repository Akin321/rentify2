package com.example.demo.Dto;

import org.springframework.web.multipart.MultipartFile;


import com.example.demo.model.Gender;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
 // ← This one!
import jakarta.validation.constraints.Size;




public class ProductTypeDto {
	

	@NotNull(message="Choose a gender")
	private Gender gender;
	
	private int id;

	@Size(min=5,max=500,message="Description must be between 5 and 500 characters")
	private String description;

	private MultipartFile  image;
	private String imageUrl;
	

	@Size(min=2,max=50,message="Name must be between 2 and 50 characters")
	private String name;
	

	
	public ProductTypeDto() {
		
	}
	public ProductTypeDto(Gender gender, String description, MultipartFile image, String name) {
	
		this.gender = gender;
		this.description = description;
		this.image = image;
	
		this.name = name;
	}

	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	
	public String getImageUrl() {
		return imageUrl;
	}
	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}
	public Gender getGender() {
		return gender;
	}
	public void setGender(Gender gender) {
		this.gender = gender;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public MultipartFile getImage() {
		return image;
	}
	public void setImage(MultipartFile image) {
		this.image = image;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	
	
}

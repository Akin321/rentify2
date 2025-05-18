package com.example.demo.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;

@Entity
public class NewUserModel { //model for user and admin
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private int id;
	@Enumerated(EnumType.STRING)
	private Gender gender;
	private String name;
	@Column(nullable = true)
	private String image;
	private String email;
	private String password;
	private String phone;
	@Enumerated(EnumType.STRING)
	private Role role=Role.User;
	private LocalDateTime createdAt;
	@Column(nullable = false, columnDefinition = "BOOLEAN DEFAULT true")
	private boolean is_active=true;
	@Column(unique = true)
	private String referralToken;
	
	public NewUserModel() {
		
	}

	

	public NewUserModel(int id, Gender gender, String name, String image, String email, String password, String phone,
			Role role, LocalDateTime createdAt, boolean is_active, String referralToken) {
		super();
		this.id = id;
		this.gender = gender;
		this.name = name;
		this.image = image;
		this.email = email;
		this.password = password;
		this.phone = phone;
		this.role = role;
		this.createdAt = createdAt;
		this.is_active = is_active;
		this.referralToken = referralToken;
	}



	@PrePersist //call before inserting an entity for the first time
	private void onCreate() {
		this.createdAt=LocalDateTime.now(); //to set date automatically for each user
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Gender getGender() {
		return gender;
	}

	public void setGender(Gender gender) {
		this.gender = gender;
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

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public Role getRole() {
		return role;
	}

	public void setRole(Role role) {
		this.role = role;
	}

	public LocalDateTime getCreated_at() {
		return createdAt;
	}

	public void setCreated_at(LocalDateTime created_at) {
		this.createdAt = created_at;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public boolean isIs_active() {
		return is_active;
	}

	public void setIs_active(boolean is_active) {
		this.is_active = is_active;
	}



	public String getReferralToken() {
		return referralToken;
	}



	public void setReferralToken(String referralToken) {
		this.referralToken = referralToken;
	}
	
	

}

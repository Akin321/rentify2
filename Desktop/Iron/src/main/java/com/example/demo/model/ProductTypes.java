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
public class ProductTypes {
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private int id;
	@Enumerated(EnumType.STRING)
	private Gender gender;
	@Column(columnDefinition = "TEXT")
	private String description;
	private String image;
	private String name;
	private boolean is_active=true;
	private LocalDateTime createdAt;

	
	public ProductTypes() {

	}
	public ProductTypes(int id, Gender gender, String description, String image, String name, boolean is_active,LocalDateTime created_at) {
		super();
		this.id = id;
		this.gender = gender;
		this.description = description;
		this.image = image;
		this.name = name;
		this.is_active = is_active;
		this.createdAt=created_at;
	}
	@PrePersist
	public void onCreate() {
		this.createdAt=LocalDateTime.now();
	}
	public LocalDateTime getCreatedAt() {
		return createdAt;
	}
	public void setCreatedAt(LocalDateTime created_at) {
		this.createdAt = created_at;
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
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getImage() {
		return image;
	}
	public void setImage(String image) {
		this.image = image;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public boolean getIs_active() {
		return is_active;
	}
	public void setIs_active(boolean is_active) {
		this.is_active = is_active;
	}
	
}

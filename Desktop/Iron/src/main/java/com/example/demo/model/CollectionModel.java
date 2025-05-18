package com.example.demo.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;

@Entity
public class CollectionModel {
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private int id;
	private String name;
	private LocalDateTime createdAt;
	private boolean is_active=true;
	
	@PrePersist
	public void onCreate() {
		this.createdAt=LocalDateTime.now();
	}
	
	//to map with database and allows to create new instance of the class
	public CollectionModel() {
	
	}


	public CollectionModel(int id, String name, LocalDateTime created_on, boolean is_active) {
		super();
		this.id = id;
		this.name = name;
		this.createdAt = created_on;
		this.is_active = is_active;
	}


	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public boolean isIs_active() {
		return is_active;
	}

	public void setIs_active(boolean is_active) {
		this.is_active = is_active;
	}
	
}

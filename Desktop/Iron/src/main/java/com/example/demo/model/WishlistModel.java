package com.example.demo.model;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class WishlistModel {
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private int id;
	@ManyToOne()
	@JoinColumn(name="user_id",nullable=false)
	private NewUserModel user;
	@ManyToOne()
	@JoinColumn(name="product_id",nullable=false)
	private ProductModel product;
	private LocalDateTime createdAt;
	public WishlistModel(int id, NewUserModel user, ProductModel product, LocalDateTime createdAt) {
		super();
		this.id = id;
		this.user = user;
		this.product = product;
		this.createdAt = createdAt;
	}
	public WishlistModel() {
		
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public NewUserModel getUser() {
		return user;
	}
	public void setUser(NewUserModel user) {
		this.user = user;
	}
	public ProductModel getProduct() {
		return product;
	}
	public void setProduct(ProductModel product) {
		this.product = product;
	}
	public LocalDateTime getCreatedAt() {
		return createdAt;
	}
	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}
	
	
	
	
	
	
}

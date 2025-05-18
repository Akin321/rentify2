package com.example.demo.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;


@Entity
public class CartModel {
		
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private int id;
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="user_id",nullable=false)
	private NewUserModel user;
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="product_id",nullable=false)
	private ProductModel product;
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="variant_id",nullable=false)
	private ProductVariant variant;
	@Column(nullable = false)
    private Integer quantity;
	@Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
	@ManyToOne()
	@JoinColumn(name = "coupon_id",nullable=true)
	private CouponModel coupon;
	
	
	 @PrePersist
	    protected void onCreate() {
	        this.createdAt = LocalDateTime.now();
	    }

	 	
	public CartModel() {
		
	}


	


	

	public CartModel(int id, NewUserModel user, ProductModel product, ProductVariant variant, Integer quantity,
			LocalDateTime createdAt, CouponModel coupon) {

		this.id = id;
		this.user = user;
		this.product = product;
		this.variant = variant;
		this.quantity = quantity;
		this.createdAt = createdAt;
		this.coupon = coupon;
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


	public ProductVariant getVariant() {
		return variant;
	}


	public void setVariant(ProductVariant variant) {
		this.variant = variant;
	}


	public Integer getQuantity() {
		return quantity;
	}


	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}


	public LocalDateTime getCreatedAt() {
		return createdAt;
	}


	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}


	public CouponModel getCoupon() {
		return coupon;
	}


	public void setCoupon(CouponModel coupon) {
		this.coupon = coupon;
	}

	

	 
}

package com.example.demo.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;

@Entity
public class CouponModel {
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private int id;
	private String couponCode;
	private int discountPer;
	private int minAmount;
	private LocalDate expiryDate;
	private int total_usage;
	private Integer perUserLimit;
	private int totalUsageLimit;
	private String description;
	@Enumerated(EnumType.STRING)
	private Status status=Status.Active;
	private LocalDateTime createdAt;
	@ManyToOne
	@JoinColumn(name="user_id",nullable=true)
	private NewUserModel user;
	@PrePersist()
	public void onCreate() {
		this.createdAt=LocalDateTime.now();
	}
	
	public CouponModel() {
		
	}
	
	public CouponModel(int id, String couponCode, int discountPer, int minAmount, LocalDate expiryDate, int total_usage,
			Integer perUserLimit, int totalUsageLimit, String description, Status status, LocalDateTime createdAt,
			NewUserModel user) {
		super();
		this.id = id;
		this.couponCode = couponCode;
		this.discountPer = discountPer;
		this.minAmount = minAmount;
		this.expiryDate = expiryDate;
		this.total_usage = total_usage;
		this.perUserLimit = perUserLimit;
		this.totalUsageLimit = totalUsageLimit;
		this.description = description;
		this.status = status;
		this.createdAt = createdAt;
		this.user = user;
	}

	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getCouponCode() {
		return couponCode;
	}
	public void setCouponCode(String couponCode) {
		this.couponCode = couponCode;
	}
	public int getDiscountPer() {
		return discountPer;
	}
	public void setDiscountPer(int discountPer) {
		this.discountPer = discountPer;
	}
	public int getMinAmount() {
		return minAmount;
	}
	public void setMinAmount(int minAmount) {
		this.minAmount = minAmount;
	}
	public LocalDate getExpiryDate() {
		return expiryDate;
	}
	public void setExpiryDate(LocalDate expiryDate) {
		this.expiryDate = expiryDate;
	}
	public int getTotal_usage() {
		return total_usage;
	}
	public void setTotal_usage(int total_usage) {
		this.total_usage = total_usage;
	}
	public Integer getPerUserLimit() {
		return perUserLimit;
	}
	public void setPerUserLimit(Integer perUserLimit) {
		this.perUserLimit = perUserLimit;
	}
	public int getTotalUsageLimit() {
		return totalUsageLimit;
	}
	public void setTotalUsageLimit(int totalUsageLimit) {
		this.totalUsageLimit = totalUsageLimit;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public Status getStatus() {
		return status;
	}
	public void setStatus(Status status) {
		this.status = status;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public NewUserModel getUser() {
		return user;
	}

	public void setUser(NewUserModel user) {
		this.user = user;
	}
	
	
	
}

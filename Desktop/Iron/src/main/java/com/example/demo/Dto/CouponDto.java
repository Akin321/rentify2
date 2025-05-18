package com.example.demo.Dto;

import java.time.LocalDate;


import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class CouponDto {
	

    private int id;
	 @NotNull(message = "Coupon code cannot be null")
	    @Size(min = 4, message = "Minimum 4 characters are needed for coupon code")
	    private String couponCode;

	    @Max(value = 100, message = "Discount cannot exceed 100%")
	    @Min(value = 1, message = "Discount must be at least 1%")
	    private int discountPer;

	    @Min(value = 0, message = "Minimum amount should not be negative")
	    private int minAmount;

	    @NotNull(message = "Expiry date cannot be null")
	    private LocalDate expiryDate;
	    
	    @Min(value=-1,message="Minimum usage must be -1")
	    private int total_usage;

	    @Min(value=-1,message="Minimum usage must be -1")
	    private Integer perUserLimit;

	    private Integer totalUsageLimit;

	    @Size(min = 5, max = 50, message = "Description must be between 5 to 50 characters")
	    private String description;

	
	
	public CouponDto() {
		
	}
	
	public CouponDto(int id,
			@NotNull(message = "Coupon code cannot be null") @Size(min = 4, message = "Minimum 4 characters are needed for coupon code") String couponCode,
			@Max(value = 100, message = "Discount cannot exceed 100%") @Min(value = 1, message = "Discount must be at least 1%") int discountPer,
			@Min(value = 0, message = "Minimum amount should not be negative") int minAmount,
			@NotNull(message = "Expiry date cannot be null") LocalDate expiryDate, int total_usage,
			@Min(value = 0, message = "Per-user limit should not be negative") Integer perUserLimit,
			@Min(value = 0, message = "Total usage limit should not be negative") int totalUsageLimit,
			@Size(min = 5, max = 50, message = "Description must be between 5 to 50 characters") String description) {
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
	public Integer getTotalUsageLimit() {
		return totalUsageLimit;
	}
	public void setTotalUsageLimit(Integer totalUsageLimit) {
		this.totalUsageLimit = totalUsageLimit;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
	
}

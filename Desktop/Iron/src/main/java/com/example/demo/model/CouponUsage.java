package com.example.demo.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class CouponUsage {
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private int id;
	@ManyToOne
	@JoinColumn(name="user_id")
	private NewUserModel user;
	@ManyToOne
	@JoinColumn(name="coupon_id")
	private CouponModel coupon;
	private int usage_count=0;
	
	public CouponUsage() {
	
	}
	public CouponUsage(int id, NewUserModel user, CouponModel coupon, int usage_count) {
		super();
		this.id = id;
		this.user = user;
		this.coupon = coupon;
		this.usage_count = usage_count;
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
	public CouponModel getCoupon() {
		return coupon;
	}
	public void setCoupon(CouponModel coupon) {
		this.coupon = coupon;
	}
	public int getUsage_count() {
		return usage_count;
	}
	public void setUsage_count(int usage_count) {
		this.usage_count = usage_count;
	}
	
	
	

}

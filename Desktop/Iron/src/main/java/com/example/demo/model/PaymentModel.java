package com.example.demo.model;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class PaymentModel {
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private int id;
	private String order_id;
	private String payment_id;
	private String signature;
	private int amount;
	private String status;
	 @CreationTimestamp
	private LocalDateTime paymentTime;
	@ManyToOne()
	@JoinColumn(name="user_id")
	private NewUserModel user;
	public PaymentModel(int id, String order_id, String payment_id, String signature, int amount, String status,
			LocalDateTime paymentTime, NewUserModel user) {
		
		this.id = id;
		this.order_id = order_id;
		this.payment_id = payment_id;
		this.signature = signature;
		this.amount = amount;
		this.status = status;
		this.paymentTime = paymentTime;
		this.user = user;
	}
	public PaymentModel() {
	
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getOrder_id() {
		return order_id;
	}
	public void setOrder_id(String order_id) {
		this.order_id = order_id;
	}
	public String getPayment_id() {
		return payment_id;
	}
	public void setPayment_id(String payment_id) {
		this.payment_id = payment_id;
	}
	public String getSignature() {
		return signature;
	}
	public void setSignature(String signature) {
		this.signature = signature;
	}
	public int getAmount() {
		return amount;
	}
	public void setAmount(int amount) {
		this.amount = amount;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public LocalDateTime getPaymentTime() {
		return paymentTime;
	}
	public void setPaymentTime(LocalDateTime paymentTime) {
		this.paymentTime = paymentTime;
	}
	public NewUserModel getUser() {
		return user;
	}
	public void setUser(NewUserModel user) {
		this.user = user;
	}
	
	
	
}

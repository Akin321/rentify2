package com.example.demo.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import jakarta.persistence.Column;
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
public class WalletTransaction {
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private int id;
	@ManyToOne()
	@JoinColumn(name="wallet_id",nullable=false)
	private WalletModel wallet;
	@Column(nullable=false)
	private BigDecimal amount;
	@Enumerated(EnumType.STRING)
	@Column(nullable=false)
	private TransStatus status;
	private String description;
	@Enumerated(EnumType.STRING)
	@Column(nullable=false)
	private TransType type;
	private LocalDateTime createdAt;


	
	@PrePersist()
	public void onCreate() {
		this.createdAt=LocalDateTime.now();
	}
	
	public WalletTransaction(int id, WalletModel wallet, BigDecimal amount, TransStatus status, String description,
			TransType type, LocalDateTime createdAt, String razorpayOrderId) {
		super();
		this.id = id;
		this.wallet = wallet;
		this.amount = amount;
		this.status = status;
		this.description = description;
		this.type = type;
		this.createdAt = createdAt;
	}

	public WalletTransaction() {
	
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	
	public BigDecimal getAmount() {
		return amount;
	}
	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}
	public TransStatus getStatus() {
		return status;
	}
	public void setStatus(TransStatus status) {
		this.status = status;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public TransType getType() {
		return type;
	}
	public void setType(TransType type) {
		this.type = type;
	}
	public String getCreatedAt() {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM dd, yyyy");
		return createdAt.format(formatter);
	}
	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public WalletModel getWallet() {
		return wallet;
	}

	public void setWallet(WalletModel wallet) {
		this.wallet = wallet;
	}


	
	
	
	
	
	
	
	

}

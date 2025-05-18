package com.example.demo.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PreUpdate;

@Entity
public class WalletModel {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private int id;
	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name="user_id")
	private NewUserModel user;
	private BigDecimal balance=BigDecimal.ZERO;
	private LocalDate updated_at;
	
	@PreUpdate
	public void onUpdate() {
		this.updated_at=LocalDate.now();
	}

	public WalletModel(int id, NewUserModel user, BigDecimal balance, LocalDate updated_at) {
		
		this.id = id;
		this.user = user;
		this.balance = balance;
		this.updated_at = updated_at;
	}

	public WalletModel() {
		
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

	public BigDecimal getBalance() {
		return balance.setScale(2, RoundingMode.HALF_UP);
	}

	public void setBalance(BigDecimal balance) {
		this.balance = balance;
	}

	public LocalDate getUpdated_at() {
		return updated_at;
	}

	public void setUpdated_at(LocalDate updated_at) {
		this.updated_at = updated_at;
	}
	
	
	
}

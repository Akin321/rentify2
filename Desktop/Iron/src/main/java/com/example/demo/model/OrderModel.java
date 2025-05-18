package com.example.demo.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;

@Entity
public class OrderModel {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private int id;
	

	private String address;
	
	@ManyToOne
	@JoinColumn(name="user_id")
	private NewUserModel user;
	private String orderId;
	@Enumerated(EnumType.STRING)
	private PaymentType paymentType;
	private BigDecimal totalAmount;
 
    private LocalDateTime orderDate;
    
  
    
    private LocalDate expectedDeliveryDate;
    @Column(nullable=true)
    private String couponCode;
    @Column(nullable=true)

    private BigDecimal couponAmount;
	
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderItem> orderItems;

	
	
	public OrderModel(int id, String address, NewUserModel user, String orderId, PaymentType paymentType,
			BigDecimal totalAmount, LocalDateTime orderDate, LocalDate expectedDeliveryDate, String couponCode,
			BigDecimal couponAmount, List<OrderItem> orderItems) {
		super();
		this.id = id;
		this.address = address;
		this.user = user;
		this.orderId = orderId;
		this.paymentType = paymentType;
		this.totalAmount = totalAmount;
		this.orderDate = orderDate;
		this.expectedDeliveryDate = expectedDeliveryDate;
		this.couponCode = couponCode;
		this.couponAmount = couponAmount;
		this.orderItems = orderItems;
	}

	public OrderModel() {
		
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public NewUserModel getUser() {
		return user;
	}
	
	public String  getExpectedDeliveryDate() {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM dd, yyyy");
	    return orderDate.toLocalDate().plusDays(5).format(formatter);
	}


	public void setUser(NewUserModel user) {
		this.user = user;
	}

	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	public PaymentType getPaymentType() {
		return paymentType;
	}

	public void setPaymentType(PaymentType paymentType) {
		this.paymentType = paymentType;
	}

	public BigDecimal getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(BigDecimal totalAmount) {
		this.totalAmount = totalAmount.setScale(2,RoundingMode.HALF_UP);
	}

	public String getOrderDate() {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM dd, yyyy");
	    return orderDate.format(formatter);	}

	public void setOrderDate(LocalDateTime orderDate) {
		this.orderDate = orderDate;
	}

	public BigDecimal getCouponAmount() {
		return couponAmount;
	}

	public void setCouponAmount(BigDecimal couponAmount) {
		this.couponAmount = couponAmount;
	}

	public List<OrderItem> getOrderItems() {
		return orderItems;
	}

	public void setOrderItems(List<OrderItem> orderItems) {
		this.orderItems = orderItems;
	}

	public String getCouponCode() {
		return couponCode;
	}

	public void setCouponCode(String couponCode) {
		this.couponCode = couponCode;
	}


	
	
	
	
	

}

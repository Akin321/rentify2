package com.example.demo.model;

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
import jakarta.persistence.OneToOne;

@Entity
public class ReturnRequest {
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	int id;
	@OneToOne
	@JoinColumn(name="order_item_id")
	private OrderItem orderItem;
	private String reason;
	@Enumerated(EnumType.STRING)
	private ReturnStatus status;
	private LocalDateTime requestDate;
	@Column(nullable=true)
	private LocalDateTime approvedDate;
	@Column(nullable=true)
	private String rejectMessage;

	


	public ReturnRequest(int id, OrderItem orderItem, String reason, ReturnStatus status, LocalDateTime requestDate,
			LocalDateTime approvedDate, String rejectMessage) {
		super();
		this.id = id;
		this.orderItem = orderItem;
		this.reason = reason;
		this.status = status;
		this.requestDate = requestDate;
		this.approvedDate = approvedDate;
		this.rejectMessage = rejectMessage;
	}

	public ReturnRequest() {
	}

	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public OrderItem getOrderItem() {
		return orderItem;
	}
	public void setOrderItem(OrderItem orderItem) {
		this.orderItem = orderItem;
	}
	public String getReason() {
		return reason;
	}
	public void setReason(String reason) {
		this.reason = reason;
	}
	public ReturnStatus getStatus() {
		return status;
	}
	public void setStatus(ReturnStatus status) {
		this.status = status;
	}
	public String getRequestDate() {
		DateTimeFormatter formatter= DateTimeFormatter.ofPattern("MMMM dd, yyyy");
		return requestDate.format(formatter);
	}
	public void setRequestDate(LocalDateTime requestDate) {
		this.requestDate = requestDate;
	}

	public String getApprovedDate() {
		DateTimeFormatter formatter= DateTimeFormatter.ofPattern("MMMM dd, yyyy");

		return approvedDate.format(formatter);
	}

	public void setApprovedDate(LocalDateTime approvedDate) {
		this.approvedDate = approvedDate;
	}

	public String getRejectMessage() {
		return rejectMessage;
	}

	public void setRejectMessage(String rejectMessage) {
		this.rejectMessage = rejectMessage;
	}
	
	
	
}

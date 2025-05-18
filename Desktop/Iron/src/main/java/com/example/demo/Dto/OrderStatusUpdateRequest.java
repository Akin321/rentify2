package com.example.demo.Dto;

import com.example.demo.model.OrderStatus;

public class OrderStatusUpdateRequest {
	private OrderStatus status;
	private int orderItemId;
	public OrderStatusUpdateRequest(OrderStatus status, int orderItemId) {
		super();
		this.status = status;
		this.orderItemId = orderItemId;
	}
	public OrderStatus getStatus() {
		return status;
	}
	public void setStatus(OrderStatus status) {
		this.status = status;
	}
	public int getOrderItemId() {
		return orderItemId;
	}
	public void setOrderItemId(int orderItemId) {
		this.orderItemId = orderItemId;
	}
	
	
}

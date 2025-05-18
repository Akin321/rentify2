package com.example.demo.Dto;

public class QuantityUpdateRequest {
	private int cartId;
	private int delta;
	public QuantityUpdateRequest(int cartId, int delta) {
		this.cartId = cartId;
		this.delta = delta;
	}
	public QuantityUpdateRequest() {
		
	}
	public int getCartId() {
		return cartId;
	}
	public void setCartId(int cartId) {
		this.cartId = cartId;
	}
	public int getDelta() {
		return delta;
	}
	public void setDelta(int delta) {
		this.delta = delta;
	}
	
	
}

package com.example.demo.Dto;

import com.example.demo.model.ProductModel;
import com.example.demo.model.Size;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;





public class VarientDto {
   
	@NotNull(message="please select a size")
	private Size size;
	@Min(value=0,message="stock cannot be negative")
	
	private int stock;
	
	
	public VarientDto() {
		
	}
	public VarientDto(@NotNull(message = "please select a size") Size size,
			@Min(value = 0, message = "stock cannot be negative") int stock) {
		super();
		this.size = size;
		this.stock = stock;
	}
	public Size getSize() {
		return size;
	}
	public void setSize(Size size) {
		this.size = size;
	}
	public int getStock() {
		return stock;
	}
	public void setStock(int stock) {
		this.stock = stock;
	}

	
}

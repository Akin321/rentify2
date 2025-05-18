package com.example.demo.Dto;

import java.math.BigDecimal;

public class InvoiceDto {
	private String address;
	private String orderId;
	private String orderDate;
	private String deliveryDate;
	
	private String productName;
	private int quantity;
	private BigDecimal totalmrp;
	private BigDecimal discount;
	private BigDecimal totalamount;
	private String invoiceNumber;
	private String paymentMethod;
	private String logo;
	public String getLogo() {
		return logo;
	}

	public void setLogo(String logo) {
		this.logo = logo;
	}

	public InvoiceDto(String address, String orderId, String orderDate, String deliveryDate, String productName,
			int quantity, BigDecimal totalmrp, BigDecimal discount, BigDecimal totalamount, String invoiceNumber,
			String paymentMethod,String logo) {
		super();
		this.address = address;
		this.orderId = orderId;
		this.orderDate = orderDate;
		this.deliveryDate = deliveryDate;
		this.productName = productName;
		this.quantity = quantity;
		this.totalmrp = totalmrp;
		this.discount = discount;
		this.totalamount = totalamount;
		this.invoiceNumber = invoiceNumber;
		this.paymentMethod = paymentMethod;
		this.logo=logo;
	}
	
	public InvoiceDto() {

	}

	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getOrderId() {
		return orderId;
	}
	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}
	public String getOrderDate() {
		return orderDate;
	}
	public void setOrderDate(String orderDate) {
		this.orderDate = orderDate;
	}
	public String getDeliveryDate() {
		return deliveryDate;
	}
	public void setDeliveryDate(String deliveryDate) {
		this.deliveryDate = deliveryDate;
	}
	public String getProductName() {
		return productName;
	}
	public void setProductName(String productName) {
		this.productName = productName;
	}
	public int getQuantity() {
		return quantity;
	}
	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}
	public BigDecimal getTotalmrp() {
		return totalmrp;
	}
	public void setTotalmrp(BigDecimal totalmrp) {
		this.totalmrp = totalmrp;
	}
	public BigDecimal getDiscount() {
		return discount;
	}
	public void setDiscount(BigDecimal discount) {
		this.discount = discount;
	}
	public BigDecimal getTotalamount() {
		return totalamount;
	}
	public void setTotalamount(BigDecimal totalamount) {
		this.totalamount = totalamount;
	}
	public String getInvoiceNumber() {
		return invoiceNumber;
	}
	public void setInvoiceNumber(String invoiceNumber) {
		this.invoiceNumber = invoiceNumber;
	}
	public String getPaymentMethod() {
		return paymentMethod;
	}
	public void setPaymentMethod(String paymentMethod) {
		this.paymentMethod = paymentMethod;
	}

	
	
	
	
	
	

}

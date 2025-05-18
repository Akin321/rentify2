package com.example.demo.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

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
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;

@Entity
public class OrderItem {
	
	  @Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    private Long id;
	    
	    @ManyToOne
	    @JoinColumn(name = "order_id")
	    private OrderModel order;  // Foreign Key to Order table
	    

	    
	    private String productName;
	    private String size;
	    private int quantity;
	    private String fitName;
	    private String color;
	    private String productType;
	    private String image;
	    private LocalDateTime createdAt;
	    private BigDecimal totalMRP;   // Total MRP of the product
	    private BigDecimal totalDiscount;   // Discount applied to this item
	    private BigDecimal totalAmount;// Total amount after discount
	    @Enumerated(EnumType.STRING)
	    private OrderStatus orderStatus;
	  // Payment status for this item (e.g., "Paid", "Unpaid")
	    private Boolean refunded;   // Indicates whether this item has been refunded or not
	    private BigDecimal refundAmount;  // Refund amount for the item (if applicable)
	    private LocalDateTime deliveryDate;
	    @Column(nullable=true)

	    private LocalDateTime cancelDate;
	    
	    @OneToOne(mappedBy = "orderItem", cascade = CascadeType.ALL)
	    private ReturnRequest returnRequest;
	    	
	    
	    @ManyToOne
	    @JoinColumn(name="product_id")
	    private ProductModel product;
	    @ManyToOne
	    @JoinColumn(name="variant_id")
	    private ProductVariant variant;
	    
	  
		@PrePersist
		public void onCreate() {
			this.createdAt=LocalDateTime.now();
		} 
	    
		public LocalDateTime getCreatedAt() {
			return createdAt;
		}
		public void setCreatedAt(LocalDateTime createdAt) {
			this.createdAt = createdAt;
		}
		
	

		

		

		

		public OrderItem(Long id, OrderModel order, String productName, String size, int quantity, String fitName,
				String color, String productType, String image, LocalDateTime createdAt, BigDecimal totalMRP,
				BigDecimal totalDiscount, BigDecimal totalAmount, OrderStatus orderStatus, Boolean refunded,
				BigDecimal refundAmount, LocalDateTime deliveryDate, LocalDateTime cancelDate,
				ReturnRequest returnRequest, ProductModel product, ProductVariant variant) {
			super();
			this.id = id;
			this.order = order;
			this.productName = productName;
			this.size = size;
			this.quantity = quantity;
			this.fitName = fitName;
			this.color = color;
			this.productType = productType;
			this.image = image;
			this.createdAt = createdAt;
			this.totalMRP = totalMRP;
			this.totalDiscount = totalDiscount;
			this.totalAmount = totalAmount;
			this.orderStatus = orderStatus;
			this.refunded = refunded;
			this.refundAmount = refundAmount;
			this.deliveryDate = deliveryDate;
			this.cancelDate = cancelDate;
			this.returnRequest = returnRequest;
			this.product = product;
			this.variant = variant;
		}

		public OrderItem() {
		
		}
		public Long getId() {
			return id;
		}
		public void setId(Long id) {
			this.id = id;
		}
		public OrderModel getOrder() {
			return order;
		}
		public void setOrder(OrderModel order) {
			this.order = order;
		}
		public String getProductName() {
			return productName;
		}
		public void setProductName(String productName) {
			this.productName = productName;
		}
		public String getSize() {
			return size;
		}
		public void setSize(String size) {
			this.size = size;
		}
		public int getQuantity() {
			return quantity;
		}
		public void setQuantity(int quantity) {
			this.quantity = quantity;
		}
		public String getFitName() {
			return fitName;
		}
		public void setFitName(String fitName) {
			this.fitName = fitName;
		}
		public String getColor() {
			return color;
		}
		public void setColor(String color) {
			this.color = color;
		}
		public String getProductType() {
			return productType;
		}
		public void setProductType(String productType) {
			this.productType = productType;
		}
		public String getImage() {
			return image;
		}
		public void setImage(String image) {
			this.image = image;
		}
		public BigDecimal getTotalMRP() {
			return totalMRP;
		}
		public void setTotalMRP(BigDecimal totalMRP) {
			this.totalMRP = totalMRP.setScale(2,RoundingMode.HALF_UP);
		}
		public BigDecimal getTotalDiscount() {
			return totalDiscount;
		}
		public void setTotalDiscount(BigDecimal totalDiscount) {
			this.totalDiscount = totalDiscount.setScale(2,RoundingMode.HALF_UP);
		}
		public BigDecimal getTotalAmount() {
			return totalAmount.setScale(2,RoundingMode.HALF_UP);
		}
		public void setTotalAmount(BigDecimal totalAmount) {
			this.totalAmount = totalAmount;
		}
		public OrderStatus getOrderStatus() {
			return orderStatus;
		}
		public void setOrderStatus(OrderStatus orderStatus) {
			this.orderStatus = orderStatus;
		}
		public Boolean getRefunded() {
			return refunded;
		}
		public void setRefunded(Boolean refunded) {
			this.refunded = refunded;
		}
		public BigDecimal getRefundAmount() {
			return refundAmount;
		}
		public void setRefundAmount(BigDecimal refundAmount) {
			this.refundAmount = refundAmount.setScale(2,RoundingMode.HALF_UP);
		}

		public String getDeliveryDate() {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM dd, yyyy");
		    return deliveryDate.format(formatter);	
		}

		public void setDeliveryDate(LocalDateTime deliveryDate) {
			this.deliveryDate = deliveryDate;
		}

		public ProductModel getProduct() {
			return product;
		}

		public void setProduct(ProductModel product) {
			this.product = product;
		}

		public ProductVariant getVariant() {
			return variant;
		}

		public void setVariant(ProductVariant variant) {
			this.variant = variant;
		}

		public String getCancelDate() {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM dd, yyyy");
			return cancelDate.format(formatter);
		}

		public void setCancelDate(LocalDateTime cancelDate) {
			this.cancelDate = cancelDate;
		}

		public ReturnRequest getReturnRequest() {
			return returnRequest;
		}

		public void setReturnRequest(ReturnRequest returnRequest) {
			this.returnRequest = returnRequest;
		}
	    
	    

}

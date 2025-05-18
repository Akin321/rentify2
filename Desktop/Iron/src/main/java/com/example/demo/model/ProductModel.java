package com.example.demo.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
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
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;

@Entity
public class ProductModel {
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private int id;
	private String name;
	@Column(columnDefinition = "TEXT")
	private String description;
	private BigDecimal basePrice;
	private BigDecimal discountPer;
	@ManyToOne
	@JoinColumn(name="product_type_id")
	private ProductTypes productType;
	@Enumerated(EnumType.STRING)
	private Gender gender;
	@ManyToOne
	@JoinColumn(name="collection_id")
	private CollectionModel collection;
	private String color;
	@Enumerated(EnumType.STRING)
	private FitType fit;
	private LocalDateTime createAt;
	private LocalDateTime updatedAt;
	private boolean is_active=true;
	private int maxDiscountPrice=0;
	private BigDecimal finalPrice;
	@OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<ProductImages> images;
	@OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<ProductVariant> variants;
	
	
	
	


	public ProductModel(int id, String name, String description, BigDecimal basePrice, BigDecimal discountPer,
			ProductTypes productType, Gender gender, CollectionModel collection, String color, FitType fit,
			LocalDateTime createAt, LocalDateTime updatedAt, boolean is_active, int maxDiscountPrice,
			BigDecimal finalPrice, List<ProductImages> images, List<ProductVariant> variants) {
		super();
		this.id = id;
		this.name = name;
		this.description = description;
		this.basePrice = basePrice;
		this.discountPer = discountPer;
		this.productType = productType;
		this.gender = gender;
		this.collection = collection;
		this.color = color;
		this.fit = fit;
		this.createAt = createAt;
		this.updatedAt = updatedAt;
		this.is_active = is_active;
		this.maxDiscountPrice = maxDiscountPrice;
		this.finalPrice = finalPrice;
		this.images = images;
		this.variants = variants;
	}
	@PrePersist()
	public void onCreate() {
		this.createAt=LocalDateTime.now();
			this.updatedAt = LocalDateTime.now(); 
	}
	@PreUpdate()
	public void onUpdate() {
		this.updatedAt=LocalDateTime.now();
	}
	
	public ProductModel() {
		
	}

	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public BigDecimal getBasePrice() {
		return basePrice != null ? basePrice.setScale(2, RoundingMode.HALF_UP) : null;
	}
	public void setBasePrice(BigDecimal basePrice) {
	    this.basePrice = basePrice != null ? basePrice.setScale(2, RoundingMode.HALF_UP) : null;
	}
	public BigDecimal getDiscountPer() {
		return discountPer;
	}
	public void setDiscountPer(BigDecimal discountPer) {
		this.discountPer = discountPer;
	}
	public ProductTypes getProductType() {
		return productType;
	}
	public void setProductType(ProductTypes productTypeid) {
		this.productType = productTypeid;
	}
	public Gender getGender() {
		return gender;
	}
	public void setGender(Gender gender) {
		this.gender = gender;
	}
	public CollectionModel getCollection() {
		return collection;
	}
	public void setCollection  (CollectionModel collectionId) {
		this.collection = collectionId;
	}
	public String getColor() {
		return color;
	}
	public void setColor(String color) {
		this.color = color;
	}
	public FitType getFit() {
		return fit;
	}
	public void setFit(FitType fit) {
		this.fit = fit;
	}
	public LocalDateTime getCreateAt() {
		return createAt;
	}
	public void setCreateAt(LocalDateTime createAt) {
		this.createAt = createAt;
	}
	public LocalDateTime getUpdatedAt() {
		return updatedAt;
	}
	public void setUpdatedAt(LocalDateTime updatedAt) {
		this.updatedAt = updatedAt;
	}
	public boolean isIs_active() {
		return is_active;
	}
	public void setIs_active(boolean is_active) {
		this.is_active = is_active;
	}

	public int getMaxDiscountPrice() {
		return maxDiscountPrice;
	}
	public void setMaxDiscountPrice(int maxDiscountPrice) {
		this.maxDiscountPrice = maxDiscountPrice;
	}
	
	public BigDecimal getFinalPrice() {
		   if (basePrice != null && discountPer != null) {
		        BigDecimal discountAmount = basePrice.multiply(discountPer).divide(BigDecimal.valueOf(100));
		        BigDecimal finalPrice = basePrice.subtract(discountAmount);

		        // Convert int to BigDecimal for comparison
		        BigDecimal maxDiscount = BigDecimal.valueOf(maxDiscountPrice);
		       
		        if (finalPrice.compareTo(maxDiscount) < 0) {
		            return maxDiscount.setScale(2, RoundingMode.HALF_UP);
		        } else {
		            return finalPrice.setScale(2, RoundingMode.HALF_UP);
		        }
		    }
		   return basePrice;
	}
	public ProductImages getMainImage() {
	    if (images == null) return null;
	    return images.stream()
	                 .filter(ProductImages::getIs_main)
	                 .findFirst()
	                 .orElse(null);
	}
	public List<ProductImages> getImages() {
		return images;
	}
	
	public List<ProductVariant> getVariants() {
		return variants;
	}
	
	

	
}

package com.example.demo.Dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.format.annotation.NumberFormat;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.Dto.validationGroups.Step1;
import com.example.demo.Dto.validationGroups.Step2;
import com.example.demo.Dto.validationGroups.Step3;
import com.example.demo.model.CollectionModel;
import com.example.demo.model.FitType;
import com.example.demo.model.Gender;
import com.example.demo.model.ProductTypes;

import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.Valid;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class ProductDto {
	private int id;
	@NotBlank(message="Product name cannot be blank",groups=validationGroups.Step1.class)
	private String name;
	@Size(min=5,max=500,message="Description must be between 5 and 500 characters",groups=validationGroups.Step1.class)
	private String description;
	@DecimalMin(value = "0.01", message = "Price must be greater than 0",groups=validationGroups.Step1.class)
	@Digits(integer = 10, fraction = 3, message = "Max 3 digits after decimal allowed",groups=validationGroups.Step1.class)
	private BigDecimal basePrice;
	@DecimalMin(value = "0.0", inclusive = true, message = "Discount can't be negative", groups = validationGroups.Step1.class)
	@DecimalMax(value = "100.0", inclusive = false, message = "Discount can't be more than 100", groups = validationGroups.Step1.class)
	private BigDecimal discountPer;
	@NotNull(message="Please select a product type",groups=validationGroups.Step1.class)
	private Integer productType;
	@NotNull(message="Please select a gender",groups=validationGroups.Step1.class)
	private Gender gender;
	@NotNull(message="Please select a Collection Type",groups=validationGroups.Step1.class)
	private Integer collection;
	@NotBlank(message="Color cannot be null,groups=validationGroups.Step1.class",groups=validationGroups.Step1.class)
	private String color;
	@NotNull(message="Please select a fit type",groups=validationGroups.Step1.class)
	private FitType fit;
    @Valid
    @NotEmpty(message = "At least 1 variant is required", groups = validationGroups.Step2.class)	
    private List<VarientDto> variants=new ArrayList<>();;
    @NotNull(message = "Main image cannot be empty", groups = validationGroups.Step3.class)
   	private MultipartFile mainImage;
    @Size(min = 3, message = "At least 3 images are required", groups = validationGroups.Step3.class)
	private List<MultipartFile> images=new ArrayList<>();
	@Min(value=0,message="please enter a positive value",groups=validationGroups.Step1.class)
	private int maxDiscountprice;
    private String existingMainImageUrl;
    private List<String> existingImages=new ArrayList<>();
 
	//to check if image is null
	//if result is false validation fails
    @AssertTrue(message = "Main image cannot be empty", groups = validationGroups.Step3.class)
    public boolean isMainImagePresent() {
        return mainImage != null && !mainImage.isEmpty();
    }

	


	


	public ProductDto(@NotBlank(message = "Product name cannot be blank", groups = Step1.class) String name,
			@Size(min = 5, max = 500, message = "Description must be between 5 and 500 characters", groups = Step1.class) String description,
			@DecimalMin(value = "0.01", message = "Price must be greater than 0", groups = Step1.class) @Digits(integer = 10, fraction = 3, message = "Max 3 digits after decimal allowed", groups = Step1.class) BigDecimal basePrice,
			@DecimalMin(value = "0.0", inclusive = true, message = "Discount can't be negative", groups = Step1.class) @DecimalMax(value = "100.0", inclusive = false, message = "Discount can't be more than 100", groups = Step1.class) BigDecimal discountPer,
			@NotNull(message = "Please select a product type", groups = Step1.class) Integer productType,
			@NotNull(message = "Please select a gender", groups = Step1.class) Gender gender,
			@NotNull(message = "Please select a Collection Type", groups = Step1.class) Integer collection,
			@NotBlank(message = "Color cannot be null,groups=validationGroups.Step1.class", groups = Step1.class) String color,
			@NotNull(message = "Please select a fit type", groups = Step1.class) FitType fit,
			@Valid @NotEmpty(message = "At least 1 variant is required", groups = Step2.class) List<VarientDto> variants,
			@NotNull(message = "Main image cannot be empty", groups = Step3.class) MultipartFile mainImage,
			@Size(min = 3, message = "At least 3 images are required", groups = Step3.class) List<MultipartFile> images,
			@Min(value = 0, message = "please enter a positive value", groups = Step1.class) int maxDiscountprice,
			String existingMainImageUrl, List<String> existingImages) {
		super();
		this.name = name;
		this.description = description;
		this.basePrice = basePrice;
		this.discountPer = discountPer;
		this.productType = productType;
		this.gender = gender;
		this.collection = collection;
		this.color = color;
		this.fit = fit;
		this.variants = variants;
		this.mainImage = mainImage;
		this.images = images;
		this.maxDiscountprice = maxDiscountprice;
		this.existingMainImageUrl = existingMainImageUrl;
		this.existingImages = existingImages;
	}







	public int getId() {
		return id;
	}







	public void setId(int id) {
		this.id = id;
	}







	public int getMaxDiscountprice() {
		return maxDiscountprice;
	}

	public void setMaxDiscountprice(int maxDiscountprice) {
		this.maxDiscountprice = maxDiscountprice;
	}

	public List<VarientDto> getVariants() {
		return variants;
	}

	public void setVariants(List<VarientDto> variants) {
		this.variants = variants;
	}

	
	
	
	public MultipartFile getMainImage() {
		return mainImage;
	}

	public void setMainImage(MultipartFile mainImage) {
		this.mainImage = mainImage;
	}

	public List<MultipartFile> getImages() {
		return images;
	}

	public void setImages(List<MultipartFile> images) {
		this.images = images;
	}

	public ProductDto() {
		
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
		return basePrice;
	}
	public void setBasePrice(BigDecimal basePrice) {
		this.basePrice = basePrice;
	}
	public BigDecimal getDiscountPer() {
		return discountPer;
	}
	public void setDiscountPer(BigDecimal discountPer) {
		this.discountPer = discountPer;
	}
	public Integer getProductType() {
		return productType;
	}
	public void setProductType(Integer productType) {
		this.productType = productType;
	}
	public Gender getGender() {
		return gender;
	}
	public void setGender(Gender gender) {
		this.gender = gender;
	}
	public Integer getCollection() {
		return collection;
	}
	public void setCollection(Integer collection) {
		this.collection = collection;
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







	public String getExistingMainImageUrl() {
		return existingMainImageUrl;
	}







	public void setExistingMainImageUrl(String existingMainImageUrl) {
		this.existingMainImageUrl = existingMainImageUrl;
	}







	public List<String> getExistingImages() {
		return existingImages;
	}







	public void setExistingImages(List<String> existingImages) {
		this.existingImages = existingImages;
	}
	

}

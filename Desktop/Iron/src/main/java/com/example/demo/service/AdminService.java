package com.example.demo.service;



import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.Dto.CouponDto;
import com.example.demo.Dto.ProductDto;

import com.example.demo.Dto.ProductTypeDto;
import com.example.demo.Dto.UserDto;
import com.example.demo.Dto.VarientDto;
import com.example.demo.Repository.CollectionRepo;
import com.example.demo.Repository.CouponRepo;
import com.example.demo.Repository.ProductImageRepo;
import com.example.demo.Repository.ProductRepo;
import com.example.demo.Repository.ProductTypeRepo;
import com.example.demo.Repository.ProductVarientRepo;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.exception.UserNotFoundException;
import com.example.demo.model.CollectionModel;
import com.example.demo.model.CouponModel;
import com.example.demo.model.NewUserModel;
import com.example.demo.model.ProductImages;
import com.example.demo.model.ProductModel;
import com.example.demo.model.ProductTypes;
import com.example.demo.model.ProductVariant;
import com.example.demo.model.Role;
import com.example.demo.model.Size;
import com.example.demo.model.Status;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;

@Service
public class AdminService {
	@Autowired
	AuthenticationManager authmanager;
	
	@Autowired
	UserDetailsService userDetailsService;
	
	@Autowired
	com.example.demo.Repository.UserRepo userRepo;
	
	  @Autowired
	    private PasswordEncoder passwordEncoder;
	  
	  @Autowired
	  ProductTypeRepo productTypeRepo;
	  
	  @Autowired
	  CollectionRepo collectionRepo;
	  
	  @Autowired
	  ProductRepo productRepo;
	  
	  @Autowired
	  CouponRepo couponRepo;
	  
	  @Autowired
	  ProductVarientRepo varientRepo;
	  
	  @Autowired
	  ProductImageRepo productImageRepo;
	
	public void verify(String email, String password) {
	    try {
	        // Check if user exists first
	        userDetailsService.loadUserByUsername(email); // will throw UsernameNotFoundException if not found

	        // Then authenticate credentials
	        Authentication auth = authmanager.authenticate(
	            new UsernamePasswordAuthenticationToken(email, password)
	        );

	        if (!auth.isAuthenticated()) {
	            throw new BadCredentialsException("Invalid credentials");
	        }

	    } catch (UsernameNotFoundException e) {
	        throw new UsernameNotFoundException(e.getMessage());
	    } catch (BadCredentialsException e) {
	        throw new BadCredentialsException("Incorrect password");
	    } catch (Exception e) {
	    	 e.printStackTrace();
	        throw new RuntimeException("Internal server error");
	    }
	}


	
	public Page<NewUserModel>  viewUser(int page,int size,String keyword) {
		// TODO Auto-generated method stub
	
			Role userRole=Role.User;
			Pageable pageable=PageRequest.of(page,size,Sort.by("createdAt").descending());
			 Page<NewUserModel> users;
		        if (keyword != null && !keyword.isEmpty()) {
		            users = userRepo.findByRoleAndNameContainingIgnoreCaseOrRoleAndEmailContainingIgnoreCase(
		                    userRole, keyword, userRole, keyword, pageable);
		        } else {
		            users = userRepo.findByRole(userRole, pageable);
		        }

		        if (users.getContent().isEmpty()) {
		        
		            throw new	UserNotFoundException("No users found" + (keyword != null ? " for the keyword: " + keyword : ""));
		      
		        }
		     
		        return users;
			
		
	
	}

	public void setUserActiveStatus(int id,boolean active) {
		

			NewUserModel user=userRepo.findById(id).orElseThrow(()->new UsernameNotFoundException("User not Found"));
			  user.setIs_active(active); 
			    userRepo.save(user); 
	
	}


	public void addUser(UserDto userDto) {
		try {
	
			NewUserModel user=new NewUserModel();
			user.setEmail(userDto.getEmail());
			user.setName(userDto.getName());
			String password=passwordEncoder.encode(userDto.getPassword());
			user.setPassword(password);
			user.setPhone(userDto.getPhone());
			userRepo.save(user);
		}
		catch(Exception e){
			e.printStackTrace();
			throw new RuntimeException("internal server error");
		
	}


}



	public void addProductTypes(ProductTypeDto productTypeDto) {
		List<ProductTypes> existing=productTypeRepo.findByNameIgnoreCaseAndGender(productTypeDto.getName(),productTypeDto.getGender());
		if(!existing.isEmpty()) {
			throw new IllegalArgumentException("Product Type already exists");
		}
		else {
			ProductTypes productTypes=new ProductTypes();
			productTypes.setName(productTypeDto.getName());
			productTypes.setDescription(productTypeDto.getDescription());
			productTypes.setGender(productTypeDto.getGender());
		//	productTypes.setImage(productTypeDto.getImage());
			productTypeRepo.save(productTypes);
		}
		
	}
	public void addCoontrollerProductTypes(ProductTypeDto productTypeDto,MultipartFile image) throws IOException {
		String imagePath=getImagePath(image);
		
		List<ProductTypes> existing=productTypeRepo.findByNameIgnoreCaseAndGender(productTypeDto.getName(),productTypeDto.getGender());
		if(!existing.isEmpty()) {
			throw new IllegalArgumentException("Product Type already exists");
		}
		else {
			ProductTypes productTypes=new ProductTypes();
			productTypes.setName(productTypeDto.getName());
			productTypes.setDescription(productTypeDto.getDescription());
			productTypes.setGender(productTypeDto.getGender());
			productTypes.setImage(imagePath);
			productTypeRepo.save(productTypes);
		}
		
		
	}
	public String getImagePath(MultipartFile image) throws IOException {
		Path path=Paths.get("uploads/images");
		//create directory if not exists
		if(!Files.exists(path)) {
			Files.createDirectories(path);
		}
		//create unique file name
		String filename=UUID.randomUUID().toString()+"-"+image.getOriginalFilename();
		//define full path to save the url
		Path filepath=path.resolve(filename);
		Files.copy(image.getInputStream(),filepath,StandardCopyOption.REPLACE_EXISTING);
		
		return "/uploads/images/" + filename;
	}
	
	public void deleteOldImageIfExists(String imagePath) {
	    if (imagePath != null && !imagePath.isEmpty()) {
	        // Remove leading slash if present (e.g., /images/abc.jpg -> images/abc.jpg)
	        String relativePath = imagePath.startsWith("/") ? imagePath.substring(1) : imagePath;

	        // Resolve full path
	        Path path = Paths.get("uploads/images").resolve(relativePath);

	        try {
	            Files.deleteIfExists(path);
	            System.out.println("product deleted successfully");
	        } catch (IOException e) {
	            System.out.println("Failed to delete old image: " + e.getMessage());
	        }
	    }
	}



	public void editprdType(ProductTypeDto productTypeDto,int id) throws IOException {
		// TODO Auto-generated method stub
		Optional<ProductTypes> productTypeOpt=productTypeRepo.findById(id);
		if(productTypeOpt.isEmpty()) {
			throw new ResourceNotFoundException("ProductType not found");
		}
	
		List<ProductTypes> existing=productTypeRepo.findByNameIgnoreCaseAndGender(productTypeDto.getName(),productTypeDto.getGender());
		//check if product type with same gender already exists
		if(!existing.isEmpty() && existing.stream().anyMatch(p -> p.getId() != id)) {
			throw new IllegalArgumentException("Product type already exists");
		}
		ProductTypes productType=productTypeOpt.get();
		
			if(productTypeDto.getImage() != null && !productTypeDto.getImage().isEmpty()) {
				String oldImagePath = productType.getImage();
			    deleteOldImageIfExists(oldImagePath);
			    
				String imagepath=getImagePath(productTypeDto.getImage());
				System.out.println(imagepath);
				productType.setImage(imagepath);
			}
			

			productType.setName(productTypeDto.getName());
			productType.setDescription(productTypeDto.getDescription());
			productType.setGender(productTypeDto.getGender());
	
			productTypeRepo.save(productType);
		}



	public void editProductTypeStatus(int id,boolean status) {
		// TODO Auto-generated method stub
		Optional<ProductTypes> productTypesOpt=productTypeRepo.findById(id);
		if(productTypesOpt.isEmpty()) {
			throw new ResourceNotFoundException("productType does not exist");
		}
		else {
			ProductTypes productTypes=productTypesOpt.get();
			productTypes.setIs_active(status);
			productTypeRepo.save(productTypes);
		}
	}



	public Page<ProductTypes> viewProductTypes(int page, int size, String keyword) {
		// TODO Auto-generated method stub
		Pageable pageable=PageRequest.of(page,size,Sort.by("createdAt").descending());
		 Page<ProductTypes> productTypes;
		if(keyword!=null && !keyword.isEmpty()) {
			productTypes=productTypeRepo.findByNameContainingIgnoreCase(keyword, pageable);
		}
		else {
			productTypes=productTypeRepo.findAll(pageable);
		}
		if(productTypes.isEmpty()) {
			throw new ResourceNotFoundException("No ProductType found "+ (keyword!=null ? "for keyword: "+keyword :""));
			
		}
		return productTypes;
		
	}



	public ProductTypes getProductType(int id) {
		// TODO Auto-generated method stub
		
		ProductTypes productType=productTypeRepo.findById(id).orElseThrow(()->new ResourceNotFoundException ("Product Type not found"));
		return productType;
	}



	public void addCollection(String name) {
		// TODO Auto-generated method stub
		 if (name == null || name.trim().isEmpty()) {
		        throw new IllegalArgumentException("Collection name cannot be null or empty");
		    }

		 
		Optional <CollectionModel> collection=collectionRepo.findByName(name);
		if(collection.isPresent()) {
			throw new IllegalArgumentException("Collection already exists with name: "+name);
		}
		else {
			CollectionModel newCollection=new CollectionModel();
			//set name and delete trailing spaces
			newCollection.setName(name.trim());
			collectionRepo.save(newCollection);
		}
			
		
		
	}



	public Page<CollectionModel> getCollections(int page, int size, String keyword) {
		// TODO Auto-generated method stub
		
		Pageable pageable=PageRequest.of(page,size,Sort.by("createdAt").descending());
		
		Page<CollectionModel> collections;
		if(keyword!=null && !keyword.isEmpty()) {
			collections=collectionRepo.findByNameContainingIgnoreCase(keyword,pageable);
		}
		else {
			collections=collectionRepo.findAll(pageable);
			
		}
		if(collections.isEmpty()) {
			throw new ResourceNotFoundException("No Collections Present "+(keyword!=null ? "for keyword: "+keyword :""));
		}
		return collections;
		
	}



	public void setStatus(int id, boolean status) {
		// TODO Auto-generated method stub
		Optional <CollectionModel> collectionopt=collectionRepo.findById(id);
		if(collectionopt.isEmpty()) {
			throw new ResourceNotFoundException("Collection not found");
			
		}
		CollectionModel collection=collectionopt.get();
		collection.setIs_active(status);
		collectionRepo.save(collection);
	}



	public void editCollection(int id, String name) {
		// TODO Auto-generated method stub
		Optional <CollectionModel> collectionopt=collectionRepo.findById(id);

		Optional <CollectionModel> existing=collectionRepo.findByNameIgnoreCase(name);
		if(collectionopt.isEmpty()) {
			throw new ResourceNotFoundException("Collection not found");
		}
		if(existing.isPresent() && existing.get().getId()!=id) {
			throw new IllegalArgumentException ("Collection name already exists");
		}

		CollectionModel collection=collectionopt.get();
		collection.setName(name);
		collectionRepo.save(collection);
	}



	public CollectionModel getCollection(int id) {
		// TODO Auto-generated method stub
		Optional <CollectionModel> collectionopt=collectionRepo.findById(id);
		if(collectionopt.isEmpty()) {
			throw new ResourceNotFoundException("Collection not found");
		}
		CollectionModel collection=collectionopt.get();
		return collection;
	}


	@Transactional
	public void addProduct(@Valid ProductDto productDto) throws IOException {
		// TODO Auto-generated method stub
		
		
			
			
			CollectionModel collection=collectionRepo.findById(productDto.getCollection()).orElseThrow(()->new ResourceNotFoundException ("Collection not found"));
			ProductTypes productType=productTypeRepo.findById(productDto.getProductType()).orElseThrow(()->new ResourceNotFoundException ("product type not found"));
			
			ProductModel product=new ProductModel ();
			product.setName(productDto.getName());
			product.setBasePrice(productDto.getBasePrice());
			product.setCollection(collection);
			System.out.println(productDto.getColor());
			product.setColor(productDto.getColor());
			product.setDescription(productDto.getDescription());
			product.setDiscountPer(productDto.getDiscountPer());
			product.setFit(productDto.getFit());
			product.setGender(productDto.getGender());
			product.setProductType(productType);
			product.setMaxDiscountPrice(productDto.getMaxDiscountprice());
		    product.setIs_active(true);
			
			productRepo.save(product);
			
		
			
			
			for(VarientDto varient:productDto.getVariants()) {
				if (varient.getStock()==0 ) {
					continue;
				}
				ProductVariant productVarient=new ProductVariant();
				productVarient.setProduct(product);
				productVarient.setSize(varient.getSize());
				productVarient.setStock(varient.getStock());
				productVarient.setIs_active(true);
				varientRepo.save(productVarient);
			}
			
			if(productDto.getMainImage()!=null && !productDto.getMainImage().isEmpty()) {
				ProductImages mainImage=new ProductImages();
				String mainimageurl=getImagePath(productDto.getMainImage());
				mainImage.setProduct(product);
				mainImage.setImage(mainimageurl);
				mainImage.setIs_main(true);
				productImageRepo.save(mainImage);
			}
	
			
			
			for(MultipartFile images:productDto.getImages()) {
				ProductImages productImage=new ProductImages();
				productImage.setProduct(product);
				productImage.setIs_main(false);
				String imageurl=getImagePath(images);
				productImage.setImage(imageurl);
				productImageRepo.save(productImage);
			}
			
			
		
		
	}



	public Page<ProductModel> viewProducts(int page, int size, String keyword) {
		// TODO Auto-generated method stub
		Pageable pageable= PageRequest.of(page, size,Sort.by("createAt").descending());
		Page<ProductModel> products;
		if(keyword!=null && !keyword.isEmpty()) {
			products=productRepo.findByNameContainingIgnoreCase(pageable,keyword);
		}
		else {
			products=productRepo.findAll(pageable);
		}
	
		if(products.isEmpty()) {
			throw new ResourceNotFoundException("No Products Found "+(keyword!=null ? "for keyword"+keyword:""));
		}
		return products;
	}



	public List<CollectionModel> getCollections() {
		// TODO Auto-generated method stub
		return collectionRepo.findAll();
		
	}



	public List<CollectionModel> getAllCollections() {
		// TODO Auto-generated method stub
		return collectionRepo.findAll();
	}



	public void disableProducts(int id, boolean status) {
		// TODO Auto-generated method stub
		ProductModel product=productRepo.findById(id).orElseThrow(()->new ResourceNotFoundException("Product not found"));
			product.setIs_active(status);
			productRepo.save(product);
		
		
	}



	public ProductModel getProduct(int id) {
		// TODO Auto-generated method stub
		ProductModel product=productRepo.findById(id).orElseThrow(()->new ResourceNotFoundException("Product detail not found"));
		return product;
	}



	public void editProductDiscount(int id, BigDecimal discountPer) {
		// TODO Auto-generated method stub
		ProductModel product=productRepo.findById(id).orElseThrow(()->new ResourceNotFoundException("Product  not found"));
		product.setDiscountPer(discountPer);
		productRepo.save(product);
		
	}



	public ProductDto converToDto(ProductModel product) {
		// TODO Auto-generated method stub
		
		ProductDto productDto=new ProductDto();
		productDto.setId(product.getId());
		productDto.setBasePrice(product.getBasePrice());
		productDto.setCollection(product.getCollection().getId());
		productDto.setColor(product.getColor());
		productDto.setDescription(product.getDescription());
		productDto.setDiscountPer(product.getDiscountPer());
		productDto.setFit(product.getFit());
		productDto.setGender(product.getGender());
		productDto.setExistingMainImageUrl(product.getMainImage().getImage());
		productDto.setExistingImages(product.getImages().stream().filter(image->image.getIs_main()==false).map(image->image.getImage()).collect(Collectors.toList()));
		productDto.setMaxDiscountprice(product.getMaxDiscountPrice());
		productDto.setName(product.getName());
		List<VarientDto> variantDtos = product.getVariants()
			    .stream()
			    .map(variant -> {
			        VarientDto dto = new VarientDto();
			        dto.setSize(variant.getSize());
			        dto.setStock(variant.getStock());
			        return dto;
			    })
			    .collect(Collectors.toList());
	    Set<Size> existingSizes = variantDtos.stream().map(VarientDto::getSize).collect(Collectors.toSet());
	    for (Size size : Size.values()) {
	        if (!existingSizes.contains(size)) {
	            VarientDto dto = new VarientDto();
	            dto.setSize(size);
	            dto.setStock(0); // default
	            variantDtos.add(dto);
	        }
	    }
	    variantDtos.sort(Comparator.comparing(v -> v.getSize()));

		productDto.setVariants(variantDtos);

		productDto.setProductType(product.getProductType().getId());
	
		return productDto;
	}
	
	public void deleteImage(String imageName) {
		
		  try {
	            // Assuming images are stored in a directory like /uploads/
	            Path imagePath = Paths.get("uploads", imageName); // or absolute path if needed
	            Files.deleteIfExists(imagePath);
	            System.out.println("Deleted image: " + imagePath);
	        } catch (IOException e) {
	            System.out.println("Failed to delete image: " + imageName + " - " + e.getMessage());
	        }
		
	}


	@Transactional
	public void editProduct(ProductDto productDto, List<String> deletedImages) throws IOException {
		// TODO Auto-generated method stub
	
		if (deletedImages != null && !deletedImages.isEmpty()) {
		    for (String imageName : deletedImages) {
		    	deleteImage(imageName);
		    	   productImageRepo.deleteByImageName(imageName);
		    }
		 
		    }
		
		
		

			CollectionModel collection=collectionRepo.findById(productDto.getCollection()).orElseThrow(()->new ResourceNotFoundException ("Collection not found"));
			ProductTypes productType=productTypeRepo.findById(productDto.getProductType()).orElseThrow(()->new ResourceNotFoundException ("product type not found"));
			
			ProductModel product=productRepo.findById(productDto.getId()).orElseThrow(()->new ResourceNotFoundException("resource not found"));
			product.setName(productDto.getName());
			product.setBasePrice(productDto.getBasePrice());
			product.setCollection(collection);
			System.out.println(productDto.getColor());
			product.setColor(productDto.getColor());
			product.setDescription(productDto.getDescription());
			product.setDiscountPer(productDto.getDiscountPer());
			product.setFit(productDto.getFit());
			product.setGender(productDto.getGender());
			product.setProductType(productType);
			product.setMaxDiscountPrice(productDto.getMaxDiscountprice());
		    product.setIs_active(true);
			
			productRepo.save(product);
			
		
			

			
			for (VarientDto varient : productDto.getVariants()) {
			

			    // Check if variant exists by product and size
			    Optional<ProductVariant> existingVariantOpt = varientRepo.findByProductAndSize(product, varient.getSize());

			    ProductVariant productVariant;

			    if (existingVariantOpt.isPresent()) {
			        productVariant = existingVariantOpt.get();
			        // Update existing variant
			     
			        productVariant.setStock(varient.getStock());
			        productVariant.setIs_active(true);
				    varientRepo.save(productVariant);

			    } else {
			        // Create new variant
			        productVariant = new ProductVariant();
			        productVariant.setProduct(product);
			        productVariant.setSize(varient.getSize());
			        productVariant.setStock(varient.getStock());
			        productVariant.setIs_active(true);
				    varientRepo.save(productVariant);

			    }

			}

			
			if(productDto.getMainImage()!=null && !productDto.getMainImage().isEmpty()) {
				
				ProductImages mainImage=productImageRepo.findAllByProduct_IdAndIsMainTrue(productDto.getId());
				deleteImage(mainImage.getImage());
				String mainimageurl=getImagePath(productDto.getMainImage());
				mainImage.setProduct(product);
				mainImage.setImage(mainimageurl);
				mainImage.setIs_main(true);
				productImageRepo.save(mainImage);
			}
	
			
			
			for(MultipartFile images:productDto.getImages()) {
				ProductImages productImage=new ProductImages();
				productImage.setProduct(product);
				productImage.setIs_main(false);
				String imageurl=getImagePath(images);
				productImage.setImage(imageurl);
				productImageRepo.save(productImage);
			}
			

		
	}



	public void deleteCollection(int id) {
		// TODO Auto-generated method stub
		CollectionModel collection=collectionRepo.findById(id).orElseThrow(()-> new ResourceNotFoundException("Collection not found"));
		 collectionRepo.delete(collection);
		
	}



	public void addCoupon(CouponDto dto) {
		// TODO Auto-generated method stub
		CouponModel coupon =new CouponModel();
		coupon.setCouponCode(dto.getCouponCode());
		coupon.setDescription(dto.getDescription());
		coupon.setDiscountPer(dto.getDiscountPer());
		coupon.setExpiryDate(dto.getExpiryDate());
		coupon.setMinAmount(dto.getMinAmount());
		if (dto.getPerUserLimit() == null || dto.getPerUserLimit() == -1) {
			
			coupon.setPerUserLimit(-1);
		}
		else {
			coupon.setPerUserLimit(dto.getPerUserLimit());

		}

		if (dto.getTotalUsageLimit() == null || dto.getTotalUsageLimit() == -1) {
			coupon.setTotalUsageLimit(-1);

		}
		else {
			coupon.setTotalUsageLimit(dto.getTotalUsageLimit());

		}
		couponRepo.save(coupon);
	}



	public Page<CouponModel> getCoupons(String keyword, Status filter, int page, int size) {
		// TODO Auto-generated method stub
			Pageable pageable=PageRequest.of(page, size,Sort.by("createdAt").descending());
			 if (StringUtils.hasText(keyword)) {
			        return couponRepo.findByCouponCodeContainingIgnoreCase(keyword, pageable);
			    }
			    
			    // If only filter is provided
			    if (filter!=null) {
			        return couponRepo.findByStatus(filter, pageable);
			    }
			return couponRepo.findAll(pageable);
			
	}



	public CouponModel getcoupon(int couponId) {
		// TODO Auto-generated method stub
		CouponModel coupon=couponRepo.findById(couponId).orElseThrow(()-> new ResourceNotFoundException("coupon not found"));
		
		return coupon;
	}



	public CouponDto setdto(CouponModel coupon) {
		// TODO Auto-generated method stub
		CouponDto dto=new CouponDto();
		dto.setCouponCode(coupon.getCouponCode());
		dto.setDescription(coupon.getDescription());
		dto.setDiscountPer(coupon.getDiscountPer());
		dto.setExpiryDate(coupon.getExpiryDate());
		dto.setId(coupon.getId());
		dto.setMinAmount(coupon.getMinAmount());
		dto.setPerUserLimit(coupon.getPerUserLimit());
		dto.setTotalUsageLimit(coupon.getTotalUsageLimit());
		
		
		return dto;
	}



	public void editcouponValue(CouponDto dto) {
		// TODO Auto-generated method stub
		CouponModel coupon=couponRepo.findById(dto.getId()).orElseThrow(()->new ResourceNotFoundException("coupon not found"));
		coupon.setCouponCode(dto.getCouponCode());
		coupon.setDescription(dto.getDescription());
		coupon.setDiscountPer(dto.getDiscountPer());
		coupon.setExpiryDate(dto.getExpiryDate());
		coupon.setMinAmount(dto.getMinAmount());
		if (dto.getPerUserLimit() == null || dto.getPerUserLimit() == -1) {
			
			coupon.setPerUserLimit(-1);
		}
		else {
			coupon.setPerUserLimit(dto.getPerUserLimit());

		}

		if (dto.getTotalUsageLimit() == null || dto.getTotalUsageLimit() == -1) {
			coupon.setTotalUsageLimit(-1);

		}
		else {
			coupon.setTotalUsageLimit(dto.getTotalUsageLimit());

		}
		couponRepo.save(coupon);
	}



	public void deleteCoupon(int couponid) {
		// TODO Auto-generated method stub
		couponRepo.deleteById(couponid);
		
	}
				  
		
	




	
	
	
	
}

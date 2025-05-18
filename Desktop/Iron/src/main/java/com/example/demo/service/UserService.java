package com.example.demo.service;


import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.data.domain.Pageable;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.demo.Dto.AddressDto;
import com.example.demo.Dto.UserDto;
import com.example.demo.Dto.UserEditDto;
import com.example.demo.Repository.AddressRepo;
import com.example.demo.Repository.CollectionRepo;
import com.example.demo.Repository.CouponRepo;
import com.example.demo.Repository.ProductImageRepo;
import com.example.demo.Repository.ProductRepo;
import com.example.demo.Repository.ProductTypeRepo;
import com.example.demo.Repository.ProductVarientRepo;
import com.example.demo.Repository.WishlistRepo;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.exception.UserNotFoundException;
import com.example.demo.model.AddressModel;
import com.example.demo.model.CollectionModel;
import com.example.demo.model.CouponModel;
import com.example.demo.model.Gender;
import com.example.demo.model.NewUserModel;
import com.example.demo.model.ProductModel;
import com.example.demo.model.ProductTypes;
import com.example.demo.model.Status;
import com.example.demo.model.WishlistModel;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;

@Service
public class UserService {

	
	
	@Autowired
	com.example.demo.Repository.UserRepo userRepo;
	
	  
	  @Autowired
	  ProductTypeRepo productTypeRepo;
	  
	  @Autowired
	  CollectionRepo collectionRepo;
	  
	  @Autowired
	  ProductRepo productRepo;
	  
	  @Autowired
	  AdminService adminService;
	  
	
	  @Autowired
	   PasswordEncoder passwordEncoder;
	  
	  @Autowired
	  ProductVarientRepo varientRepo;
	  
	  @Autowired
	  ProductImageRepo productImageRepo;
	  
	  @Autowired
	  AddressRepo addressRepo;
	  @Autowired
	  WishlistRepo wishlistRepo;
	  
	  @Autowired
	  CouponRepo couponRepo;

	public boolean emailExsits(String email) {
		// TODO Auto-generated method stub
		if(userRepo.existsByEmail(email)) {
			return true;
		}
		else {
			return false;
		}
	}
	@Transactional
	public void addUser(UserDto tempUser) {
		// TODO Auto-generated method stub
			
			NewUserModel user=new NewUserModel();
			user.setEmail(tempUser.getEmail());
			user.setGender(tempUser.getGender());
			user.setName(tempUser.getName());
			String password=passwordEncoder.encode(tempUser.getPassword());
			user.setPassword(password);
			user.setPhone(tempUser.getPhone());
			userRepo.save(user);
			if(tempUser.getRefToken()!=null) {
		        NewUserModel referrer = userRepo.findByReferralToken(tempUser.getRefToken());

				CouponModel coupon=new CouponModel();
				coupon.setCouponCode("Refferal-" + UUID.randomUUID().toString().substring(0, 5).toUpperCase());
				coupon.setCreatedAt(LocalDateTime.now());
				coupon.setDescription("Referral offer");
				coupon.setDiscountPer(20);

				LocalDate currentDateTime = LocalDate.now();
				LocalDate expiryDateTime = currentDateTime.plusYears(1);

				coupon.setExpiryDate(expiryDateTime);
				coupon.setMinAmount(50);
				coupon.setPerUserLimit(1);
				coupon.setStatus(Status.Active);
				coupon.setTotal_usage(0);
				coupon.setTotalUsageLimit(1);
				coupon.setUser(referrer);
				couponRepo.save(coupon);
			}
	
		
		
	}

	public void setPassword(String email, String newPassword) {
		// TODO Auto-generated method stub
		NewUserModel user=userRepo.findByEmail(email).orElseThrow(()-> new UserNotFoundException ("user not found"));
		String password=passwordEncoder.encode(newPassword);
		user.setPassword(password);
		userRepo.save(user);
	}


	
	public List<ProductTypes> getCategories(Gender gender) {
		// TODO Auto-generated method stub
		return productTypeRepo.findByGender(gender).stream().filter(ProductTypes::getIs_active).sorted(Comparator.comparing(ProductTypes::getName)).collect(Collectors.toList());
	}

	public Page<ProductModel> getProducts(Gender gender, List<Integer>  typeId, List<Integer>  collectionId, List<String> fit, String sort, int page, int size) {
		// TODO Auto-generated method stub
		Pageable pageable= PageRequest.of(page, size,Sort.by("createAt").descending());
		List<ProductModel> products=productRepo.findByGender(gender).stream().filter(ProductModel::isIs_active).collect(Collectors.toList());
	   
	    if (sort != null) {
	        switch (sort) {
	            case "LowToHigh":
	                products.sort(Comparator.comparing(ProductModel::getFinalPrice));
	                break;
	            case "HighToLow":
	                products.sort(Comparator.comparing(ProductModel::getFinalPrice).reversed());
	                break;
	            case "A-Z":
	                products.sort(Comparator.comparing(ProductModel::getName));
	                break;
	            case "Z-A":
	                products.sort(Comparator.comparing(ProductModel::getName).reversed());
	                break;
	        }
			
		}
		if(typeId != null && !typeId.isEmpty()) {
			products=products.stream().filter(product -> typeId.contains(product.getProductType().getId())).collect(Collectors.toList());
		}
		if(collectionId!=null && !collectionId.isEmpty()) {
			products=products.stream().filter(product->collectionId.contains(product.getCollection().getId())).collect(Collectors.toList());
		}
		if(fit!=null && !fit.isEmpty()) {
			products=products.stream().filter(product->fit.contains(product.getFit().name())).collect(Collectors.toList());

		}
		
		
		int start = (int) pageable.getOffset();
		int end = Math.min((start + pageable.getPageSize()), products.size());
		List<ProductModel> paginatedProducts = products.subList(start, end);
		return new PageImpl<>(paginatedProducts, pageable, products.size());

	}

	public ProductTypes getCategoryById(int id) {
		// TODO Auto-generated method stub
		return productTypeRepo.findById(id).orElseThrow(()->new ResourceNotFoundException ("category not found"));
	}

	public List<CollectionModel> getcollections() {
		// TODO Auto-generated method stub
		return collectionRepo.findAll().stream().filter(CollectionModel::isIs_active).sorted(Comparator.comparing(CollectionModel::getName)).collect(Collectors.toList());
	}

	public ProductModel getProduct(int id) {
		// TODO Auto-generated method stub
		return productRepo.findById(id).orElseThrow(()->new ResourceNotFoundException ("product not found"));
	}

	public List<ProductModel> getProductByCollection(CollectionModel collection,int id) {
		// TODO Auto-generated method stub
		return productRepo.findByCollection(collection)
                .stream()
                .filter(product -> product.getId() != id)
                .limit(4)
                .collect(Collectors.toList());
	}

	public Page<ProductModel> getSearchProducts(Gender gender, String keyword, List<Integer> collectionId,
			List<String> fit, String sort, int page, int size,Integer typeId) {
		// TODO Auto-generated method stub
		Pageable pageable=PageRequest.of(page, size,Sort.by("CreateAt").descending());
		List<ProductModel> products=productRepo.findAll().stream().filter(ProductModel::isIs_active).collect(Collectors.toList());
		 if (sort != null) {
		        switch (sort) {
		            case "LowToHigh":
		                products.sort(Comparator.comparing(ProductModel::getFinalPrice));
		                break;
		            case "HighToLow":
		                products.sort(Comparator.comparing(ProductModel::getFinalPrice).reversed());
		                break;
		            case "A-Z":
		                products.sort(Comparator.comparing(ProductModel::getName));
		                break;
		            case "Z-A":
		                products.sort(Comparator.comparing(ProductModel::getName).reversed());
		                break;
		        }
		 }
			if(gender!=null) {
				products=products.stream().filter(product->product.getGender()==gender).collect(Collectors.toList());
			}
			if(typeId!=null) {
				products=products.stream().filter(product->product.getProductType().getId()==typeId).collect(Collectors.toList());

			}
			if(keyword!=null) {
				products=products.stream().filter(product->product.getName().toLowerCase().contains(keyword.toLowerCase())).collect(Collectors.toList());
			}
			
			if(collectionId!=null && !collectionId.isEmpty()) {
				products=products.stream().filter(product->collectionId.contains(product.getCollection().getId())).collect(Collectors.toList());
			}
			if(fit!=null && !fit.isEmpty()) {
				products=products.stream().filter(product->fit.contains(product.getFit().name())).collect(Collectors.toList());

			}
			
			
			int start = (int) pageable.getOffset();
			int end = Math.min((start + pageable.getPageSize()), products.size());
		
			List<ProductModel> paginatedProducts = products.subList(start, end);
			return new PageImpl<>(paginatedProducts, pageable, products.size());

	
	
	}

	public NewUserModel findUser(String email) {
		// TODO Auto-generated method stub
		NewUserModel user=userRepo.findByEmail(email).orElseThrow(()->new UserNotFoundException ("User Not found"));
		return user;
	}

	public UserEditDto setUserDto(NewUserModel user) {
		// TODO Auto-generated method stub
		UserEditDto userdto=new UserEditDto();
		userdto.setId(user.getId());
		userdto.setEmail(user.getEmail());
		userdto.setGender(user.getGender());
		userdto.setName(user.getName());
		userdto.setPhone(user.getPhone());
	
	
		return userdto;
	}

	public void changeEmail(String email, int id) {
		// TODO Auto-generated method stub
		if(userRepo.existsByEmail(email)) {
			throw new IllegalArgumentException("Email already exists");
		}
		else {
			NewUserModel usermodel=userRepo.findById(id).orElseThrow(()-> new UserNotFoundException("Unable to find the user"));
			usermodel.setEmail(email);
			userRepo.save(usermodel);
			
		}
		
	}

	public void ChangeProfile(@Valid UserEditDto userDto, int userid) throws IOException {
		// TODO Auto-generated method stub
		NewUserModel user=userRepo.findById(userid).orElseThrow(()->new UserNotFoundException("user not found"));
		user.setName(userDto.getName());
		user.setPhone(userDto.getPhone());
		user.setGender(userDto.getGender());
		
		if(!userDto.getImage().isEmpty() && userDto.getImage()!=null) {
			if(user.getImage()!=null && !user.getImage().isEmpty()) {
				adminService.deleteImage(user.getImage());
			}
			String image=adminService.getImagePath(userDto.getImage());
			user.setImage(image);
		}
	
		userRepo.save(user);
	}

	public void changePassword(String newPassword, String email) {
		// TODO Auto-generated method stub
		NewUserModel user= userRepo.findByEmail(email).orElseThrow(()->new UserNotFoundException("User Does not exist"));
		 String encodedPassword = passwordEncoder.encode(newPassword);
		user.setPassword(encodedPassword);
		userRepo.save(user);
	}

	public void addAddress(AddressDto addressDto, String username) {
		// TODO Auto-generated method stub
		 NewUserModel user = userRepo.findByEmail(username)
			        .orElseThrow(() -> new UserNotFoundException("User not found"));

			    AddressModel address = new AddressModel();
			    address.setName(addressDto.getName());
			    address.setPhone(addressDto.getPhone());
			    address.setPincode(addressDto.getPincode());
			    address.setLocality(addressDto.getLocality());
			    address.setAddress(addressDto.getAddress());
			    address.setCity(addressDto.getCity());
			    address.setState(addressDto.getState());
			    address.setLandmark(addressDto.getLandmark());
			    boolean isFirstAddress = addressRepo.countByUser(user) == 0;
			    address.setDeliveryAddress(isFirstAddress);
			    address.setAddressType(addressDto.getAddressType());
			    address.setUser(user);

			    addressRepo.save(address);
	}

	public List<AddressModel> getAddress(UserDetails userDetails) {
		// TODO Auto-generated method stub
		String email=userDetails.getUsername();
		NewUserModel user=userRepo.findByEmail(email).orElseThrow(()->new UserNotFoundException ("User not Found"));
	
		List<AddressModel> address=addressRepo.findByUser(user);
		return address;
	}

	public AddressDto setAddressDto(int id) {
		// TODO Auto-generated method stub
		AddressModel address=addressRepo.findById(id).orElseThrow(()->new UserNotFoundException ("Address not Found"));
		AddressDto addressDto=new AddressDto();
		addressDto.setId(address.getId());
		addressDto.setAddress(address.getAddress());
		addressDto.setAddressType(address.getAddressType());
		addressDto.setCity(address.getCity());
		addressDto.setLandmark(address.getLandmark());
		addressDto.setLocality(address.getLocality());
		addressDto.setName(address.getName());
		addressDto.setPhone(address.getPhone());
		addressDto.setPincode(address.getPincode());
		addressDto.setState(address.getState());
		return addressDto;
	}

	public void updateAddress(@Valid AddressDto addressDto) {
		// TODO Auto-generated method stub
		AddressModel address=addressRepo.findById(addressDto.getId()).orElseThrow(()->new UserNotFoundException ("Address not Found"));
		 address.setName(addressDto.getName());
		    address.setPhone(addressDto.getPhone());
		    address.setPincode(addressDto.getPincode());
		    address.setLocality(addressDto.getLocality());
		    address.setAddress(addressDto.getAddress());
		    address.setCity(addressDto.getCity());
		    address.setState(addressDto.getState());
		    address.setLandmark(addressDto.getLandmark());
		    address.setAddressType(addressDto.getAddressType());
		    addressRepo.save(address);
	}

	public void addProductToWishlist(ProductModel product, int userId) {
		  WishlistModel wishlist = new WishlistModel();
		    wishlist.setProduct(product);

		    NewUserModel user = userRepo.findById(userId)
		        .orElseThrow(() -> new UserNotFoundException("User not found"));

		    wishlist.setUser(user);
		    wishlist.setCreatedAt(LocalDateTime.now()); // Important: set createdAt timestamp

		    wishlistRepo.save(wishlist);
	}

		public Page<ProductModel> getWishlistProducts(Integer user_id, int page, int size, Gender gender, String sortBy, List<Integer> collection, List<String> fitType) {
			// TODO Auto-generated method stub
			Pageable pageable=PageRequest.of(page, size,Sort.by("createdAt").descending());
			NewUserModel user=userRepo.findById(user_id).orElseThrow(()->new UserNotFoundException("user not found"));
			List<ProductModel> products=wishlistRepo.findProductsByUserId(user_id);
			 if (sortBy != null) {
			        switch (sortBy) {
			            case "LowToHigh":
			                products.sort(Comparator.comparing(ProductModel::getFinalPrice));
			                break;
			            case "HighToLow":
			                products.sort(Comparator.comparing(ProductModel::getFinalPrice).reversed());
			                break;
			            case "A-Z":
			                products.sort(Comparator.comparing(ProductModel::getName));
			                break;
			            case "Z-A":
			                products.sort(Comparator.comparing(ProductModel::getName).reversed());
			                break;
			        }
			 }
				if(gender!=null) {
					products=products.stream().filter(product->product.getGender()==gender).collect(Collectors.toList());
				}
			
				
				if(collection!=null && !collection.isEmpty()) {
					products=products.stream().filter(product->collection.contains(product.getCollection().getId())).collect(Collectors.toList());
				}
				if(fitType!=null && !fitType.isEmpty()) {
					products=products.stream().filter(product->fitType.contains(product.getFit().name())).collect(Collectors.toList());

				}
				
				
				int start = (int) pageable.getOffset();
				int end = Math.min((start + pageable.getPageSize()), products.size());
			
				List<ProductModel> paginatedProducts = products.subList(start, end);
				return new PageImpl<>(paginatedProducts, pageable, products.size());

			
		}
		@Transactional
		public void removeFromWishlist(int user_id, int prd_id) {
			// TODO Auto-generated method stub
			NewUserModel user=userRepo.findById(user_id).orElseThrow(()->new UserNotFoundException ("user not found"));
			ProductModel product=productRepo.findById(prd_id).orElseThrow(()->new UserNotFoundException ("product not found"));
			wishlistRepo.deleteByUserAndProduct(user,product);
		}

		public List<ProductModel> getProductsWithoutFilter(Integer user_id) {
			// TODO Auto-generated method stub
			
			NewUserModel user=userRepo.findById(user_id).orElseThrow(()->new UserNotFoundException("user not found"));
			List<ProductModel> products=wishlistRepo.findProductsByUserId(user_id);
			return products;
		}

		public String getReferralCode(NewUserModel user) {
			// TODO Auto-generated method stub
			if (user.getReferralToken() == null) {
			    user.setReferralToken(UUID.randomUUID().toString());
			    userRepo.save(user);
			}
			String referralLink = "http://localhost:8082/user/signup?ref=" + user.getReferralToken();
			return referralLink;

		}
		
		
	

	
	  
	
}

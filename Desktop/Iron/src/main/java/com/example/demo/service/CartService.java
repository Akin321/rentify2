package com.example.demo.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.Dto.AddressDto;
import com.example.demo.Repository.AddressRepo;
import com.example.demo.Repository.CartRepo;
import com.example.demo.Repository.CouponRepo;
import com.example.demo.Repository.CouponUsageRepo;
import com.example.demo.Repository.ProductRepo;
import com.example.demo.Repository.ProductVarientRepo;
import com.example.demo.Repository.UserRepo;
import com.example.demo.Repository.WishlistRepo;

import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.exception.UserNotFoundException;
import com.example.demo.model.AddressModel;
import com.example.demo.model.CartModel;
import com.example.demo.model.CouponModel;
import com.example.demo.model.CouponUsage;
import com.example.demo.model.NewUserModel;
import com.example.demo.model.ProductModel;
import com.example.demo.model.ProductVariant;
import com.example.demo.model.Status;
import com.example.demo.model.WishlistModel;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;

@Service
public class CartService {
	@Autowired
	CartRepo cartRepo;
	
	@Autowired
	UserRepo userRepo;
	
	@Autowired
	AddressRepo addressRepo;
	
	@Autowired
	ProductRepo productRepo;
	
	@Autowired
	ProductVarientRepo variantRepo;
	
	@Autowired
	WishlistRepo wishlistRepo;
	
	@Autowired
	CouponRepo couponRepo;
	
	@Autowired
	CouponUsageRepo couponUsageRepo;

	@Transactional
	public void addCartItem(Integer user_id, Integer productId, Integer variantId) {
		// TODO Auto-generated method stub
		NewUserModel user=userRepo.findById(user_id).orElseThrow(()->new UserNotFoundException("user Not found"));
		ProductModel product=productRepo.findById(productId).orElseThrow(()->new ResourceNotFoundException("product Not found"));
		ProductVariant variant=variantRepo.findById(variantId).orElseThrow(()->new ResourceNotFoundException("variant Not found"));
		Optional<WishlistModel> existingWishlist=wishlistRepo.findByUserAndProduct(user,product);
		if(existingWishlist.isPresent()) {
			wishlistRepo.deleteById(existingWishlist.get().getId());
		}
		Optional<CartModel> existingCart=cartRepo.findByUserAndProductAndVariant(user,product,variant);
		if(existingCart.isPresent()) {
			CartModel cartItem=existingCart.get();
			cartItem.setQuantity(cartItem.getQuantity()+1);
			cartRepo.save(cartItem);
		}
		else {
			CartModel cartItem=new CartModel();
			cartItem.setProduct(product);
			cartItem.setQuantity(1);
			cartItem.setVariant(variant);
			cartItem.setUser(user);
			cartRepo.save(cartItem);
		}
	}

	public List<CartModel> getcartItems(Integer user_id) {
		// TODO Auto-generated method stub
	    NewUserModel user = userRepo.findById(user_id)
                .orElseThrow(() -> new RuntimeException("User not found"));
	    List<CartModel> cartItems = cartRepo.findByUser(user);

	    for (CartModel item : cartItems) {
	        ProductVariant variant = item.getVariant();

	        boolean updated = false;

	        if (variant.getStock() <= 0 && item.getQuantity() != 0) {
	            item.setQuantity(0);
	            cartRepo.save(item);
	        } 

	        
	    }
	    
	    return cartItems.stream()
	            .sorted(Comparator.comparing(CartModel::getCreatedAt))
	            .collect(Collectors.toList());
	}

	public int updateQuantity(int cartId, int delta) {

	    CartModel cartItem = cartRepo.findById(cartId)
	        .orElseThrow(() -> new RuntimeException("Cart not found"));

	    int currentQty = cartItem.getQuantity();
	    int newQty = currentQty + delta;

	    int maxAllowed = Math.min(10, cartItem.getVariant().getStock());

	    if (newQty < 1) {
	        newQty = 1; // Minimum quantity is 1
	    } else if (newQty > maxAllowed) {
	        newQty = maxAllowed;
	    }

	    cartItem.setQuantity(newQty);
	    cartRepo.save(cartItem);

	    return newQty; // return the updated quantity
	}

	public BigDecimal getTotalMrp(List<CartModel> cartItems) {
		// TODO Auto-generated method stub
		BigDecimal totalPrice = cartItems.stream()
			    .map(item -> item.getProduct().getBasePrice().multiply(BigDecimal.valueOf(item.getQuantity())))
			    .reduce(BigDecimal.ZERO, BigDecimal::add);

		return totalPrice;
	}

	public BigDecimal getTotalDiscount(List<CartModel> cartItems) {
		// TODO Auto-generated method stub
		BigDecimal totalDiscount=cartItems.stream().map(item->(item.getProduct().getBasePrice().subtract(item.getProduct().getFinalPrice())
				.multiply(BigDecimal.valueOf(item.getQuantity()))))
				.reduce(BigDecimal.ZERO,BigDecimal::add);
		return totalDiscount;
	}

	public void removeCartItem(int cart_id) {
		// TODO Auto-generated method stub
		cartRepo.deleteById(cart_id);
	}
	@Transactional
	public void deleteAllCartItems(int user_id) {
		// TODO Auto-generated method stub
		  NewUserModel user = userRepo.findById(user_id)
		            .orElseThrow(() -> new EntityNotFoundException("User with ID " + user_id + " not found"));

		    // Delete all cart items associated with the user
		    cartRepo.deleteByUser(user);
		
	}

	@Transactional
	public void setDeliveryAddress(int addressId, NewUserModel user) {
		// TODO Auto-generated method stub
		List<AddressModel> addressList = addressRepo.findByUser(user)
	            .stream()
	            .filter(AddressModel::isDeliveryAddress)
	            .collect(Collectors.toList());

	    // Set all current delivery addresses to false
		if (addressList != null && !addressList.isEmpty()) {
		    for (AddressModel addr : addressList) {
		        addr.setDeliveryAddress(false);
		        addressRepo.save(addr);
		    }
		}

	    // Set the selected address as delivery address
	    AddressModel delAddress = addressRepo.findById(addressId)
	            .orElseThrow(() -> new RuntimeException("Address Not Found"));

	    delAddress.setDeliveryAddress(true); // ✅ this was missing
	    addressRepo.save(delAddress);
		
	}
	@Transactional
	public void addAddress(@Valid AddressDto addressDto, NewUserModel user) {
		// TODO Auto-generated method stub
		
		List<AddressModel> addressList = addressRepo.findByUser(user)
	            .stream()
	            .filter(AddressModel::isDeliveryAddress)
	            .collect(Collectors.toList());

	    // Set all current delivery addresses to false
		if (addressList != null && !addressList.isEmpty()) {
		    for (AddressModel addr : addressList) {
		        addr.setDeliveryAddress(false);
		        addressRepo.save(addr);
		    }
		}
		  AddressModel address = new AddressModel();
		    address.setName(addressDto.getName());
		    address.setPhone(addressDto.getPhone());
		    address.setPincode(addressDto.getPincode());
		    address.setLocality(addressDto.getLocality());
		    address.setAddress(addressDto.getAddress());
		    address.setCity(addressDto.getCity());
		    address.setState(addressDto.getState());
		    address.setLandmark(addressDto.getLandmark());
		    address.setAddressType(addressDto.getAddressType());
		    address.setDeliveryAddress(true);
		    address.setUser(user);

		    addressRepo.save(address);
		   
	}

	public void editAddress(@Valid AddressDto addressDto, NewUserModel user) {
		// TODO Auto-generated method stub

		List<AddressModel> addressList = addressRepo.findByUser(user)
	            .stream()
	            .filter(AddressModel::isDeliveryAddress)
	            .collect(Collectors.toList());

	    // Set all current delivery addresses to false
		if (addressList != null && !addressList.isEmpty()) {
		    for (AddressModel addr : addressList) {
		        addr.setDeliveryAddress(false);
		        addressRepo.save(addr);
		    }
		}
		  AddressModel address = addressRepo.findById(addressDto.getId()).orElseThrow(()-> new ResourceNotFoundException("Address not found"));
		    address.setName(addressDto.getName());
		    address.setPhone(addressDto.getPhone());
		    address.setPincode(addressDto.getPincode());
		    address.setLocality(addressDto.getLocality());
		    address.setAddress(addressDto.getAddress());
		    address.setCity(addressDto.getCity());
		    address.setState(addressDto.getState());
		    address.setLandmark(addressDto.getLandmark());
		    address.setAddressType(addressDto.getAddressType());
		    address.setDeliveryAddress(true);
		    address.setUser(user);

		    addressRepo.save(address);
		   
	}

	public void clearUserCart(NewUserModel user2) {
		// TODO Auto-generated method stub
		cartRepo.deleteByUser(user2);
	}

	public List<CouponModel> getCoupons(NewUserModel user, BigDecimal totalAmount) {
		// TODO Auto-generated method stub
	    List<CouponModel> allCoupons = couponRepo.findByStatus(Status.Active);
	  

		 return allCoupons.stream()
		            .filter(coupon -> {
		                Optional<CouponUsage> usageOpt = couponUsageRepo.findByUserAndCoupon(user, coupon);

		                int usageCount = usageOpt.map(CouponUsage::getUsage_count).orElse(0);
		                int totalUsage = coupon.getTotal_usage(); // assuming this tracks total usage

		                return (usageCount < coupon.getPerUserLimit()||coupon.getPerUserLimit()==-1) && 
		                		(totalUsage < coupon.getTotalUsageLimit() || coupon.getTotalUsageLimit()==-1)
		                		&&(totalAmount.compareTo(BigDecimal.valueOf(coupon.getMinAmount())) >= 0 && (coupon.getUser() == null || coupon.getUser().getId()==(user.getId())));
		            })
		            .collect(Collectors.toList());
		
	}

	public CouponModel getCoupon(String keyword,NewUserModel user, BigDecimal totalAmount) {
		// TODO Auto-generated method stub
		
		 CouponModel coupon = couponRepo.findByCouponCodeIgnoreCaseAndStatus(keyword,Status.Active)
			        .orElseThrow(() -> new RuntimeException("Coupon not found"));

			    // Check if user has used this coupon
			    Optional<CouponUsage> usageOpt = couponUsageRepo.findByUserAndCoupon(user, coupon);

			    int usageCount = usageOpt.map(CouponUsage::getUsage_count).orElse(0);
			    int totalUsage = coupon.getTotal_usage();

			    boolean isUnderUserLimit = coupon.getPerUserLimit() == -1 || usageCount < coupon.getPerUserLimit();
			    boolean isUnderTotalLimit = coupon.getTotalUsageLimit() == -1 || totalUsage < coupon.getTotalUsageLimit();
			    boolean isUnderAmount=totalAmount.compareTo(BigDecimal.valueOf(coupon.getMinAmount())) >= 0;


			    if (!isUnderUserLimit || !isUnderTotalLimit ||!isUnderAmount) {
			        throw new RuntimeException("Coupon is not valid for use");
			    }

			    return coupon;
		
		
	}

	public CouponModel getAppliedCoupon(String coupon) {
		// TODO Auto-generated method stub
		CouponModel appliedCoupon=couponRepo.findByCouponCodeIgnoreCaseAndStatus(coupon, Status.Active).orElseThrow(() -> new RuntimeException("Coupon not found"));
		return appliedCoupon;
	}

	public BigDecimal getcouponDisount(int discountPer, BigDecimal preTotalAmount) {
		// TODO Auto-generated method stub
		 BigDecimal discount = preTotalAmount.multiply(BigDecimal.valueOf(discountPer))
                 .divide(BigDecimal.valueOf(100));	
		 return discount.setScale(2, RoundingMode.HALF_UP);	}




	
		
		

}

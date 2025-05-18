package com.example.demo.controller;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.demo.Dto.QuantityUpdateRequest;
import com.example.demo.Repository.CartRepo;
import com.example.demo.Repository.UserRepo;
import com.example.demo.exception.UserNotFoundException;
import com.example.demo.model.CartModel;
import com.example.demo.model.CouponModel;
import com.example.demo.model.NewUserModel;
import com.example.demo.service.CartService;



import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Controller
public class CartController {
	@Autowired
	UserRepo userRepo;
	
	@Autowired
	CartService cartService;
	
	@Autowired
	CartRepo cartRepo;
	
	 private static final Logger logger = LoggerFactory.getLogger(CartController.class);
	
	public Integer getCurrentUserId() {
	    Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

	    if (principal instanceof UserDetails) {
	        String email = ((UserDetails) principal).getUsername();
	        NewUserModel user = userRepo.findByEmail(email)
	                .orElseThrow(() -> new UserNotFoundException("User not found"));
	        return user.getId();
	    }

	    return null;
	}

	
	@PostMapping("/add-cart")
	public String addCart(@RequestParam Integer productId,@RequestParam Integer variantId,Model model,
			RedirectAttributes redirectAttributes,HttpServletRequest request) {
		String referer=request.getHeader("Referer");
		try { 
			Integer user_id=getCurrentUserId();
			if(user_id==null) {
				redirectAttributes.addFlashAttribute("errorMessage","Please login to add items to cart.");
				return "redirect:/user/login";
			}
			cartService.addCartItem(user_id,productId,variantId);
		
	        redirectAttributes.addFlashAttribute("successMessage", "Item added to cart.");
			return "redirect:" + (referer != null ? referer : "/user/home");
			
		}catch(Exception e) {
			e.printStackTrace();
	        redirectAttributes.addFlashAttribute("errorMessage", "Something went wrong. Could not add item to cart.");
			return "redirect:" + (referer != null ? referer : "/user/home");

		}
	}
	
	@GetMapping("/view-cart")
	public String viewCart(RedirectAttributes redirectAttributes,Model model,HttpServletRequest request,HttpSession session) {
		String referer=request.getHeader("referer");
	try {
		Integer user_id=getCurrentUserId();
		if(user_id==null) {
			redirectAttributes.addFlashAttribute("success	Message","Please login to view your cart items");
			return "redirect:/user/login";
		}
		List<CartModel> cartItems=cartService.getcartItems(user_id);
		BigDecimal totalMrp = cartService.getTotalMrp(cartItems);
		BigDecimal totalDiscount=cartService.getTotalDiscount(cartItems);
		BigDecimal preTotalAmount=totalMrp.subtract(totalDiscount);
		BigDecimal couponDiscount = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
		
		CouponModel coupon=(CouponModel)session.getAttribute("coupon");
		if(coupon!=null) {
			couponDiscount=cartService.getcouponDisount(coupon.getDiscountPer(),preTotalAmount);
		}
		BigDecimal totalAmount=preTotalAmount.subtract(couponDiscount);
		model.addAttribute("cartItems",cartItems);
		model.addAttribute("couponDiscount",couponDiscount);
		model.addAttribute("cartCount",cartItems.size());
		model.addAttribute("totalMrp",totalMrp);
		model.addAttribute("totalDiscount",totalDiscount);
		model.addAttribute("totalAmount",totalAmount);
		return "/user/viewCart";
	}
	catch(Exception e) {
		logger.error("Error occurred while viewing cart for user: {}", e.getMessage(), e);
		redirectAttributes.addFlashAttribute("errorMessage","something unexpected happened");
		return "redirect:" + referer;	}
		
	}
	
	@GetMapping("/cart/update-quantity")
	public String updateCartQty(@RequestParam Integer cartId, @RequestParam int delta,RedirectAttributes redirectAttributes) {
		try {
			cartService.updateQuantity(cartId, delta);
		    return "redirect:/view-cart"; // Redirects to updated cart page
			
		}
		catch(Exception e) {
			logger.error("Error occured while updating cart item quanity",e);
			redirectAttributes.addFlashAttribute("errorMessage","Something went wrong");
			return "redirect:/view-cart"; 
		}
	    
	}
	
	@GetMapping("/remove-cart")
	public String RemoveCart(@RequestParam("id") int cart_id,RedirectAttributes redirectAttributes) {
		try {
			cartService.removeCartItem(cart_id);
			redirectAttributes.addFlashAttribute("successMessage", "Item removed successfully");
			return "redirect:/view-cart";
		}
		catch(Exception e) {
			logger.error("Error occured while removing cart item",e);
	        redirectAttributes.addFlashAttribute("errorMessage", "Something went wrong while removing the item.");
	        return "redirect:/view-cart";
		}
	}
	
	@GetMapping("/remove-allCartItems")
	public String RemoveAllCartItems(RedirectAttributes redirectAttributes) {
		try {
			int user_id=getCurrentUserId();
			cartService.deleteAllCartItems(user_id);
			return "redirect:/view-cart";

		}
		catch(Exception e) {
			logger.error("Error occured while removing All cart items",e);
	        redirectAttributes.addFlashAttribute("errorMessage", "Something went wrong while removing the item.");
	        return "redirect:/view-cart";
		}
	}

	@GetMapping("/cart/validate")
	@ResponseBody
	public ResponseEntity<?> validateCart() {
		Integer user_id=getCurrentUserId();
		if(user_id==null) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("session expired");
		}
		NewUserModel user = userRepo.findById(user_id).orElseThrow();

	    List<CartModel> cartItems = cartRepo.findByUser(user);
	    List<Map<String, Object>> issues = new ArrayList<>();

	    for (CartModel item : cartItems) {
	        int stock = item.getVariant().getStock();
	        int qty = item.getQuantity();

	        if (qty > stock || stock <= 0) {
	            Map<String, Object> issue = new HashMap<>();
	            issue.put("name", item.getProduct().getName());
	            issue.put("size", item.getVariant().getSize());
	            issue.put("cartQty", qty);
	            issue.put("stock", stock);
	            issues.add(issue);
	        }
	    }

	    Map<String, Object> response = new HashMap<>();
	    response.put("valid", issues.isEmpty());
	    response.put("issues", issues);

	    return ResponseEntity.ok(response);
		
	}
	
	@PostMapping("/cart/fix-quantity")
	public ResponseEntity<String> fixCartQuantities() {
	    Integer userId = getCurrentUserId();
	    NewUserModel user = userRepo.findById(userId).orElseThrow();

	    List<CartModel> cartItems = cartRepo.findByUser(user);

	    for (CartModel item : cartItems) {
	        int stock = item.getVariant().getStock();

	        if (stock <= 0) {
	            cartRepo.delete(item); // Or set quantity to 0
	        } else if (item.getQuantity() > stock) {
	            item.setQuantity(stock);
	            cartRepo.save(item);
	        }
	    }

	    return ResponseEntity.ok("Fixed");
	}

	@GetMapping("/getCoupons")
	@ResponseBody
	public ResponseEntity<?> getCoupons(){
		Integer user_id=getCurrentUserId();
		if(user_id==null) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("session expired");
		}
		List<CartModel> cartItems=cartService.getcartItems(user_id);
		BigDecimal totalMrp = cartService.getTotalMrp(cartItems);
		BigDecimal totalDiscount=cartService.getTotalDiscount(cartItems);
		BigDecimal totalAmount=totalMrp.subtract(totalDiscount);
	    NewUserModel user = userRepo.findById(user_id).orElseThrow();

		List<CouponModel> coupons=cartService.getCoupons(user,totalAmount);
		return ResponseEntity.ok(coupons);
	}
	
	@GetMapping("/checkCoupon")
	@ResponseBody
	public ResponseEntity<?> checkCoupon(@RequestParam String keyword) {
		try {
			Integer user_id=getCurrentUserId();
			if(user_id==null) {
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("session expired");
			}
	        NewUserModel user = userRepo.findById(user_id).orElseThrow(() -> new RuntimeException("User not found"));
	        List<CartModel> cartItems=cartService.getcartItems(user_id);
			BigDecimal totalMrp = cartService.getTotalMrp(cartItems);
			BigDecimal totalDiscount=cartService.getTotalDiscount(cartItems);
			BigDecimal totalAmount=totalMrp.subtract(totalDiscount);

			CouponModel coupon=cartService.getCoupon(keyword,user,totalAmount);
			
			return ResponseEntity.ok(coupon);
			
		}
		catch(Exception e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
		}
	}
	
	@GetMapping("/applyCoupon")
	@ResponseBody
	public ResponseEntity<?> ApplyCoupon(@RequestParam String coupon,HttpSession session) {
		try {
			CouponModel appliedCoupon=cartService.getAppliedCoupon(coupon);
			session.setAttribute("coupon", appliedCoupon);
			return ResponseEntity.ok("coupon Applied");
		}
		catch(Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Server down");
		}
	}
	@GetMapping("/cancelCoupon")
	public ResponseEntity<?> CancelCoupon(HttpSession session) {
		session.removeAttribute("coupon");
		return ResponseEntity.ok("coupon removed successfully");
	}

}

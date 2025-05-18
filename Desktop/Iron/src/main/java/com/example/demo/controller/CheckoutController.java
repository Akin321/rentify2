package com.example.demo.controller;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.demo.Dto.AddressDto;
import com.example.demo.Repository.AddressRepo;
import com.example.demo.Repository.UserRepo;
import com.example.demo.Repository.WalletRepo;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.exception.UserNotFoundException;
import com.example.demo.model.AddressModel;
import com.example.demo.model.CartModel;
import com.example.demo.model.CouponModel;
import com.example.demo.model.NewUserModel;
import com.example.demo.model.PaymentType;
import com.example.demo.model.WalletModel;
import com.example.demo.service.CartService;
import com.example.demo.service.OrderService;
import com.example.demo.service.UserService;
import com.example.demo.service.WalletService;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller
@RequestMapping("/user")
public class CheckoutController {
	
	@Autowired
	UserRepo userRepo;
	
	@Autowired
	AddressRepo addressRepo;

	@Autowired
	CartService cartService;
	
	@Autowired
	UserService userService;
	
	@Autowired
	OrderService orderService;
	
	@Autowired
	WalletRepo walletRepo;
	
	@Autowired
	WalletService walletService;
	
	 private static final Logger logger = LoggerFactory.getLogger(CheckoutController.class);

	
	public NewUserModel getCurrentUserId() {
	    Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

	    if (principal instanceof UserDetails) {
	        String email = ((UserDetails) principal).getUsername();
	        NewUserModel user = userRepo.findByEmail(email)
	                .orElseThrow(() -> new UserNotFoundException("User not found"));
	        return user;
	    }

	    return null;
	}
	
	@GetMapping("checkout")
	public String Checkout(Model model,HttpServletRequest request,HttpSession session) {
		String referer=request.getHeader("referer");
		try {
			NewUserModel user=getCurrentUserId();
			if (user == null) {
				logger.warn("User not logged in or user is null during checkout");
				return "redirect:" + (referer != null ? referer : "/user/home");
			}
			WalletModel wallet=walletRepo.findByUser(user).orElseThrow(()->new ResourceNotFoundException("wallet not found"));
			List<CartModel> cartItems=cartService.getcartItems(user.getId());
			List<AddressModel> addresses=addressRepo.findByUserOrderByIsDeliveryAddressDesc(user);
			BigDecimal totalMrp = cartService.getTotalMrp(cartItems);
			BigDecimal totalDiscount=cartService.getTotalDiscount(cartItems);
			BigDecimal preTotalAmount=totalMrp.subtract(totalDiscount);
			BigDecimal couponDiscount = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
			
			CouponModel coupon=(CouponModel)session.getAttribute("coupon");
			if(coupon!=null) {
				couponDiscount=cartService.getcouponDisount(coupon.getDiscountPer(),preTotalAmount);
			}
			BigDecimal totalAmount=preTotalAmount.subtract(couponDiscount);
			model.addAttribute("couponDiscount",couponDiscount);
			model.addAttribute("cartItems",cartItems);
			model.addAttribute("cartCount",cartItems.size());
			model.addAttribute("totalMrp",totalMrp);
			model.addAttribute("totalDiscount",totalDiscount);
			model.addAttribute("totalAmount",totalAmount);
			model.addAttribute("wallet",wallet);


			model.addAttribute("addresses",addresses);
			return "user/checkout";
			
		}
		catch(Exception e) {
			logger.error("error occured while visiting checkout page",e);
	        return "redirect:" + (referer != null ? referer : "/user/home");
		}
	}
	
	@GetMapping("/select-address")
	@ResponseBody
	public ResponseEntity<String> setAddress(@RequestParam int addressId,Model model) {
		try {
			NewUserModel user=getCurrentUserId();
			if(user==null) {
	
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not Found");
			}
			cartService.setDeliveryAddress(addressId,user);
	
			return ResponseEntity.ok("Delivery Address Changed Successfully");
		}
		catch(Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Something unexpected happened");
		}
	}
	
	@PostMapping("/save-address")
	@ResponseBody
	public ResponseEntity<?> addAddress(@ModelAttribute @Valid AddressDto addressDto,BindingResult result){
		
		try {
			NewUserModel user=getCurrentUserId();

			if(user==null) {
				
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not Found");
			}
			if(result.hasErrors()) {
				Map<String,String> fieldErrors=new HashMap<>();
				result.getFieldErrors().forEach(
						error->fieldErrors.put(error.getField(), error.getDefaultMessage())
						);
				return ResponseEntity.badRequest().body(fieldErrors);
			}
			
			cartService.addAddress(addressDto,user);
		    return ResponseEntity.ok(Map.of("message", "Address saved successfully"));
						
		}
		catch(Exception e) {
			logger.error("Error occured when adding address at checkout",e);
		    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("something went wrong");

		}
		
	}
	
	

	
	@PostMapping("/edit-address")
	@ResponseBody
	public ResponseEntity<?> editAddress(@ModelAttribute @Valid AddressDto addressDto,BindingResult result){
		
		try {
			NewUserModel user=getCurrentUserId();

			if(user==null) {
				
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not Found");
			}
			if(result.hasErrors()) {
				Map<String,String> fieldErrors=new HashMap<>();
				result.getFieldErrors().forEach(
						error->fieldErrors.put(error.getField(), error.getDefaultMessage())
						);
				return ResponseEntity.badRequest().body(fieldErrors);
			}
			
			cartService.editAddress(addressDto,user);
		    return ResponseEntity.ok(Map.of("message", "Address edited successfully"));
						
		}
		catch(Exception e) {
			logger.error("Error occured when adding address at checkout",e);
		    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("something went wrong");

		}
		
	}
	
	@GetMapping("/api/payment/order")
	@ResponseBody
	public ResponseEntity<?> razorPay(HttpSession session) throws RazorpayException{
		NewUserModel user=getCurrentUserId();
		if (user == null) {
			logger.warn("User not logged in or user is null during checkout");
			return ResponseEntity.badRequest().body("User Not Found");
		}
		List<CartModel> cartItems=cartService.getcartItems(user.getId());
		BigDecimal totalMrp = cartService.getTotalMrp(cartItems);
		BigDecimal totalDiscount=cartService.getTotalDiscount(cartItems);
		BigDecimal preTotalAmount=totalMrp.subtract(totalDiscount);
		BigDecimal couponDiscount = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
		
		CouponModel coupon=(CouponModel)session.getAttribute("coupon");
		if(coupon!=null) {
			couponDiscount=cartService.getcouponDisount(coupon.getDiscountPer(),preTotalAmount);
		}
		BigDecimal totalAmount=preTotalAmount.subtract(couponDiscount);
		
		int amountInPaise = totalAmount.multiply(BigDecimal.valueOf(100)).intValue();

	    RazorpayClient client = new RazorpayClient("rzp_test_OmCIXPHWUDWbjB", "GTIz8JEGgmfHFhQRBK0YZfB1");

	    JSONObject orderRequest = new JSONObject();
	    orderRequest.put("amount", amountInPaise); // must be integer
	    orderRequest.put("currency", "INR");
	    orderRequest.put("receipt", "order_rcptid_11"); // typo fixed: "Reciept" → "receipt"

	    Order order = client.orders.create(orderRequest);

	    Map<String, Object> response = new HashMap<>();
	    response.put("id", order.get("id"));
	    response.put("amount", order.get("amount"));
	    response.put("currency", order.get("currency"));

	    return ResponseEntity.ok(response);
		

		
	}
	
	//payment verification to check payment is not tampered
	
	 @PostMapping("/api/payment/verify")
	 @ResponseBody
	    public ResponseEntity<?> verifyPayment(@RequestBody Map<String, String> payload,HttpSession session) {
		 try {
		        boolean isVerified = orderService.verifySignature(payload);
		        NewUserModel user = getCurrentUserId();

		        if (user == null) {
		            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not authenticated");
		        }

		        if (isVerified) {
		    		
		    		CouponModel coupon=(CouponModel)session.getAttribute("coupon");
		    	
		            orderService.placeOrderWithPayment( user,PaymentType.RazorPay,coupon);
		            session.removeAttribute("coupon");
		            Map<String, Object> response = new HashMap<>();
		            response.put("verified", true);
		            response.put("message", "Payment verified and order placed successfully.");
		            
		            return ResponseEntity.ok(response);
		        } else {
		            return ResponseEntity.badRequest().body("Payment verification failed.");
		        }

		    } catch (Exception e) {
		        e.printStackTrace(); // Optional: Log this to a logger in production
		        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
		                .body("Something went wrong while verifying payment.");
		    }
	      

	       
	    }
	 
	 @GetMapping("/order/cod/verify")
	 @ResponseBody
	 public ResponseEntity<?> verifyOrder(HttpSession session) {
		 try {
			 NewUserModel user=getCurrentUserId();
			 if(user==null) {
				 return ResponseEntity.badRequest().body("User not found");
			 }
			 CouponModel coupon=(CouponModel)session.getAttribute("coupon");
		    	
	         orderService.placeOrderWithPayment( user,PaymentType.CashOnDelivery,coupon);
	            session.removeAttribute("coupon");

	         Map<String, Object> response = new HashMap<>();
	         response.put("verified", true);
	         return ResponseEntity.ok(response);
		 }
		 catch(Exception e) {
			 e.printStackTrace(); // Optional: Log this to a logger in production
		        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
		                .body("Something went wrong while verifying payment.");
		 }
		
		 
	 }
	 
	 @PostMapping("/wallet/pay")
	 public ResponseEntity<?> payWithWallet(@RequestBody Map<String, Object> data,HttpSession session) {
		    BigDecimal amount = new BigDecimal(data.get("amount").toString());
	     NewUserModel user=getCurrentUserId();
		 if(user==null) {
			 return ResponseEntity.badRequest().body("User not found");
		 }
		 CouponModel coupon=(CouponModel)session.getAttribute("coupon");
		   
         orderService.placeOrderWithPayment( user,PaymentType.Wallet,coupon);
            session.removeAttribute("coupon");
	     // Process payment logic

	     return ResponseEntity.ok("Payment successful");
	 }


	   
}

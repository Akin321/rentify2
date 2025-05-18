package com.example.demo.controller;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.demo.Repository.UserRepo;
import com.example.demo.Repository.WalletRepo;
import com.example.demo.Repository.WalletTransactionRepo;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.exception.UserNotFoundException;
import com.example.demo.model.CouponModel;
import com.example.demo.model.NewUserModel;
import com.example.demo.model.PaymentType;
import com.example.demo.model.TransStatus;
import com.example.demo.model.WalletModel;
import com.example.demo.model.WalletTransaction;
import com.example.demo.service.OrderService;
import com.example.demo.service.WalletService;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/user")
public class WalletController {
	@Autowired
	UserRepo userRepo;
	
	@Autowired
	WalletService walletService;
	
	@Autowired
	OrderService orderService;
	
	@Autowired
	WalletTransactionRepo transactionRepo;
	
	@Autowired
	WalletRepo walletRepo;
	
	public NewUserModel getUser(UserDetails userDetails) {
		NewUserModel user=userRepo.findByEmail(userDetails.getUsername()).orElseThrow(()->new UserNotFoundException("User Not Found"));
		return user;
	}
	
	@GetMapping("/view-wallet")
	public String viewWallet(Model model,@AuthenticationPrincipal UserDetails userDetails,RedirectAttributes redirectAttributes) {
		try {
		    if (userDetails == null) {
		        redirectAttributes.addFlashAttribute("errorMessage", "Please login to see your wallet");
		        return "redirect:/user/login";
		    }
			NewUserModel user=getUser(userDetails);
			WalletModel wallet=walletService.getWallet(user);
			model.addAttribute("wallet",wallet);
			return "/user/wallet";
		}
		catch(Exception e) {
			redirectAttributes.addFlashAttribute("errorMessage","Unable to view wallet");
			return "redirect:/user/home";
		}
	}
	
	@PostMapping("/wallet/checkout")
	public String walletCheckout(@RequestParam BigDecimal amount,@AuthenticationPrincipal UserDetails userDetails,RedirectAttributes redirectAttributes,
			Model model,HttpSession session) {
		try {
			 if (userDetails == null) {
			        redirectAttributes.addFlashAttribute("errorMessage", "Please login to see your wallet");
			        return "redirect:/user/login";
			    }
				NewUserModel user=getUser(userDetails);
				WalletModel wallet=walletService.getWallet(user);
				model.addAttribute("wallet",wallet);
				model.addAttribute("amount",amount);
				session.setAttribute("Walletamount", amount);
				return "/user/walletCheckout";
			
		}
		catch(Exception e) {
			redirectAttributes.addFlashAttribute("errorMessage","Unable to checkout");
			return "redirect:/user/view-wallet";
		}
	}
	
	@GetMapping("/wallet/payment")
	@ResponseBody
	public ResponseEntity<?> walletPayment(HttpSession session) throws RazorpayException {
	
		Object amountObj = session.getAttribute("Walletamount");
		BigDecimal amount = new BigDecimal(amountObj.toString());		
		int amountInPaise = amount.multiply(BigDecimal.valueOf(100)).intValue();

	    RazorpayClient client = new RazorpayClient("rzp_test_OmCIXPHWUDWbjB", "GTIz8JEGgmfHFhQRBK0YZfB1");

	    JSONObject orderRequest = new JSONObject();
	    orderRequest.put("amount", amountInPaise); 
	    orderRequest.put("currency", "INR");
	    orderRequest.put("receipt", "wallet_topup_123"); 

	    Order order = client.orders.create(orderRequest);
	    Map<String, Object> response = new HashMap<>();
	    response.put("id", order.get("id"));
	    response.put("amount", order.get("amount"));
	    response.put("currency", order.get("currency"));

	    return ResponseEntity.ok(response);
		
	}
	
	@PostMapping("/wallet/payment/verify")
	@ResponseBody
	public ResponseEntity<?> verifyPayment(@RequestBody Map<String, String> payload,HttpSession session,@AuthenticationPrincipal UserDetails userDetails){
		try {
	        boolean isVerified = orderService.verifySignature(payload);
	        if (userDetails == null) {
	            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not authenticated");

		    }
	        NewUserModel user = getUser(userDetails);
	    	Object amountObj = session.getAttribute("Walletamount");
    		BigDecimal amount = new BigDecimal(amountObj.toString());	

	        WalletTransaction transaction=walletService.addToWallet(user,amount);
        	

	        if (isVerified) { 	
	    		walletService.updateWalletBalance(user,amount);

	            session.removeAttribute("Walletamount");
	            Map<String, Object> response = new HashMap<>();
	            response.put("verified", true);
	            response.put("message", "Payment verified and order placed successfully.");
	            transaction.setStatus(TransStatus.SUCCESS);
	        	transactionRepo.save(transaction);
	            return ResponseEntity.ok(response);
	        } else {
	            transaction.setStatus(TransStatus.FAILED);
	        	transactionRepo.save(transaction);


	            return ResponseEntity.badRequest().body("Payment verification failed.");
	        }


	    } catch (Exception e) {
	        e.printStackTrace(); // Optional: Log this to a logger in production
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	                .body("Something went wrong while verifying payment.");
	    }
	}
	
	@GetMapping("/wallet/view-transaction")
	public String viewTransaction(@RequestParam(defaultValue="0") int page,@RequestParam(defaultValue="5") int size,@AuthenticationPrincipal UserDetails userDetails,RedirectAttributes redirectAttributes,Model model) {
		try {
			 if (userDetails == null) {
				 redirectAttributes.addFlashAttribute("errorMessage", "Please login to see your wallet");
			        return "redirect:/user/login";

			    }
		        NewUserModel user = getUser(userDetails);
			Page<WalletTransaction> transactions=walletService.getTransactions(user,page,size);
			model.addAttribute("transactions",transactions);
			model.addAttribute("currentPage",page);
			model.addAttribute("totalPages", transactions.getTotalPages());
			return "user/walletTransaction";
		}
		catch(Exception e) {
			 redirectAttributes.addFlashAttribute("errorMessage", "Something went wrong,cannot acess transaction");
				return "redirect:/user/view-wallet";
		}
	}
	
	@GetMapping("/wallet/filter/transactions")
	@ResponseBody
	public ResponseEntity<?> getFilteredTransactions(
	    @RequestParam(defaultValue = "0") int page,
	    @RequestParam(defaultValue = "5") int size,
	    @RequestParam(required = false) String sort,
	    @AuthenticationPrincipal UserDetails userDetails
	) {
	    try {
	        if (userDetails == null) {
	            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Please login");
	        }

	        NewUserModel user = getUser(userDetails);

	        Pageable pageable = walletService.buildPageRequest(page, size, sort);

	        Page<WalletTransaction> transactions = walletService.getFilteredTransactions(user, pageable);

	        return ResponseEntity.ok(transactions);
	    } catch (Exception e) {
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Something went wrong");
	    }
	}
	
	

	
}

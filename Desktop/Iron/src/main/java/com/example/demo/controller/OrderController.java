package com.example.demo.controller;


import java.math.BigDecimal;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.demo.Dto.InvoiceDto;
import com.example.demo.Repository.OrderRepo;
import com.example.demo.Repository.UserRepo;
import com.example.demo.model.NewUserModel;
import com.example.demo.model.OrderModel;
import com.example.demo.model.OrderItem;
import com.example.demo.service.OrderService;
import com.example.demo.service.PdfGeneratorService;
import com.example.demo.service.WalletService;

import jakarta.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/user")
public class OrderController {
	@Autowired
	CheckoutController checkoutController;
	
	@Autowired
	OrderRepo orderRepo;
	
	@Autowired
	OrderService orderService;
	
	@Autowired
	PdfGeneratorService pdfGeneratorService;
	
	@Autowired
	WalletService walletService;
	
	@GetMapping("/view-order")
	public String ViewUserOrder(@RequestParam(required=false) List<String> status,@RequestParam(required=false) String keyword,@RequestParam(defaultValue="0") int page,@RequestParam(defaultValue="5") int size,RedirectAttributes redirectAttributes,Model model,HttpServletRequest request) {
		String referer=request.getHeader("referer");
		try {
			NewUserModel user=checkoutController.getCurrentUserId();
			if(user==null) {
				redirectAttributes.addFlashAttribute("successMessage","Please login to view order details");
				return "redirect:/user/login";
			}
			 boolean isFilterApplied = (status != null && !status.isEmpty()) || keyword != null ;


			Page<OrderItem> orders=orderService.getOrders(status,keyword,page,size,user);	
		
			model.addAttribute("filterApplied",isFilterApplied);

			model.addAttribute("orderItems", orders);
			model.addAttribute("currentPage",page);
			 model.addAttribute("status", status); 
			    model.addAttribute("keyword", keyword); 

			model.addAttribute("totalPages",orders.getTotalPages());
			
			
			return "/user/order";
		}
		catch(Exception e) {
			e.printStackTrace();
			redirectAttributes.addFlashAttribute("errorMessage","Something unexpected occured");
			return "redirect:"+(referer!=null? referer:"/user/home");
		}
		
		
	}
	@GetMapping("/invoice/{orderItemId}")
	@ResponseBody()
	public ResponseEntity<?> invoice(@PathVariable int orderItemId){
	    InvoiceDto invoiceDto = orderService.generateInvoice(orderItemId);
	    byte[] pdf = pdfGeneratorService.generateInvoicePdf(invoiceDto);
	    
	    HttpHeaders headers = new HttpHeaders();
	    headers.setContentType(MediaType.APPLICATION_PDF);
	    headers.setContentDisposition(ContentDisposition.inline().filename("invoice-" + orderItemId + ".pdf").build());

	    return new ResponseEntity<>(pdf, headers, HttpStatus.OK);
	    
	    
	    
	}
	
	@PostMapping("/cancelorder/{orderId}")
	@ResponseBody()
	public ResponseEntity<?> cancelOrder(@PathVariable int orderId){
		try {
			NewUserModel user=checkoutController.getCurrentUserId();
			orderService.updateProductAndStatus(orderId);
			BigDecimal amount=orderService.refundAmount(orderId);
	    	System.out.println(amount);

		        Map<String, Object> response = new HashMap<>();

		        if (amount.compareTo(BigDecimal.ZERO) > 0) {
		        	response.put("refund", true);
		        	walletService.addRefundAmount(amount,user,orderId);
		        } else {
		        	response.put("refund", false);
		        }

		        response.put("refundAmount", amount);

		        return ResponseEntity.ok(response);
		
			
		}
		catch(Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Order Cancellation Failed");
			
		}
	}

	

}

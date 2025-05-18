package com.example.demo.controller;


import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.demo.Dto.OrderStatusUpdateRequest;
import com.example.demo.model.OrderModel;
import com.example.demo.model.OrderStatus;
import com.example.demo.service.OrderService;

import jakarta.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("admin")
public class AdminOrderController {
	
	@Autowired
	OrderService orderService;


	
	
	 private static final Logger logger = LoggerFactory.getLogger(CartController.class);

	
	@GetMapping("/view-order")
	public String viewOrder(@RequestParam(defaultValue="0") int page,@RequestParam(defaultValue="5") int size,
			@RequestParam(required=false) String keyword,Model model,HttpServletRequest request,RedirectAttributes redirectAttributes) {
		String referer=request.getHeader("referer");
		try {
			Page<OrderModel> orders=orderService.getAllOrders(page,size,keyword);
			model.addAttribute("orders",orders);
			model.addAttribute("totalPages", orders.getTotalPages());
			model.addAttribute("currentPage", page);
			model.addAttribute("keyword", keyword);
			return "adminOrder";
		}
		catch(Exception e) {
			logger.error("error occured while fetching orders in admin",e);
			redirectAttributes.addFlashAttribute("errorMessage","Something went wrong cannot acces orders");
			return "redirect:"+referer;
		}
	}
	
	@PostMapping("/update-orderStatus")
	@ResponseBody
	public ResponseEntity<?> viewOrderStatus(@RequestBody OrderStatusUpdateRequest request) {
		try {
			int orderItemId=request.getOrderItemId();
			OrderStatus status=request.getStatus();
			orderService.updateStatus(orderItemId,status);
			 Map<String, Object> response = new HashMap<>();
			    response.put("orderItemId", orderItemId);
	
			    response.put("status", status.name());
			return ResponseEntity.ok(response);
		}
		catch(Exception e) {
			
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
		}
	}
}

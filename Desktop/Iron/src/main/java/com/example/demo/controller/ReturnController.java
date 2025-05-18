package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.demo.model.ReturnRequest;
import com.example.demo.model.ReturnStatus;
import com.example.demo.service.ReturnService;

@Controller
public class ReturnController {
	@Autowired
	ReturnService returnService;
		
	@PostMapping("/return-request")
	@ResponseBody()
	public ResponseEntity<?> returnRequest(@RequestParam int orderItemId,@RequestParam String reason) {
		try {
			returnService.addRequest(orderItemId,reason);
			return ResponseEntity.ok().body("requestPlaced");
		}
		catch(Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("return request failed");
		}

	}
	
	@GetMapping("/return/request")
	public String viewReturn(@RequestParam(defaultValue="0") int page,@RequestParam(defaultValue="5") int size,Model model) {
	
			Page<ReturnRequest> requests=returnService.getAllRequest(page,size);
			model.addAttribute("requests",requests);
			model.addAttribute("currentPage",page);
			model.addAttribute("totalPages", requests.getTotalPages());
			return "returnRequest";
		
		
	}
	
	@GetMapping("/request/approve")
	public String approveRequest(@RequestParam int requestId,RedirectAttributes redirectAttributes) {
		try {
			returnService.changeStatus(requestId);
			return "redirect:/return/request";
			
		}
		catch(Exception e) {
			redirectAttributes.addFlashAttribute("errorMessage","Something went wrong");
			return "redirect:/return/request";
		}
	}
	@PostMapping("/request/reject")
	@ResponseBody
	public ResponseEntity<?> rejectRequest(@RequestParam int requestId,@RequestParam String reason) {
	
			returnService.setrejectStatus(requestId,reason);
			return ResponseEntity.ok().body("request rejected successfully");
			
		
		
	}
}

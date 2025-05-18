package com.example.demo.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.demo.Dto.CouponDto;
import com.example.demo.model.CouponModel;
import com.example.demo.model.Status;
import com.example.demo.service.AdminService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Controller
@RequestMapping("/admin")
public class Couponcontroller {
	
	@Autowired
	AdminService adminService;
	
	 private static final Logger logger = LoggerFactory.getLogger(CartController.class);

	
	@GetMapping("/add-coupon")
	public String addCoupon(Model model) {
		CouponDto dto=new CouponDto();
		model.addAttribute("dto",dto);
		return "coupon";
	}
	@PostMapping("/validate/coupon")
	@ResponseBody
	public ResponseEntity<?> validateCoupon(@ModelAttribute @Valid CouponDto dto,BindingResult result) {
		
		try {
			if(result.hasErrors()) {
				Map<String,String> errors=new HashMap<>();
				result.getFieldErrors().forEach(error->
				errors.put(error.getField(), error.getDefaultMessage()));
				return ResponseEntity.badRequest().body(errors);
			}
			adminService.addCoupon(dto);
			return ResponseEntity.ok("Coupon Successfully Added");
		}
		catch(Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("error occured");
		}
	}
	
	@GetMapping("/view-coupon")
	public String viewCoupon(@RequestParam(required=false) String keyword,@RequestParam(required=false) Status status,
			@RequestParam(defaultValue="0") int page,@RequestParam(defaultValue="5") int size,Model model,HttpServletRequest request,RedirectAttributes redirectAttributes) {
		String referer=request.getHeader("referer");
		try {
			Page<CouponModel> coupons=adminService.getCoupons(keyword,status,page,size);
			model.addAttribute("coupons",coupons);
			model.addAttribute("keyword",keyword);
			model.addAttribute("status",status);
			model.addAttribute("currentPage",page);
			model.addAttribute("totalPages",coupons.getTotalPages());
			return "view-coupon";
		}
		catch(Exception e) {
			logger.error("something happend during viewing coupon page",e);
			redirectAttributes.addFlashAttribute("errorMessage","Something unexpected happened");
			return "redirect:"+referer;
		}
	}
	
	@GetMapping("coupon/{id}")
	public String viewCoupon(@PathVariable("id") int couponId,Model model,RedirectAttributes redirectAttributes) {
		try {
			CouponModel coupon=adminService.getcoupon(couponId);
			model.addAttribute("coupon",coupon);
			return "couponDetail";
		}
		catch(Exception e) {
			logger.error("error occured when viewing coupon details",e);
			redirectAttributes.addFlashAttribute("errorMessage","Something unexpected occured");
			return "redirect:/admin/view-coupon";
		}
	}
	
	@GetMapping("coupon/edit/{id}")
	public String editCoupon(@PathVariable("id") int couponId,Model model,RedirectAttributes redirectAttributes,HttpServletRequest request) {
		String referer=request.getHeader("referer");
		try {
			CouponModel coupon=adminService.getcoupon(couponId);
			CouponDto couponDto=adminService.setdto(coupon);
			model.addAttribute("couponDto",couponDto);
			return "editCoupon";
		}
		catch(Exception e) {
			logger.error("error occured while visiting edit page of coupon",e);
			redirectAttributes.addFlashAttribute("errorMessage","Something unexpected happened");
			return "redirect:" + (referer != null ? referer : "/admin/view-coupon");
		}
	}
	
	@PostMapping("/edit/coupon")
	@ResponseBody
	public ResponseEntity<?> editCouponValue(@ModelAttribute @Valid CouponDto couponDto,BindingResult result){
		if(result.hasErrors()) {
			Map<String,String> errors=new HashMap<>();
			result.getFieldErrors().forEach(error->
			errors.put(error.getField(), error.getDefaultMessage()));
			return ResponseEntity.badRequest().body(errors);
		}
		else {
			adminService.editcouponValue(couponDto);
			return ResponseEntity.ok().body("coupon edited successfully");
		}
	}
	
	@GetMapping("/coupon/delete/{id}")
	public String deleteCoupon(@PathVariable("id") int couponid,RedirectAttributes redirectAttributes,HttpServletRequest request){
		String referer=request.getHeader("referer");
		try {
			adminService.deleteCoupon(couponid);
			redirectAttributes.addFlashAttribute("successMessage","Coupon deleted successfully");
			return "redirect:/admin/view-coupon";
		}
		catch(Exception e){
			redirectAttributes.addFlashAttribute("errorMessage","Something Went wrong.Coupon deletion failed");
			return "redirect:"+(referer!=null ? referer:"/admin/view-coupon");

		}
	}
	
	
}

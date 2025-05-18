package com.example.demo.controller;

import java.net.http.HttpRequest;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.demo.Repository.UserRepo;
import com.example.demo.Repository.WishlistRepo;
import com.example.demo.exception.UserNotFoundException;
import com.example.demo.model.CollectionModel;
import com.example.demo.model.FitType;
import com.example.demo.model.Gender;
import com.example.demo.model.NewUserModel;
import com.example.demo.model.ProductModel;
import com.example.demo.model.ProductVariant;
import com.example.demo.service.CustomUserDetailService;
import com.example.demo.service.UserService;


import jakarta.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/user")
public class wishlistController {
	
	@Autowired
	UserService userService;
	
	@Autowired
	UserRepo userRepo;
	
	@Autowired
	WishlistRepo wishlistRepo;
	
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

	
	@GetMapping("add-wishlist/{id}")
	public String viewWishlist(@PathVariable int id,Model model,RedirectAttributes redirectAttributes,HttpServletRequest request) {
		String referer=request.getHeader("Referer");
		try {
			if(getCurrentUserId()==null) {
				return "redirect:/user/login";
			}
		
			int user_id=getCurrentUserId();
			NewUserModel user=userRepo.findById(user_id).orElseThrow(()->new UserNotFoundException("user not found"));
			ProductModel product=userService.getProduct(id);
			if(wishlistRepo.existsByProductAndUser(product,user)) {
				return "redirect:" + (referer != null ? referer : "/user/home");
			}
			
		
			userService.addProductToWishlist(product,user_id);
			return "redirect:" + (referer != null ? referer : "/user/home");
		}
		catch(Exception e) {
			redirectAttributes.addFlashAttribute("errorMessage","Something went wrong");
			return "redirect:" + (referer != null ? referer : "/user/home");
		}
		
	}
	
	@GetMapping("/view-wishlist")
	public String viewWishlist(@RequestParam(defaultValue="0") int page,@RequestParam(defaultValue="6") int size,
			@RequestParam(required=false) String sort,@RequestParam(required=false) Gender gender,@RequestParam(required=false) List<Integer> collection,
			@RequestParam(required=false) List<String> fit,RedirectAttributes redirectAttributes,Model model) {
		try {
			 boolean isFilterApplied = (sort != null && !sort.isEmpty()) || gender != null ||(collection!=null)||(fit!=null && !fit.isEmpty());
			Integer user_id=getCurrentUserId();
			if(user_id ==null) {
				redirectAttributes.addFlashAttribute("successMessage","please login to access this feature");

				return "redirect:/user/login";
			}
			Page<ProductModel> products=userService.getWishlistProducts(user_id,page,size,gender,sort,collection,fit);
			if(!products.hasContent()) {
				 model.addAttribute("filterApplied", isFilterApplied);
				return "/user/viewWishlist";
			}
			List<CollectionModel> collections = userService.getProductsWithoutFilter(user_id)
				    .stream()
				    .map(ProductModel::getCollection)
				    .filter(Objects::nonNull)
				    .sorted(Comparator.comparing(CollectionModel::getName))
				    .distinct()
				    .collect(Collectors.toList());
			List<FitType> fitOptions = userService.getProductsWithoutFilter(user_id).stream()
				    .map(ProductModel::getFit)
				    .filter(Objects::nonNull) // <-- handle nulls safely
				    .distinct()
				    .sorted(Comparator.comparing(FitType::name)) // cleaner and safer for enums
				    .collect(Collectors.toList());
			


			model.addAttribute("currentPage",page);
			model.addAttribute("fitOptions", fitOptions);
			model.addAttribute("collections",collections);
			model.addAttribute("totalPages", products.getTotalPages());
			model.addAttribute("products", products.getContent());
			model.addAttribute("selectedSort",sort);
			model.addAttribute("selectedGender",gender);
			model.addAttribute("selectedCollections",collection);
			model.addAttribute("selectedFits",fit);
			return "/user/viewWishlist";
		}
		catch(Exception e) {
			e.printStackTrace();
			redirectAttributes.addFlashAttribute("errorMessage","Something unexpected happened");
			return "redirect:/user/home";

		}
	}
		
	
	@PostMapping("/remove-from-wishlist")
	public String removeWishlistProduct(@RequestParam("productId") int prd_id,RedirectAttributes redirectAttributes,HttpServletRequest request) {
		try {
			String referer=request.getHeader("Referer");
			int user_id=getCurrentUserId();
			userService.removeFromWishlist(user_id,prd_id);
			return "redirect:" + (referer != null ? referer : "/user/home");

		}
		catch(Exception e) {
			e.printStackTrace();
			redirectAttributes.addFlashAttribute("errorMessage","Something unexpected happened");
			return "redirect:/user/view-wishlist";
		}
	}
	
	@PostMapping("/wishlist/add-wishlist")
	@ResponseBody
	public ResponseEntity<?> addtoWishlist(@RequestParam int productId) {
		int user_id=getCurrentUserId();
		NewUserModel user=userRepo.findById(user_id).orElseThrow(()->new UserNotFoundException("user not found"));
		ProductModel product=userService.getProduct(productId);
		if(wishlistRepo.existsByProductAndUser(product,user)) {
			return ResponseEntity.ok("product already exists");
		}
		
	
		userService.addProductToWishlist(product,user_id);
		return ResponseEntity.ok("product added to wishlist");

	}
	
	
	
}

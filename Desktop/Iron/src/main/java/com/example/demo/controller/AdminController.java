package com.example.demo.controller;

import java.math.BigDecimal;
import java.util.List;




import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.demo.Dto.ProductDto;
import com.example.demo.Dto.ProductTypeDto;
import com.example.demo.Dto.validationGroups.Step1;
import com.example.demo.Repository.ProductImageRepo;
import com.example.demo.Repository.ProductTypeRepo;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.exception.UserNotFoundException;
import com.example.demo.model.CollectionModel;
import com.example.demo.model.Gender;
import com.example.demo.model.NewUserModel;
import com.example.demo.model.ProductModel;
import com.example.demo.model.ProductImages;
import com.example.demo.model.ProductTypes;
import com.example.demo.service.AdminService;
import com.example.demo.service.JwtService;


import io.jsonwebtoken.io.IOException;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
@RequestMapping("admin")
public class AdminController {
	
	@Autowired
	AdminService adminService;
	
	@Autowired 
	JwtService jwtService;
	
	
	@Autowired 
	ProductTypeRepo productTypeRepo;
	
	@Autowired 
	ProductImageRepo productImageRepo;
	
	@Autowired
	UserDetailsService userDetailsService;
	@GetMapping("/login")
	public String login() {
		return "login";
	}
	
	
	@PostMapping("/login")
	public String login(@RequestParam String email,@RequestParam String password,HttpSession session,RedirectAttributes redirectAttributes) {
		try {
			adminService.verify(email,password);
			
				UserDetails userDetails=userDetailsService.loadUserByUsername(email);
				String token=jwtService.generateToken(userDetails);
				session.setAttribute("jwttoken", token);
				return "redirect:/admin/home";
	
		}
		catch(UsernameNotFoundException e) {
			redirectAttributes.addFlashAttribute("usererror","User not found");
			return "redirect:/admin/login";
		}
		catch(BadCredentialsException e) {
			redirectAttributes.addFlashAttribute("passerror","Password is invalid");
			return "redirect:/admin/login";

		}
		catch(Exception e) {
	        redirectAttributes.addFlashAttribute("error", "Internal server error");
	        return "redirect:/admin/login";

		}
	}
	
	@GetMapping("/home")
	public String home() {
		return "home";
	}
		
	@GetMapping("/view-user")
	public String ViewUser(@RequestParam (defaultValue="0") int page,@RequestParam (defaultValue="5") int size,@RequestParam(required=false) String keyword,Model model){
		try {
			Page<NewUserModel> users=adminService.viewUser(page,size,keyword);
		
				model.addAttribute("users", users.getContent());
				model.addAttribute("currentPage", page);
				model.addAttribute("keyword", keyword);
				model.addAttribute("totalPages", users.getTotalPages());
			
			
			return "users";
		
	}catch(UserNotFoundException e) {
		model.addAttribute("error",e.getMessage());
		 model.addAttribute("totalPages", 0); 
		 model.addAttribute("currentPage", page);
		return "users";
	}
		
		catch(Exception e) {
		model.addAttribute("error","internal server error");
		 model.addAttribute("totalPages", 0); 
		 model.addAttribute("currentPage", page);
			return "users";

		}
}
	
	
	
	@GetMapping("/block-user/{id}")
	public String BlockUser(@PathVariable int id,@RequestParam boolean active,RedirectAttributes redirectAttributes){
		try {
			adminService.setUserActiveStatus(id,active);
			redirectAttributes.addFlashAttribute("successMessage","User Status Changed Successfully");
			return "redirect:/admin/view-user";
		}
		catch(UserNotFoundException e) {
			redirectAttributes.addFlashAttribute("errorMessage","User Not Found");
			return "redirect:/admin/view-user"; 

		}
		catch(Exception e) {
		
			redirectAttributes.addFlashAttribute("errorMessage","Something went wrong!");
			return "redirect:/admin/view-user"; 

		}
	}
	
	@GetMapping("/view-productTypes")
	public String viewProductTypes(@RequestParam(defaultValue="0") int page,@RequestParam(defaultValue="5") int size,@RequestParam(required=false) String keyword,Model model){
		try {
			Page<ProductTypes> productTypes=adminService.viewProductTypes(page,size,keyword);
			model.addAttribute("productTypes", productTypes.getContent());
			model.addAttribute("currentPage", page);
			model.addAttribute("keyword", keyword);
			model.addAttribute("totalPages", productTypes.getTotalPages());
			return "productType";
		}
		catch(ResourceNotFoundException e) {
			model.addAttribute("currentPage", 0);
			model.addAttribute("totalPages",0);
			return "productType";
		}
		catch(Exception e) {
			model.addAttribute("currentPage", 0);
			model.addAttribute("totalPages",0);
			return "productType";
		}
		
	}
	
	@GetMapping("/disable-productTypes/{id}")
	public String disableTypes(@PathVariable int id,
	                           @RequestParam boolean active,
	                           RedirectAttributes redirectAttributes) {
	    try {
	        // ✅ Call service method to update status
	        adminService.editProductTypeStatus(id, active); 

	        redirectAttributes.addFlashAttribute("successMessage", "Product type status changed successfully.");
	        return "redirect:/admin/view-productTypes";
	    } catch (ResourceNotFoundException e) {
	        redirectAttributes.addFlashAttribute("errorMessage", "Product type not found.");
	        return "redirect:/admin/view-productTypes";
	    } catch (Exception e) {
	        e.printStackTrace();
	        redirectAttributes.addFlashAttribute("errorMessage", "Something went wrong!");
	        return "redirect:/admin/view-productTypes";
	    }
	}
	
	@GetMapping("/add-productTypes")
	public String addProductType(Model model) {
		 model.addAttribute("productTypeDto", new ProductTypeDto());
		return "addPrdType";
	}
	
	@PostMapping("/add-productTypes")
	public String addProductTypes ( @ModelAttribute("productTypeDto") @Valid ProductTypeDto productTypeDto,BindingResult result,
			RedirectAttributes redirectAttributes,Model model){
	
		MultipartFile image=productTypeDto.getImage();
		
	
		if(result.hasErrors()) {
			return "addPrdType";
		}
	
		if(image.isEmpty()) {
			model.addAttribute("errorMessage", "please upload an image");
			return "addPrdType";
		}
	
		//checks if the file is of type image
		String contentType=image.getContentType();
		if(!contentType.startsWith("image/")){
			model.addAttribute("errorMessage", "please upload a file of type image");
			return "addPrdType";
		}

	
		try {
			adminService.addCoontrollerProductTypes(productTypeDto,image);
			redirectAttributes.addFlashAttribute("successMessage","Product added successfully");
			return "redirect:/admin/view-productTypes";
		}
		catch(IllegalArgumentException e) {
			model.addAttribute("mainMessage","Product type already exists");
			return "addPrdType";
		}
		catch(IOException e) {
			model.addAttribute("errorMessage","error uploading image");
			return "addPrdType"; 
		}
		catch(Exception e) {
			e.printStackTrace();
			model.addAttribute("mainMessage","Error uploading producttype");
			return "addPrdType";
		}
	}
	@GetMapping("/view-productTypes/{id}")
	public String viewType(@PathVariable int id,RedirectAttributes redirectAttributes,Model model){
		try {
			ProductTypes productType=adminService.getProductType(id);
			model.addAttribute("productType",productType);
			return "productTypeCont";
		}
		catch(ResourceNotFoundException e) {
			redirectAttributes.addFlashAttribute("errorMessage","ProductType details not found");  
			return "redirect:/view-productTypes";
		}
		catch(Exception e) {
			redirectAttributes.addFlashAttribute("errorMessage","Internal Server error");  
			return "redirect:/view-productTypes";
		}
	}
	
	@GetMapping("/edit-productTypes/{id}")
	public String EditProductTypes (@PathVariable int id,RedirectAttributes redirectAttributes,Model model){
		try {
			ProductTypes productType=adminService.getProductType(id);
			ProductTypeDto productTypeDto=new ProductTypeDto();
			productTypeDto.setId(id);
			productTypeDto.setName(productType.getName());
			productTypeDto.setDescription(productType.getDescription());
			productTypeDto.setGender(productType.getGender());
			productTypeDto.setImageUrl(productType.getImage());


			model.addAttribute("productTypeDto", productTypeDto);
	
			return "editPrdtType";
		}
		catch(ResourceNotFoundException e) {
			redirectAttributes.addFlashAttribute("errorMessage","ProductType cannot  be found");  
			return "redirect:/view-productTypes";
		}
		catch(Exception e) {
			redirectAttributes.addFlashAttribute("errorMessage","Internal Server error");  
			return "redirect:/view-productTypes";
		}
	}
	
	@PostMapping("/edit-productTypes")
	public String editProductTypes ( @ModelAttribute("productTypeDto") @Valid ProductTypeDto productTypeDto,BindingResult result,@RequestParam int id,
			RedirectAttributes redirectAttributes,Model model){
	
		MultipartFile image=productTypeDto.getImage();
		
	
		if(result.hasErrors()) {
			return "editPrdtType";
		}
	
		
		if(!image.isEmpty()) {
			//checks if the file is of type image
			String contentType=image.getContentType();
			if(!contentType.startsWith("image/")){
				model.addAttribute("errorMessage", "please upload a file of type image");
				return "editPrdtType";
			}

		}
	
	
		try {
			adminService.editprdType(productTypeDto,id);
			redirectAttributes.addFlashAttribute("successMessage","ProductType edited successfully");
			return "redirect:/admin/view-productTypes/"+ id;
		}
		catch(IllegalArgumentException e) {
			model.addAttribute("mainMessage","Product type already exists");
			return "editPrdtType";
		}
		catch(IOException e) {
			model.addAttribute("errorMessage","error uploading image");
			return "editPrdtType"; 
		}
		catch(Exception e) {
			e.printStackTrace();
			model.addAttribute("mainMessage","Error editing producttype");
			return "editPrdtType";
		}
	}
	
	@GetMapping("/view-collections")
	public String viewCollections(@RequestParam(defaultValue="0") int page,@RequestParam(defaultValue="5") int size
			,@RequestParam(required=false) String keyword ,Model model){
		try {
			Page<CollectionModel> collections= adminService.getCollections(page,size,keyword);
			model.addAttribute("collections",collections.getContent());
			model.addAttribute("currentPage", page);
			model.addAttribute("keyword", keyword);
			model.addAttribute("totalPages", collections.getTotalPages());
			
			return "collection";
		}
		catch(ResourceNotFoundException e) {
			model.addAttribute("totalPages", 0);
			model.addAttribute("error", e.getMessage());
			return "collection";
		}
		catch(Exception e) {
			e.printStackTrace();
			model.addAttribute("totalPages", 0);
			model.addAttribute("errorMessage", e.getMessage());
			return "collection";
		}
	}
	@GetMapping("/add-collections")
	public String addCollections() {
	

		return "addCollection";
	}
	
	@PostMapping("/add-collections")
	public String addCollection(@RequestParam String name,RedirectAttributes redirectAttributes,Model model){
		try {
			adminService.addCollection(name);
			redirectAttributes.addFlashAttribute("successMessage","Collection added successfully");
			return "redirect:/admin/view-collections";
		}
		catch(IllegalArgumentException e){
		
			model.addAttribute("error","Collection already exists");
			return "addCollection";
		}
		catch(Exception e) {
			e.printStackTrace();
			redirectAttributes.addFlashAttribute("errorMessage","Serverdown please try later");
			return "redirect:/admin/view-collections";
		}
	}
	
	@GetMapping("disable-collections/{id}")
	public String disableCollections(@PathVariable int  id ,@RequestParam(name="active") boolean status,RedirectAttributes redirectAttributes){
		try {
			adminService.setStatus(id,status);
			redirectAttributes.addFlashAttribute("successMessage","Status Changed Successfully");
			return "redirect:/admin/view-collections";
		}
		catch(ResourceNotFoundException e) {
			redirectAttributes.addFlashAttribute("errorMessage","Collection not found");
			return "redirect:/admin/view-collections";
		}
		catch(Exception e) {
			e.printStackTrace();
			redirectAttributes.addFlashAttribute("errorMessage","Server down try later");
			return "redirect:/admin/view-collections";		}
	}
	@GetMapping("edit-collections/{id}")
	public String editCollectionByid(@PathVariable int id,RedirectAttributes redirectAttributes,Model model) {
		try {
			CollectionModel collection= adminService.getCollection(id);
			model.addAttribute("collection",collection);
			return "editcollection";
		}
		catch(ResourceNotFoundException e) {
			redirectAttributes.addFlashAttribute("errorMessage","Collection not found");
			return "redirect:/admin/view-collections";
		}
		catch(Exception e) {
			e.printStackTrace();
			redirectAttributes.addFlashAttribute("errorMessage","Server down try later");
			return "redirect:/admin/view-collections";		}
	}
	@PostMapping("edit-collections/{id}")
	public String editCollections(@PathVariable int id,@RequestParam String name,RedirectAttributes redirectAttributes,Model model){
		try {
			adminService.editCollection(id,name);
			redirectAttributes.addFlashAttribute("successMessage","Edited Successfully");
			return "redirect:/admin/view-collections";
		}
		catch(IllegalArgumentException e) {
			CollectionModel collection= adminService.getCollection(id);
			model.addAttribute("collection",collection);
			model.addAttribute("error","Collection name already exists");
			return "editCollection";			
		}
		catch(ResourceNotFoundException e) {
			redirectAttributes.addFlashAttribute("errorMessage","Collection not found");
			return "redirect:/admin/view-collections";
		}
		catch(Exception e) {
			e.printStackTrace();
			redirectAttributes.addFlashAttribute("errorMessage","Server down try later");
			return "redirect:/admin/view-collections";
		}
	}
	
	@GetMapping("/view-products")
	public String viewProducts(@RequestParam(defaultValue="0") int page,@RequestParam(defaultValue="5") int size,
			@RequestParam(required=false) String keyword,Model model){
		
		try {
			Page<ProductModel> productpg=adminService.viewProducts(page,size,keyword);
			List<ProductModel> products=productpg.getContent();
			model.addAttribute("collections",productpg.getContent());
			model.addAttribute("currentPage", page);
			model.addAttribute("keyword", keyword);
			model.addAttribute("totalPages", productpg.getTotalPages());
			model.addAttribute("products", products);
			return "productPage";
		}
		catch(ResourceNotFoundException e) {
			model.addAttribute("totalPages", 0);
			model.addAttribute("errorMessage", e.getMessage());
			return "productPage";
		}
		catch(Exception e) {
			e.printStackTrace();
			model.addAttribute("totalPages", 0);
			model.addAttribute("errorMessage", "Server Down");
			return "productPage";
		}
	}
	@GetMapping("/disable-products/{id}")
	public String disableProducts(@PathVariable int id,@RequestParam(name="active") boolean status,RedirectAttributes redirectAttributes){
		try {
			adminService.disableProducts(id,status);
			redirectAttributes.addFlashAttribute("successMessage","Product status changed successfully");
			return "redirect:/admin/view-products";
		}
		catch(ResourceNotFoundException e){
			redirectAttributes.addFlashAttribute("errorMessage",e.getMessage());
			return "redirect:/admin/view-products";
		}
		catch(Exception e) {
			e.printStackTrace();
			redirectAttributes.addFlashAttribute("errorMessage","Server Down Please Try Later");
			return "redirect:/admin/view-products";
		}
	}
	@GetMapping("/view-product/{id}")
	public String viewProductId(@PathVariable int id,Model model,RedirectAttributes redirectAttributes) {
		try {
			ProductModel product=adminService.getProduct(id);
			model.addAttribute("product", product);
			return "productDetail";
		}
		catch(ResourceNotFoundException e) {
			redirectAttributes.addFlashAttribute("errorMessage",e.getMessage());
			return "redirect:/admin/view-products";
		}
		catch(Exception e) {
			e.printStackTrace();
			redirectAttributes.addFlashAttribute("errorMessage","Server Down Please Try Later");
			return "redirect:/admin/view-products";
		}
	}
	@PostMapping("edit-productdiscount/{id}")
	public String editDiscount(@PathVariable int id,@RequestParam BigDecimal discountPer,RedirectAttributes redirectAttributes){
		try {
			adminService.editProductDiscount(id,discountPer);
			redirectAttributes.addFlashAttribute("successMessage","Discount Updated Successfully");
			return "redirect:/admin/view-product/"+id;
			
		}
		catch(ResourceNotFoundException e){
			redirectAttributes.addFlashAttribute("errorMessage",e.getMessage());
			return "redirect:/admin/view-product/"+id;
		}
		catch(Exception e) {
			e.printStackTrace();
			redirectAttributes.addFlashAttribute("errorMessage","Server Down please try later");
			return "redirect:/admin/view-product/"+id;
		}
	}
	
	

	



}


package com.example.demo.controller;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.demo.Dto.ProductDto;

import com.example.demo.Dto.VarientDto;
import com.example.demo.Dto.validationGroups.Step1;
import com.example.demo.Repository.CollectionRepo;
import com.example.demo.Repository.ProductRepo;
import com.example.demo.Repository.ProductTypeRepo;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.model.CollectionModel;
import com.example.demo.model.Gender;
import com.example.demo.model.ProductModel;
import com.example.demo.model.ProductTypes;
import com.example.demo.model.Size;
import com.example.demo.service.AdminService;

import io.jsonwebtoken.io.IOException;

import com.example.demo.Dto.validationGroups.Step2;
import com.example.demo.Dto.validationGroups.Step3;

@Controller
@RequestMapping("admin/add-products")
@SessionAttributes("productDto")
public class productController {
	
	@Autowired
	ProductTypeRepo productTypeRepo;
	
	@Autowired
	AdminService adminService;
	

	@Autowired
	ProductRepo productRepo;
	
	//creates an object to store in seesion if object doesnt exist
	@ModelAttribute("productDto")
    public ProductDto productDto() {
        return new ProductDto();
    }
	
	@GetMapping("/step1")
	public String addProductsForm(Model model) {
		return "productStep1";
	}

	@PostMapping("/step1")

	public String addProducts(@Validated(Step1.class) @ModelAttribute("productDto") ProductDto productDto,BindingResult result,Model model){
		
		if(productRepo.existsByName(productDto.getName())){
			result.rejectValue("name", "invalid.name", "Product Name Exists");
		}
	 
		
		if(result.hasErrors()) {
		
			return "productStep1";
		}
		System.out.println(productDto.getColor());
		return "redirect:/admin/add-products/step2";
	}
	
	@GetMapping("/step2")
	public String showStep2(@ModelAttribute("productDto") ProductDto productDto) {
		 if (productDto.getVariants().isEmpty()) {
		        for (Size size : Size.values()) {
		            VarientDto variant = new VarientDto();
		            variant.setSize(size); // Set enum name as variant name
		            productDto.getVariants().add(variant);
		        }
		    }
		return "productStep2";
	}
	 @PostMapping("/step2")
		public String addStep2(@Validated(Step2.class) @ModelAttribute("productDto") ProductDto productDto,BindingResult result,Model model ) {
		
			if(result.hasErrors()) {
			
				return "productStep2";
			}
			return "redirect:/admin/add-products/step3";
		
	 }
	 @GetMapping("/step3")
	 public String showStep3(@ModelAttribute("productDto") ProductDto productDto) {
		    return "productStep3";
	 }
	 
	 @PostMapping("/step3")
	 public String handleImages(@Validated(Step3.class) @ModelAttribute("productDto") ProductDto productDto,
	                            BindingResult result,
	                            RedirectAttributes  redirectAttributes, SessionStatus status)  {
		
		
		 MultipartFile mainImage=productDto.getMainImage();
		 List<MultipartFile> images=productDto.getImages();
		 
		 if(mainImage!=null && !mainImage.isEmpty() && !mainImage.getContentType().startsWith("image/")) {
	            result.rejectValue("mainImage", "invalid.mainImage", "Main image must be an image file");

		 }
		 
		 for (MultipartFile img : images) {
	            if (img != null && !img.isEmpty() && !img.getContentType().startsWith("image/")) {
	                result.rejectValue("images", "invalid.images", "All additional files must be images");
	                break; // stop after the first invalid file
	            }
	        }
		 
		
		 if(result.hasErrors()) {
		        return "productStep3"; 
			 }
		 
		 try {
			 adminService.addProduct(productDto);
			  status.setComplete();
			 redirectAttributes.addFlashAttribute("successMessage","Product added successfully");
			 return "redirect:/admin/view-products";
		 }
		 catch(Exception e) {
			 e.printStackTrace();
			 redirectAttributes.addFlashAttribute("errorMessage","Server Down Please try later");
			 return "redirect:/admin/view-products";
		 }
		 

	   
	 }
	 
		 
	 
	 @GetMapping("/product-types/{gender}")
	    @ResponseBody
	    public List<ProductTypes> getProductTypesByGender(@PathVariable Gender gender) {
	        return productTypeRepo.findByGender(gender);
	    }
	 
	 @GetMapping("/collections")
	 @ResponseBody
	 public List<CollectionModel> getCollections() {

	     return adminService.getAllCollections(); // Adjust to your DTO/entity
	    
	 }
	 
	
	
	
	 
	 


}

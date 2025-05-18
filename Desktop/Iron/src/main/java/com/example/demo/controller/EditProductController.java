package com.example.demo.controller;

import java.util.ArrayList;
import java.util.List;

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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.example.demo.Dto.validationGroups.Step1;
import com.example.demo.Dto.validationGroups.Step2;
import com.example.demo.Dto.validationGroups.Step3;
import com.example.demo.Repository.ProductVarientRepo;
import com.example.demo.Dto.ProductDto;
import com.example.demo.Dto.VarientDto;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.model.ProductModel;
import com.example.demo.model.Size;
import com.example.demo.service.AdminService;

@Controller
@RequestMapping("admin")
@SessionAttributes("productDto")
public class EditProductController {
	
	@Autowired
	AdminService adminService;
	
	@Autowired
	ProductVarientRepo varientRepo;
	
	@ModelAttribute("productDto")
    public ProductDto productDto() {
        return new ProductDto();
    }
	
	
	@GetMapping("/edit-product/step1/{id}")
	public String editProduct(@PathVariable int id,@ModelAttribute("productDto") ProductDto productDto,Model model,RedirectAttributes redirectAttributes) {
		try {
			ProductModel product=adminService.getProduct(id);
			productDto=adminService.converToDto(product);
			model.addAttribute("productDto",productDto);
			return "editProductStep1";
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
	@PostMapping("edit-product/step1/{id}")
	public String step1Verify(@Validated(Step1.class) @ModelAttribute("productDto") ProductDto productDto,
            BindingResult result) {
		if(result.hasErrors()) {
			return "editproductStep1";
		}
		else {
			return "redirect:/admin/edit-product/step2/"+productDto.getId();
		}
	}
	@GetMapping("edit-product/step2/{id}")
	public String showStep2(@PathVariable int id, @ModelAttribute("productDto") ProductDto productDto,Model model) {
		if(productDto.getVariants().isEmpty()) {
			for(Size size:Size.values()) {
				 VarientDto variant = new VarientDto();
	                variant.setSize(size);
	                productDto.getVariants().add(variant);

			}
		}
		

		
	
		return "editProductStep2";
	}
	
	  @PostMapping("edit-product/step2/{id}")
	    public String processStep2(@Validated(Step2.class) @ModelAttribute("productDto") ProductDto productDto,
	                               BindingResult result) {
	        if (result.hasErrors()) {
	            return "productStep2";
	        }
	        return "redirect:/admin/edit-product/step3/"+ productDto.getId() ;
	    }
	  @GetMapping("edit-product/step3/{id}")
	  public String showStep3(@PathVariable int id, @ModelAttribute("productDto") ProductDto productDto,Model model) {
		  return "editProductStep3";
	  }
	  @PostMapping("edit-product/step3/{id}")
	  public String processStep3(@PathVariable int id, @ModelAttribute("productDto") ProductDto productDto,Model model
			  ,@RequestParam("mainImage") MultipartFile mainImage,@RequestParam("additionalImages") List<MultipartFile> additionalImages,
			  @RequestParam(value = "deletedImages", required = false) List<String> deletedImages,RedirectAttributes redirectAttributes,
			  SessionStatus status){
		  
		    try {
		    	if(!mainImage.isEmpty() && mainImage!=null) {
		    		 productDto.setMainImage(mainImage);
		    	}
		    	
		    		if(!additionalImages.isEmpty() && additionalImages!=null) {
		    			 productDto.setImages(additionalImages);
		    		}
				 
				
				  adminService.editProduct(productDto, deletedImages);
				  status.setComplete();
					 redirectAttributes.addFlashAttribute("successMessage","Product Updated successfully");
					 return "redirect:/admin/view-product/"+productDto.getId();
				 }
				 catch(Exception e) {
					 e.printStackTrace();
					 redirectAttributes.addFlashAttribute("errorMessage","Server Down Please try later");
					 return "redirect:/admin/view-product/"+productDto.getId();
				 }

		 
	  }

}

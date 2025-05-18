package com.example.demo.controller;

import java.util.List;
import java.util.Map;


import org.apache.catalina.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.demo.Dto.AddressDto;
import com.example.demo.Dto.EmailRequestDto;
import com.example.demo.Dto.PasswordForm;
import com.example.demo.Dto.UserDto;
import com.example.demo.Dto.UserEditDto;
import com.example.demo.Dto.VerifyEmail;
import com.example.demo.Repository.AddressRepo;
import com.example.demo.Repository.ProductRepo;
import com.example.demo.Repository.ProductTypeRepo;
import com.example.demo.Repository.UserRepo;
import com.example.demo.controller.util.OtpUtil;
import com.example.demo.model.AddressModel;
import com.example.demo.model.NewUserModel;
import com.example.demo.service.AdminService;
import com.example.demo.service.EmailService;
import com.example.demo.service.JwtService;
import com.example.demo.service.UserService;

import io.jsonwebtoken.io.IOException;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;



@Controller
@RequestMapping("/user")
public class userProfileController {
	@Autowired
	ProductTypeRepo productTypeRepo;
	
	@Autowired
	ProductRepo productRepo;
	
	@Autowired
	PasswordEncoder passwordEncoder;
	
	@Autowired
	EmailService emailService;
	
	@Autowired
	UserDetailsService userDetailsService;
	
	@Autowired
	UserService userService;
	
	@Autowired
	JwtService jwtService;
	
	@Autowired
	UserRepo userRepo;
	
	@Autowired
	AddressRepo addressRepo;
	

	

	
	@Autowired
	AdminService adminService;
	
	@GetMapping("/view-profile")
	public String viewProfile(@AuthenticationPrincipal UserDetails userDetails,Model model,RedirectAttributes redirectAttributes){
		try {
			if(userDetails!=null) {
				String email=userDetails.getUsername();
				NewUserModel user=userService.findUser(email);
				String referralLink=userService.getReferralCode(user);
				model.addAttribute("user", user);
				model.addAttribute("referralLink", referralLink);
				return "/user/userProfile";
			}
			else {
				redirectAttributes.addFlashAttribute("successMessage","please login to view profile features");
				return "redirect:/user/login";
			}
		
		}
		catch(Exception e){
			e.printStackTrace();
			redirectAttributes.addAttribute("errorMessage","Something went wrong");
			return "redirect:/user/login";
		}
	}
	
		@GetMapping("/refer")
		public String ReferalPage(@AuthenticationPrincipal UserDetails userDetails,Model model,RedirectAttributes redirectAttributes) {
			try {
				if(userDetails!=null) {
					String email=userDetails.getUsername();
					NewUserModel user=userService.findUser(email);
					String referralLink=userService.getReferralCode(user);
					model.addAttribute("referralLink", referralLink);
					return "/user/refer";

			}
				else {
					redirectAttributes.addFlashAttribute("successMessage","please login to view profile features");
					return "redirect:/user/login";
				}
			}catch(Exception e) {
					redirectAttributes.addAttribute("errorMessage","Something went wrong");
					return "redirect:/user/home";
				}
			
		}
	
	

	
	@GetMapping("/edit-profile")
	public String showEditPage(@AuthenticationPrincipal UserDetails userDetails, Model model,RedirectAttributes redirectAttributes,HttpSession session) {
		try {
			if(userDetails!=null) {
				String email=userDetails.getUsername();
				NewUserModel user=userService.findUser(email);
				UserEditDto userDto=userService.setUserDto(user);
				session.setAttribute("userid", userDto.getId());
				model.addAttribute("userDto", userDto);
				 // Add this to ensure userDto is not null

				return "user/editProfile";
			}
			else {
				redirectAttributes.addFlashAttribute("errorMessage","please login to edit profile features");

		
				return "redirect:/user/login";
			}
		
		}
		catch(Exception e){
			e.printStackTrace();
			redirectAttributes.addAttribute("errorMessage","Something went wrong");
			return "redirect:/user/login";
		}
	
	}
	@PostMapping("/send-email-otp")
	@ResponseBody
	public String sendEmail(@RequestBody EmailRequestDto request,HttpSession session) {
	 try {
		 String email=request.getEmail();
		
		 String otp=OtpUtil.generateOtp();
	
		 emailService.ChangeEmailOtp(email, otp);
		 
		 session.setAttribute("otpEmail", otp);
		 return "success";
	 }
	 catch(Exception e) {
		 e.printStackTrace();
		 return "failed";
	 }
	}
	
	@PostMapping("/verify-email-otp")
	@ResponseBody
	public String verifyEmail(@RequestBody VerifyEmail request,HttpSession session) {
		try {
			String email=request.getEmail();
			String otp=request.getOtp();
			String ogOtp=(String)session.getAttribute("otpEmail");
			if(otp.equals(ogOtp)) {
				session.removeAttribute("otpEmail");
				int userid=(int) session.getAttribute("userid");
				userService.changeEmail(email,userid);
				UserDetails userDetails=userDetailsService.loadUserByUsername(email);
				String token=jwtService.generateToken(userDetails);
				session.setAttribute("jwttoken", token);
				return "verified";
				
			}
			return "verification failed";
		}catch(Exception e) {
			return e.getMessage();
		}
		
	}
	
	@PostMapping("/clear-otp-session")
	@ResponseBody
	public String clearOtp(HttpSession session) {
		String otp=(String)session.getAttribute("otpEmail");
		if(otp!=null) {
			session.removeAttribute("otpEmail");
			return "status cleared";
		}
		else {
			return "no otp";
		}
	}
	
	@PostMapping("/updateProfile")
	public String updateProfile(@Valid @ModelAttribute("userDto")  UserEditDto userDto,BindingResult result,HttpSession session,Model model,RedirectAttributes redirectAttributes) {
		try {
			MultipartFile image = userDto.getImage();
			 if (image != null && !image.isEmpty()) {
		            String contentType = image.getContentType();
		            if (contentType == null || !contentType.startsWith("image/")) {
		                result.rejectValue("image", "image.error", "Please upload a valid image file");
		                
		                return "user/editProfile";
		            }
		        }

	        // Check for errors first (including fields other than image)
	        if (result.hasErrors()) {
	            model.addAttribute("userDto", userDto);
	            result.getFieldErrors().forEach(System.out::println);
	         
	            return "user/editProfile";
	        }
	       

	        // Validate image only if it's not empty
	      

	        // Get user ID from session
	        Integer userId = (Integer) session.getAttribute("userid");
	        if (userId == null) {
	            redirectAttributes.addFlashAttribute("errorMessage", "User session expired. Please log in again.");
	            return "redirect:/login"; // Adjust login path if needed
	        }

	        // Update profile
	        userService.ChangeProfile(userDto, userId);

	        // Clean up
	        session.removeAttribute("userid");
	        redirectAttributes.addFlashAttribute("successMessage", "Profile updated successfully");
	        return "redirect:/user/view-profile";
		}
		catch(Exception e) {
			e.printStackTrace();
			redirectAttributes.addFlashAttribute("errorMessage",e.getMessage());
			return "redirect:/user/view-profile";
		}
	}
	
	@GetMapping("/updatePassword")
	public String ChangePassword(Model model) {
		PasswordForm passwordForm=new PasswordForm();
		model.addAttribute("passwordForm",passwordForm);
		return "/user/userPassword";
	}
	
	@PostMapping("/updatePassword")
	public String changePassword(@ModelAttribute("passwordForm") @Valid PasswordForm passwordForm,BindingResult result,@AuthenticationPrincipal UserDetails userDetails,Model model,RedirectAttributes redirectAttributes) {
		 try {
		        if (userDetails == null) {
					redirectAttributes.addFlashAttribute("errorMessage","please login to access this features");

		            return "redirect:/user/login";
		        }

		        String encodedPassword = userDetails.getPassword(); // Hashed password
		        String email = userDetails.getUsername();

		        if (result.hasErrors()) {
		        	model.addAttribute("passwordForm",passwordForm);
		            return "user/userPassword";
		        }

		        // Use PasswordEncoder to compare raw current password with hashed one
		        if (!passwordEncoder.matches(passwordForm.getCurrentPassword(), encodedPassword)) {
		            result.rejectValue("currentPassword", "currentPassword.error", "Current password is incorrect");
		            return "user/userPassword";
		        }

		        if (!passwordForm.getNewPassword().equals(passwordForm.getConfirmNewPassword())) {
		            result.rejectValue("confirmNewPassword", "confirmNewPassword.error", "New passwords do not match");
		            return "user/userPassword";
		        }

		        userService.changePassword(passwordForm.getNewPassword(), email);
		        redirectAttributes.addFlashAttribute("successMessage", "Password changed successfully");
		        return "redirect:/user/view-profile";

		    } catch (Exception e) {
		        e.printStackTrace();
		        redirectAttributes.addFlashAttribute("errorMessage", "An error occurred: " + e.getMessage());
		        return "redirect:/user/view-profile";
		    }
	}
	@GetMapping("/add-address")
	public String addAddress(Model model) {
		AddressDto addressDto=new AddressDto();
		model.addAttribute(addressDto);
		return "user/addAddress";
	}
	
	@PostMapping("/address/save")
	public String saveAddress(@ModelAttribute("addressDto") @Valid AddressDto addressDto,BindingResult result,@AuthenticationPrincipal UserDetails userdetails,Model model,RedirectAttributes redirectAttributes) {
		
		try {
			if(userdetails==null) {
				redirectAttributes.addFlashAttribute("errorMessage","please login to access this features");

				return "redirect:/user/login";
			}
			if(result.hasErrors()) {
				return "user/addAddress";
			}
			
			userService.addAddress(addressDto,userdetails.getUsername());
			redirectAttributes.addFlashAttribute("successMessage","Address added Successfully");
			return "redirect:/user/view-address";
		}
		catch(Exception e) {
			e.printStackTrace();
			redirectAttributes.addFlashAttribute("errorMessage", "Something unexpected happened");
	        return "redirect:/user/view-address";
		}
	}
	
	@GetMapping("/view-address")
	public String viewAddress(Model model,@AuthenticationPrincipal UserDetails userDetails,RedirectAttributes redirectAttributes) {
		try {
			if(userDetails==null) {
				redirectAttributes.addFlashAttribute("successMessage","please login to view your address information");

				return "redirect:/user/login";
			}
			List<AddressModel> addresses=userService.getAddress(userDetails);
			model.addAttribute("addresses",addresses);
			return "/user/viewAddress";
		}
		catch(Exception e) {
			model.addAttribute("errorMessage", "Something unexpected happened");
	        return "user/viewAddress";
		}
	}
	
	@GetMapping("/edit-address/{id}")
	public String editAddress(@PathVariable int id,Model model) {
		AddressDto addressDto=userService.setAddressDto(id);
		
		model.addAttribute("addressDto",addressDto);
		return "/user/editAddress";
	}
	
	@PostMapping("/update-address")
	public String updateAddress(@ModelAttribute("addressDto") @Valid AddressDto addressDto,BindingResult result,Model model,RedirectAttributes redirectAttributes) {
		
		try {
			
			if(result.hasErrors()) {
				return "user/editAddress";
			}

			userService.updateAddress(addressDto);
			redirectAttributes.addFlashAttribute("successMessage","Address saved Successfully");
			return "redirect:/user/view-address";
		}
		catch(Exception e) {
			e.printStackTrace();
			redirectAttributes.addFlashAttribute("errorMessage", "Something unexpected happened");
	        return "redirect:/user/view-address";
		}
	}
	
	@GetMapping("/delete-address/{id}")
	public String deleteAddress(@PathVariable int id,RedirectAttributes redirectAttributes) {
		try {
			addressRepo.deleteById(id);
	        redirectAttributes.addFlashAttribute("successMessage", "Address deleted successfully.");
	        return "redirect:/user/view-address";
		}
		catch(Exception e) {
	        redirectAttributes.addFlashAttribute("errorMessage", "Failed to delete address.");
	        return "redirect:/user/view-address";

		}
	}
	
	
	
	
}

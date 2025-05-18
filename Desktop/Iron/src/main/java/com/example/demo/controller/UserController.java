package com.example.demo.controller;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
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

import com.example.demo.Dto.LoginCred;
import com.example.demo.Dto.UserDto;
import com.example.demo.Repository.ProductRepo;
import com.example.demo.Repository.ProductTypeRepo;
import com.example.demo.Repository.UserRepo;
import com.example.demo.controller.util.OtpUtil;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.exception.UserNotFoundException;
import com.example.demo.model.CollectionModel;
import com.example.demo.model.FitType;
import com.example.demo.model.Gender;
import com.example.demo.model.NewUserModel;
import com.example.demo.model.ProductImages;
import com.example.demo.model.ProductModel;
import com.example.demo.model.ProductTypes;
import com.example.demo.model.Size;
import com.example.demo.service.AdminService;
import com.example.demo.service.EmailService;
import com.example.demo.service.JwtService;
import com.example.demo.service.UserService;
import com.example.demo.service.WalletService;
import com.fasterxml.jackson.annotation.JsonCreator.Mode;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/user")
public class UserController {
	@Autowired
	ProductTypeRepo productTypeRepo;
	
	@Autowired
	ProductRepo productRepo;
	
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
	WalletService walletService;
	
	@Autowired
	AdminService adminService;
	
	@GetMapping("/base")
	public String viewBase() {
		return "user/base";
	}
	
	@GetMapping("/home")
	public String viewHome(@RequestParam(required=false,defaultValue="Female") Gender gender,Model model) {
		try {
			List<ProductTypes> categories=productTypeRepo.findByGender(gender).stream().filter(category->category.getIs_active()==true).collect(Collectors.toList());
			List<ProductModel> products=productRepo.findAll().stream().limit(5).sorted(Comparator.comparing(ProductModel::getCreateAt).reversed()).collect(Collectors.toList());
			model.addAttribute("products", products); 
			model.addAttribute("categories", categories);
			return "user/home";
		}
		catch(Exception e) {
			e.printStackTrace();
			model.addAttribute("errorMessage", "Cannot Fetch Categories");
			return "user/home";
		}
	}
	
	@GetMapping("/signup")
	public String signUp(Model model,@RequestParam(value="ref",required=false) String refToken) {
		UserDto userdto=new UserDto();
		model.addAttribute("userdto",userdto);
		model.addAttribute("refToken",refToken);
		return "user/signUp";
	}
	
	@PostMapping("/signup")
	public String Register(@Valid @ModelAttribute("userdto") UserDto userdto,BindingResult result,
			@RequestParam String cpassword,HttpSession session,RedirectAttributes redirectAttributes) {

	try {
		if(!userdto.getPassword().equals(cpassword)) {
			 result.rejectValue("password", "error.userdto", "Password does not Match");
		}
		if(userService.emailExsits(userdto.getEmail())) {
			result.rejectValue("email", "error.userdto","Email already exist");
		}
		
		if(result.hasErrors()) {
			return "user/signUp";
		}
		String otp=OtpUtil.generateOtp();
		userdto.setOtp(otp);
		userdto.setOtpGeneratedTime(LocalDateTime.now());
		session.setAttribute("tempUser", userdto);
		emailService.SendotpMail(userdto.getEmail(), otp);
		
		return "redirect:/user/verify-otp";
		
	}
	catch(Exception e) {
		e.printStackTrace();
		redirectAttributes.addAttribute("errorMessage", "Something unexpected happended");
		return "redirect:/user/signup";
	}
		
	}
	@GetMapping("/verify-otp")
	public String verifyotp(Model model,HttpSession session,RedirectAttributes redirectAttributes) {
		UserDto tempUser=(UserDto) session.getAttribute("tempUser");
	 // assuming it's a LocalDateTime
		if(tempUser==null) {
			redirectAttributes.addFlashAttribute("errorMessage", "Session expired Please register again");
		        return "redirect:/user/signup";
		}
		return "/user/verifyOtp";
	}
	
	
	@PostMapping("/verify-otp")
	public String verifyOtp(@RequestParam("otp") String otp,HttpSession session,Model model,RedirectAttributes redirectAttributes) {
		try {
			UserDto tempUser=(UserDto) session.getAttribute("tempUser");
			System.out.println(tempUser.getOtp()+" "+tempUser.getEmail());
			if(tempUser==null) {
				  model.addAttribute("errorMessage", "Session expired. Please register again.");
			        return "user/signUp";
			}
			if (!tempUser.getOtp().equals(otp)) {
		        model.addAttribute("error", "Invalid OTP");
		        return "user/verifyOtp";
		    }
			if(Duration.between(tempUser.getOtpGeneratedTime(), LocalDateTime.now()).toMinutes() >1){
				   model.addAttribute("error", "OTP expired");
			        return "user/verifyOtp";
			}
			session.removeAttribute("tempUser");
			session.setAttribute("otpVerifiedUser", tempUser);
	
			return "redirect:/user/register";

		}
		catch(Exception e) {
			e.printStackTrace();
			redirectAttributes.addAttribute("errorMessage", "Something unexpected happended");
			return "redirect:/user/signup";
		}
	}
	
	@GetMapping("/register")
	public String register (HttpSession session,Model model,RedirectAttributes redirectAttributes) {
		try {
			UserDto tempUser=(UserDto) session.getAttribute("otpVerifiedUser");
			if (tempUser!=null) {
				userService.addUser(tempUser);
				session.removeAttribute("otpVerifiedUser");
				redirectAttributes.addFlashAttribute("successMessage","User Registered successfully");
				NewUserModel user=userRepo.findByEmail(tempUser.getEmail()).orElseThrow(()->new UserNotFoundException("user not found"));
				walletService.setWallet(user);
				return "redirect:/user/login";
			}
			else {
				redirectAttributes.addFlashAttribute("errorMessage","session expired");
				return "redirect:/user/signup";
			}
		
		}
		catch(Exception e) {
			e.printStackTrace();
			redirectAttributes.addFlashAttribute("errorMessage","something unexpected happened");
			return "redirect:/user/signup";
		}
	}

	@GetMapping("/resend-otp")
	public String resenOtp(HttpSession session,Model model) {
		UserDto tempUser=(UserDto) session.getAttribute("tempUser");
		   if (tempUser == null) {
		        return "redirect:/signup";
		    }
		String otp=OtpUtil.generateOtp();
		tempUser.setOtp(otp);
		tempUser.setOtpGeneratedTime(LocalDateTime.now());
		emailService.SendotpMail(tempUser.getEmail(), otp);
	

		
		  model.addAttribute("message", "OTP sent successfully");
		    return "user/verifyOtp";
	}
	@GetMapping("/login")
	public String Login(Model model) {
		LoginCred loginCred=new LoginCred();
		model.addAttribute("loginCred", loginCred);
		return "/user/login";
		
	}
	@PostMapping("/login")
	public String ValidateUser(@Valid @ModelAttribute LoginCred loginCred,BindingResult result,Model model,HttpSession session) {
		
		
		if(result.hasErrors()) {
			return "/user/login";
		}
		
		   try {
			
			   adminService.verify(loginCred.getEmail(),loginCred.getPassword());
		        UserDetails userdetails = userDetailsService.loadUserByUsername(loginCred.getEmail());
		        String token=jwtService.generateToken(userdetails);
				session.setAttribute("jwttoken", token);
				   System.out.println(token);
				return "redirect:/user/home";
		    } catch (UsernameNotFoundException ex) {
		        result.rejectValue("email", "error.email", ex.getMessage());
		        return "/user/login"; // back to login view
		    }
		   catch(BadCredentialsException e) {
			   result.rejectValue("password", "error.password", e.getMessage());
		        return "/user/login";
		   }
		   catch(Exception e) {
			   e.printStackTrace();
			   model.addAttribute("error","Unexpected error occured");
			   return "/user/login";
		   }
	}
	
	@GetMapping("/forget-password/step1")
	public String forgetPassword() {
		return "/user/forgetPassword1";
	}
	@PostMapping("/forget-password/step1")
	public String SendMail(@RequestParam String email,Model model,RedirectAttributes redirectAttributes,HttpSession session) {
		try {
		if(email==null) {
			model.addAttribute("error", "Please enter your Mail id");
			return "/user/forgetPassword1";
		}
		if(!userRepo.existsByEmail(email)) {
			model.addAttribute("error", "Email does not exist");
			return "/user/forgetPassword1";
		}
		
			String otp=OtpUtil.generateOtp();
			session.setAttribute("email", email);
			session.setAttribute("otp", otp);
			session.setAttribute("otpGeneratedTime", LocalDateTime.now());
			emailService.forgetPasswordOtpMail(email, otp);
			return "redirect:/user/forget-password/step2";
		}
		catch(Exception e) {
			e.printStackTrace();
			redirectAttributes.addFlashAttribute("message", "Something unexpected occured");
			return "redirect:/user/forget-password/step1";
		}
	
	}
	@GetMapping("/forget-password/step2")
	public String otpPage() {
		return "user/verifyOtp2";
	}
	@PostMapping("/forget-password/step2")
	public String verifyForgetPasswordOtp(@RequestParam String otp,HttpSession session,RedirectAttributes redirectAttributes,Model model) {
		try {
		String email=(String) session.getAttribute("email");
		String Ogotp=(String) session.getAttribute("otp");
		LocalDateTime otpGeneratedTime = (LocalDateTime) session.getAttribute("otpGeneratedTime");
		if(email==null||otp==null||otpGeneratedTime==null) {
			redirectAttributes.addFlashAttribute("errorMessage","Session Expired Try Again");
			return "redirect:/user/forget-password/step1";
		}	
			if (!Ogotp.equals(otp)) {
		        model.addAttribute("error", "Invalid OTP");
		        return "user/verifyOtp2";
		    }
			if(Duration.between(otpGeneratedTime, LocalDateTime.now()).toMinutes() >1){
				   model.addAttribute("error", "OTP expired");
			        return "user/verifyOtp2";
			}
			System.out.println(otp);
			session.removeAttribute("email");
			session.removeAttribute("otp");
			session.removeAttribute("otpGeneratedTime");
			session.setAttribute("otpVerifiedemail", email);
	
			return "redirect:/user/changePassword";
		
	}catch(Exception e) {
		e.printStackTrace();
		redirectAttributes.addFlashAttribute("errorMessage","Unexpected error occured");
		return "redirect:/user/forget-password/step1";
		
	}
	}
		
	@GetMapping("/changePassword")
	public String ChangePassword() {
		return "user/changePassword";
	}
	@PostMapping("/changePassword")
	public String setPassword(@RequestParam String newPassword,@RequestParam String confirmPassword,HttpSession session,
			RedirectAttributes redirectAttributes,Model model) {
		try {
			String email=(String) session.getAttribute("otpVerifiedemail");
			if(email==null) {
				redirectAttributes.addFlashAttribute("errorMessage","Session Expired Try Again");
				return "redirect:/user/forget-password/step1";
			}
			if(!newPassword.equals(confirmPassword)) {
				model.addAttribute("error","Password not matching");
				return "user/changePassword";
			}
			userService.setPassword(email,newPassword);
			session.removeAttribute("otpVerifiedemail");
			redirectAttributes.addFlashAttribute("successMessage","Password Changed successfully");
			return "redirect:/user/login";
		}
		catch(Exception e) {
			e.printStackTrace();
		
			redirectAttributes.addFlashAttribute("errorMessage","Something unexpected occured");
			return "redirect:/user/login";
		}
	}
	
	@GetMapping("/forget-password/resend-otp")
	public String resendOtp(HttpSession session,RedirectAttributes redirectAttributes) {
		String email=(String) session.getAttribute("email");
		if(email==null) {
			redirectAttributes.addFlashAttribute("errorMessage","Session Expired Try Again");
			return "redirect:/user/forget-password/step1";
			
		}
		String otp=OtpUtil.generateOtp();
		emailService.forgetPasswordOtpMail(email, otp);
		session.setAttribute("otp", otp);
		session.setAttribute("otpGeneratedTime",LocalDateTime.now());
		redirectAttributes.addFlashAttribute("message", "OTP has been resent to your email.");

		return "redirect:/user/forget-password/step2";
	}
	@GetMapping("/categories/{genderType}")
	@ResponseBody
	public List<ProductTypes> getCategory(@PathVariable("genderType") Gender gender){
		return userService.getCategories(gender);
	}
	
	@GetMapping("/view-products")
	public String viewProducts(@RequestParam Gender gender,@RequestParam(required=false,name="type")  List<Integer> typeId,
			@RequestParam(required=false,name="collection") List<Integer>  collectionId,@RequestParam (required=false) List<String> fit,
			@RequestParam(required=false) String sort,@RequestParam(defaultValue = "0") int page,@RequestParam(defaultValue = "6") int size,Model model) {
		
		
		try {
			 // ✅ Correct
			
			
			Page<ProductModel> products= userService.getProducts(gender,typeId,collectionId,fit,sort,page,size);
			model.addAttribute("products",products.getContent());
			List<CollectionModel> collections=userService.getcollections();
			List<ProductTypes> productTypes=userService.getCategories(gender);
			long totalProducts = products.stream().count();  // Count of all products in the database
			
		      if (products.isEmpty()) {
		            model.addAttribute("errorMessage", "No Products Found ");
		        }
			model.addAttribute("gender",gender);	
			model.addAttribute("productTypes", productTypes);
			model.addAttribute("collections", collections);
			model.addAttribute("fitOptions",FitType.values());
		    model.addAttribute("selectedTypes", typeId);
		    model.addAttribute("selectedCollections", collectionId);
		    model.addAttribute("selectedSort", sort); // 'sort' is the selected value from request param
		    model.addAttribute("currentPage", page);
		    model.addAttribute("totalPages",products.getTotalPages());
		    model.addAttribute("selectedFits", fit);
		    model.addAttribute("sizeTypes", Size.values());
			return "/user/view-products";
		}
		catch(Exception e) {
			e.printStackTrace();
			model.addAttribute("errorMessage","Something Unexpected happened");
			return "/user/view-products";
		}
	}
	@GetMapping("view-product/{id}")
	public String viewProducts(@PathVariable int id,Model model)
	{
		try {
			ProductModel product=userService.getProduct(id);
			CollectionModel collection=product.getCollection();
			List<ProductModel> similarProducts=userService.getProductByCollection(collection,id);
			model.addAttribute("product",product);
			model.addAttribute("similarProducts",similarProducts);
			return "user/productDetails";
		}
		catch(Exception e){
			
			model.addAttribute("errorMessage","something unexpected occured");

			return "user/productDetails";
		}
		
		
	}
	@GetMapping("/view-products/search")
	public String ShowSearchProducts(@RequestParam String keyword,@RequestParam(defaultValue="0") int page,@RequestParam(defaultValue="6") int size,
			@RequestParam(required=false) Gender gender,@RequestParam(required=false,name="collection") List<Integer>  collectionId,@RequestParam (required=false) List<String> fit,
			@RequestParam(required=false) String sort,Model model,RedirectAttributes redirectAttributes) {
		try {
			Integer typeId=null;
			Page<ProductModel> products=userService.getSearchProducts(gender, keyword, collectionId, fit, sort, page, size,typeId);
			List<CollectionModel> collections=userService.getcollections();
			 // Count of all products in the database
			
			if(products==null || products.isEmpty()) {
				model.addAttribute("errorMessage", "No results found for: " + keyword);
				
	          
			}
			String selectedGender=null;
			model.addAttribute("products",products.getContent());
			model.addAttribute("keyword",keyword);
			model.addAttribute("collections", collections);
			model.addAttribute("fitOptions",FitType.values());
		//    model.addAttribute("selectedTypes", typeId);
		    model.addAttribute("selectedCollections", collectionId);
		    if(gender!=null) {
		    	 selectedGender=gender.toString();
		    }
		   
		    model.addAttribute("selectedGender", selectedGender);
		    model.addAttribute("selectedSort", sort); // 'sort' is the selected value from request param
		    model.addAttribute("currentPage", page);
		    System.out.print(products.getTotalPages());
		    model.addAttribute("totalPages",products.getTotalPages());
		    model.addAttribute("selectedFits", fit);
		    model.addAttribute("sizeTypes", Size.values());
			return "/user/view-search-products";
		}
		catch(Exception e) {
			e.printStackTrace();
			model.addAttribute("errorMessage","Something Unexpected happened");
			return "/user/view-search-products";
		}
	
		
	}
	@GetMapping("/view-products/{gender}/{id}")
	public String ViewProducts(@PathVariable("id") Integer typeId,@PathVariable Gender gender,@RequestParam(defaultValue="0") int page,@RequestParam(defaultValue="6") int size,
		@RequestParam(required=false,name="collection") List<Integer>  collectionId,@RequestParam (required=false) List<String> fit,
			@RequestParam(required=false) String sort,Model model,RedirectAttributes redirectAttributes) {
		try {
			String keyword=null;
			Page<ProductModel> products=userService.getSearchProducts(gender, keyword, collectionId, fit, sort, page, size,typeId);
			List<CollectionModel> collections=userService.getcollections();
			ProductTypes productType=userService.getCategoryById(typeId);
			
			 // Count of all products in the database
			
			if(products==null || products.isEmpty()) {
				model.addAttribute("errorMessage", "No Products Found");
				
	          
			}

			model.addAttribute("products",products.getContent());
			model.addAttribute("collections", collections);
			model.addAttribute("fitOptions",FitType.values());
		//    model.addAttribute("selectedTypes", typeId);
		    model.addAttribute("selectedCollections", collectionId);
		    
		   
		    model.addAttribute("gender", gender);
		    model.addAttribute("productType", productType);

		    model.addAttribute("selectedSort", sort); // 'sort' is the selected value from request param
		    model.addAttribute("currentPage", page);

		    model.addAttribute("totalPages",products.getTotalPages());
		    model.addAttribute("selectedFits", fit);
		    model.addAttribute("sizeTypes", Size.values());
			return "/user/view-cat-products";
		}
		catch(Exception e) {
			e.printStackTrace();
			model.addAttribute("errorMessage","Something Unexpected happened");
			return "/user/view-cat-products";
		}
	}
	
	


	
}

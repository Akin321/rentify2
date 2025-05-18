package com.example.demo.controller;

import java.math.BigDecimal;
import java.util.List;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.demo.Dto.ProductDto;
import com.example.demo.Dto.ProductTypeDto;
import com.example.demo.Dto.UserDto;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.exception.UserNotFoundException;
import com.example.demo.model.CollectionModel;
import com.example.demo.model.NewUserModel;
import com.example.demo.model.ProductModel;
import com.example.demo.model.ProductTypes;
import com.example.demo.service.AdminService;
import com.example.demo.service.JwtService;

import io.jsonwebtoken.io.IOException;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
@RestController
@RequestMapping("admin-rest")
public class AdminRestController {
	@Autowired
	AdminService adminService;
	
	@Autowired 
	JwtService jwtService;
	
	
	@Autowired
	UserDetailsService userDetailsService;
	@Operation(summary="User Login",description="User login and generate token if validated")
	@PostMapping("/login")
	public ResponseEntity<String> login(@RequestParam String email,@RequestParam String password) {
		try {
			adminService.verify(email,password);

				UserDetails userDetails=userDetailsService.loadUserByUsername(email);
				String token=jwtService.generateToken(userDetails);
		
				return ResponseEntity.ok("token "+token);
			}

		
		catch(UsernameNotFoundException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("email not found");
		}
		catch(BadCredentialsException e) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("password not matching");


		}
		catch(Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Server Down pls try again later");

		}
	}
	
	@GetMapping("/view-user")
	public ResponseEntity<Page<NewUserModel>> ViewUser(@RequestParam(defaultValue="0") int page,@RequestParam(defaultValue="5") int size, @RequestParam(required = false)String keyword){
		try {
			Page<NewUserModel> users= adminService.viewUser(page,size,keyword);
			return ResponseEntity.ok(users);
	}catch(UserNotFoundException e ) {
		e.printStackTrace();
		return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
	}
		catch(Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();

		}
}
	
	@PatchMapping("/block-user/{id}")
	public ResponseEntity<String> BlockUser(@PathVariable int id,@RequestParam boolean active){
		try {
			adminService.setUserActiveStatus(id,active);
			return ResponseEntity.ok("User "+(active ?"UnBlocked":"Blocked")+ "Successfully");
		}
		catch(UsernameNotFoundException e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("User not found");

		}
		catch(Exception e) {

			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Server Down pls try again later");

		}
		
	}
	
	@PostMapping("/add-user")
	public ResponseEntity<String> AddUser (@RequestBody UserDto userDto){
	
		try {
			adminService.addUser(userDto);
			return ResponseEntity.ok("USer Added Successfully");
		}
		catch(Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("internal server error");
		}
	}
	
	@GetMapping("/view-productTypes")
	public ResponseEntity<?> viewProductTypes(@RequestParam(defaultValue="0") int page,@RequestParam(defaultValue="5") int size,@RequestParam(required=false) String keyword){
		try {
			return ResponseEntity.ok(adminService.viewProductTypes(page,size,keyword));
		}
		catch(ResourceNotFoundException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
		}
		catch(Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
		
	}
	
	@PostMapping("/add-productTypes")
	public ResponseEntity<String> AddProductTypes (@RequestBody ProductTypeDto productTypeDto){
		try {
			adminService.addProductTypes(productTypeDto);
			return ResponseEntity.ok("product addedd successfully");
		}
		catch(IllegalArgumentException e) {
			return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
		}
		catch(Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("internal server error");
		}
	}
	
	@PutMapping("/edit-productTypes/{id}")
	public ResponseEntity<String> EditProductTypes (@PathVariable int id,@RequestBody ProductTypeDto productTypeDto ){
		try {
			adminService.editprdType(productTypeDto,id);
			return ResponseEntity.ok("productType edited successfully");
		}
		catch(ResourceNotFoundException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Product Type not found");
		}
		catch(IllegalArgumentException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Product already exists");
		}
		catch(Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("internal server error");
		}
	}
	
	@PatchMapping("/disable-productTypes/{id}")
	public ResponseEntity<String> disableTypes(@PathVariable int id,boolean status){
		try {
			adminService.editProductTypeStatus(id,status);
			return ResponseEntity.ok("Status Changed Successfully");
		}
		catch(ResourceNotFoundException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Product Type not found");
		}
		catch(Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("internal server error");
		}
	}
	
	@GetMapping("/view-productTypes/{id}")
	public ResponseEntity<?> viewType(@PathVariable int id){
		try {
			ProductTypes productType=adminService.getProductType(id);
			return ResponseEntity.ok(productType);
		}
		catch(ResourceNotFoundException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
		}
		catch(Exception e) {
			
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal server error");
		}
	}
	
	@PostMapping("/add-collections")
	public ResponseEntity<String> addCollection(@RequestParam String name){
		try {
			adminService.addCollection(name);
			return ResponseEntity.ok("collection added successfully");
		}
		catch(IllegalArgumentException e){
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
		}
		catch(Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal server error");

		}
	}
	
	@GetMapping("/view-collections")
	public ResponseEntity<?> viewCollections(@RequestParam(defaultValue="0") int page,@RequestParam(defaultValue="5") int size,@RequestParam(required=false) String keyword){
		try {
			Page<CollectionModel> collections= adminService.getCollections(page,size,keyword);
			return ResponseEntity.ok(collections);
		}
		catch(ResourceNotFoundException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
		}
		catch(Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal server error");
		}
	}
	
	@GetMapping("disable-collections/{id}")
	public ResponseEntity<String> disableCollections(@PathVariable int  id ,@RequestParam boolean status){
		try {
			adminService.setStatus(id,status);
			return ResponseEntity.ok("Status Changed Successfully");
		}
		catch(ResourceNotFoundException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Collection not found");
		}
		catch(Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal server error");
		}
	}
	
	@PutMapping("edit-collections/{id}")
	public ResponseEntity<String> editCollections(@PathVariable int id,@RequestParam String name){
		try {
			adminService.editCollection(id,name);
			return ResponseEntity.ok("Edited Successfully");
		}
		catch(IllegalArgumentException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Collection name already exists");
			
		}
		catch(ResourceNotFoundException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Collection not found");
		}
		catch(Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal server error");
		}
	}
	
	@DeleteMapping("delete-collection/{id}")
	public ResponseEntity<String> deleteCollection (@PathVariable int id){
		try {
			adminService.deleteCollection(id);
			return ResponseEntity.ok("Deleted Successfully");
		}
		catch(ResourceNotFoundException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
		}
		catch(Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal server error");
		}
	}
	
	@PostMapping(value="/add-products")
	//requestpart json + files
	public ResponseEntity<String> addProducts(@RequestBody  @Valid ProductDto productDto){
		
		try {
			adminService.addProduct(productDto);
			return ResponseEntity.ok("Product addedd successfully");
		}
		catch(IllegalArgumentException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());

		}
		catch(IOException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());

		}
		catch(ResourceNotFoundException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
		}
		catch(Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal server error");
		}
	}
	
	@GetMapping("/view-products")
	public ResponseEntity<?> viewProducts(@RequestParam(defaultValue="0") int page,@RequestParam(defaultValue="5") int size,@RequestParam(required=false) String keyword){
		try {
			Page<ProductModel> product=adminService.viewProducts(page,size,keyword);
			return ResponseEntity.ok(product);
		}
		catch(ResourceNotFoundException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
		}
		catch(Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal server error");
		}
	}
	
	@GetMapping("/disable-products/{id}")
	public ResponseEntity<String> disableProducts(@PathVariable int id,@RequestParam boolean status){
		try {
			adminService.disableProducts(id,status);
			return ResponseEntity.ok("product disabled successfully");
		}
		catch(ResourceNotFoundException e){
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
		}
		catch(Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal server error");
		}
	}
	@PostMapping("edit-productdiscount/{id}")
	public ResponseEntity<String> editDiscount(@PathVariable int id,@RequestParam BigDecimal discountPer){
		try {
			adminService.editProductDiscount(id,discountPer);
				return ResponseEntity.ok("Discount updated successfully");
			
		}
		catch(ResourceNotFoundException e){
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
		}
		catch(Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal server error");
		}
	}
	
	
	
}

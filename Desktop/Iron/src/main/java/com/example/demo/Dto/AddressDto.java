package com.example.demo.Dto;

import com.example.demo.model.AddType;
import com.example.demo.model.NewUserModel;


import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class AddressDto {

	    private int id;
	    @NotBlank(message = "Enter your full name")
	    @Size(min = 3, message = "Enter minimum 3 characters")
	    private String name;

	    @NotBlank(message = "Phone number is required")
	    @Pattern(regexp = "\\d{10}", message = "Phone number must be 10 digits")
	    private String phone;

	    @NotBlank(message = "Pincode is required")
	    @Pattern(regexp = "\\d{6}", message = "Pincode must be 6 digits")
	    private String pincode;

	    private String locality;

	    @NotBlank(message = "Address is required")
	    @Size(max = 500, message = "Address can't exceed 500 characters")
	    private String address;

	    @NotBlank(message = "City is required")
	    private String city;

	    @NotBlank(message = "State is required")
	    private String state;

	    private String landmark;

	    @NotNull(message = "Please select a type")
	    private AddType addressType;
	    
	    

		public AddressDto() {
			
		}

		public AddressDto(int id,
				@NotBlank(message = "Enter your full name") @Size(min = 3, message = "Enter minimum 3 characters") String name,
				@NotBlank(message = "Phone number is required") @Pattern(regexp = "\\d{10}", message = "Phone number must be 10 digits") String phone,
				@NotBlank(message = "Pincode is required") @Pattern(regexp = "\\d{6}", message = "Pincode must be 6 digits") String pincode,
				String locality,
				@NotBlank(message = "Address is required") @Size(max = 500, message = "Address can't exceed 500 characters") String address,
				@NotBlank(message = "City is required") String city,
				@NotBlank(message = "State is required") String state, String landmark,
				@NotNull(message = "Please select a type") AddType addressType) {
			super();
			this.id = id;
			this.name = name;
			this.phone = phone;
			this.pincode = pincode;
			this.locality = locality;
			this.address = address;
			this.city = city;
			this.state = state;
			this.landmark = landmark;
			this.addressType = addressType;
		}

		public int getId() {
			return id;
		}

		public void setId(int id) {
			this.id = id;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getPhone() {
			return phone;
		}

		public void setPhone(String phone) {
			this.phone = phone;
		}

		public String getPincode() {
			return pincode;
		}

		public void setPincode(String pincode) {
			this.pincode = pincode;
		}

		public String getLocality() {
			return locality;
		}

		public void setLocality(String locality) {
			this.locality = locality;
		}

		public String getAddress() {
			return address;
		}

		public void setAddress(String address) {
			this.address = address;
		}

		public String getCity() {
			return city;
		}

		public void setCity(String city) {
			this.city = city;
		}

		public String getState() {
			return state;
		}

		public void setState(String state) {
			this.state = state;
		}

		public String getLandmark() {
			return landmark;
		}

		public void setLandmark(String landmark) {
			this.landmark = landmark;
		}

		public AddType getAddressType() {
			return addressType;
		}

		public void setAddressType(AddType addressType) {
			this.addressType = addressType;
		}
	    
	    
}

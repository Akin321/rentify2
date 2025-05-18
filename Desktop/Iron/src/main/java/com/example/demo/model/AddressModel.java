package com.example.demo.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "addresses")
public class AddressModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    // RELATION: Many addresses can belong to one user
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private NewUserModel user;

    private String name;
    private String phone;
    private String pincode;
    private String locality;

    @Column(length = 500)
    private String address;

    private String city;
    private String state;
    private String landmark;

    @Enumerated(EnumType.STRING)
    private AddType addressType;

    private boolean isDeliveryAddress=false;
    
    

	public AddressModel() {
		
	}

	public AddressModel(int id, NewUserModel user, String name, String phone, String pincode, String locality,
			String address, String city, String state, String landmark, AddType addressType,
			boolean isDeliveryAddress) {
		super();
		this.id = id;
		this.user = user;
		this.name = name;
		this.phone = phone;
		this.pincode = pincode;
		this.locality = locality;
		this.address = address;
		this.city = city;
		this.state = state;
		this.landmark = landmark;
		this.addressType = addressType;
		this.isDeliveryAddress = isDeliveryAddress;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public NewUserModel getUser() {
		return user;
	}

	public void setUser(NewUserModel user) {
		this.user = user;
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

	public boolean isDeliveryAddress() {
		return isDeliveryAddress;
	}

	public void setDeliveryAddress(boolean isDeliveryAddress) {
		this.isDeliveryAddress = isDeliveryAddress;
	}
    
    
    
	
	
}

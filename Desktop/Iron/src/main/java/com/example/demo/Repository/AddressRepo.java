package com.example.demo.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.model.AddressModel;
import com.example.demo.model.NewUserModel;

public interface AddressRepo extends JpaRepository<AddressModel,Integer> {



	List<AddressModel> findByUser(NewUserModel user);



	List<AddressModel> findByUserOrderByIsDeliveryAddressDesc(NewUserModel user);



	int countByUser(NewUserModel user);



	AddressModel findByUserAndIsDeliveryAddress(NewUserModel user2, boolean isDeliveryAddress);

}

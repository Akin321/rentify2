package com.example.demo.Repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.model.CartModel;
import com.example.demo.model.NewUserModel;
import com.example.demo.model.ProductModel;
import com.example.demo.model.ProductVariant;

public interface CartRepo extends JpaRepository<CartModel,Integer> {

	Optional<CartModel> findByUserAndProductAndVariant(NewUserModel user, ProductModel product, ProductVariant variant);

	List<CartModel> findByUser(NewUserModel user);

	void deleteByUser(NewUserModel user);

}

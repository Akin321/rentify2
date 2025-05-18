package com.example.demo.Repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.util.Streamable;

import com.example.demo.model.CollectionModel;
import com.example.demo.model.Gender;
import com.example.demo.model.NewUserModel;
import com.example.demo.model.ProductModel;

public interface ProductRepo extends JpaRepository<ProductModel,Integer>{

		boolean existsByName(String name);
		// TODO Auto-generated method stub

		Page<ProductModel> findByNameContainingIgnoreCase(Pageable pageable, String keyword);

		Page<ProductModel> findByGender(Gender gender, Pageable pageable);
		
		List<ProductModel> findByGender(Gender gender);



		List<ProductModel> findByCollection(CollectionModel collection);



}

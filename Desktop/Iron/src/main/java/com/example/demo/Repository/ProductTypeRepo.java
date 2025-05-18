package com.example.demo.Repository;

import java.util.List;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.model.Gender;
import com.example.demo.model.ProductTypes;

public interface ProductTypeRepo extends JpaRepository<ProductTypes,Integer> {
	List<ProductTypes> findByNameIgnoreCaseAndGender (String Name,Gender gender);

	Page<ProductTypes> findByNameContainingIgnoreCase(String keyword, Pageable pageable);

	List<ProductTypes> findByGender(Gender gender);

}

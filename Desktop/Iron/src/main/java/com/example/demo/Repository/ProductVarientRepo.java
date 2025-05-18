package com.example.demo.Repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.Dto.VarientDto;
import com.example.demo.model.NewUserModel;
import com.example.demo.model.ProductModel;
import com.example.demo.model.ProductVariant;
import com.example.demo.model.Size;

public interface ProductVarientRepo extends JpaRepository<ProductVariant,Integer> {




	void deleteAllByProduct_Id(int id);

	Optional<ProductVariant> findByProductAndSize(ProductModel product, Size size);

	

}

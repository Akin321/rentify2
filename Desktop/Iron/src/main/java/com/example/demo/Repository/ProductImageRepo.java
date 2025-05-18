package com.example.demo.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.demo.model.ProductImages;

import jakarta.transaction.Transactional;

public interface ProductImageRepo extends JpaRepository <ProductImages,Integer>{


    @Query("SELECT p FROM ProductImages p WHERE p.product.id = :productId AND p.is_main = true")
	ProductImages findAllByProduct_IdAndIsMainTrue(@Param("productId") int productId);
    
    @Modifying
    @Transactional
    @Query("DELETE FROM ProductImages p WHERE p.image = :imageName")
	void deleteByImageName(String imageName);



}

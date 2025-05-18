package com.example.demo.Repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.demo.model.NewUserModel;
import com.example.demo.model.ProductModel;
import com.example.demo.model.WishlistModel;

public interface WishlistRepo extends JpaRepository<WishlistModel,Integer> {



	boolean existsByProductAndUser(ProductModel product, NewUserModel user);

	 @Query("SELECT w.product FROM WishlistModel w WHERE w.user.id = :userId")
	    List<ProductModel> findProductsByUserId(@Param("userId") Integer userId);

	void deleteByUserAndProduct(NewUserModel user, ProductModel product);

	Optional<WishlistModel> findByUserAndProduct(NewUserModel user, ProductModel product);
}

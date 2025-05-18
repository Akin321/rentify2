package com.example.demo.Repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.model.CouponModel;
import com.example.demo.model.NewUserModel;
import com.example.demo.model.Status;

public interface CouponRepo extends JpaRepository<CouponModel,Integer> {

	Page<CouponModel> findByCouponCodeContainingIgnoreCaseAndStatus(String keyword, Status filter, Pageable pageable);

	List<CouponModel> findByStatus(Status filter);

	Page<CouponModel> findByCouponCodeContainingIgnoreCase(String keyword, Pageable pageable);

	Page<CouponModel> findByStatus(Status filter, Pageable pageable);



	Optional<CouponModel> findByCouponCodeIgnoreCaseAndStatus(String keyword, Status active);

}

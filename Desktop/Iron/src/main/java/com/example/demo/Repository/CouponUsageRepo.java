package com.example.demo.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.model.CouponModel;
import com.example.demo.model.CouponUsage;
import com.example.demo.model.NewUserModel;

public interface CouponUsageRepo extends JpaRepository<CouponUsage,Integer> {

	Optional<CouponUsage> findByUserAndCoupon(NewUserModel user, CouponModel coupon);
	
}

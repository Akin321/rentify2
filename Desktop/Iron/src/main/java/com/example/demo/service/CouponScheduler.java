package com.example.demo.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.example.demo.Repository.CouponRepo;
import com.example.demo.model.CouponModel;
import com.example.demo.model.Status;

@Service
public class CouponScheduler {
	  @Autowired
	    private CouponRepo couponRepo;

	    @Scheduled(cron = "0 0 0 * * *") // Every day at midnight
	    public void updateExpiredCoupons() {
	        List<CouponModel> activeCoupons = couponRepo.findByStatus(Status.Active);
	        LocalDate today = LocalDate.now();

	        for (CouponModel coupon : activeCoupons) {
	            if (coupon.getExpiryDate() != null && coupon.getExpiryDate().isBefore(today)) {
	                coupon.setStatus(Status.Expired); // Set to EXPIRED
	                couponRepo.save(coupon); // Save the coupon to update the status
	            }
	        }
	    }
}

package com.example.demo.Repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.example.demo.model.NewUserModel;
import com.example.demo.model.OrderModel;

public interface OrderRepo extends JpaRepository<OrderModel,Integer>, JpaSpecificationExecutor<OrderModel>{

	List<OrderModel> findByUser(NewUserModel user);

	Page<OrderModel> findByOrderIdContainingIgnoreCaseOrUser_EmailContainingIgnoreCase(String keyword, String keyword2,
			Pageable pageable);

}

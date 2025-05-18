package com.example.demo.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.example.demo.model.OrderItem;

public interface OrderItemRepo extends JpaRepository<OrderItem,Integer> ,JpaSpecificationExecutor<OrderItem>{

}

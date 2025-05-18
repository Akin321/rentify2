package com.example.demo.service;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.example.demo.Repository.OrderItemRepo;
import com.example.demo.Repository.OrderRepo;
import com.example.demo.Repository.ReturnRequestRepo;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.model.OrderItem;
import com.example.demo.model.OrderModel;
import com.example.demo.model.OrderStatus;
import com.example.demo.model.PaymentType;
import com.example.demo.model.ReturnRequest;
import com.example.demo.model.ReturnStatus;

import jakarta.transaction.Transactional;

@Service
public class ReturnService {
	@Autowired
	OrderItemRepo orderItemRepo;
	
	@Autowired
	ReturnRequestRepo returnRequestRepo;
	
	@Autowired
	WalletService walletService;

	public void addRequest(int orderItemId, String reason) {
		// TODO Auto-generated method stub
		OrderItem orderItem=orderItemRepo.findById(orderItemId).orElseThrow(()-> new ResourceNotFoundException("order not found"));
		if(orderItem.getOrderStatus()!=OrderStatus.Delivered) {
			throw new IllegalArgumentException("status is not proper");
		}
		ReturnRequest returnRequest=new ReturnRequest();
		returnRequest.setOrderItem(orderItem);
		returnRequest.setReason(reason);
		returnRequest.setRequestDate(LocalDateTime.now());
		returnRequest.setStatus(ReturnStatus.Pending);
		returnRequestRepo.save(returnRequest);
	}

	public Page<ReturnRequest> getAllRequest(int page, int size) {
		// TODO Auto-generated method stub
		Pageable pageable=PageRequest.of(page, size,Sort.by("requestDate").descending());
		return returnRequestRepo.findAll(pageable);
	}
@Transactional
	public void changeStatus(int requestId) {
		// TODO Auto-generated method stub
		ReturnRequest request=returnRequestRepo.findById(requestId).orElseThrow(()->new ResourceNotFoundException("return id not found"));
		request.setStatus(ReturnStatus.Approved);
		request.setApprovedDate(LocalDateTime.now());
		returnRequestRepo.save(request);
		OrderItem orderItem=request.getOrderItem();
		orderItem.setOrderStatus(OrderStatus.Returned);
		OrderModel order=orderItem.getOrder();
		if(order.getPaymentType()!=PaymentType.CashOnDelivery) {
			walletService.refundAmount(orderItem.getTotalAmount(),order.getUser(),orderItem.getProductName());
		}
		orderItemRepo.save(orderItem);
	}

	public void setrejectStatus(int requestId, String message) {
		// TODO Auto-generated method stub
		ReturnRequest request=returnRequestRepo.findById(requestId).orElseThrow(()->new ResourceNotFoundException("return id not found"));
		request.setStatus(ReturnStatus.Rejected);
		request.setRejectMessage(message);
		returnRequestRepo.save(request);



	}

}

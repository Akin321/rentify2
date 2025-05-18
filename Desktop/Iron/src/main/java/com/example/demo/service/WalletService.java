package com.example.demo.service;

import java.math.BigDecimal;

import org.springframework.data.domain.Pageable;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.example.demo.Repository.OrderItemRepo;
import com.example.demo.Repository.WalletRepo;
import com.example.demo.Repository.WalletTransactionRepo;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.model.NewUserModel;
import com.example.demo.model.OrderItem;
import com.example.demo.model.TransStatus;
import com.example.demo.model.TransType;
import com.example.demo.model.WalletModel;
import com.example.demo.model.WalletTransaction;

import jakarta.transaction.Transactional;

@Service
public class WalletService {
	
	@Autowired
	WalletRepo walletRepo;
	
	@Autowired
	WalletTransactionRepo transactionRepo;
	
	@Autowired
	OrderItemRepo orderItemRepo;

	public WalletModel getWallet(NewUserModel user) {
		// TODO Auto-generated method stub
		Optional<WalletModel> walletOpt=walletRepo.findByUser(user);
		if(walletOpt.isPresent()) {
			return walletOpt.get();
		}
		WalletModel wallet=new WalletModel();
		wallet.setBalance(BigDecimal.ZERO);
		wallet.setUser(user);
		walletRepo.save(wallet);
		return wallet;
	}

	public WalletTransaction addToWallet(NewUserModel user, BigDecimal amount) {
		// TODO Auto-generated method stub
		WalletModel wallet=walletRepo.findByUser(user).orElseThrow(()->new ResourceNotFoundException("wallet not found"));
		WalletTransaction transaction=new WalletTransaction();
		transaction.setDescription("Wallet Top Up");
		transaction.setType(TransType.Credit);
		transaction.setWallet(wallet);
		transaction.setStatus(TransStatus.PENDING);
		transaction.setAmount(amount);
		return transaction;
	}

	public void updateWalletBalance(NewUserModel user, BigDecimal amount) {
		// TODO Auto-generated method stub
		WalletModel wallet = walletRepo.findByUser(user)
		        .orElseThrow(() -> new ResourceNotFoundException("Wallet not found"));
		    wallet.setBalance(wallet.getBalance().add(amount));
		    walletRepo.save(wallet);
	}

	public Page<WalletTransaction> getTransactions(NewUserModel user, int page, int size) {
		// TODO Auto-generated method stub
		WalletModel wallet=walletRepo.findByUser(user).orElseThrow(()->new ResourceNotFoundException("wallet not found"));
		Pageable pageable=PageRequest.of(page, size,Sort.by("createdAt").descending());
		Page<WalletTransaction> transactions=transactionRepo.findByWallet(wallet,pageable);
		return transactions;
	}

	public Pageable buildPageRequest(int page, int size, String sort) {
		// TODO Auto-generated method stub
		 if (sort == null || sort.isEmpty()) {
		        return PageRequest.of(page, size, Sort.by("createdAt").descending());
		    }

		    switch (sort) {
		        case "date_asc": return PageRequest.of(page, size, Sort.by("createdAt").ascending());
		        case "date_desc": return PageRequest.of(page, size, Sort.by("createdAt").descending());
		        case "amount_asc": return PageRequest.of(page, size, Sort.by("amount").ascending());
		        case "amount_desc": return PageRequest.of(page, size, Sort.by("amount").descending());
		        case "type_credit": return PageRequest.of(page, size, Sort.by("type").ascending()); // assuming "credit" < "debit"
		        case "type_debit": return PageRequest.of(page, size, Sort.by("type").descending());
		        default: return PageRequest.of(page, size);
		    }
		
	}

	public Page<WalletTransaction> getFilteredTransactions(NewUserModel user, Pageable pageable) {
		// TODO Auto-generated method stub
		WalletModel wallet=walletRepo.findByUser(user).orElseThrow(()->new ResourceNotFoundException("wallet not found"));
		Page<WalletTransaction> transactions=transactionRepo.findByWallet(wallet,pageable);
		return transactions;
	}
	@Transactional
	public void addRefundAmount(BigDecimal amount, NewUserModel user, int orderId) {
		// TODO Auto-generated method stub
		OrderItem orderItem=orderItemRepo.findById(orderId).orElseThrow(()->new ResourceNotFoundException("orderItem not found"));
		WalletModel wallet=walletRepo.findByUser(user).orElseThrow(()->new ResourceNotFoundException("wallet not found"));
		WalletTransaction transaction=new WalletTransaction();
		transaction.setAmount(amount);
		transaction.setDescription("Refund for cancellation of " + orderItem.getProduct().getName());
		transaction.setStatus(TransStatus.SUCCESS);
		transaction.setType(TransType.Credit);
		transaction.setWallet(wallet);
		transactionRepo.save(transaction);
		wallet.setBalance(wallet.getBalance().add(amount));
		walletRepo.save(wallet);
		
	}

	public void refundAmount(BigDecimal totalAmount, NewUserModel user, String productName) {
		// TODO Auto-generated method stub
		WalletModel wallet=walletRepo.findByUser(user).orElseThrow(()->new ResourceNotFoundException("wallet not found"));
		WalletTransaction transaction=new WalletTransaction();
		transaction.setAmount(totalAmount);
		transaction.setDescription("Refund for return  of  " +productName);
		transaction.setStatus(TransStatus.SUCCESS);
		transaction.setType(TransType.Credit);
		transaction.setWallet(wallet);
		transactionRepo.save(transaction);
		wallet.setBalance(wallet.getBalance().add(totalAmount));
		walletRepo.save(wallet);
	}

	public void setWallet(NewUserModel user) {
		// TODO Auto-generated method stub
		WalletModel wallet=new WalletModel();
		wallet.setBalance(BigDecimal.ZERO);
		wallet.setUser(user);
		walletRepo.save(wallet);
	
	}
	@Transactional
	public void payWithWallet(BigDecimal amount, NewUserModel user) {
		// TODO Auto-generated method stub
		WalletModel wallet=walletRepo.findByUser(user).orElseThrow(()-> new ResourceNotFoundException("wallet not found"));
		if(amount.compareTo(wallet.getBalance())>0) {
			throw new IllegalArgumentException("Not sufficient balande");
		}
		WalletTransaction transaction=new WalletTransaction();
		transaction.setAmount(amount);
		transaction.setDescription("Debited for product purchase");
		transaction.setStatus(TransStatus.SUCCESS);
		transaction.setType(TransType.Debit);
		transaction.setWallet(wallet);
		transactionRepo.save(transaction);
		wallet.setBalance(wallet.getBalance().subtract(amount));
		walletRepo.save(wallet);	}



}

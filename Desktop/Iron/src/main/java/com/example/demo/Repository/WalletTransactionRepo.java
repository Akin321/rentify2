package com.example.demo.Repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.model.NewUserModel;
import com.example.demo.model.WalletModel;
import com.example.demo.model.WalletTransaction;

public interface WalletTransactionRepo extends JpaRepository<WalletTransaction,Integer> {

	Page<WalletTransaction> findByWallet(WalletModel wallet, Pageable pageable);


}

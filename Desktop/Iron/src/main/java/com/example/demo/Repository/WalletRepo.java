package com.example.demo.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.model.NewUserModel;
import com.example.demo.model.WalletModel;

public interface WalletRepo extends JpaRepository<WalletModel,Integer> {

	Optional<WalletModel> findByUser(NewUserModel user);

}

package com.example.demo.Repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.model.NewUserModel;
import com.example.demo.model.Role;

@Repository
public interface UserRepo extends JpaRepository<NewUserModel,Integer> {
	Optional<NewUserModel> findByEmail(String email);
	
	Page<NewUserModel> findByRoleAndNameContainingIgnoreCaseOrRoleAndEmailContainingIgnoreCase(
		     Role role1,String name,Role role2, String email, Pageable pageable);
	Page<NewUserModel> findByRole(Role role, Pageable pageable);

	boolean existsByEmail(String email);

	NewUserModel findByReferralToken(String refToken);

}

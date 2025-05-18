package com.example.demo.Repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.model.CollectionModel;

public interface CollectionRepo extends JpaRepository<CollectionModel,Integer>{

	Optional<CollectionModel> findByName(String name);

	Page<CollectionModel> findByNameContainingIgnoreCase(String keyword, Pageable pageable);

	Optional<CollectionModel> findByNameIgnoreCase(String name);

}

package com.example.demo.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.model.ReturnRequest;

public interface ReturnRequestRepo extends JpaRepository<ReturnRequest,Integer> {

}

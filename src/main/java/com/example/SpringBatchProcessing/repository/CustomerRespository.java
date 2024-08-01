package com.example.SpringBatchProcessing.repository;

import com.example.SpringBatchProcessing.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.io.Serializable;

public interface CustomerRespository extends JpaRepository<Customer, Serializable> {
}

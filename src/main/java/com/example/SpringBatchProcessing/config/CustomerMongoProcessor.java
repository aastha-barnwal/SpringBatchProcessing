package com.example.SpringBatchProcessing.config;

import com.example.SpringBatchProcessing.entity.Customer;
import com.example.SpringBatchProcessing.entity.CustomerWriter;
import jakarta.persistence.Column;
import org.springframework.batch.item.ItemProcessor;

public class CustomerMongoProcessor implements ItemProcessor<CustomerWriter, Customer> {
//    private int id;
//    @Column(name = "FIRST_NAME")
//    private String firstName;
//    @Column(name = "LAST_NAME")
//    private String lastName;
//    @Column(name = "EMAIL")
//    private String email;
//    @Column(name = "GENDER")
//    private String gender;
//    @Column(name = "CONTACT")
//    private String contactNo;
//    @Column(name = "COUNTRY")
//    private String country;
//    @Column(name = "DOB")
//    private String dob;
//    @Column(name = "FULL_NAME")
//    private String fullname;

    @Override
    public Customer process(CustomerWriter item) throws Exception {
        if(item.getGender().equalsIgnoreCase("male")){
            return new Customer(item.getId(),item.getFirstName(),item.getLastName(), item.getEmail(),item.getGender(), item.getContactNo(), item.getCountry(),
                    item.getDob(), item.getFullname());
        }
        return null;
    }
}

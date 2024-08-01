package com.example.SpringBatchProcessing.config;

import com.example.SpringBatchProcessing.entity.Customer;
import com.example.SpringBatchProcessing.entity.CustomerWriter;
import lombok.Data;
import org.springframework.batch.item.ItemProcessor;

import java.time.LocalDate;

public class CustomerProcessor implements ItemProcessor<CustomerWriter, CustomerWriter> {

    public CustomerWriter process(CustomerWriter item) throws Exception{
//        logic
        if (item != null) {
            // Concatenate first name and last name
            String fullName = item.getFirstName() + " " + item.getLastName();
//            LocalDate currentDate = LocalDate.now();
//            // Extract the year from the current date
//            int current_year = currentDate.getYear();
//            int old_year = Integer.parseInt(item.getDob().substring(6));
//            int age = current_year-old_year;
//            item.setFullname(fullName);
//            if(age < 40){
                return item;
            }
//        }

        return null;
    }

}

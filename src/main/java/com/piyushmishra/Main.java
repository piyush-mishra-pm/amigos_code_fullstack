package com.piyushmishra;

import com.piyushmishra.customer.Customer;
import com.piyushmishra.customer.CustomerRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.List;

@SpringBootApplication
public class Main {
    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }

    @Bean
    CommandLineRunner runner(CustomerRepository customerRepository){
        return args -> customerRepository.saveAll(
                List.of(new Customer("Piyush","p@g.com",21),
                        new Customer("Skyrim","s@k.rim",12)));
    }
}

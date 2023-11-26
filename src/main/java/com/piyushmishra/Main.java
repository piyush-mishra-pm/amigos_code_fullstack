package com.piyushmishra;

import com.github.javafaker.Faker;
import com.piyushmishra.customer.Customer;
import com.piyushmishra.customer.CustomerRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.Random;

@SpringBootApplication
public class Main {
    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }

    @Bean
    CommandLineRunner runner(CustomerRepository customerRepository){
        return args -> {
            Faker faker = new Faker();
            Random random = new Random();
            customerRepository.save(
                    new Customer(
                        faker.name().fullName(),
                        faker.internet().emailAddress(),
                            random.nextInt(12,65)
                    )
            );
        };
    }
}

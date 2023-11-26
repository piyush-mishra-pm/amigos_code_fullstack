package com.piyushmishra.customer;

import com.piyushmishra.exception.DuplicateResourceException;
import com.piyushmishra.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomerService {
    private final CustomerDao customerDao;

    public CustomerService(@Qualifier("jpa") CustomerDao customerDao) {
        this.customerDao = customerDao;
    }

    public List<Customer> getAllCustomers(){
        return customerDao.selectAllCustomers();
    }

    public Customer getCustomerById(Integer id){
        return customerDao.selectCustomerById(id)
                .orElseThrow(()->new ResourceNotFoundException("customer id="+id+" doesnt exist!"));
    }

    public void addCustomer(CustomerRegistrationRequest customerRegistrationRequest) throws DuplicateResourceException {
        // Check if Email already exists:
        String email = customerRegistrationRequest.email();
        if (customerDao.existsCustomerWithEmail(email)) {
            throw new DuplicateResourceException("Email already exists.");
        }

        // Add user (if email doesn't exist already)
        Customer newCustomer = new Customer(
                customerRegistrationRequest.name(),
                customerRegistrationRequest.email(),
                customerRegistrationRequest.age()
        );
        customerDao.insertCustomer(newCustomer);
    }

    public void removeCustomer(Integer customerId) {
        if(!customerDao.existsCustomerById(customerId)){
            throw new ResourceNotFoundException("Customer with id=%s doesnt exist.".formatted(customerId));
        }
        customerDao.removeCustomer(customerId);
    }
}

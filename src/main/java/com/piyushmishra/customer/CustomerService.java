package com.piyushmishra.customer;

import com.piyushmishra.exception.BadRequestException;
import com.piyushmishra.exception.DuplicateResourceException;
import com.piyushmishra.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomerService {
    private final CustomerDao customerDao;

    public CustomerService(@Qualifier("list") CustomerDao customerDao) {
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
        emailAlreadyExistsThenThrowException(email);

        // Add user (if email doesn't exist already)
        Customer newCustomer = new Customer(
                customerRegistrationRequest.name(),
                customerRegistrationRequest.email(),
                customerRegistrationRequest.age()
        );
        customerDao.insertCustomer(newCustomer);
    }

    private void emailAlreadyExistsThenThrowException(String email) {
        if (customerDao.existsCustomerWithEmail(email)) {
            throw new DuplicateResourceException("Email already exists.");
        }
    }

    public void removeCustomer(Integer customerId) {
        if(!customerDao.existsCustomerById(customerId)){
            throw new ResourceNotFoundException("Customer with id=%s doesnt exist.".formatted(customerId));
        }
        customerDao.removeCustomer(customerId);
    }

    public void updateCustomer(Integer customerId, CustomerRegistrationRequest request) {
        Customer existingCustomer = getCustomerById(customerId);

        boolean changed = false;
        if (isValidValue(request.name()) && !request.name().equals(existingCustomer.getName())) {
            existingCustomer.setName(request.name());
            changed = true;
        }
        if (request.age() != null && request.age() > 0 && !request.age().equals(existingCustomer.getAge())) {
            existingCustomer.setAge(request.age());
            changed = true;
        }
        if (isValidValue(request.email()) && !request.email().equals(existingCustomer.getEmail())) {
            emailAlreadyExistsThenThrowException(request.email());
            existingCustomer.setEmail(request.email());
            changed = true;
        }

        if (!changed) {
            throw new BadRequestException("No updates for Customer, as no prop different.");
        }

        customerDao.updateCustomer(existingCustomer);
    }

    private boolean isValidValue(String value) {
        return value != null && !value.isEmpty() && !value.isBlank();
    }
}

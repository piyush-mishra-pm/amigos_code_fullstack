package com.piyushmishra.customer;

import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository("list")
public class CustomerListDataAccessService implements CustomerDao{
    private static final List<Customer> customersDb =  new ArrayList<>();
    static {
        customersDb.add(new Customer(1,"Piyush","p@g.com",21));
        customersDb.add(new Customer(2,"Skyrim","s@k.rim",12));
    }
    @Override
    public List<Customer> selectAllCustomers() {
        return customersDb;
    }

    @Override
    public Optional<Customer> selectCustomerById(Integer id) {
        return customersDb.stream()
                .filter(c -> c.getId().equals(id))
                .findFirst();
    }

    @Override
    public void insertCustomer(Customer customer) {
        customersDb.add(customer);
    }

    @Override
    public boolean existsCustomerWithEmail(String email) {
        return customersDb.stream().anyMatch(customer -> customer.getEmail().equalsIgnoreCase(email));
    }

    @Override
    public boolean existsCustomerById(Integer customerId) {
        return customersDb.stream().anyMatch(customer -> customer.getId().equals(customerId));
    }

    @Override
    public void removeCustomer(Integer customerId) {
        customersDb.removeIf(customer -> customer.getId().equals(customerId));
    }
}

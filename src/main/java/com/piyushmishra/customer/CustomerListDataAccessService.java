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
}

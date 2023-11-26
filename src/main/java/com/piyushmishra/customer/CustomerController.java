package com.piyushmishra.customer;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/customers")
public class CustomerController {

    final private CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @GetMapping
    private List<Customer> getCustomers(){
        return customerService.getAllCustomers();
    }

    @GetMapping("/{customerId}")
    private Customer getCustomerById(@PathVariable("customerId") Integer customerId){
        return customerService.getCustomerById(customerId);
    }

    @PostMapping
    private void registerCustomer(@RequestBody CustomerRegistrationRequest request){
        customerService.addCustomer(request);
    }
}

package com.piyushmishra.customer;

import com.github.javafaker.Faker;
import com.piyushmishra.AbstractTestContainers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

class CustomerJPADataAccessServiceTest {

    private CustomerJPADataAccessService underTest;
    private AutoCloseable autoCloseable;
    @Mock
    private CustomerRepository customerRepository;

    @BeforeEach
    void setUp() {
        autoCloseable = MockitoAnnotations.openMocks(this);
        underTest = new CustomerJPADataAccessService(customerRepository);
    }

    @AfterEach
    void tearDown() throws Exception {
        autoCloseable.close();
    }

    @Test
    void selectAllCustomers() {
        // when
        underTest.selectAllCustomers();

        // then
        Mockito.verify(customerRepository).findAll();
    }

    @Test
    void selectCustomerById() {
        // given
        int id=1;

        // when
        underTest.selectCustomerById(id);

        // then
        Mockito.verify(customerRepository).findById(id);
    }

    @Test
    void insertCustomer() {
        // given
        Customer customer = AbstractTestContainers.getFakerCustomer();

        // when
        underTest.insertCustomer(customer);

        // then
        Mockito.verify(customerRepository).save(customer);
    }

    @Test
    void existsCustomerWithEmail() {
        String email = new Faker().internet().emailAddress();
        // when
        underTest.existsCustomerWithEmail(email);

        // then
        Mockito.verify(customerRepository).existsCustomerByEmail(email);
    }

    @Test
    void existsCustomerById() {
        int id = 1;
        // when
        underTest.existsCustomerById(id);

        // then
        Mockito.verify(customerRepository).existsById(id);
    }

    @Test
    void removeCustomer() {
        // given
        int id = 1;

        // when
        underTest.removeCustomer(id);

        // then
        Mockito.verify(customerRepository).deleteById(id);
    }

    @Test
    void updateCustomer() {
        // given
        Customer customer = AbstractTestContainers.getFakerCustomer();

        // when
        underTest.updateCustomer(customer);

        // then
        Mockito.verify(customerRepository).save(customer);
    }
}
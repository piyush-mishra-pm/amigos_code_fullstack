package com.piyushmishra.customer;

import com.piyushmishra.AbstractTestContainers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class CustomerJDBCDataAccessServiceTest extends AbstractTestContainers {

    private CustomerJDBCDataAccessService underTest;
    private final CustomerRowMapper customerRowMapper = new CustomerRowMapper();

    @BeforeEach
    void setUp() {
        underTest = new CustomerJDBCDataAccessService(
                getJdbcTemplate(),
                customerRowMapper
        );
    }

    @Test
    void selectAllCustomers() {
        // given
        final Customer customer = new Customer(
                FAKER.name().fullName(),
                FAKER.number().numberBetween(0, 1000) + FAKER.internet().emailAddress(),
                FAKER.number().numberBetween(12,90)
        );
        underTest.insertCustomer(customer);

        // when
        List<Customer> customers = underTest.selectAllCustomers();

        // then
        assertThat(customers).isNotEmpty();
    }

    @Test
    void selectCustomerById() {
        // given
        final String email = FAKER.number().numberBetween(0, 1000) + FAKER.internet().emailAddress();
        final String name = FAKER.name().fullName();
        final int age = FAKER.number().numberBetween(12,90);
        final Customer customer = new Customer(name, email, age);

        underTest.insertCustomer(customer);
        int customerId = underTest.selectAllCustomers().stream().filter(c->c.getEmail().equalsIgnoreCase(email)).findFirst().orElseThrow().getId();

        // when
        var actualCustomer = underTest.selectCustomerById(customerId);

        // then
        assertThat(actualCustomer).isPresent().hasValueSatisfying(c->{
            assertThat(c.getId()).isEqualTo(customerId);
            assertThat(c.getName()).isEqualTo(name);
            assertThat(c.getEmail()).isEqualTo(email);
        });
    }

    @Test
    void willResultEmptyWhenSelectCustomerByInvalidId() {
        // given
        // Since IDs sequence start from 1, so ID can never be negative.
        int id = -1;

        // when
        var actualCustomer = underTest.selectCustomerById(id);

        // then
        assertThat(actualCustomer).isEmpty();
    }

    @Test
    void insertCustomer() {

    }

    @Test
    void existsCustomerWithEmail() {
        // given

        // then

        // when

    }

    @Test
    void existsCustomerById() {
        // given

        // then

        // when

    }

    @Test
    void removeCustomer() {
        // given

        // then

        // when

    }

    @Test
    void updateCustomer() {
        // given

        // then

        // when

    }
}
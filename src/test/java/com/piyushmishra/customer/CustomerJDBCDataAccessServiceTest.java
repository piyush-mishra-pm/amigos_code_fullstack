package com.piyushmishra.customer;

import com.piyushmishra.AbstractTestContainers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

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
        final Customer customer = getFakerCustomer();
        underTest.insertCustomer(customer);

        // when
        List<Customer> customers = underTest.selectAllCustomers();

        // then
        assertThat(customers).isNotEmpty();
    }

    @Test
    void selectCustomerById() {
        // given
        final Customer customer = getFakerCustomer();
        underTest.insertCustomer(customer);
        int customerId = getCustomerId(customer);

        // when
        var actualCustomer = underTest.selectCustomerById(customerId);

        // then
        assertThat(actualCustomer).isPresent().hasValueSatisfying(c->{
            assertThat(c.getId()).isEqualTo(customerId);
            assertThat(c.getName()).isEqualTo(customer.getName());
            assertThat(c.getEmail()).isEqualTo(customer.getEmail());
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
        // given
        Customer customer = getFakerCustomer();

        // when
        underTest.insertCustomer(customer);
        Optional<Customer> insertedCustomer = underTest
                .selectAllCustomers()
                .stream()
                .filter(c -> c.getEmail().equalsIgnoreCase(customer.getEmail()))
                .findFirst();

        // then
        assertThat(insertedCustomer).isPresent().hasValueSatisfying(c -> {
            assertThat(c.getName()).isEqualTo(customer.getName());
            assertThat(c.getEmail()).isEqualTo(customer.getEmail());
            assertThat(c.getAge()).isEqualTo(customer.getAge());
        });
    }

    @Test
    void existsCustomerWithEmail() {
        // given
        Customer customer = getFakerCustomer();
        underTest.insertCustomer(customer);

        // when
        boolean present = underTest.existsCustomerWithEmail(customer.getEmail());

        // then
        assertThat(present).isTrue();
    }

    @Test
    void notExistsCustomerWithEmailIfNotInserted() {
        // given
        Customer customer = getFakerCustomer();

        // when
        boolean present = underTest.existsCustomerWithEmail(customer.getEmail());

        // then
        assertThat(present).isFalse();
    }

    @Test
    void existsCustomerById() {
        // given
        Customer customer = getFakerCustomer();
        underTest.insertCustomer(customer);
        int id = getCustomerId(customer);

        // when
        boolean present = underTest.existsCustomerById(id);

        // then
        assertThat(present).isTrue();
    }

    @Test
    void notExistsCustomerByIdIfNotInserted() {
        // given
        // We know -1 id will not exist in DB.
        int id=-1;

        // when
        boolean actual = underTest.existsCustomerById(id);

        // then
        assertThat(actual).isFalse();
    }

    @Test
    void removeCustomer() {
        // given
        Customer customer = getFakerCustomer();
        underTest.insertCustomer(customer);
        int id = getCustomerId(customer);

        // when
        underTest.removeCustomer(id);
        boolean actual = underTest.existsCustomerById(id);

        // then
        assertThat(actual).isFalse();
    }

    @Test
    void updateCustomerNameOnly() {
        // given
        Customer customer = getFakerCustomer();
        underTest.insertCustomer(customer);
        int id = getCustomerId(customer);
        String newName = "foo";

        // when (id, age, email kept same):
        Customer update = new Customer();
        update.setId(id);
        update.setName(newName); // change

        underTest.updateCustomer(update);

        // then
        Optional<Customer> actual = underTest.selectCustomerById(id);

        assertThat(actual).isPresent().hasValueSatisfying(c -> {
            assertThat(c.getId()).isEqualTo(id);
            assertThat(c.getName()).isEqualTo(newName); // change
            assertThat(c.getEmail()).isEqualTo(customer.getEmail());
            assertThat(c.getAge()).isEqualTo(customer.getAge());
        });
    }

    @Test
    void updateCustomerEmailOnly() {
        // given
        Customer customer = getFakerCustomer();
        underTest.insertCustomer(customer);
        int id = getCustomerId(customer);
        String newEmail = "foo";

        // when (id, age, email kept same):
        Customer update = new Customer();
        update.setId(id);
        update.setEmail(newEmail); // change

        underTest.updateCustomer(update);

        // then
        Optional<Customer> actual = underTest.selectCustomerById(id);

        assertThat(actual).isPresent().hasValueSatisfying(c -> {
            assertThat(c.getId()).isEqualTo(id);
            assertThat(c.getName()).isEqualTo(customer.getName());
            assertThat(c.getEmail()).isEqualTo(newEmail); // change
            assertThat(c.getAge()).isEqualTo(customer.getAge());
        });
    }

    @Test
    void updateCustomerAgeOnly() {
        // given
        Customer customer = getFakerCustomer();
        underTest.insertCustomer(customer);
        int id = getCustomerId(customer);
        int newAge = FAKER.number().numberBetween(10,90);

        // when (id, age, email kept same):
        Customer update = new Customer();
        update.setId(id);
        update.setAge(newAge);

        underTest.updateCustomer(update);

        // then
        Optional<Customer> actual = underTest.selectCustomerById(id);

        assertThat(actual).isPresent().hasValueSatisfying(c -> {
            assertThat(c.getId()).isEqualTo(id);
            assertThat(c.getName()).isEqualTo(customer.getName());
            assertThat(c.getEmail()).isEqualTo(customer.getEmail());
            assertThat(c.getAge()).isEqualTo(newAge); // change
        });
    }

    @Test
    void updateCustomerAllProps() {
        // given
        Customer customer = getFakerCustomer();
        underTest.insertCustomer(customer);
        int id = getCustomerId(customer);
        int newAge = FAKER.number().numberBetween(10,90);
        String newName = FAKER.name().fullName();
        String newEmail = FAKER.number().numberBetween(0, 1000) + FAKER.internet().emailAddress();

        // when (id, age, email kept same):
        Customer update = new Customer();
        update.setId(id);
        update.setName(newName);
        update.setEmail(newEmail);
        update.setAge(newAge);

        underTest.updateCustomer(update);

        // then
        Optional<Customer> actual = underTest.selectCustomerById(id);

        assertThat(actual).isPresent().hasValueSatisfying(c -> {
            assertThat(c.getId()).isEqualTo(id);
            assertThat(c.getName()).isEqualTo(newName);
            assertThat(c.getEmail()).isEqualTo(newEmail);
            assertThat(c.getAge()).isEqualTo(newAge);
        });
    }

    @Test
    void willNotUpdateWhenNothingToUpdate() {
        // given
        Customer customer = getFakerCustomer();
        underTest.insertCustomer(customer);
        int id = getCustomerId(customer);

        // when (id, age, email kept same):
        Customer update = new Customer();
        update.setId(id);

        underTest.updateCustomer(update);

        // then
        Optional<Customer> actual = underTest.selectCustomerById(id);

        assertThat(actual).isPresent().hasValueSatisfying(c -> {
            assertThat(c.getId()).isEqualTo(id);
            assertThat(c.getName()).isEqualTo(customer.getName());
            assertThat(c.getEmail()).isEqualTo(customer.getEmail());
            assertThat(c.getAge()).isEqualTo(customer.getAge());
        });
    }

    private Customer getFakerCustomer() {
        return new Customer(
                FAKER.name().fullName(),
                FAKER.number().numberBetween(0, 1000) + FAKER.internet().emailAddress(),
                FAKER.number().numberBetween(12, 90)
        );
    }

    private int getCustomerId(Customer customer) {
        return underTest
                .selectAllCustomers()
                .stream()
                .filter(c -> c.getEmail().equalsIgnoreCase(customer.getEmail()))
                .findFirst().orElseThrow().getId();
    }
}
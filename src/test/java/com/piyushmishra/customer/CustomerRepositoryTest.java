package com.piyushmishra.customer;

import com.piyushmishra.AbstractTestContainers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class CustomerRepositoryTest extends AbstractTestContainers {

    @Autowired
    private CustomerRepository underTest;

    @BeforeEach
    void setUp() {
        // In main, we are explicitly adding a customer in DB.
        underTest.deleteAll();
    }

    @Test
    void existsCustomerByEmail() {
        // given
        Customer customer = getFakerCustomer();
        underTest.save(customer);

        // when
        boolean present = underTest.existsCustomerByEmail(customer.getEmail());

        // then
        assertThat(present).isTrue();
    }

    @Test
    void notExistsCustomerByEmail() {
        // given
        String randomEmail = FAKER.number().numberBetween(0, 1000) + FAKER.internet().emailAddress();

        // when
        boolean present = underTest.existsCustomerByEmail(randomEmail);

        // then
        assertThat(present).isFalse();
    }
}
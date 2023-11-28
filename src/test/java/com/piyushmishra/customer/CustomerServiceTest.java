package com.piyushmishra.customer;

import com.piyushmishra.AbstractTestContainers;
import com.piyushmishra.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @Mock
    private CustomerDao customerDao;
    private CustomerService underTest;
    @BeforeEach
    void setUp() {
        underTest = new CustomerService(customerDao);
    }

    @Test
    void getAllCustomers() {
        // when
        underTest.getAllCustomers();

        // then
        Mockito.verify(customerDao).selectAllCustomers();
    }

    @Test
    void canGetCustomerById() {
        // given
        int id = 1;
        Customer customer = AbstractTestContainers.getFakerCustomer();
        Mockito.when(customerDao.selectCustomerById(id)).thenReturn(Optional.of(customer));

        // when
        Customer actual = underTest.getCustomerById(id);

        // then
        assertThat(actual).isEqualTo(customer);
    }

    @Test
    void willThrowWhenGetCustomerByIdReturnsEmptyOptional() {
        // given
        int id = -1;
        Mockito.when(customerDao.selectCustomerById(id)).thenReturn(Optional.empty());

        // when, then
        assertThatThrownBy(() -> underTest.getCustomerById(id))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("customer id=" + id + " doesnt exist!");
    }

    @Test
    void addCustomer() {
        // given

        // when

        // then

    }

    @Test
    void removeCustomer() {
        // given

        // when

        // then

    }

    @Test
    void updateCustomer() {
        // given

        // when

        // then

    }
}
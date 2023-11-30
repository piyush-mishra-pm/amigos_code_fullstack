package com.piyushmishra.journey;

import com.github.javafaker.Faker;
import com.piyushmishra.customer.Customer;
import com.piyushmishra.customer.CustomerRegistrationRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CustomerIT {
    @Autowired
    private WebTestClient webTestClient;
    private static final String CUSTOMERS_API_URI = "/api/v1/customers";

    @Test
    void canRegisterACustomer() {
        // construct request body
        Faker faker = new Faker();
        CustomerRegistrationRequest customerRegistrationRequest = new CustomerRegistrationRequest(
                faker.name().fullName(),
                UUID.randomUUID() + faker.name().firstName() + "@integration-test.com",
                faker.number().numberBetween(10,90)
        );

        // send a Post request
        webTestClient.post().uri(CUSTOMERS_API_URI)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(customerRegistrationRequest), CustomerRegistrationRequest.class)
                .exchange()
                .expectStatus()
                .isOk();

        // get all customers:
        List<Customer> allCustomers = webTestClient.get().uri(CUSTOMERS_API_URI)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(new ParameterizedTypeReference<Customer>(){})
                .returnResult()
                .getResponseBody();

        // Ensure that the customer is present:
        Customer expectedCustomer = new Customer(
                customerRegistrationRequest.name(),
                customerRegistrationRequest.email(),
                customerRegistrationRequest.age()
        );

        assertThat(allCustomers)
                .usingRecursiveFieldByFieldElementComparatorIgnoringFields("id")
                .contains(expectedCustomer);

        // get customer by ID:
        assert allCustomers != null;
        int id = allCustomers.stream().filter(c->c.getEmail().equalsIgnoreCase(expectedCustomer.getEmail())).findFirst().orElseThrow().getId();
        expectedCustomer.setId(id);

        webTestClient.get().uri(CUSTOMERS_API_URI+"/{id}",expectedCustomer.getId())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(new ParameterizedTypeReference<Customer>(){})
                .isEqualTo(expectedCustomer);
    }

    @Test
    void canDeleteCustomer() {
        // create registration request
        Faker faker = new Faker();

        CustomerRegistrationRequest request = new CustomerRegistrationRequest(
                faker.name().fullName(),
                UUID.randomUUID()+faker.internet().emailAddress(),
                faker.number().numberBetween(10,90)
        );

        // send a post request to create customer 1
        webTestClient.post()
                .uri(CUSTOMERS_API_URI)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(request), CustomerRegistrationRequest.class)
                .exchange()
                .expectStatus()
                .isOk();

        // get all customers
        List<Customer> allCustomers = webTestClient.get()
                .uri(CUSTOMERS_API_URI)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(new ParameterizedTypeReference<Customer>() {
                })
                .returnResult()
                .getResponseBody();


        assert allCustomers != null;
        int id = allCustomers.stream()
                .filter(customer -> customer.getEmail().equals(request.email()))
                .map(Customer::getId)
                .findFirst()
                .orElseThrow();

        // customer 2 deletes customer 1
        webTestClient.delete()
                .uri(CUSTOMERS_API_URI + "/{id}", id)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk();

        // customer 2 gets customer 1 by id
        webTestClient.get()
                .uri(CUSTOMERS_API_URI + "/{id}", id)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isNotFound();
    }

    @Test
    void canUpdateCustomer() {
        // create registration request
        Faker faker = new Faker();
        CustomerRegistrationRequest request = new CustomerRegistrationRequest(
                faker.name().fullName(),
                UUID.randomUUID()+faker.internet().emailAddress(),
                faker.number().numberBetween(10,90)
        );

        // send a post request
        webTestClient.post()
                .uri(CUSTOMERS_API_URI)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(request), CustomerRegistrationRequest.class)
                .exchange()
                .expectStatus()
                .isOk();


        // get all customers
        List<Customer> allCustomers = webTestClient.get()
                .uri(CUSTOMERS_API_URI)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(new ParameterizedTypeReference<Customer>() {
                })
                .returnResult()
                .getResponseBody();


        assert allCustomers != null;
        int id = allCustomers.stream()
                .filter(customer -> customer.getEmail().equals(request.email()))
                .map(Customer::getId)
                .findFirst()
                .orElseThrow();

        // update customer (name only):
        String newName = "Ali";

        CustomerRegistrationRequest updateRequest = new CustomerRegistrationRequest(
                newName, null, null
        );

        webTestClient.put()
                .uri(CUSTOMERS_API_URI + "/{id}", id)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(updateRequest), CustomerRegistrationRequest.class)
                .exchange()
                .expectStatus()
                .isOk();

        // get customer by id
        Customer updatedCustomer = webTestClient.get()
                .uri(CUSTOMERS_API_URI + "/{id}", id)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(Customer.class)
                .returnResult()
                .getResponseBody();

        Customer expected = new Customer(
                id,
                newName,
                request.email(),
                request.age()
        );

        assertThat(updatedCustomer).isEqualTo(expected);
    }
}

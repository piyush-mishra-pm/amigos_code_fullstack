package com.piyushmishra.customer;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository("jdbc")
public class CustomerJDBCDataAccessService implements CustomerDao {

    private final JdbcTemplate jdbcTemplate;
    private final CustomerRowMapper customerRowMapper;

    public CustomerJDBCDataAccessService(JdbcTemplate jdbcTemplate, CustomerRowMapper customerRowMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.customerRowMapper = customerRowMapper;
    }

    @Override
    public List<Customer> selectAllCustomers() {
        var sql = """
                SELECT id, name, email, age
                FROM customer
                """;
        return jdbcTemplate.query(sql, customerRowMapper);
    }

    @Override
    public Optional<Customer> selectCustomerById(Integer id) {
        var sql = """
                SELECT id, name, email, age
                FROM customer
                WHERE id = ?
                """;
        return jdbcTemplate.query(sql, customerRowMapper, id).stream().findFirst();
    }

    @Override
    public void insertCustomer(Customer customer) {
        var sql = """
                INSERT INTO customer(name,email,age)
                VALUES (?, ?, ?)
                """;
        jdbcTemplate.update(
                sql,
                customer.getName(),
                customer.getEmail(),
                customer.getAge()
        );
    }

    @Override
    public boolean existsCustomerWithEmail(String email) {
        var sql = """
                SELECT count(*)
                FROM customer
                WHERE email = ?
                """;
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, email);
        return count != null && count > 0;
    }

    @Override
    public boolean existsCustomerById(Integer customerId) {
        return selectCustomerById(customerId).isPresent();
    }

    @Override
    public void removeCustomer(Integer customerId) {
        var sql = """
                DELETE
                FROM customer
                WHERE id = ?
                """;
        jdbcTemplate.update(sql, customerId);
    }

    @Override
    public void updateCustomer(Customer updatedCustomer) {
        if (updatedCustomer.getName() != null) {
            jdbcTemplate.update(
                    "UPDATE customer SET name = ? WHERE id = ?",
                    updatedCustomer.getName(),
                    updatedCustomer.getId()
            );
        }
        if (updatedCustomer.getAge() != null) {
            jdbcTemplate.update(
                    "UPDATE customer SET age = ? WHERE id = ?",
                    updatedCustomer.getAge(),
                    updatedCustomer.getId()
            );
        }
        if (updatedCustomer.getEmail() != null) {
            jdbcTemplate.update(
                    "UPDATE customer SET email = ? WHERE id = ?",
                    updatedCustomer.getEmail(),
                    updatedCustomer.getId());
        }
    }
}

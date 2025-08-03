package com.example.graphqldgstrino.service;

import com.example.graphqldgstrino.model.Customer;
import com.example.graphqldgstrino.model.Address;
import com.example.graphqldgstrino.model.Order;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class CustomerService {

    private final Map<String, Customer> customers = new HashMap<>();

    public CustomerService() {
        initializeMockData();
    }

    private void initializeMockData() {
        Address address1 = new Address("123 Main St", "New York", "NY", "10001", "USA");
        Address address2 = new Address("456 Oak Ave", "Los Angeles", "CA", "90210", "USA");
        Address address3 = new Address("789 Pine Rd", "Chicago", "IL", "60601", "USA");

        Customer customer1 = new Customer("1", "John", "Doe", "john.doe@email.com", "+1-555-0101", address1);
        Customer customer2 = new Customer("2", "Jane", "Smith", "jane.smith@email.com", "+1-555-0102", address2);
        Customer customer3 = new Customer("3", "Bob", "Johnson", "bob.johnson@email.com", "+1-555-0103", address3);

        customers.put("1", customer1);
        customers.put("2", customer2);
        customers.put("3", customer3);
    }

    public List<Customer> getAllCustomers() {
        return new ArrayList<>(customers.values());
    }

    public Customer getCustomerById(String id) {
        return customers.get(id);
    }

    public List<Customer> getCustomersByLocation(String city) {
        return customers.values().stream()
            .filter(customer -> customer.getAddress() != null && 
                    customer.getAddress().getCity().equalsIgnoreCase(city))
            .collect(Collectors.toList());
    }

    public Customer createCustomer(String firstName, String lastName, String email, String phone, Map<String, Object> addressInput) {
        String id = UUID.randomUUID().toString();
        
        Address address = null;
        if (addressInput != null) {
            address = new Address(
                (String) addressInput.get("street"),
                (String) addressInput.get("city"),
                (String) addressInput.get("state"),
                (String) addressInput.get("zipCode"),
                (String) addressInput.get("country")
            );
        }
        
        Customer customer = new Customer(id, firstName, lastName, email, phone, address);
        customers.put(id, customer);
        return customer;
    }

    public Customer updateCustomer(String id, Map<String, Object> input) {
        Customer customer = customers.get(id);
        if (customer == null) {
            return null;
        }

        if (input.containsKey("firstName")) {
            customer.setFirstName((String) input.get("firstName"));
        }
        if (input.containsKey("lastName")) {
            customer.setLastName((String) input.get("lastName"));
        }
        if (input.containsKey("email")) {
            customer.setEmail((String) input.get("email"));
        }
        if (input.containsKey("phone")) {
            customer.setPhone((String) input.get("phone"));
        }
        if (input.containsKey("address")) {
            @SuppressWarnings("unchecked")
            Map<String, Object> addressInput = (Map<String, Object>) input.get("address");
            if (addressInput != null) {
                Address address = new Address(
                    (String) addressInput.get("street"),
                    (String) addressInput.get("city"),
                    (String) addressInput.get("state"),
                    (String) addressInput.get("zipCode"),
                    (String) addressInput.get("country")
                );
                customer.setAddress(address);
            }
        }

        return customer;
    }

    public List<Order> getOrdersByCustomerId(String customerId) {
        // Mock implementation - in real app, this would query the database
        return new ArrayList<>();
    }
}
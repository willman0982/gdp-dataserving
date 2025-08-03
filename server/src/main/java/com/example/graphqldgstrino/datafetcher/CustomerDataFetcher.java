package com.example.graphqldgstrino.datafetcher;

import com.example.graphqldgstrino.model.Customer;
import com.example.graphqldgstrino.model.Order;
import com.example.graphqldgstrino.service.CustomerService;
import com.netflix.graphql.dgs.DgsComponent;
import com.netflix.graphql.dgs.DgsData;
import com.netflix.graphql.dgs.DgsDataFetchingEnvironment;
import com.netflix.graphql.dgs.InputArgument;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;

@DgsComponent
public class CustomerDataFetcher {

    @Autowired
    private CustomerService customerService;

    @DgsData(parentType = "Query", field = "customers")
    public List<Customer> customers() {
        return customerService.getAllCustomers();
    }

    @DgsData(parentType = "Query", field = "customer")
    public Customer customer(@InputArgument String id) {
        return customerService.getCustomerById(id);
    }

    @DgsData(parentType = "Query", field = "customersByLocation")
    public List<Customer> customersByLocation(@InputArgument String city) {
        return customerService.getCustomersByLocation(city);
    }

    @DgsData(parentType = "Mutation", field = "createCustomer")
    public Customer createCustomer(@InputArgument Map<String, Object> input) {
        @SuppressWarnings("unchecked")
        Map<String, Object> addressInput = (Map<String, Object>) input.get("address");
        
        return customerService.createCustomer(
            (String) input.get("firstName"),
            (String) input.get("lastName"),
            (String) input.get("email"),
            (String) input.get("phone"),
            addressInput
        );
    }

    @DgsData(parentType = "Mutation", field = "updateCustomer")
    public Customer updateCustomer(@InputArgument String id, @InputArgument Map<String, Object> input) {
        return customerService.updateCustomer(id, input);
    }

    @DgsData(parentType = "Customer", field = "orders")
    public List<Order> customerOrders(DgsDataFetchingEnvironment dfe) {
        Customer customer = dfe.getSource();
        return customerService.getOrdersByCustomerId(customer.getId());
    }
}
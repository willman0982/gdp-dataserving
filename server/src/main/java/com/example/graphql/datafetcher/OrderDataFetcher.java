package com.example.graphql.datafetcher;

import com.example.graphql.input.CreateOrderInput;
import com.example.graphql.model.Order;
import com.example.graphql.model.User;
import com.example.graphql.service.TrinoService;
import com.netflix.graphql.dgs.DgsComponent;
import com.netflix.graphql.dgs.DgsData;
import com.netflix.graphql.dgs.DgsDataFetchingEnvironment;
import com.netflix.graphql.dgs.InputArgument;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@DgsComponent
public class OrderDataFetcher {

    @Autowired
    private TrinoService trinoService;

    @DgsData(parentType = "Query", field = "orders")
    public List<Order> orders() {
        return trinoService.getAllOrders();
    }

    @DgsData(parentType = "Query", field = "order")
    public Order order(@InputArgument String id) {
        return trinoService.getOrderById(id);
    }

    @DgsData(parentType = "Query", field = "userOrders")
    public List<Order> userOrders(@InputArgument String userId) {
        return trinoService.getOrdersByUserId(userId);
    }

    @DgsData(parentType = "Mutation", field = "createOrder")
    public Order createOrder(@InputArgument CreateOrderInput input) {
        return trinoService.createOrder(
            input.getUserId(),
            input.getProductName(),
            input.getQuantity(),
            input.getPrice()
        );
    }

    @DgsData(parentType = "Order", field = "user")
    public User orderUser(DgsDataFetchingEnvironment dfe) {
        Order order = dfe.getSource();
        return trinoService.getUserById(order.getUserId());
    }
}
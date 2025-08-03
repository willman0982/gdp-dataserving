package com.example.graphql.datafetcher;

import com.example.graphql.input.CreateUserInput;
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
public class UserDataFetcher {

    @Autowired
    private TrinoService trinoService;

    @DgsData(parentType = "Query", field = "users")
    public List<User> users() {
        return trinoService.getAllUsers();
    }

    @DgsData(parentType = "Query", field = "user")
    public User user(@InputArgument String id) {
        return trinoService.getUserById(id);
    }

    @DgsData(parentType = "Mutation", field = "createUser")
    public User createUser(@InputArgument CreateUserInput input) {
        return trinoService.createUser(input.getName(), input.getEmail());
    }

    @DgsData(parentType = "User", field = "orders")
    public List<Order> userOrders(DgsDataFetchingEnvironment dfe) {
        User user = dfe.getSource();
        return trinoService.getOrdersByUserId(user.getId());
    }
}
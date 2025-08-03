package com.example.graphql.service;

import com.example.graphql.model.Order;
import com.example.graphql.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class TrinoService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private final RowMapper<User> userRowMapper = new RowMapper<User>() {
        @Override
        public User mapRow(ResultSet rs, int rowNum) throws SQLException {
            User user = new User();
            user.setId(rs.getString("id"));
            user.setName(rs.getString("name"));
            user.setEmail(rs.getString("email"));
            user.setCreatedAt(LocalDateTime.parse(rs.getString("created_at"), DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            return user;
        }
    };

    private final RowMapper<Order> orderRowMapper = new RowMapper<Order>() {
        @Override
        public Order mapRow(ResultSet rs, int rowNum) throws SQLException {
            Order order = new Order();
            order.setId(rs.getString("id"));
            order.setUserId(rs.getString("user_id"));
            order.setProductName(rs.getString("product_name"));
            order.setQuantity(rs.getInt("quantity"));
            order.setPrice(rs.getDouble("price"));
            order.setOrderDate(LocalDateTime.parse(rs.getString("order_date"), DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            return order;
        }
    };

    // User operations
    public List<User> getAllUsers() {
        try {
            return jdbcTemplate.query("SELECT id, name, email, created_at FROM memory.default.users", userRowMapper);
        } catch (Exception e) {
            // Fallback to mock data if Trino is not available
            return getMockUsers();
        }
    }

    public User getUserById(String id) {
        try {
            List<User> users = jdbcTemplate.query(
                "SELECT id, name, email, created_at FROM memory.default.users WHERE id = ?", 
                userRowMapper, id);
            return users.isEmpty() ? null : users.get(0);
        } catch (Exception e) {
            // Fallback to mock data
            return getMockUsers().stream()
                .filter(user -> user.getId().equals(id))
                .findFirst()
                .orElse(null);
        }
    }

    public User createUser(String name, String email) {
        String id = UUID.randomUUID().toString();
        LocalDateTime now = LocalDateTime.now();
        
        try {
            jdbcTemplate.update(
                "INSERT INTO memory.default.users (id, name, email, created_at) VALUES (?, ?, ?, ?)",
                id, name, email, now.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        } catch (Exception e) {
            // In a real scenario, you might want to handle this differently
            System.out.println("Could not insert into Trino, using mock data: " + e.getMessage());
        }
        
        return new User(id, name, email, now);
    }

    // Order operations
    public List<Order> getAllOrders() {
        try {
            return jdbcTemplate.query("SELECT id, user_id, product_name, quantity, price, order_date FROM memory.default.orders", orderRowMapper);
        } catch (Exception e) {
            return getMockOrders();
        }
    }

    public Order getOrderById(String id) {
        try {
            List<Order> orders = jdbcTemplate.query(
                "SELECT id, user_id, product_name, quantity, price, order_date FROM memory.default.orders WHERE id = ?", 
                orderRowMapper, id);
            return orders.isEmpty() ? null : orders.get(0);
        } catch (Exception e) {
            return getMockOrders().stream()
                .filter(order -> order.getId().equals(id))
                .findFirst()
                .orElse(null);
        }
    }

    public List<Order> getOrdersByUserId(String userId) {
        try {
            return jdbcTemplate.query(
                "SELECT id, user_id, product_name, quantity, price, order_date FROM memory.default.orders WHERE user_id = ?", 
                orderRowMapper, userId);
        } catch (Exception e) {
            return getMockOrders().stream()
                .filter(order -> order.getUserId().equals(userId))
                .collect(Collectors.toList());
        }
    }

    public Order createOrder(String userId, String productName, Integer quantity, Double price) {
        String id = UUID.randomUUID().toString();
        LocalDateTime now = LocalDateTime.now();
        
        try {
            jdbcTemplate.update(
                "INSERT INTO memory.default.orders (id, user_id, product_name, quantity, price, order_date) VALUES (?, ?, ?, ?, ?, ?)",
                id, userId, productName, quantity, price, now.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        } catch (Exception e) {
            System.out.println("Could not insert into Trino, using mock data: " + e.getMessage());
        }
        
        return new Order(id, userId, productName, quantity, price, now);
    }

    // Mock data for demonstration when Trino is not available
    private List<User> getMockUsers() {
        return List.of(
            new User("1", "John Doe", "john@example.com", LocalDateTime.now().minusDays(30)),
            new User("2", "Jane Smith", "jane@example.com", LocalDateTime.now().minusDays(20)),
            new User("3", "Bob Johnson", "bob@example.com", LocalDateTime.now().minusDays(10))
        );
    }

    private List<Order> getMockOrders() {
        return List.of(
            new Order("101", "1", "Laptop", 1, 999.99, LocalDateTime.now().minusDays(5)),
            new Order("102", "1", "Mouse", 2, 25.50, LocalDateTime.now().minusDays(3)),
            new Order("103", "2", "Keyboard", 1, 75.00, LocalDateTime.now().minusDays(2)),
            new Order("104", "3", "Monitor", 1, 299.99, LocalDateTime.now().minusDays(1))
        );
    }
}
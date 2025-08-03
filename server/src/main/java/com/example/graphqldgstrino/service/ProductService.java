package com.example.graphqldgstrino.service;

import com.example.graphqldgstrino.model.Product;
import com.example.graphqldgstrino.model.Category;
import com.example.graphqldgstrino.model.Order;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ProductService {

    private final Map<String, Product> products = new HashMap<>();
    private final Map<String, Category> categories = new HashMap<>();

    public ProductService() {
        initializeMockData();
    }

    private void initializeMockData() {
        // Initialize categories
        Category electronics = new Category("1", "Electronics", "Electronic devices and gadgets");
        Category books = new Category("2", "Books", "Books and literature");
        Category clothing = new Category("3", "Clothing", "Apparel and accessories");
        
        categories.put("1", electronics);
        categories.put("2", books);
        categories.put("3", clothing);

        // Initialize products
        Product laptop = new Product("1", "Gaming Laptop", "High-performance gaming laptop", 1299.99, "1", 10);
        Product mouse = new Product("2", "Wireless Mouse", "Ergonomic wireless mouse", 29.99, "1", 50);
        Product book = new Product("3", "Java Programming", "Complete guide to Java programming", 49.99, "2", 25);
        Product shirt = new Product("4", "Cotton T-Shirt", "Comfortable cotton t-shirt", 19.99, "3", 100);
        
        products.put("1", laptop);
        products.put("2", mouse);
        products.put("3", book);
        products.put("4", shirt);
    }

    public List<Product> getAllProducts() {
        return new ArrayList<>(products.values());
    }

    public Product getProductById(String id) {
        return products.get(id);
    }

    public List<Product> getProductsByCategory(String categoryId) {
        return products.values().stream()
            .filter(product -> product.getCategoryId().equals(categoryId))
            .collect(Collectors.toList());
    }

    public Product createProduct(String name, String description, Double price, String categoryId, Integer stockQuantity) {
        String id = UUID.randomUUID().toString();
        Product product = new Product(id, name, description, price, categoryId, stockQuantity);
        products.put(id, product);
        return product;
    }

    public Product updateProduct(String id, Map<String, Object> input) {
        Product product = products.get(id);
        if (product == null) {
            return null;
        }

        if (input.containsKey("name")) {
            product.setName((String) input.get("name"));
        }
        if (input.containsKey("description")) {
            product.setDescription((String) input.get("description"));
        }
        if (input.containsKey("price")) {
            product.setPrice(((Number) input.get("price")).doubleValue());
        }
        if (input.containsKey("categoryId")) {
            product.setCategoryId((String) input.get("categoryId"));
        }
        if (input.containsKey("stockQuantity")) {
            product.setStockQuantity(((Number) input.get("stockQuantity")).intValue());
        }

        return product;
    }

    public Boolean deleteProduct(String id) {
        return products.remove(id) != null;
    }

    public Category getCategoryByProductId(String productId) {
        Product product = products.get(productId);
        if (product != null) {
            return categories.get(product.getCategoryId());
        }
        return null;
    }

    public List<Order> getOrdersByProductId(String productId) {
        // Mock implementation - in real app, this would query the database
        return new ArrayList<>();
    }

    public Category getCategoryById(String id) {
        return categories.get(id);
    }

    public List<Category> getAllCategories() {
        return new ArrayList<>(categories.values());
    }

    public Category createCategory(String name, String description) {
        String id = UUID.randomUUID().toString();
        Category category = new Category(id, name, description);
        categories.put(id, category);
        return category;
    }

    public Category updateCategory(String id, Map<String, Object> input) {
        Category category = categories.get(id);
        if (category == null) {
            return null;
        }

        if (input.containsKey("name")) {
            category.setName((String) input.get("name"));
        }
        if (input.containsKey("description")) {
            category.setDescription((String) input.get("description"));
        }

        return category;
    }
}
package com.example.graphqldgstrino.datafetcher;

import com.example.graphqldgstrino.model.Product;
import com.example.graphqldgstrino.model.Category;
import com.example.graphqldgstrino.model.Order;
import com.example.graphqldgstrino.service.ProductService;
import com.netflix.graphql.dgs.DgsComponent;
import com.netflix.graphql.dgs.DgsData;
import com.netflix.graphql.dgs.DgsDataFetchingEnvironment;
import com.netflix.graphql.dgs.InputArgument;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;

@DgsComponent
public class ProductDataFetcher {

    @Autowired
    private ProductService productService;

    @DgsData(parentType = "Query", field = "products")
    public List<Product> products() {
        return productService.getAllProducts();
    }

    @DgsData(parentType = "Query", field = "product")
    public Product product(@InputArgument String id) {
        return productService.getProductById(id);
    }

    @DgsData(parentType = "Query", field = "productsByCategory")
    public List<Product> productsByCategory(@InputArgument String categoryId) {
        return productService.getProductsByCategory(categoryId);
    }

    @DgsData(parentType = "Mutation", field = "createProduct")
    public Product createProduct(@InputArgument Map<String, Object> input) {
        return productService.createProduct(
            (String) input.get("name"),
            (String) input.get("description"),
            ((Number) input.get("price")).doubleValue(),
            (String) input.get("categoryId"),
            ((Number) input.get("stockQuantity")).intValue()
        );
    }

    @DgsData(parentType = "Mutation", field = "updateProduct")
    public Product updateProduct(@InputArgument String id, @InputArgument Map<String, Object> input) {
        return productService.updateProduct(id, input);
    }

    @DgsData(parentType = "Mutation", field = "deleteProduct")
    public Boolean deleteProduct(@InputArgument String id) {
        return productService.deleteProduct(id);
    }

    @DgsData(parentType = "Product", field = "category")
    public Category productCategory(DgsDataFetchingEnvironment dfe) {
        Product product = dfe.getSource();
        return productService.getCategoryByProductId(product.getId());
    }

    @DgsData(parentType = "Product", field = "orders")
    public List<Order> productOrders(DgsDataFetchingEnvironment dfe) {
        Product product = dfe.getSource();
        return productService.getOrdersByProductId(product.getId());
    }
}
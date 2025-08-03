package com.example.graphqldgstrino.datafetcher;

import com.example.graphqldgstrino.model.Category;
import com.example.graphqldgstrino.model.Product;
import com.example.graphqldgstrino.service.ProductService;
import com.netflix.graphql.dgs.DgsComponent;
import com.netflix.graphql.dgs.DgsData;
import com.netflix.graphql.dgs.DgsDataFetchingEnvironment;
import com.netflix.graphql.dgs.InputArgument;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;

@DgsComponent
public class CategoryDataFetcher {

    @Autowired
    private ProductService productService;

    @DgsData(parentType = "Query", field = "categories")
    public List<Category> categories() {
        return productService.getAllCategories();
    }

    @DgsData(parentType = "Query", field = "category")
    public Category category(@InputArgument String id) {
        return productService.getCategoryById(id);
    }

    @DgsData(parentType = "Mutation", field = "createCategory")
    public Category createCategory(@InputArgument Map<String, Object> input) {
        return productService.createCategory(
            (String) input.get("name"),
            (String) input.get("description")
        );
    }

    @DgsData(parentType = "Mutation", field = "updateCategory")
    public Category updateCategory(@InputArgument String id, @InputArgument Map<String, Object> input) {
        return productService.updateCategory(id, input);
    }

    @DgsData(parentType = "Category", field = "products")
    public List<Product> categoryProducts(DgsDataFetchingEnvironment dfe) {
        Category category = dfe.getSource();
        return productService.getProductsByCategory(category.getId());
    }
}
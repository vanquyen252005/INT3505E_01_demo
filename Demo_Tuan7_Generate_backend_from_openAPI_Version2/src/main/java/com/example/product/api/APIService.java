package com.example.product.api;

import com.example.product.model.Product;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
@Service
public class APIService {
    private List<Product> productList = new ArrayList<>();

    // Create a product
    public Product createProduct(Product product) {
        productList.add(product);  // Thêm sản phẩm vào danh sách (mock DB)
        return product;
    }

    // Get all products
    public List<Product> getAllProducts() {
        return productList;
    }

    // Get a product by ID
    public Optional<Product> getProductById(String id) {
        return productList.stream().filter(product -> product.getId().equals(id)).findFirst();
    }

    // Update a product
    public Optional<Product> updateProduct(String id, Product product) {
        Optional<Product> existingProduct = getProductById(id);
        if (existingProduct.isPresent()) {
            Product updatedProduct = existingProduct.get();
            updatedProduct.setName(product.getName());
            updatedProduct.setDescription(product.getDescription());
            updatedProduct.setPrice(product.getPrice());
            return Optional.of(updatedProduct);
        }
        return Optional.empty();
    }

    // Delete a product
    public boolean deleteProduct(String id) {
        return productList.removeIf(product -> product.getId().equals(id));
    }
}

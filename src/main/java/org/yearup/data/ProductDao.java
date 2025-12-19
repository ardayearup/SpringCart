package org.yearup.data;

import org.yearup.models.Product;

import java.math.BigDecimal;
import java.util.List;

// Defines the contract for product data access
public interface ProductDao
{
    // Retrieves all products
    List<Product> getAllProducts();

    // Retrieves a product by its ID
    Product getById(int productId);

    // Creates a new product
    Product create(Product product);

    // Updates an existing product
    void update(int productId, Product product);

    // Deletes a product by ID
    void delete(int productId);

    // Retrieves products by category ID
    List<Product> getProductsByCategoryId(int categoryId);

    // Searches products by category, price range, and color (mapped to subcategory)
    List<Product> search(
            Integer categoryId,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            String color
    );

    // Updates stock quantity (positive or negative)
    void updateStock(int productId, int quantityChange);
}

package org.yearup.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.yearup.data.ProductDao;
import org.yearup.models.Product;

import java.math.BigDecimal;
import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("products")
public class ProductsController
{
    private final ProductDao productDao;

    @Autowired
    public ProductsController(ProductDao productDao)
    {
        this.productDao = productDao;
    }

    // GET /products
    // Retrieves all products or searches based on query parameters
    @GetMapping
    @PreAuthorize("permitAll()")
    public List<Product> searchProducts(
            @RequestParam(name = "cat", required = false) Integer categoryId,
            @RequestParam(name = "minPrice", required = false) BigDecimal minPrice,
            @RequestParam(name = "maxPrice", required = false) BigDecimal maxPrice,
            @RequestParam(name = "color", required = false) String color
    )
    {
        try
        {
            boolean hasFilters =
                    categoryId != null ||
                            minPrice != null ||
                            maxPrice != null ||
                            (color != null && !color.trim().isEmpty());

            if (hasFilters)
            {
                // "color" is mapped internally to DB column "subcategory"
                return productDao.search(categoryId, minPrice, maxPrice, color);
            }

            return productDao.getAllProducts();
        }
        catch (Exception ex)
        {
            System.err.println("Error searching or retrieving products: " + ex.getMessage());
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Oops... our bad."
            );
        }
    }

    // GET /products/{id}
    // Retrieves a product by ID
    @GetMapping("{id}")
    @PreAuthorize("permitAll()")
    public Product getProductById(@PathVariable int id)
    {
        try
        {
            Product product = productDao.getById(id);

            if (product == null)
            {
                throw new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Product not found."
                );
            }

            return product;
        }
        catch (ResponseStatusException rse)
        {
            throw rse;
        }
        catch (Exception ex)
        {
            System.err.println("Error retrieving product ID " + id + ": " + ex.getMessage());
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Oops... our bad."
            );
        }
    }

    // POST /products
    // Creates a new product (ADMIN only)
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.CREATED)
    public Product addProduct(@RequestBody Product product)
    {
        try
        {
            if (product.getName() == null || product.getName().trim().isEmpty())
            {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Product name is required."
                );
            }

            if (product.getCategoryId() <= 0)
            {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Valid category ID is required."
                );
            }

            return productDao.create(product);
        }
        catch (ResponseStatusException rse)
        {
            throw rse;
        }
        catch (Exception ex)
        {
            System.err.println("Error adding product: " + ex.getMessage());
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Failed to add product."
            );
        }
    }

    // PUT /products/{id}
    // Updates a product (ADMIN only)
    @PutMapping("{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void updateProduct(@PathVariable int id, @RequestBody Product product)
    {
        try
        {
            Product existingProduct = productDao.getById(id);
            if (existingProduct == null)
            {
                throw new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Product not found."
                );
            }

            if (product.getName() == null || product.getName().trim().isEmpty())
            {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Product name is required."
                );
            }

            if (product.getCategoryId() <= 0)
            {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Valid category ID is required."
                );
            }

            productDao.update(id, product);
        }
        catch (ResponseStatusException rse)
        {
            throw rse;
        }
        catch (Exception ex)
        {
            System.err.println("Error updating product ID " + id + ": " + ex.getMessage());
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Failed to update product."
            );
        }
    }

    // DELETE /products/{id}
    // Deletes a product (ADMIN only)
    @DeleteMapping("{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteProduct(@PathVariable int id)
    {
        try
        {
            Product existingProduct = productDao.getById(id);
            if (existingProduct == null)
            {
                throw new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Product not found."
                );
            }

            productDao.delete(id);
        }
        catch (ResponseStatusException rse)
        {
            throw rse;
        }
        catch (Exception ex)
        {
            System.err.println("Error deleting product ID " + id + ": " + ex.getMessage());
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Failed to delete product."
            );
        }
    }
}
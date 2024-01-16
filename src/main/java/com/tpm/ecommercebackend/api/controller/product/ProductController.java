package com.tpm.ecommercebackend.api.controller.product;

import com.tpm.ecommercebackend.model.Product;
import com.tpm.ecommercebackend.service.ProductService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * REST controller for products
 */
@RestController
@RequestMapping("/product")
public class ProductController {
    private ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    /**
     * Get all products
     * @return list of products
     */
    @GetMapping
    public List<Product> getProducts() {
        return productService.getAllProducts();
    }
}

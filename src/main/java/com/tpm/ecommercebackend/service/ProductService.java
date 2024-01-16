package com.tpm.ecommercebackend.service;

import com.tpm.ecommercebackend.model.Product;
import com.tpm.ecommercebackend.model.dao.ProductDAO;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service for handling product actions
 */
@Service
public class ProductService {
    private ProductDAO productDAO;

    public ProductService(ProductDAO productDAO) {
        this.productDAO = productDAO;
    }

    /**
     * Gets all products available
     * @return The list of products
     */
    public List<Product> getAllProducts() {
        return productDAO.findAll();
    }

}

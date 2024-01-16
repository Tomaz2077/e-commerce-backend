package com.tpm.ecommercebackend.service;

import com.tpm.ecommercebackend.model.LocalUser;
import com.tpm.ecommercebackend.model.WebOrder;
import com.tpm.ecommercebackend.model.dao.WebOrderDAO;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service for handling order actions
 */
@Service
public class OrderService {
    private WebOrderDAO webOrderDAO;

    public OrderService(WebOrderDAO webOrderDAO) {
        this.webOrderDAO = webOrderDAO;
    }

    /**
     * Gets all orders for the given user
     * @param user The user to get the orders for
     * @return The list of orders
     */
    public List<WebOrder> getOrders(LocalUser user) {
        return webOrderDAO.findByUser(user);
    }
}

package com.tpm.ecommercebackend.model.dao;

import com.tpm.ecommercebackend.model.LocalUser;
import com.tpm.ecommercebackend.model.WebOrder;
import org.springframework.data.repository.ListCrudRepository;

import java.util.List;

public interface WebOrderDAO extends ListCrudRepository<WebOrder, Long> {

    List<WebOrder> findByUser(LocalUser user);

}

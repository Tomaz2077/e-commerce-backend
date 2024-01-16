package com.tpm.ecommercebackend.model.dao;

import com.tpm.ecommercebackend.model.LocalUser;
import com.tpm.ecommercebackend.model.VerificationToken;
import org.springframework.data.repository.ListCrudRepository;

import java.util.Optional;

public interface VerificationTokenDAO extends ListCrudRepository<VerificationToken, Long> {

    Optional<VerificationToken> findByToken(String token);

    void deleteByUser(LocalUser user);

}

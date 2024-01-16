package com.tpm.ecommercebackend.service;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

/**
 * Service for handling encryption of passwords
 */
@Service
public class EncryptionService {

    /** How many salt rounds should the encryption run. The higher them more secure password */
    @Value("${encryption.salt.rounds}")
    private int saltRounds;
    private String salt;

    @PostConstruct
    public void postConstruct() {
        salt = BCrypt.gensalt(saltRounds);
    }

    /**
     * Encrypts the given password.
     * @param password The plain text password.
     * @return The encrypted password.
     */
    public String encryptPassword(String password) {
        return BCrypt.hashpw(password, salt);
    }

    /**
     * Verifies the given password against the given hash.
     * @param password The plain text password.
     * @param hash The hash to compare against.
     * @return True if the password matches the hash, false otherwise.
     */
    public boolean verifyPassword(String password, String hash) {
        return BCrypt.checkpw(password, hash);
    }

}

package com.tpm.ecommercebackend.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;


@SpringBootTest
@AutoConfigureMockMvc
class EncryptionServiceTest {

    @Autowired
    private EncryptionService encryptionService;

    @Test
    void testPasswordEncryption() {
        String password = "PasswordIsASecret123";
        String hash = encryptionService.encryptPassword(password);
        Assertions.assertTrue(encryptionService.verifyPassword(password, hash), "Password should match hash");
        Assertions.assertFalse(encryptionService.verifyPassword("WrongPassword", hash), "Password should not match hash");
    }
}
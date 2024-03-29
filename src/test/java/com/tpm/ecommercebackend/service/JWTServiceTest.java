package com.tpm.ecommercebackend.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.MissingClaimException;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.tpm.ecommercebackend.model.LocalUser;
import com.tpm.ecommercebackend.model.dao.LocalUserDAO;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@AutoConfigureMockMvc
class JWTServiceTest {

    @Autowired
    private JWTService jwtService;
    @Autowired
    private LocalUserDAO localUserDAO;
    @Value("${jwt.algorithm.key}")
    private String algorithmKey;
    @Value("${jwt.issuer}")
    private String issuer;

    @Test
    void testVerificationTokenNorUsableForLogin() {
        LocalUser user = localUserDAO.findByUsernameIgnoreCase("UserA").get();
        String token = jwtService.generateVerificationJWT(user);
        Assertions.assertNull(jwtService.getUsername(token), "Verification token should not be usable for login");
    }

    @Test
    void testAuthTokeReturnsUserName() {
        LocalUser user = localUserDAO.findByUsernameIgnoreCase("UserA").get();
        String token = jwtService.generateJWT(user);
        Assertions.assertEquals(user.getUsername(), jwtService.getUsername(token), "Username should match with the tokens username");
    }

    @Test
    void testLoginJWTNotGeneratedByUs() {
        String token = JWT.create().withClaim("USERNAME", "UserA").sign(Algorithm.HMAC256("NotTheSecretKey"));
        Assertions.assertThrows(SignatureVerificationException.class, () ->
            jwtService.getUsername(token));
    }

    @Test
    void testLoginJWTCorrectlySignedNoIssuer() {
        String token = JWT.create().withClaim("USERNAME", "UserA").sign(Algorithm.HMAC256(algorithmKey));
        Assertions.assertThrows(MissingClaimException.class, () ->
                jwtService.getUsername(token));
    }
    @Test
    void testJWTHappy() {
        String token = JWT.create().withClaim("USERNAME", "UserA").withClaim("iss", issuer).sign(Algorithm.HMAC256(algorithmKey));
        Assertions.assertDoesNotThrow(() ->
                jwtService.getUsername(token));
    }

    @Test
    void testPasswordResetToken() {
        LocalUser user = localUserDAO.findByUsernameIgnoreCase("UserA").get();
        String token = jwtService.generatePasswordResetJWT(user);
        Assertions.assertEquals(user.getEmail(),
                jwtService.getResetPasswordEmail(token), "Email should match with the tokens email");
    }

    @Test
    void testResetJWTNotGeneratedByUs() {
        String token = JWT.create().withClaim("RESET_PASSWORD_EMAIL", "test1@tester.com").sign(Algorithm.HMAC256("NotTheSecretKey"));
        Assertions.assertThrows(SignatureVerificationException.class, () ->
                jwtService.getUsername(token));
    }

    @Test
    void testResetJWTCorrectlySignedNoIssuer() {
        String token = JWT.create().withClaim("RESET_PASSWORD_EMAIL", "test1@tester.com").sign(Algorithm.HMAC256(algorithmKey));
        Assertions.assertThrows(MissingClaimException.class, () ->
                jwtService.getUsername(token));
    }

}
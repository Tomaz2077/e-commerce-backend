package com.tpm.ecommercebackend.service;

import com.icegreen.greenmail.configuration.GreenMailConfiguration;
import com.icegreen.greenmail.junit5.GreenMailExtension;
import com.icegreen.greenmail.util.ServerSetupTest;
import com.tpm.ecommercebackend.api.model.LoginBody;
import com.tpm.ecommercebackend.api.model.PasswordResetBody;
import com.tpm.ecommercebackend.api.model.RegistrationBody;
import com.tpm.ecommercebackend.exception.EmailFailureException;
import com.tpm.ecommercebackend.exception.EmailNotFoundException;
import com.tpm.ecommercebackend.exception.UserAlreadyExistException;
import com.tpm.ecommercebackend.exception.UserNotVerifiedException;
import com.tpm.ecommercebackend.model.LocalUser;
import com.tpm.ecommercebackend.model.VerificationToken;
import com.tpm.ecommercebackend.model.dao.LocalUserDAO;
import com.tpm.ecommercebackend.model.dao.VerificationTokenDAO;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
@AutoConfigureMockMvc
class UserServiceTest {

    @RegisterExtension
    private static GreenMailExtension greenMailExtension = new GreenMailExtension(ServerSetupTest.SMTP)
            .withConfiguration(GreenMailConfiguration.aConfig().withUser("springboot", "secret"))
            .withPerMethodLifecycle(true);

    @Autowired
    private UserService userService;
    @Autowired
    private VerificationTokenDAO verificationTokenDAO;
    @Autowired
    private JWTService jwtService;
    @Autowired
    private LocalUserDAO localUserDAO;
    @Autowired
    private EncryptionService encryptionService;

    @Test
    @Transactional
    public void testRegisterUser() throws MessagingException {
        // Arrange
        RegistrationBody body = new RegistrationBody();
        body.setFirstName("FirstName");
        body.setLastName("LastName");
        body.setPassword("MySecretPassword123");

        // Act and assert test username already in use
        body.setUsername("UserA");
        body.setEmail("UserServiceTest$testRegistrationUser@junit.com");
        Assertions.assertThrows(UserAlreadyExistException.class, () -> {
            userService.registerUser(body);
        }, "username should already be in use");

        // Act and assert test email already in use
        body.setUsername("UserServiceTest$testRegistrationUser");
        body.setEmail("UserA@junit.com");
        Assertions.assertThrows(UserAlreadyExistException.class, () -> {
            userService.registerUser(body);
        }, "Email should already be in use");

        // Act and assert test valid registration
        body.setEmail("UserServiceTest$testRegistrationUser@junit.com");
        Assertions.assertDoesNotThrow(() -> {
            userService.registerUser(body);
        }, "User should register successfully");

        // Assert verification email was sent
        Assertions.assertEquals(body.getEmail(), greenMailExtension.getReceivedMessages()[0]
                        .getRecipients(Message.RecipientType.TO)[0].toString()
                , "Email should be sent to the correct address");

    }

    @Test
    @Transactional
    public void testLoginUser() throws UserNotVerifiedException, EmailFailureException {
        LoginBody body = new LoginBody();
        body.setUsername("UserA-NotExists");
        body.setPassword("PasswordA123");
        Assertions.assertNull(userService.loginUser(body), "User should not exist");

        body.setUsername("UserA");
        body.setPassword("PasswordA123-BadPassword");
        Assertions.assertNull(userService.loginUser(body), "Password should be incorrect");

        body.setPassword("PasswordA123");
        Assertions.assertNotNull(userService.loginUser(body), "User should login successfully");

        body.setUsername("UserB");
        body.setPassword("PasswordB123");
        try {
            userService.loginUser(body);
            Assertions.fail("User should not be email verified");
        } catch (UserNotVerifiedException ex) {
            Assertions.assertTrue(ex.isNewEmailSent(), "Email verification link should had been sent");
            Assertions.assertEquals(1, greenMailExtension.getReceivedMessages().length, "Email should be received");
        }
        try {
            userService.loginUser(body);
            Assertions.fail("User should not be email verified");
        } catch (UserNotVerifiedException ex) {
            Assertions.assertFalse(ex.isNewEmailSent(), "Email verification link should had been resent");
            Assertions.assertEquals(1, greenMailExtension.getReceivedMessages().length, "Only one email should be received in total");
        }
    }

    @Test
    @Transactional
    void testVerifyUser() throws EmailFailureException {
        Assertions.assertFalse(userService.verifyUser("InvalidToken"), "Invalid tokens should return false");
        LoginBody body = new LoginBody();
        body.setUsername("UserB");
        body.setPassword("PasswordB123");
        try {
            //We know the user is not verified. We are doing this to trigger a verification token generation
            userService.loginUser(body);
            Assertions.fail("User should not be email verified");
        } catch (UserNotVerifiedException ex) {
            List<VerificationToken> tokens = verificationTokenDAO.findByUser_IdOrderByIdDesc(2L);
            String token = tokens.get(0).getToken();
            boolean isUserNowLoggedIn = userService.verifyUser(token);
            Assertions.assertTrue(isUserNowLoggedIn, "User should now be logged in");
            Assertions.assertNotNull(body, "loginUser method should not had returned null");
        }
    }

    @Test
    @Transactional
    void testForgotPassword() throws EmailNotFoundException, EmailFailureException {
        // assert that an exception is thrown when the email is not found
        Assertions.assertThrows(EmailNotFoundException.class, () -> {
            userService.forgotPassword("InvalidEmail");
        }, "Invalid emails should throw an exception");

        // assert that an exception is not thrown when the email is found
        Assertions.assertDoesNotThrow(() -> {
            userService.forgotPassword("UserA@junit.com");
        }, "Valid emails should not throw an exception");

        // assert that the email was sent
        Assertions.assertEquals(1, greenMailExtension.getReceivedMessages().length, "Email should be received");
    }

    @Test
    @Transactional
    void testResetPassword() {
        LocalUser user = localUserDAO.findByUsernameIgnoreCase("UserA").get();
        String token = jwtService.generatePasswordResetJWT(user);
        String newPassword = "NewPassword123";
        PasswordResetBody body = new PasswordResetBody();
        body.setToken(token);
        body.setPassword(newPassword);
        userService.resetPassword(body);

        user = localUserDAO.findByUsernameIgnoreCase("UserA").get();
        Assertions.assertTrue(encryptionService.verifyPassword(newPassword, user.getPassword()), "Password should be updated");
    }
}
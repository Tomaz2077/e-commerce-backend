package com.tpm.ecommercebackend.api.controller.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.icegreen.greenmail.configuration.GreenMailConfiguration;
import com.icegreen.greenmail.junit5.GreenMailExtension;
import com.icegreen.greenmail.util.ServerSetupTest;
import com.tpm.ecommercebackend.api.model.RegistrationBody;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AuthenticationControllerTest {

    @Autowired
    private MockMvc mvc;

    @RegisterExtension
    private static GreenMailExtension greenMailExtension = new GreenMailExtension(ServerSetupTest.SMTP)
            .withConfiguration(GreenMailConfiguration.aConfig().withUser("springboot", "secret"))
            .withPerMethodLifecycle(true);

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    @Transactional
    void testRegisterUser_happyFlow() throws Exception {
        RegistrationBody body = getGoodRegistrationBody();
        mvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(body)))
                .andExpect(status().is(HttpStatus.OK.value()));
    }

    @Test
    @Transactional
    void testRegisterUser_testNotNull() throws Exception {
        RegistrationBody body = getGoodRegistrationBody();

        // Test username null
        body.setUsername(null);
        mvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(body)))
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()));

        // Test email null
        body = getGoodRegistrationBody();
        body.setEmail(null);
        mvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(body)))
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()));

        // Test password null
        body = getGoodRegistrationBody();
        body.setPassword(null);
        mvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(body)))
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()));

        // Test firstName null
        body = getGoodRegistrationBody();
        body.setFirstName(null);
        mvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(body)))
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()));

        // Test lastName null
        body = getGoodRegistrationBody();
        body.setLastName(null);
        mvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(body)))
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()));

    }

    @Test
    @Transactional
    void testRegisterUser_testNotBlank() throws Exception {
        RegistrationBody body = getGoodRegistrationBody();

        // Test username not blank
        body.setUsername("");
        mvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(body)))
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()));

        // Test email not blank
        body = getGoodRegistrationBody();
        body.setEmail("");
        mvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(body)))
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()));

        // Test password not blank
        body = getGoodRegistrationBody();
        body.setPassword("");
        mvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(body)))
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()));

        // Test firstName not blank
        body = getGoodRegistrationBody();
        body.setFirstName("");
        mvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(body)))
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()));

        // Test lastName not blank
        body = getGoodRegistrationBody();
        body.setLastName("");
        mvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(body)))
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()));
    }

    @Test
    @Transactional
    void testRegisterUser_testPassword() throws Exception {
        String[] badPasswords = {
                "password", //too short
                "passwordpassword", //does not contain a number
                "123456789", //does not contain a letter
        };

        RegistrationBody body = getGoodRegistrationBody();

        for (String badPassword : badPasswords) {
            body.setPassword(badPassword);
            mvc.perform(post("/auth/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(body)))
                    .andExpect(status().is(HttpStatus.BAD_REQUEST.value()));
        }
    }


    private RegistrationBody getGoodRegistrationBody() {
        RegistrationBody body = new RegistrationBody();
        body.setUsername("AuthenticationControllerTest-User");
        body.setEmail("AuthenticationControllerTest@junit.com");
        body.setPassword("Password1");
        body.setFirstName("UserA-FirstName");
        body.setLastName("UserA-LastName");
        return body;
    }

    @Test
    void loginUser() {
    }

    @Test
    void verifyEmail() {
    }

    @Test
    void getLoggedInUserProfile() {
    }
}
package com.tpm.ecommercebackend.api.security;

import com.tpm.ecommercebackend.model.LocalUser;
import com.tpm.ecommercebackend.model.dao.LocalUserDAO;
import com.tpm.ecommercebackend.service.JWTService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class JWTRequestFilterTest {

    @Autowired
    private MockMvc mvc;
    @Autowired
    private JWTService jwtService;
    @Autowired
    private LocalUserDAO localUserDAO;
    private static final String AUTHENTICATED_PATH = "/auth/me";


    @Test
    void testUnauthorizedRequest() throws Exception {
        mvc.perform(get(AUTHENTICATED_PATH)).andExpect(status().is(HttpStatus.FORBIDDEN.value()));
    }

    @Test
    void testBadToken() throws Exception {
        String[] badTokens = {"Badtoken", "Bearer Badtoken"};

        for(String badToken : badTokens) {
            mvc.perform(get(AUTHENTICATED_PATH).header("Authorization", badToken))
                    .andExpect(status().is(HttpStatus.FORBIDDEN.value()));
        }
    }

    @Test
    void testUnverifiedUser() throws Exception {
        LocalUser user = localUserDAO.findByUsernameIgnoreCase("UserB").get();
        String token = jwtService.generateJWT(user);
        mvc.perform(get(AUTHENTICATED_PATH).header("Authorization", "Bearer " + token))
                .andExpect(status().is(HttpStatus.FORBIDDEN.value()));

    }

    @Test
    void testSuccessfulAuthentication() throws Exception {
        LocalUser user = localUserDAO.findByUsernameIgnoreCase("UserA").get();
        String token = jwtService.generateJWT(user);
        mvc.perform(get(AUTHENTICATED_PATH).header("Authorization", "Bearer " + token))
                .andExpect(status().is(HttpStatus.OK.value()));
    }
}
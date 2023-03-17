package com.flab.oasis.login;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.flab.oasis.controller.UserAuthController;
import com.flab.oasis.model.JwtToken;
import com.flab.oasis.model.UserLoginRequest;
import com.flab.oasis.service.JwtService;
import com.flab.oasis.service.UserAuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class UserAuthControllerTest {
    @InjectMocks
    UserAuthController userAuthController;

    @Mock
    UserAuthService userAuthService;

    @Mock
    JwtService jwtService;

    MockMvc mockMvc;

    @BeforeEach
    void init() {
        mockMvc = MockMvcBuilders.standaloneSetup(userAuthController).build();
    }

    @Test
    void testLoginDefault() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        UserLoginRequest userLoginRequest = new UserLoginRequest("test@test.com", "1234");
        JwtToken jwtToken = new JwtToken("accessToken", "refreshToken");

        BDDMockito.given(userAuthService.createJwtTokenByUserLoginRequest(userLoginRequest)).willReturn(jwtToken);

        mockMvc.perform(
                MockMvcRequestBuilders
                        .post("/api/auth/login/default")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userLoginRequest))
        ).andExpect(
                MockMvcResultMatchers.status().isOk()
        ).andExpect(
                MockMvcResultMatchers.cookie().value("RefreshToken", jwtToken.getRefreshToken())
        );
    }
}
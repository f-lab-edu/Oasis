package com.flab.oasis.login;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.flab.oasis.controller.UserAuthController;
import com.flab.oasis.model.JwtToken;
import com.flab.oasis.model.UserLoginRequest;
import com.flab.oasis.service.UserAuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith(MockitoExtension.class)
class UserAuthControllerTest {
    @InjectMocks
    UserAuthController userAuthController;

    @Mock
    UserAuthService userAuthService;

    MockMvc mockMvc;

    @BeforeEach
    void init() {
        mockMvc = MockMvcBuilders.standaloneSetup(userAuthController).build();
    }

    @DisplayName("기본 로그인 테스트")
    @Test
    void testLoginDefault() throws Exception {
        UserLoginRequest userLoginRequest = new UserLoginRequest();

        JwtToken jwtToken = new JwtToken(null, "");

        BDDMockito.doReturn(jwtToken)
                .when(userAuthService)
                .createJwtTokenByUserLoginRequest(
                        ArgumentMatchers.any(UserLoginRequest.class)
                );

        mockMvc.perform(
                MockMvcRequestBuilders
                        .post("/api/auth/login-default")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(userLoginRequest))
        ).andExpect(
                MockMvcResultMatchers.status().isOk()
        ).andExpect(
                MockMvcResultMatchers.cookie().value("RefreshToken", jwtToken.getRefreshToken())
        );
    }
}
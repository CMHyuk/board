package com.spring.board.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.spring.board.domain.User;
import com.spring.board.repository.UserRepository;
import com.spring.board.request.login.LoginRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ExtendWith(RestDocumentationExtension.class)
class LoginControllerTest {

    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    UserRepository userRepository;

    @BeforeEach
    void setUp(WebApplicationContext webApplicationContext,
               RestDocumentationContextProvider restDocumentation) {

        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(documentationConfiguration(restDocumentation))
                .alwaysDo(print())
                .addFilters(new CharacterEncodingFilter("UTF-8", true))
                .build();

        userRepository.deleteAll();
    }

    @Test
    @DisplayName("/login ?????? ?????????")
    void successLoginTest() throws Exception {
        //given
        User user = User.builder()
                .nickname("?????????")
                .loginId("?????????")
                .password("????????????")
                .build();

        userRepository.save(user);

        LoginRequest request = new LoginRequest();
        request.setLoginId("?????????");
        request.setPassword("????????????");
        String json = objectMapper.writeValueAsString(request);

        //expected
        mockMvc.perform(post("/login")
                        .contentType(APPLICATION_JSON)
                        .content(json))
                .andExpect(jsonPath("$.loginId").value("?????????"))
                .andExpect(jsonPath("$.password").value("????????????"))
                .andExpect(status().isOk())
                .andDo(document("login",
                        requestFields(
                                fieldWithPath("loginId").description("?????????"),
                                fieldWithPath("password").description("????????????")
                        )));
    }

    @Test
    @DisplayName("/login ?????? ?????????")
    void failLoginTest() throws Exception {
        //given
        LoginRequest request = new LoginRequest();
        request.setLoginId("?????????");
        request.setPassword("????????????");
        String json = objectMapper.writeValueAsString(request);

        //expected
        mockMvc.perform(post("/login")
                        .contentType(APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isNotFound())
                .andDo(document("login-fail"));
    }
}
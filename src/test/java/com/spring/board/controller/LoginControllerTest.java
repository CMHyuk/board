package com.spring.board.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.spring.board.domain.User;
import com.spring.board.repository.UserRepository;
import com.spring.board.request.LoginRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class LoginControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    UserRepository userRepository;

    @BeforeEach
    void clean() {
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("/login 성공 테스트")
    void successLoginTest() throws Exception {
        //given
        User user = User.builder()
                .nickname("닉네임")
                .loginId("아이디")
                .password("비밀번호")
                .build();

        userRepository.save(user);

        LoginRequest request = new LoginRequest();
        request.setLoginId("아이디");
        request.setPassword("비밀번호");
        String json = objectMapper.writeValueAsString(request);

        //expected
        mockMvc.perform(post("/login")
                        .contentType(APPLICATION_JSON)
                        .content(json))
                .andExpect(jsonPath("$.loginId").value("아이디"))
                .andExpect(jsonPath("$.password").value("비밀번호"))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @DisplayName("/login 실패 테스트")
    void failLoginTest() throws Exception {
        //given
        LoginRequest request = new LoginRequest();
        request.setLoginId("아이디");
        request.setPassword("비밀번호");
        String json = objectMapper.writeValueAsString(request);

        //expected
        mockMvc.perform(post("/login")
                        .contentType(APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isNotFound())
                .andDo(print());
    }
}
package com.spring.board.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.spring.board.domain.User;
import com.spring.board.repository.UserRepository;
import com.spring.board.request.user.EditUserRequest;
import com.spring.board.request.user.SaveUserRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;

import static com.spring.board.Const.LOGIN_USER;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest {

    @Autowired
    UserRepository userRepository;

    @Mock
    private MockHttpSession mockHttpSession;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockHttpSession = new MockHttpSession();
        userRepository.deleteAll();
    }

    @AfterEach
    void cleanSession() {
        mockHttpSession.clearAttributes();
    }

    @Test
    @DisplayName("/user/save 테스트")
    void saveTest() throws Exception {
        //given
        SaveUserRequest request = new SaveUserRequest();
        request.setNickname("닉네임");
        request.setLoginId("아이디");
        request.setPassword("비밀번호");

        String json = objectMapper.writeValueAsString(request);

        //expected
        mockMvc.perform(post("/user/save")
                        .contentType(APPLICATION_JSON)
                        .content(json))
                .andExpect(jsonPath("$.nickname").value("닉네임"))
                .andExpect(jsonPath("$.loginId").value("아이디"))
                .andExpect(jsonPath("$.password").value("비밀번호"))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @DisplayName("/user/edit/{userId} 성공 테스트")
    void successEditTest() throws Exception {
        //given
        User user = User.builder()
                .nickname("닉네임")
                .loginId("아이디")
                .password("비밀번호")
                .build();

        User saveUser = userRepository.save(user);

        mockHttpSession.setAttribute(LOGIN_USER, saveUser);

        EditUserRequest request = new EditUserRequest();
        request.setPassword("새로운비밀번호");
        String json = objectMapper.writeValueAsString(request);

        //expected
        mockMvc.perform(patch("/user/edit/{userId}", saveUser.getId())
                        .contentType(APPLICATION_JSON)
                        .session(mockHttpSession)
                        .content(json))
                .andExpect(jsonPath("$.password").value("새로운비밀번호"))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @DisplayName("/user/edit/{userId} 실패 테스트")
    void failEditTest() throws Exception {
        //given
        User user = User.builder()
                .nickname("닉네임")
                .loginId("아이디")
                .password("비밀번호")
                .build();

        User saveUser = userRepository.save(user);
        mockHttpSession.setAttribute(LOGIN_USER, saveUser);

        EditUserRequest request = new EditUserRequest();
        request.setPassword("새로운비밀번호");
        String json = objectMapper.writeValueAsString(request);

        //expected
        mockMvc.perform(patch("/user/edit/{userId}", 100L)
                        .contentType(APPLICATION_JSON)
                        .session(mockHttpSession)
                        .content(json))
                .andExpect(status().isNotFound())
                .andDo(print());
    }

    @Test
    @DisplayName("/user/edit/{userId} 미인증 테스트")
    void unauthorizedEditTest() throws Exception {
        //given
        User user = User.builder()
                .nickname("닉네임")
                .loginId("아이디")
                .password("비밀번호")
                .build();

        User saveUser = userRepository.save(user);

        EditUserRequest request = new EditUserRequest();
        request.setPassword("새로운비밀번호");
        String json = objectMapper.writeValueAsString(request);

        //expected
        mockMvc.perform(patch("/user/edit/{userId}", saveUser.getId())
                        .contentType(APPLICATION_JSON)
                        .session(mockHttpSession)
                        .content(json))
                .andExpect(status().isUnauthorized())
                .andDo(print());
    }
}
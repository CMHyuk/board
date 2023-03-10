package com.spring.board.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.spring.board.domain.Board;
import com.spring.board.domain.Like;
import com.spring.board.domain.User;
import com.spring.board.repository.BoardRepository;
import com.spring.board.repository.LikeRepository;
import com.spring.board.repository.UserRepository;
import com.spring.board.request.user.EditUserRequest;
import com.spring.board.request.user.SaveUserRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.spring.board.Const.LOGIN_USER;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ExtendWith(RestDocumentationExtension.class)
class UserControllerTest {

    @Autowired
    UserRepository userRepository;

    @Autowired
    BoardRepository boardRepository;

    @Autowired
    LikeRepository likeRepository;

    @Mock
    private MockHttpSession mockHttpSession;

    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp(WebApplicationContext webApplicationContext,
               RestDocumentationContextProvider restDocumentation) {

        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(documentationConfiguration(restDocumentation))
                .alwaysDo(print())
                .addFilters(new CharacterEncodingFilter("UTF-8", true))
                .build();

        mockHttpSession = new MockHttpSession();
        userRepository.deleteAll();
        boardRepository.deleteAll();
        likeRepository.deleteAll();
    }

    @AfterEach
    void cleanSession() {
        mockHttpSession.clearAttributes();
    }

    @Test
    @DisplayName("/user/save ?????????")
    void saveTest() throws Exception {
        //given
        SaveUserRequest request = new SaveUserRequest();
        request.setNickname("?????????");
        request.setLoginId("testId1234");
        request.setPassword("testPassword123!");

        String json = objectMapper.writeValueAsString(request);

        //expected
        mockMvc.perform(post("/user/save")
                        .contentType(APPLICATION_JSON)
                        .content(json))
                .andExpect(jsonPath("$.nickname").value("?????????"))
                .andExpect(jsonPath("$.loginId").value("testId1234"))
                .andExpect(jsonPath("$.password").value("testPassword123!"))
                .andExpect(status().isOk())
                .andDo(document("user-save",
                        requestFields(
                                fieldWithPath("nickname").description("?????????"),
                                fieldWithPath("loginId").description("?????????"),
                                fieldWithPath("password").description("????????????")
                        )));
    }

    @Test
    @DisplayName("/user/save ???????????? ?????? ?????????")
    void badRequest() throws Exception {
        //given
        User user = User.builder()
                .nickname("?????????")
                .loginId("123")
                .password("123")
                .build();

        String json = objectMapper.writeValueAsString(user);

        mockMvc.perform(post("/user/save")
                        .contentType(APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andDo(document("user-badRequest"));
    }

    @Test
    @DisplayName("/user/save ?????? ?????????")
    void duplicationSaveTest() throws Exception {
        //given
        User user1 = User.builder()
                .nickname("?????????")
                .loginId("testId1234")
                .password("testPassword123!")
                .build();

        userRepository.save(user1);

        User user2 = User.builder()
                .nickname("?????????")
                .loginId("testId1234")
                .password("testPassword123!")
                .build();

        String json = objectMapper.writeValueAsString(user2);

        mockMvc.perform(post("/user/save")
                        .contentType(APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isConflict())
                .andDo(document("user-duplication"));
    }

    @Test
    @DisplayName("/user/{userId}/boards ???????????? ????????? ????????? ??????")
    void getUserBoardsTest() throws Exception {
        //given
        User user = User.builder()
                .nickname("?????????")
                .loginId("?????????")
                .password("????????????")
                .build();

        User savedUser = userRepository.save(user);
        mockHttpSession.setAttribute(LOGIN_USER, savedUser);

        List<Board> boards = IntStream.range(0, 10).mapToObj(i -> Board.builder()
                        .title("??????" + i)
                        .content("??????" + i)
                        .user(savedUser)
                        .build())
                .collect(Collectors.toList());

        boardRepository.saveAll(boards);

        //expected
        mockMvc.perform(get("/user/{userId}/boards", savedUser.getId())
                        .contentType(APPLICATION_JSON)
                        .session(mockHttpSession))
                .andExpect(jsonPath("$.length()").value(10))
                .andExpect(status().isOk())
                .andDo(document("user-get"));
    }

    @Test
    @DisplayName("/user/edit/{userId} ????????? ???????????? ?????? ?????????")
    void successEditTest() throws Exception {
        //given
        User user = User.builder()
                .nickname("?????????")
                .loginId("?????????")
                .password("????????????")
                .build();

        User saveUser = userRepository.save(user);

        mockHttpSession.setAttribute(LOGIN_USER, saveUser);

        EditUserRequest request = new EditUserRequest();
        request.setPassword("?????????????????????");
        String json = objectMapper.writeValueAsString(request);

        //expected
        mockMvc.perform(patch("/user/edit/{userId}", saveUser.getId())
                        .contentType(APPLICATION_JSON)
                        .session(mockHttpSession)
                        .content(json))
                .andExpect(jsonPath("$.password").value("?????????????????????"))
                .andExpect(status().isOk())
                .andDo(document("user-edit",
                        requestFields(fieldWithPath("password").description("?????????????????????"))));
    }

    @Test
    @DisplayName("/user/edit/{userId} ?????? ?????????")
    void failEditTest() throws Exception {
        //given
        User user = User.builder()
                .nickname("?????????")
                .loginId("?????????")
                .password("????????????")
                .build();

        User saveUser = userRepository.save(user);
        mockHttpSession.setAttribute(LOGIN_USER, saveUser);

        EditUserRequest request = new EditUserRequest();
        request.setPassword("?????????????????????");
        String json = objectMapper.writeValueAsString(request);

        //expected
        mockMvc.perform(patch("/user/edit/{userId}", 0L)
                        .contentType(APPLICATION_JSON)
                        .session(mockHttpSession)
                        .content(json))
                .andExpect(status().isNotFound())
                .andDo(document("user-fail-edit"));
    }

    @Test
    @DisplayName("/user/edit/{userId} ????????? ?????????")
    void unauthorizedEditTest() throws Exception {
        //given
        User user = User.builder()
                .nickname("?????????")
                .loginId("?????????")
                .password("????????????")
                .build();

        User saveUser = userRepository.save(user);

        EditUserRequest request = new EditUserRequest();
        request.setPassword("?????????????????????");
        String json = objectMapper.writeValueAsString(request);

        //expected
        mockMvc.perform(patch("/user/edit/{userId}", saveUser.getId())
                        .contentType(APPLICATION_JSON)
                        .session(mockHttpSession)
                        .content(json))
                .andExpect(status().isUnauthorized())
                .andDo(document("user-edit-unauthorized"));
    }

    @Test
    @DisplayName("/user/delete/{userId}")
    void deleteTest() throws Exception {
        //given
        User user = User.builder()
                .nickname("?????????")
                .loginId("?????????")
                .password("????????????")
                .build();

        userRepository.save(user);
        mockHttpSession.setAttribute(LOGIN_USER, user);

        //expected
        mockMvc.perform(delete("/user/delete/{userId}", user.getId())
                .contentType(APPLICATION_JSON)
                .session(mockHttpSession))
                .andExpect(status().isOk())
                .andDo(document("user-delete"));
    }

    @Test
    @DisplayName("/user/delete/{userId} ????????? ????????? ?????? ?????????")
    void unauthorizedDeleteTest() throws Exception {
        //given
        User user = User.builder()
                .nickname("?????????")
                .loginId("?????????")
                .password("????????????")
                .build();

        userRepository.save(user);

        //expected
        mockMvc.perform(delete("/user/delete/{userId}", user.getId())
                        .contentType(APPLICATION_JSON)
                        .session(mockHttpSession))
                .andExpect(status().isUnauthorized())
                .andDo(document("user-delete-unauthorized"));
    }

    @Test
    @DisplayName("/user/{userId}/likeBoards ????????? ?????? ????????? ??????")
    void getLikeBoards() throws Exception {
        //given
        User user = User.builder()
                .nickname("?????????")
                .loginId("?????????")
                .password("????????????")
                .build();

        User savedUser = userRepository.save(user);
        mockHttpSession.setAttribute(LOGIN_USER, savedUser);

        Board board = Board.builder()
                .title("??????")
                .content("??????")
                .user(savedUser)
                .build();

        Board savedBoard = boardRepository.save(board);

        Optional<Like> findLike = likeRepository.findByBoardIdAndUserId(board.getId(), user.getId());

        Like like = Like.builder()
                .board(savedBoard)
                .user(savedUser)
                .like(findLike)
                .build();

        likeRepository.save(like);

        //expected
        mockMvc.perform(get("/user/{userId}/likeBoards", savedUser.getId())
                        .contentType(APPLICATION_JSON)
                        .session(mockHttpSession))
                .andExpect(jsonPath("$.[0].boardId").value(savedBoard.getId()))
                .andExpect(jsonPath("$.[0].title").value("??????"))
                .andExpect(jsonPath("$.[0].content").value("??????"))
                .andExpect(status().isOk())
                .andDo(document("user-likeBoard"));
    }
}
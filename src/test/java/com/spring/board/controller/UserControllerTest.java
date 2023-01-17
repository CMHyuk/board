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
                .andDo(document("user-save",
                        requestFields(
                                fieldWithPath("nickname").description("닉네임"),
                                fieldWithPath("loginId").description("아이디"),
                                fieldWithPath("password").description("비밀번호")
                        )));
    }

    @Test
    @DisplayName("/user/save 중복 테스트")
    void duplicationSaveTest() throws Exception {
        //given
        User user1 = User.builder()
                .nickname("닉네임")
                .loginId("아이디")
                .password("비밀번호")
                .build();

        userRepository.save(user1);

        User user2 = User.builder()
                .nickname("닉네임")
                .loginId("아이디")
                .password("비밀번호")
                .build();

        String json = objectMapper.writeValueAsString(user2);

        mockMvc.perform(post("/user/save")
                        .contentType(APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isConflict())
                .andDo(document("user-duplication"));
    }

    @Test
    @DisplayName("/user/{userId}/boards 사용자가 작성한 게시글 조회")
    void getUserBoardsTest() throws Exception {
        //given
        User user = User.builder()
                .nickname("닉네임")
                .loginId("아이디")
                .password("비밀번호")
                .build();

        User savedUser = userRepository.save(user);

        List<Board> boards = IntStream.range(0, 10).mapToObj(i -> Board.builder()
                        .title("제목" + i)
                        .content("내용" + i)
                        .user(savedUser)
                        .build())
                .collect(Collectors.toList());

        boardRepository.saveAll(boards);

        //expected
        mockMvc.perform(get("/user/{userId}/boards", savedUser.getId())
                        .contentType(APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(10))
                .andExpect(status().isOk())
                .andDo(document("user-get"));
    }

    @Test
    @DisplayName("/user/edit/{userId} 사용자 비밀번호 수정 테스트")
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
                .andDo(document("user-edit",
                        requestFields(fieldWithPath("password").description("새로운비밀번호"))));
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
        mockMvc.perform(patch("/user/edit/{userId}", 0L)
                        .contentType(APPLICATION_JSON)
                        .session(mockHttpSession)
                        .content(json))
                .andExpect(status().isNotFound())
                .andDo(document("user-fail-edit"));
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
                .andDo(document("user-edit-unauthorized"));
    }

    @Test
    @DisplayName("/user/delete/{userId}")
    void deleteTest() throws Exception {
        //given
        User user = User.builder()
                .nickname("닉네임")
                .loginId("아이디")
                .password("비밀번호")
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
    @DisplayName("/user/delete/{userId} 미인증 사용자 삭제 테스트")
    void unauthorizedDeleteTest() throws Exception {
        //given
        User user = User.builder()
                .nickname("닉네임")
                .loginId("아이디")
                .password("비밀번호")
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
    @DisplayName("/user/{userId}/likeBoards 좋아요 누른 게시글 조회")
    void getLikeBoards() throws Exception {
        //given
        User user = User.builder()
                .nickname("닉네임")
                .loginId("아이디")
                .password("비밀번호")
                .build();

        User savedUser = userRepository.save(user);
        mockHttpSession.setAttribute(LOGIN_USER, savedUser);

        Board board = Board.builder()
                .title("제목")
                .content("내용")
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
                .andExpect(jsonPath("$.[0].title").value("제목"))
                .andExpect(jsonPath("$.[0].content").value("내용"))
                .andExpect(status().isOk())
                .andDo(document("user-likeBoard"));
    }
}
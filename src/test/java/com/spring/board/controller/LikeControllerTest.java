package com.spring.board.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.spring.board.domain.Board;
import com.spring.board.domain.Like;
import com.spring.board.domain.User;
import com.spring.board.repository.BoardRepository;
import com.spring.board.repository.LikeRepository;
import com.spring.board.repository.UserRepository;
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

import java.util.Optional;

import static com.spring.board.Const.LOGIN_USER;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ExtendWith(RestDocumentationExtension.class)
class LikeControllerTest {

    @Autowired
    UserRepository userRepository;

    @Autowired
    BoardRepository boardRepository;

    @Autowired
    LikeRepository likeRepository;

    private MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Mock
    MockHttpSession mockHttpSession;


    private User user;
    private Board board;

    @BeforeEach
    void setUp(WebApplicationContext webApplicationContext,
               RestDocumentationContextProvider restDocumentation) {

        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(documentationConfiguration(restDocumentation))
                .alwaysDo(print())
                .addFilters(new CharacterEncodingFilter("UTF-8", true))
                .build();

        userRepository.deleteAll();
        boardRepository.deleteAll();
        likeRepository.deleteAll();

        mockHttpSession = new MockHttpSession();

        user = User.builder()
                .nickname("닉네임")
                .loginId("아이디")
                .password("패스워드")
                .build();

        userRepository.save(user);
        mockHttpSession.setAttribute(LOGIN_USER, user);

        board = Board.builder()
                .user(user)
                .title("제목")
                .content("내용")
                .build();

        boardRepository.save(board);
    }

    @Test
    @DisplayName("/board/{boardId}/like 좋아요 테스트")
    void likeControllerTest() throws Exception {
        //expected
        mockMvc.perform(post("/board/{boardId}/like", board.getId())
                        .contentType(APPLICATION_JSON)
                        .session(mockHttpSession))
                .andExpect(jsonPath("$.userNickname").value("닉네임"))
                .andExpect(jsonPath("$.boardUserNickname").value("닉네임"))
                .andExpect(jsonPath("$.title").value("제목"))
                .andExpect(jsonPath("$.content").value("내용"))
                .andExpect(status().isOk())
                .andDo(document("board-like"));
    }

    @Test
    @DisplayName("/board/{boardId}/cancelLike")
    void cancelLikeTest() throws Exception {
        //given
        Optional<Like> findLike = likeRepository.findByBoardIdAndUserId(board.getId(), user.getId());

        Like like = Like.builder()
                .board(board)
                .user(user)
                .like(findLike)
                .build();

        likeRepository.save(like);

        //expected
        mockMvc.perform(delete("/board/{boardId}/cancelLike", board.getId())
                        .contentType(APPLICATION_JSON)
                        .session(mockHttpSession))
                .andExpect(status().isOk())
                .andDo(document("board-cancelLike"));
    }
}
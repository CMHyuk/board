package com.spring.board.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.spring.board.domain.Board;
import com.spring.board.domain.User;
import com.spring.board.repository.BoardRepository;
import com.spring.board.repository.UserRepository;
import com.spring.board.request.board.EditBoardRequest;
import com.spring.board.request.board.WriteBoardRequest;
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
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.spring.board.Const.LOGIN_USER;
import static org.hamcrest.Matchers.is;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ExtendWith(RestDocumentationExtension.class)
class BoardControllerTest {

    @Autowired
    UserRepository userRepository;

    @Autowired
    BoardRepository boardRepository;

    @Mock
    MockHttpSession mockHttpSession;

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
    }

    @Test
    @DisplayName("/board/{boardId} 게시글 조회")
    void getTest() throws Exception {
        //given
        User user = User.builder()
                .nickname("닉네임")
                .loginId("아이디")
                .password("패스워드")
                .build();

        User savedUser = userRepository.save(user);

        Board board = Board.builder()
                .title("제목")
                .content("내용")
                .user(savedUser)
                .build();

        boardRepository.save(board);

        //expected
        mockMvc.perform(get("/board/{boardId}", board.getId())
                        .contentType(APPLICATION_JSON))
                .andExpect(jsonPath("$.nickname").value("닉네임"))
                .andExpect(jsonPath("$.boardId").value(board.getId()))
                .andExpect(jsonPath("$.title").value("제목"))
                .andExpect(jsonPath("$.content").value("내용"))
                .andExpect(status().isOk())
                .andDo(document("board-get"));
    }

    @Test
    @DisplayName("/boards 게시글 전체 조회")
    void getAllTest() throws Exception {
        //given
        User user = User.builder()
                .nickname("닉네임")
                .loginId("아이디")
                .password("패스워드")
                .build();

        User savedUser = userRepository.save(user);

        List<Board> boards = IntStream.range(0, 20).mapToObj(i -> Board.builder()
                        .title("제목" + i)
                        .content("내용" + i)
                        .user(savedUser)
                        .build())
                .collect(Collectors.toList());

        boardRepository.saveAll(boards);

        //expected
        mockMvc.perform(get("/boards?page=1&sort=id,desc")
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", is(10)))
                .andExpect(jsonPath("$[0].title").value("제목19"))
                .andExpect(jsonPath("$[9].title").value("제목10"))
                .andDo(document("board-getAll"));
    }

    @Test
    @DisplayName("/board/search 게시글 검색 조회")
    void searchTest() throws Exception {
        //given
        User user = User.builder()
                .nickname("닉네임")
                .loginId("아이디")
                .password("패스워드")
                .build();

        User savedUser = userRepository.save(user);

        List<Board> boards = IntStream.range(0, 20).mapToObj(i -> Board.builder()
                        .title("제목" + i)
                        .content("내용" + i)
                        .user(savedUser)
                        .build())
                .collect(Collectors.toList());

        boardRepository.saveAll(boards);

        MultiValueMap<String, String> info = new LinkedMultiValueMap<>();
        info.add("title", "제목");

        //expected
        mockMvc.perform(get("/board/search?page=1&sort=id,desc")
                        .contentType(APPLICATION_JSON)
                        .params(info))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("제목19"))
                .andDo(document("board-search",
                        requestParameters(
                                parameterWithName("title").description("검색 제목"),
                                parameterWithName("page").description(1),
                                parameterWithName("sort").description("id,desc")
                        )));
    }

    @Test
    @DisplayName("/board/write 게시글 저장")
    void writeTest() throws Exception {
        //given
        User user = User.builder()
                .nickname("닉네임")
                .loginId("아이디")
                .password("패스워드")
                .build();

        User savedUser = userRepository.save(user);

        mockHttpSession.setAttribute(LOGIN_USER, savedUser);

        WriteBoardRequest request = new WriteBoardRequest();
        request.setTitle("제목");
        request.setContent("내용");

        String json = objectMapper.writeValueAsString(request);

        //expected
        mockMvc.perform(post("/board/write")
                        .contentType(APPLICATION_JSON)
                        .content(json)
                        .session(mockHttpSession))
                .andExpect(status().isOk())
                .andDo(document("board-save",
                        requestFields(
                                fieldWithPath("title").description("제목"),
                                fieldWithPath("content").description("내용")
                        )));
    }

    @Test
    @DisplayName("/board/write 미인증 사용자 요청")
    void createAuthError() throws Exception {
        WriteBoardRequest request = new WriteBoardRequest();
        request.setTitle("제목");
        request.setContent("내용");

        String json = objectMapper.writeValueAsString(request);

        //expected
        mockMvc.perform(post("/board/write")
                        .contentType(APPLICATION_JSON)
                        .content(json)
                        .session(mockHttpSession))
                .andExpect(status().isUnauthorized())
                .andDo(document("board-unauthorized"));
    }

    @Test
    @DisplayName("/board/edit/{boardId} 게시글 수정")
    void editBoardTest() throws Exception {
        //given
        User user = User.builder()
                .nickname("닉네임")
                .loginId("아이디")
                .password("패스워드")
                .build();

        User savedUser = userRepository.save(user);

        mockHttpSession.setAttribute(LOGIN_USER, savedUser);

        Board board = Board.builder()
                .title("제목")
                .content("내용")
                .user(user)
                .build();

        boardRepository.save(board);

        EditBoardRequest request = new EditBoardRequest();
        request.setTitle("제목수정");
        request.setContent("내용수정");

        String json = objectMapper.writeValueAsString(request);

        //expected
        mockMvc.perform(patch("/board/edit/{boardId}", board.getId())
                        .contentType(APPLICATION_JSON)
                        .session(mockHttpSession)
                        .content(json))
                .andExpect(jsonPath("$.title").value("제목수정"))
                .andExpect(jsonPath("$.content").value("내용수정"))
                .andExpect(status().isOk())
                .andDo(document("board-edit",
                        requestFields(
                                fieldWithPath("title").description("제목수정"),
                                fieldWithPath("content").description("내용수정")
                        )));
    }

    @Test
    @DisplayName("/board/edit/{boardId} 존재하지 않는 게시글 수정")
    void editNonExistBoardTest() throws Exception {
        User user = User.builder()
                .nickname("닉네임")
                .loginId("아이디")
                .password("패스워드")
                .build();

        User savedUser = userRepository.save(user);

        mockHttpSession.setAttribute(LOGIN_USER, savedUser);

        EditBoardRequest request = new EditBoardRequest();
        request.setTitle("제목수정");
        request.setContent("내용수정");

        String json = objectMapper.writeValueAsString(request);

        //expected
        mockMvc.perform(patch("/board/edit/{boardId}", 100L)
                        .contentType(APPLICATION_JSON)
                        .session(mockHttpSession)
                        .content(json))
                .andExpect(status().isNotFound())
                .andDo(document("board-notFound"));
    }

    @Test
    @DisplayName("/board/edit/{boardId} 미인증 사용자 게시글 수정")
    void UnauthorizedUserEditBoardTest() throws Exception {
        EditBoardRequest request = new EditBoardRequest();
        request.setTitle("제목수정");
        request.setContent("내용수정");

        String json = objectMapper.writeValueAsString(request);

        //expected
        mockMvc.perform(patch("/board/edit/{boardId}", 100L)
                        .contentType(APPLICATION_JSON)
                        .session(mockHttpSession)
                        .content(json))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("/board/delete/{boardId} 게시글 삭제 테스트")
    void deleteTest() throws Exception {
        //given
        User user = User.builder()
                .nickname("닉네임")
                .loginId("아이디")
                .password("패스워드")
                .build();

        User savedUser = userRepository.save(user);

        mockHttpSession.setAttribute(LOGIN_USER, savedUser);

        Board board = Board.builder()
                .title("제목")
                .content("내용")
                .user(user)
                .build();

        boardRepository.save(board);

        //expected
        mockMvc.perform(delete("/board/delete/{boardId}", board.getId())
                        .contentType(APPLICATION_JSON)
                        .session(mockHttpSession))
                .andExpect(status().isOk())
                .andDo(document("board-delete"));
    }

    @Test
    @DisplayName("/board/delete/{boardId} 존재하지 않는 게시글 삭제")
    void deleteNonExistBoardTest() throws Exception {
        //given
        User user = User.builder()
                .nickname("닉네임")
                .loginId("아이디")
                .password("패스워드")
                .build();

        User savedUser = userRepository.save(user);

        mockHttpSession.setAttribute(LOGIN_USER, savedUser);

        //expected
        mockMvc.perform(delete("/board/delete/{boardId}", 100L)
                        .contentType(APPLICATION_JSON)
                        .session(mockHttpSession))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("/board/delete/{boardId} 미인증 사용자 게시글 삭제")
    void UnauthorizedUserDeleteBoardTest() throws Exception {
        //expected
        mockMvc.perform(delete("/board/delete/{boardId}", 100L)
                        .contentType(APPLICATION_JSON)
                        .session(mockHttpSession))
                .andExpect(status().isUnauthorized());
    }
}
package com.spring.board.service;

import com.spring.board.domain.Board;
import com.spring.board.domain.User;
import com.spring.board.exception.InvalidRequest;
import com.spring.board.exception.board.BoardNotFound;
import com.spring.board.repository.BoardRepository;
import com.spring.board.repository.UserRepository;
import com.spring.board.request.board.EditBoardRequest;
import com.spring.board.request.board.WriteBoardRequest;
import com.spring.board.response.board.BoardsResponse;
import com.spring.board.response.board.EditBoardResponse;
import com.spring.board.response.board.WriteBoardResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.data.domain.Sort.Direction.DESC;

@SpringBootTest
class BoardServiceTest {

    @Autowired
    BoardService boardService;

    @Autowired
    BoardRepository boardRepository;

    @Autowired
    UserRepository userRepository;

    @BeforeEach
    void clean() {
        boardRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    @Transactional
    @DisplayName("게시글 저장 테스트")
    void writeTest() {
        //given
        User user = User.builder()
                .nickname("닉네임")
                .loginId("아이디")
                .password("비밀번호")
                .build();

        userRepository.save(user);

        WriteBoardRequest request = new WriteBoardRequest();
        request.setTitle("제목");
        request.setContent("내용");

        //when
        WriteBoardResponse write = boardService.write(request, user.getId());

        //then
        assertEquals(write.getTitle(), "제목");
        assertEquals(write.getContent(), "내용");
    }

    @Test
    @DisplayName("게시글 수정 테스트")
    void editBoardTest() {
        //given
        User user = User.builder()
                .nickname("닉네임")
                .loginId("아이디")
                .password("비밀번호")
                .build();

        userRepository.save(user);

        Board board = Board.builder()
                .title("제목")
                .content("내용")
                .user(user)
                .build();

        Board saveBoard = boardRepository.save(board);

        EditBoardRequest request = new EditBoardRequest();
        request.setTitle("제목수정");
        request.setContent("내용수정");

        //when
        EditBoardResponse response = boardService.editBoard(saveBoard.getId(), request, user);

        //then
        assertEquals(response.getTitle(), "제목수정");
        assertEquals(response.getContent(), "내용수정");
    }

    @Test
    @DisplayName("게시글 수정 실패 테스트")
    void editBoardFailTest() {
        //given
        User user1 = User.builder()
                .nickname("닉네임")
                .loginId("아이디")
                .password("비밀번호")
                .build();

        User user2 = User.builder()
                .nickname("닉네임")
                .loginId("아이디")
                .password("비밀번호")
                .build();

        userRepository.save(user1);
        userRepository.save(user2);

        Board board = Board.builder()
                .title("제목")
                .content("내용")
                .user(user1)
                .build();

        Board saveBoard = boardRepository.save(board);

        EditBoardRequest request = new EditBoardRequest();
        request.setTitle("제목수정");
        request.setContent("내용수정");

        //expected
        assertThatThrownBy(() -> boardService.editBoard(saveBoard.getId(), request, user2))
                .isInstanceOf(InvalidRequest.class);
    }

    @Test
    @DisplayName("존재하지 않는 게시글 수정 테스트")
    void editNonExistBoardTest() {
        //given
        User user = User.builder()
                .nickname("닉네임")
                .loginId("아이디")
                .password("비밀번호")
                .build();

        userRepository.save(user);

        EditBoardRequest request = new EditBoardRequest();
        request.setTitle("제목수정");
        request.setContent("내용수정");

        //expected
        assertThatThrownBy(() -> boardService.editBoard(100L, request, user))
                .isInstanceOf(BoardNotFound.class);
    }

    @Test
    @DisplayName("게시글 삭제 테스트")
    void deleteBoardTest() {
        //given
        User user = User.builder()
                .nickname("닉네임")
                .loginId("아이디")
                .password("비밀번호")
                .build();

        userRepository.save(user);

        Board board = Board.builder()
                .title("제목")
                .content("내용")
                .user(user)
                .build();

        Board saveBoard = boardRepository.save(board);

        //when
        boardService.deleteBoard(saveBoard.getId(), user);

        //then
        assertEquals(0, boardRepository.count());
    }

    @Test
    @DisplayName("게시글 삭제 실패 테스트")
    void deleteBoardFailTest() {
        //given
        User user1 = User.builder()
                .nickname("닉네임")
                .loginId("아이디")
                .password("비밀번호")
                .build();

        User user2 = User.builder()
                .nickname("닉네임")
                .loginId("아이디")
                .password("비밀번호")
                .build();

        userRepository.save(user1);
        userRepository.save(user2);

        Board board = Board.builder()
                .title("제목")
                .content("내용")
                .user(user1)
                .build();

        Board saveBoard = boardRepository.save(board);

        //expected
        assertThatThrownBy(() -> boardService.deleteBoard(saveBoard.getId(), user2))
                .isInstanceOf(InvalidRequest.class);
    }

    @Test
    @DisplayName("존재하지 않는 게시글 삭제 테스트")
    void deleteNonExistTest() {
        //given
        User user = User.builder()
                .nickname("닉네임")
                .loginId("아이디")
                .password("비밀번호")
                .build();

        userRepository.save(user);

        //expected
        assertThatThrownBy(() -> boardService.deleteBoard(100L, user))
                .isInstanceOf(BoardNotFound.class);
    }

    @Test
    @DisplayName("게시글 전체 조회")
    void getBoardsTest() {
        //given
        User user = User.builder()
                .nickname("닉네임")
                .loginId("아이디")
                .password("비밀번호")
                .build();

        userRepository.save(user);

        List<Board> boards = IntStream.range(0, 20).mapToObj(i -> Board.builder()
                        .title("제목" + i)
                        .content("내용" + i)
                        .user(user)
                        .build())
                .collect(Collectors.toList());

        boardRepository.saveAll(boards);

        //when
        Pageable pageable = PageRequest.of(0, 10, DESC, "id");
        List<BoardsResponse> findBoards = boardService.getBoards(pageable);

        //then
        assertEquals(findBoards.get(0).getTitle(), "제목19");
        assertEquals(findBoards.get(9).getTitle(), "제목10");
    }
}
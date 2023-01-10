package com.spring.board.service;

import com.spring.board.domain.Board;
import com.spring.board.domain.User;
import com.spring.board.exception.DuplicationLoginIdException;
import com.spring.board.repository.BoardRepository;
import com.spring.board.repository.UserRepository;
import com.spring.board.request.user.EditUserRequest;
import com.spring.board.request.user.SaveUserRequest;
import com.spring.board.response.user.SaveUserResponse;
import com.spring.board.response.user.UserBoardResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
class UserServiceTest {

    @Autowired
    UserService userService;

    @Autowired
    UserRepository userRepository;

    @Autowired
    BoardRepository boardRepository;

    @BeforeEach
    void clean() {
        userRepository.deleteAll();
        boardRepository.deleteAll();
    }

    @Test
    @DisplayName("회원 저장 테스트")
    void saveTest() {
        //given
        SaveUserRequest request = new SaveUserRequest();
        request.setNickname("닉네임");
        request.setLoginId("아이디");
        request.setPassword("비밀번호");

        //when
        SaveUserResponse savedUser = userService.save(request);

        //then
        assertEquals(1L, userRepository.count());
        assertEquals("닉네임", savedUser.getNickname());
        assertEquals("아이디", savedUser.getLoginId());
        assertEquals("비밀번호", savedUser.getPassword());
    }

    @Test
    @DisplayName("회원 아이디 중복 테스트")
    void duplicationTest() {
        //given
        SaveUserRequest request1 = new SaveUserRequest();
        request1.setNickname("닉네임");
        request1.setLoginId("아이디");
        request1.setPassword("비밀번호");

        SaveUserRequest request2 = new SaveUserRequest();
        request2.setNickname("닉네임");
        request2.setLoginId("아이디");
        request2.setPassword("비밀번호");

        userService.save(request1);

        //expected
        assertThrows(DuplicationLoginIdException.class, () -> {
            userService.save(request2);
        });
    }

    @Test
    @DisplayName("회원 수정 테스트")
    void editTest() {
        //given
        User user = User.builder()
                .nickname("닉네임")
                .loginId("아이디")
                .password("비밀번호")
                .build();

        User saveUser = userRepository.save(user);

        EditUserRequest request = new EditUserRequest();
        request.setPassword("새로운비밀번호");

        //when
        User editUser = userService.editUserPassword(saveUser.getId(), request, user);

        //then
        assertEquals("새로운비밀번호", editUser.getPassword());
    }

    @Test
    @DisplayName("회원 삭제 테스트")
    void deleteTest() {
        //given
        User user = User.builder()
                .nickname("닉네임")
                .loginId("아이디")
                .password("비밀번호")
                .build();

        User saveUser = userRepository.save(user);

        //when
        userService.deleteUser(saveUser.getId(), user);

        //then
        assertEquals(0, userRepository.count());
    }

    @Test
    @Transactional
    @DisplayName("회원이 작성한 게시글 조회")
    void getUserBoards() {
        //given
        User user = User.builder()
                .nickname("닉네임")
                .loginId("아이디")
                .password("비밀번호")
                .build();

        User savedUser = userRepository.save(user);

        List<Board> boards = IntStream.range(0, 10)
                .mapToObj(i -> Board.builder()
                        .title("제목" + i)
                        .content("내용" + i)
                        .user(savedUser)
                        .build())
                .collect(Collectors.toList());

        savedUser.setBoards(boards);

        boardRepository.saveAll(boards);

        //when
        List<UserBoardResponse> userBoards = userService.getUserBoards(savedUser.getId());

        //then
        assertEquals(userBoards.size(), 10);
    }
}
package com.spring.board.service;

import com.spring.board.domain.User;
import com.spring.board.repository.UserRepository;
import com.spring.board.request.user.EditUserRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class UserServiceTest {

    @Autowired
    UserService userService;

    @Autowired
    UserRepository userRepository;

    @BeforeEach
    void clean() {
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("회원 저장 테스트")
    void saveTest() {
        //given
        User user = User.builder()
                .nickname("닉네임")
                .loginId("아이디")
                .password("비밀번호")
                .build();

        //when
        User saveUser = userService.save(user);

        //then
        assertEquals(1L, userRepository.count());
        assertEquals("닉네임", saveUser.getNickname());
        assertEquals("아이디", saveUser.getLoginId());
        assertEquals("비밀번호", saveUser.getPassword());
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
}
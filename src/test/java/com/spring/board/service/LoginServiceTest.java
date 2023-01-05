package com.spring.board.service;

import com.spring.board.domain.User;
import com.spring.board.exception.UserNotFound;
import com.spring.board.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
class LoginServiceTest {

    @Autowired
    UserRepository userRepository;

    @Autowired
    LoginService loginService;

    @BeforeEach
    void clean() {
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("로그인 성공 테스트")
    void successLoginTest() {
        //given
        User user = User.builder()
                .nickname("닉네임")
                .loginId("아이디")
                .password("비밀번호")
                .build();

        User saveUser = userRepository.save(user);

        //when
        User loginUser = loginService.login(saveUser.getLoginId(), saveUser.getPassword());

        //then
        assertEquals("닉네임", loginUser.getNickname());
        assertEquals("아이디", loginUser.getLoginId());
        assertEquals("비밀번호", loginUser.getPassword());
    }

    @Test
    @DisplayName("로그인 실패 테스트")
    void failLoginTest() {
        assertThrows(UserNotFound.class, () -> {
            loginService.login("a", "a");
        });
    }
}
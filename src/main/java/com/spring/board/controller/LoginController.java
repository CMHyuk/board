package com.spring.board.controller;

import com.spring.board.domain.User;
import com.spring.board.request.login.LoginRequest;
import com.spring.board.response.login.LoginResponse;
import com.spring.board.service.LoginService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import static com.spring.board.Const.LOGIN_USER;

@RestController
@RequiredArgsConstructor
public class LoginController {

    private final LoginService loginService;

    @PostMapping("/login")
    public LoginResponse login(@RequestBody @Valid LoginRequest loginRequest, HttpServletRequest request) {
        User loginUser = loginService.login(loginRequest.getLoginId(), loginRequest.getPassword());

        HttpSession session = request.getSession();
        session.setAttribute(LOGIN_USER, loginUser);

        return LoginResponse.builder()
                .loginId(loginRequest.getLoginId())
                .password(loginRequest.getPassword())
                .build();
    }

    @PostMapping("/logout")
    public void logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
    }
}

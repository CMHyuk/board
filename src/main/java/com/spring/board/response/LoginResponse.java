package com.spring.board.response;

import lombok.Builder;
import lombok.Getter;

@Getter
public class LoginResponse {

    private final String loginId;
    private final String password;

    @Builder
    public LoginResponse(String loginId, String password) {
        this.loginId = loginId;
        this.password = password;
    }
}

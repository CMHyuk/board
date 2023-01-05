package com.spring.board.response.user;

import lombok.Builder;
import lombok.Getter;

@Getter
public class SaveUserResponse {

    private final Long id;
    private final String nickname;
    private final String loginId;
    private final String password;

    @Builder
    public SaveUserResponse(Long id, String nickname, String loginId, String password) {
        this.id = id;
        this.nickname = nickname;
        this.loginId = loginId;
        this.password = password;
    }
}

package com.spring.board.request.user;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
public class EditUserRequest {

    @NotBlank(message = "새로운 비밀번호를 입력하세요.")
    private String password;
}

package com.spring.board.exception.user;

import com.spring.board.exception.Exception;

public class UserNotFound extends Exception {

    private static final String MESSAGE = "존재하지 않는 회원입니다.";

    public UserNotFound() {
        super(MESSAGE);
    }

    @Override
    public int getStatus() {
        return 404;
    }
}

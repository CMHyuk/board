package com.spring.board.exception.user;

import com.spring.board.exception.Exception;

public class DuplicationLoginIdException extends Exception {

    private static final String MESSAGE = "이미 존재하는 아이디입니다.";

    public DuplicationLoginIdException() {
        super(MESSAGE);
    }

    @Override
    public int getStatus() {
        return 409;
    }
}

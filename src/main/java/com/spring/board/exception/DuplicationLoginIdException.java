package com.spring.board.exception;

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

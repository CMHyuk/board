package com.spring.board.exception;

public class AuthException extends Exception {

    private static final String MESSAGE = "미인증 사용자입니다.";

    public AuthException() {
        super(MESSAGE);
    }

    @Override
    public int getStatus() {
        return 401;
    }
}

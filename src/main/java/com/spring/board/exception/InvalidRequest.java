package com.spring.board.exception;

public class InvalidRequest extends Exception {

    private static final String MESSAGE = "잘못된 요청입니다.";

    public InvalidRequest() {
        super(MESSAGE);
    }

    @Override
    public int getStatus() {
        return 400;
    }
}

package com.spring.board.exception;

public class BoardNotFound extends Exception {

    private static final String MESSAGE = "게시글이 존재하지 않습니다.";

    public BoardNotFound() {
        super(MESSAGE);
    }

    @Override
    public int getStatus() {
        return 404;
    }
}

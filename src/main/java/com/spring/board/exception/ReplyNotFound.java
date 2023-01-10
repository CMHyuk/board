package com.spring.board.exception;

public class ReplyNotFound extends Exception {

    private static final String MESSAGE = "대댓글이 존재하지 않습니다.";

    public ReplyNotFound() {
        super(MESSAGE);
    }

    @Override
    public int getStatus() {
        return 404;
    }
}

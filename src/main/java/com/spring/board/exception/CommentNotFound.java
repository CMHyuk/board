package com.spring.board.exception;

public class CommentNotFound extends Exception {

    private static final String MESSAGE = "댓글이 존재하지 않습니다.";

    public CommentNotFound() {
        super(MESSAGE);
    }

    @Override
    public int getStatus() {
        return 404;
    }
}

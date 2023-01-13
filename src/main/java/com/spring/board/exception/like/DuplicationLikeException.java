package com.spring.board.exception.like;

import com.spring.board.exception.Exception;

public class DuplicationLikeException extends Exception {

    private static final String MESSAGE = "이미 좋아요를 누른 게시글입니다.";

    public DuplicationLikeException() {
        super(MESSAGE);
    }

    @Override
    public int getStatus() {
        return 409;
    }
}

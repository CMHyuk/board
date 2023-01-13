package com.spring.board.exception.like;

import com.spring.board.exception.Exception;

public class LikeNotFound extends Exception {

    private static final String MESSAGE = "좋아요가 존재하지 않습니다";

    public LikeNotFound() {
        super(MESSAGE);
    }

    @Override
    public int getStatus() {
        return 404;
    }
}

package com.spring.board.exception.report;

import com.spring.board.exception.Exception;

public class ReportNotFound extends Exception {

    private static final String MESSAGE = "신고 게시글이 존재하지 않습니다.";

    public ReportNotFound() {
        super(MESSAGE);
    }

    @Override
    public int getStatus() {
        return 404;
    }
}

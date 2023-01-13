package com.spring.board.exception.report;

import com.spring.board.exception.Exception;

public class DuplicationReportException extends Exception {

    private static final String MESSAGE = "이미 신고한 게시글은 신고할 수 없습니다.";

    public DuplicationReportException() {
        super(MESSAGE);
    }

    @Override
    public int getStatus() {
        return 409;
    }
}

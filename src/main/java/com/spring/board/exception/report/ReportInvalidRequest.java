package com.spring.board.exception.report;

import com.spring.board.exception.Exception;

public class ReportInvalidRequest extends Exception {

    private static final String MESSAGE = "자신이 작성한 게시글은 신고할 수 없습니다.";

    public ReportInvalidRequest() {
        super(MESSAGE);
    }

    @Override
    public int getStatus() {
        return 400;
    }
}

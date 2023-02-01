package com.spring.board.domain;

public enum Grade {
    SILVER, GOLD(5), RED(10), ADMIN;

    private int requiredBoardCount;

    Grade(int requiredBoardCount) {
        this.requiredBoardCount = requiredBoardCount;
    }

    Grade() {
    }

    public int getRequiredBoardCount() {
        return requiredBoardCount;
    }
}

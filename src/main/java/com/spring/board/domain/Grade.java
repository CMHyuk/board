package com.spring.board.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum Grade {
    SILVER, GOLD(5), RED(10), ADMIN;

    private int requiredBoardCount;

}

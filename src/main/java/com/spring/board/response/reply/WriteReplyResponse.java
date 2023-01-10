package com.spring.board.response.reply;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class WriteReplyResponse {
    private final String nickname;
    private final String reply;
}

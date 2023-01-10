package com.spring.board.response.comment;

import com.spring.board.response.reply.ReplyDto;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class CommentDto {
    private final String comment;
    private final List<ReplyDto> replies;
}

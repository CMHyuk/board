package com.spring.board.controller;

import com.spring.board.domain.User;
import com.spring.board.response.like.LikeResponse;
import com.spring.board.service.LikeService;
import com.spring.board.web.argumentresolver.Login;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class LikeController {

    private final LikeService likeService;

    @PostMapping("/board/{boardId}/like")
    public LikeResponse like(@PathVariable Long boardId, @Login User user) {
        return likeService.like(boardId, user.getId());
    }

    @DeleteMapping("/board/{boardId}/cancelLike")
    public void unlike(@PathVariable Long boardId, @Login User user) {
        likeService.cancelLike(boardId, user.getId());
    }
}

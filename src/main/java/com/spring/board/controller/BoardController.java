package com.spring.board.controller;

import com.spring.board.domain.Board;
import com.spring.board.domain.User;
import com.spring.board.request.board.EditBoardRequest;
import com.spring.board.request.board.WriteBoardRequest;
import com.spring.board.response.board.*;
import com.spring.board.service.BoardService;
import com.spring.board.web.argumentresolver.Login;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class BoardController {

    private final BoardService boardService;

    @GetMapping("/board/{boardId}")
    public BoardResponse getBoard(@PathVariable Long boardId) {
        Board board = boardService.get(boardId);

        return BoardResponse.builder()
                .nickname(board.getUser().getNickname())
                .boardId(board.getId())
                .title(board.getTitle())
                .content(board.getContent())
                .build();
    }

    @GetMapping("/boards")
    public List<BoardsResponse> getBoards() {
        List<Board> boards = boardService.getBoards();
        return boards.stream()
                .map(b -> new BoardsResponse(b.getUser().getNickname(), b.getId(), b.getTitle(), b.getContent()))
                .collect(Collectors.toList());
    }

    @GetMapping("/board/search")
    public List<BoardSearchResponse> getBoardsBySearch(@RequestParam String title) {
        List<Board> boards = boardService.findBySearch(title);
        return boards.stream()
                .map(b -> new BoardSearchResponse(b.getUser().getNickname(), b.getId(), b.getTitle(), b.getContent()))
                .collect(Collectors.toList());
    }

    @PostMapping("/board/write")
    public WriteBoardResponse write(@RequestBody @Valid WriteBoardRequest request, @Login User user) {
        Board board = Board.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .user(user)
                .build();

        Board writeBoard = boardService.write(board);

        return WriteBoardResponse.builder()
                .title(writeBoard.getTitle())
                .content(writeBoard.getContent())
                .build();
    }

    @PatchMapping("/board/edit/{boardId}")
    public EditBoardResponse edit(@PathVariable Long boardId, @RequestBody @Valid EditBoardRequest request,
                                  @Login User user) {
        Board board = boardService.editBoard(boardId, request, user);

        return EditBoardResponse.builder()
                .title(board.getTitle())
                .content(board.getContent())
                .build();
    }

    @DeleteMapping("/board/delete/{boardId}")
    public void delete(@PathVariable Long boardId, @Login User user) {
        boardService.deleteBoard(boardId, user);
    }
}

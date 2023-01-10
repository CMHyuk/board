package com.spring.board.service;

import com.spring.board.domain.Board;
import com.spring.board.domain.User;
import com.spring.board.exception.BoardNotFound;
import com.spring.board.exception.InvalidRequest;
import com.spring.board.exception.UserNotFound;
import com.spring.board.repository.BoardRepository;
import com.spring.board.repository.UserRepository;
import com.spring.board.request.board.EditBoardRequest;
import com.spring.board.request.board.WriteBoardRequest;
import com.spring.board.response.board.*;
import com.spring.board.response.comment.CommentDto;
import com.spring.board.response.reply.ReplyDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static java.util.stream.Collectors.toList;

@Service
@Transactional
@RequiredArgsConstructor
public class BoardService {

    private final BoardRepository boardRepository;
    private final UserRepository userRepository;

    public WriteBoardResponse write(WriteBoardRequest request, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(UserNotFound::new);

        Board board = Board.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .user(user)
                .build();

        Board savedBoard = boardRepository.save(board);

        return WriteBoardResponse.builder()
                .title(savedBoard.getTitle())
                .content(savedBoard.getContent())
                .build();
    }

    public BoardResponse get(Long boardId) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(BoardNotFound::new);

        return BoardResponse.builder()
                .nickname(board.getUser().getNickname())
                .boardId(board.getId())
                .title(board.getTitle())
                .content(board.getContent())
                .build();
    }

    public List<BoardsResponse> getBoards() {
        List<Board> boards = boardRepository.findAll();
        return getBoardsResponses(boards);
    }

    public List<BoardsResponse> findBySearch(String title) {
        List<Board> boards = boardRepository.findByTitleContaining(title);
        return getBoardsResponses(boards);
    }

    private List<BoardsResponse> getBoardsResponses(List<Board> boards) {
        return boards.stream()
                .map(b -> BoardsResponse.builder()
                        .boardId(b.getId())
                        .userNickname(b.getUser().getNickname())
                        .title(b.getTitle())
                        .content(b.getContent())
                        .comments(b.getComments().stream()
                                .map(c -> new CommentDto(c.getComment(), c.getReplies().stream()
                                        .map(r -> new ReplyDto(r.getReply()))
                                        .collect(toList())))
                                .collect(toList()))
                        .build())
                .collect(toList());
    }

    public EditBoardResponse editBoard(Long boardId, EditBoardRequest request, User user) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(BoardNotFound::new);

        validateSameUser(user, board);

        board.setTitle(request.getTitle());
        board.setContent(request.getContent());

        return EditBoardResponse.builder()
                .title(board.getTitle())
                .content(board.getContent())
                .build();
    }

    public void deleteBoard(Long boardId, User user) {
        Board findBoard = boardRepository.findById(boardId)
                .orElseThrow(BoardNotFound::new);
        validateSameUser(user, findBoard);
        boardRepository.delete(findBoard);
    }

    private void validateSameUser(User user, Board findBoard) {
        User boardUser = findBoard.getUser();
        if (user.getId() != boardUser.getId()) {
            throw new InvalidRequest();
        }
    }
}

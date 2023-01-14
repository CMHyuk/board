package com.spring.board.service;

import com.spring.board.domain.Board;
import com.spring.board.domain.User;
import com.spring.board.exception.InvalidRequest;
import com.spring.board.exception.board.BoardNotFound;
import com.spring.board.exception.user.UserNotFound;
import com.spring.board.repository.BoardRepository;
import com.spring.board.repository.UserRepository;
import com.spring.board.request.board.EditBoardRequest;
import com.spring.board.request.board.WriteBoardRequest;
import com.spring.board.response.board.BoardResponse;
import com.spring.board.response.board.BoardsResponse;
import com.spring.board.response.board.EditBoardResponse;
import com.spring.board.response.board.WriteBoardResponse;
import com.spring.board.response.comment.CommentDto;
import com.spring.board.response.reply.ReplyDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
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
                .comments(board.getComments().stream()
                        .map(c -> new CommentDto(c.getComment(), c.getReplies().stream()
                                .map(r -> new ReplyDto(r.getReply()))
                                .collect(toList())))
                        .collect(toList()))
                .build();
    }

    public List<BoardsResponse> getBoards(Pageable pageable) {
        return boardRepository.findAll(pageable).stream()
                .map(b -> BoardsResponse.builder()
                        .boardId(b.getId())
                        .userNickname(b.getUser().getNickname())
                        .title(b.getTitle())
                        .content(b.getContent())
                        .build())
                .collect(toList());
    }

    public List<BoardsResponse> findBySearch(String title, Pageable pageable) {
        return boardRepository.findByTitleContaining(title, pageable).stream()
                .map(b -> BoardsResponse.builder()
                        .boardId(b.getId())
                        .userNickname(b.getUser().getNickname())
                        .title(b.getTitle())
                        .content(b.getContent())
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
        if (!user.getId().equals(boardUser.getId())) {
            throw new InvalidRequest();
        }
    }
}

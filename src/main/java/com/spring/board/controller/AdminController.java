package com.spring.board.controller;

import com.spring.board.domain.*;
import com.spring.board.exception.board.BoardNotFound;
import com.spring.board.exception.comment.CommentNotFound;
import com.spring.board.exception.reply.ReplyNotFound;
import com.spring.board.exception.user.UserNotFound;
import com.spring.board.repository.*;
import com.spring.board.request.login.LoginForm;
import com.spring.board.request.board.BoardSearch;
import com.spring.board.request.user.EditUserRequest;
import com.spring.board.request.user.UserSearch;
import com.spring.board.response.report.ReportBoardsResponse;
import com.spring.board.service.BoardService;
import com.spring.board.service.LoginService;
import com.spring.board.service.ReportService;
import com.spring.board.web.argumentresolver.Login;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;

import static com.spring.board.Const.LOGIN_USER;
import static com.spring.board.domain.Grade.ADMIN;
import static org.springframework.data.domain.Sort.Direction.DESC;

@Slf4j
@Controller
@Transactional
@RequiredArgsConstructor
public class AdminController {

    private final UserRepository userRepository;
    private final BoardRepository boardRepository;
    private final CommentRepository commentRepository;
    private final ReplyRepository replyRepository;
    private final ReportRepository reportRepository;
    private final BoardService boardService;
    private final ReportService reportService;
    private final LoginService loginService;

    @GetMapping("/admin")
    public String home(@Login User user, Model model) {
        if (user == null) {
            return "home";
        }

        model.addAttribute("user", user);
        return "loginHome";
    }

    @GetMapping("/admin/login")
    public String loginForm(@ModelAttribute LoginForm loginForm) {
        return "loginForm";
    }

    @PostMapping("/admin/login")
    public String login(@ModelAttribute LoginForm loginForm, BindingResult bindingResult,
                        @RequestParam(defaultValue = "/") String redirectURL,
                        HttpServletRequest request) {
        if (bindingResult.hasErrors()) {
            return "loginForm";
        }

        if (!loginForm.getLoginId().equals("123") && !loginForm.getPassword().equals("123")) {
            bindingResult.reject("loginFail", "아이디 또는 비밀번호가 맞지 않습니다.");
            return "loginForm";
        }

        User admin = loginService.login(loginForm.getLoginId(), loginForm.getPassword());

        HttpSession session = request.getSession();
        session.setAttribute(LOGIN_USER, admin);

        return "redirect:/admin" + redirectURL;
    }

    @GetMapping("/admin/users")
    public String users(@ModelAttribute UserSearch userSearch, Model model,
                        @PageableDefault(sort = "id", direction = DESC) Pageable pageable) {
        List<User> findUser = userRepository.findByNickname(userSearch.getNickname());
        Page<User> pages = userRepository.findAll(pageable);

        if (findUser.isEmpty()) {
            List<User> users = userRepository.findAllWithPaging(pageable);
            model.addAttribute("users", users);
            model.addAttribute("pages", pages);
            model.addAttribute("maxPage", 10);
            return "users";
        }

        model.addAttribute("users", findUser);
        model.addAttribute("pages", pages);
        model.addAttribute("maxPage", 10);
        return "users";
    }

    @GetMapping("/admin/user/{userId}")
    public String user(@PathVariable Long userId, Model model) {
        User user = userRepository.findById(userId)
                .orElseThrow(UserNotFound::new);

        model.addAttribute("user", user);

        return "user";
    }

    @PostMapping("/admin/user/delete/{userId}")
    public String deleteUser(@PathVariable Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(UserNotFound::new);

        userRepository.delete(user);

        return "redirect:/admin";
    }

    @GetMapping("/admin/user/edit/{userId}")
    public String editUserForm(@ModelAttribute EditUserRequest request) {
        return "editUser";
    }

    @PostMapping("/admin/user/edit/{userId}")
    public String editUser(@PathVariable Long userId, @ModelAttribute EditUserRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(UserNotFound::new);
        user.setPassword(request.getPassword());
        return "redirect:/admin";
    }

    @GetMapping("/admin/boards")
    public String board(@ModelAttribute BoardSearch boardSearch, @PageableDefault(sort = "id", direction = DESC) Pageable pageable, Model model) {
        List<Board> findBoards = boardRepository.findByTitleContaining(boardSearch.getTitle(), pageable);
        Page<Board> pages = boardRepository.findAll(pageable);

        if (findBoards.isEmpty()) {
            List<Board> boards = boardRepository.findBoardWithUser(pageable);
            model.addAttribute("boards", boards);
            model.addAttribute("pages", pages);
            model.addAttribute("maxPage", 10);
            return "boards";
        }

        model.addAttribute("boards", findBoards);
        model.addAttribute("pages", pages);
        model.addAttribute("maxPage", 10);
        return "boards";
    }

    @GetMapping("/admin/reportBoards")
    public String reportBoards(Model model, @PageableDefault(sort = "id", direction = DESC) Pageable pageable) {
        List<ReportBoardsResponse> reportBoards = reportService.getReportBoards(pageable);
        Page<Report> pages = reportRepository.findAll(pageable);

        model.addAttribute("reportBoards", reportBoards);
        model.addAttribute("pages", pages);
        model.addAttribute("maxPage", 10);
        return "reportBoards";
    }

    @GetMapping("/admin/board/{boardId}")
    public String board(@PathVariable Long boardId, Model model) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(BoardNotFound::new);

        model.addAttribute("board", board);
        model.addAttribute("comments", board.getComments());

        return "board";
    }

    @PostMapping("/admin/board/delete/{boardId}")
    public String deleteBoard(@PathVariable Long boardId) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(BoardNotFound::new);

        boardService.deleteBoard(boardId, board.getUser());
        return "redirect:/admin";
    }

    @PostMapping("/admin/board/deleteComment/{commentId}")
    public String deleteComment(@PathVariable Long commentId, Model model) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(CommentNotFound::new);

        Board board = boardRepository.findById(comment.getBoard().getId())
                .orElseThrow(BoardNotFound::new);

        List<Reply> replies = replyRepository.findByCommentId(commentId);

        model.addAttribute("board", comment.getBoard());
        model.addAttribute("comments", board.getComments());

        replyRepository.deleteAllInBatch(replies);
        commentRepository.delete(comment);
        return "board";
    }

    @PostMapping("/admin/board/deleteReply/{replyId}")
    public String deleteReply(@PathVariable Long replyId, Model model) {
        Reply reply = replyRepository.findById(replyId)
                .orElseThrow(ReplyNotFound::new);

        model.addAttribute("board", reply.getBoard());
        model.addAttribute("comments", reply.getBoard().getComments());

        replyRepository.delete(reply);
        return "board";
    }

    @PostConstruct
    public void init() {
        User admin = User.builder()
                .nickname("관리자")
                .loginId("123")
                .password("123")
                .grade(ADMIN)
                .build();

        userRepository.save(admin);
    }
}

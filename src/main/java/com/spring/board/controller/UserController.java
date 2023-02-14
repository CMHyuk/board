package com.spring.board.controller;

import com.spring.board.domain.User;
import com.spring.board.request.user.EditUserRequest;
import com.spring.board.request.user.SaveUserRequest;
import com.spring.board.response.user.EditUserResponse;
import com.spring.board.response.user.SaveUserResponse;
import com.spring.board.response.user.UserBoardResponse;
import com.spring.board.service.UserService;
import com.spring.board.web.argumentresolver.Login;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/user/{userId}/boards")
    public List<UserBoardResponse> getBoards(@PathVariable Long userId) {
        return userService.getUserBoards(userId);
    }

    @GetMapping("/user/{userId}/likeBoards")
    public List<UserBoardResponse> getLikeBoards(@PathVariable Long userId) {
        return userService.getLikeBoards(userId);
    }

    @PostMapping("/user/save")
    public SaveUserResponse save(@RequestBody @Valid SaveUserRequest request, BindingResult bindingResult) throws BindException {
        if (bindingResult.hasErrors()) {
            throw new BindException(bindingResult);
        }
        return userService.save(request);
    }

    @PatchMapping("/user/edit/{userId}")
    public EditUserResponse edit(@PathVariable Long userId, @RequestBody @Valid EditUserRequest request,
                                 @Login User user) {
        User editUser = userService.editUserPassword(userId, request, user);
        return new EditUserResponse(editUser.getPassword());
    }

    @DeleteMapping("/user/delete/{userId}")
    public void delete(@PathVariable Long userId, @Login User user) {
        userService.deleteUser(userId, user);
    }
}

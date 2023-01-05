package com.spring.board.controller;

import com.spring.board.domain.User;
import com.spring.board.request.user.EditUserRequest;
import com.spring.board.request.user.SaveUserRequest;
import com.spring.board.response.user.EditUserResponse;
import com.spring.board.response.user.SaveUserResponse;
import com.spring.board.service.UserService;
import com.spring.board.web.argumentresolver.Login;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/user/save")
    public SaveUserResponse save(@RequestBody @Valid SaveUserRequest request) {
        User user = User.builder()
                .nickname(request.getNickname())
                .loginId(request.getLoginId())
                .password(request.getPassword())
                .build();

        User saveUser = userService.save(user);

        return SaveUserResponse.builder()
                .id(saveUser.getId())
                .nickname(saveUser.getNickname())
                .loginId(saveUser.getLoginId())
                .password(saveUser.getPassword())
                .build();
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

package com.creepvocab.controller;

import com.creepvocab.common.result.Result;
import com.creepvocab.entity.User;
import com.creepvocab.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "User", description = "User API")
@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Operation(summary = "Get User Profile")
    @GetMapping("/profile")
    public Result<User> getProfile() {
        Long userId = com.creepvocab.utils.SecurityUtils.getUserId();
        User user = userService.getById(userId);
        return Result.success(user);
    }

    @Operation(summary = "Update User Profile")
    @PostMapping("/update")
    public Result<Boolean> updateUser(@RequestBody UserUpdateDTO dto) {
        Long userId = com.creepvocab.utils.SecurityUtils.getUserId();
        User user = new User();
        user.setId(userId);
        if (dto.getNickname() != null) user.setNickname(dto.getNickname());
        if (dto.getAvatar() != null) user.setAvatar(dto.getAvatar());
        // Phone update usually requires verification, keeping it simple for now or omitting as per instructions (optional later)
        
        return Result.success(userService.updateById(user));
    }
    
    @lombok.Data
    public static class UserUpdateDTO {
        private String nickname;
        private String avatar;
    }

    @Operation(summary = "Get Rank List")
    @GetMapping("/rank")
    public Result<List<User>> getRank() {
        return Result.success(userService.list());
    }
}

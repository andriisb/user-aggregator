package com.user_aggregator.controller;

import com.user_aggregator.entity.UserEntity;
import com.user_aggregator.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    public List<UserEntity> getUsers() {
        return userService.getAllUsers();
    }
}

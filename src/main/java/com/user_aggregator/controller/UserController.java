package com.user_aggregator.controller;

import com.user_aggregator.entity.UserEntity;
import com.user_aggregator.service.DataAggregatorService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final DataAggregatorService aggregatorService;

    @GetMapping
    public List<UserEntity> getUsers() {
        return aggregatorService.fetchAllUsers();
    }
}

package com.user_aggregator.controller;

import com.user_aggregator.dto.UserCreationRequest;
import com.user_aggregator.entity.UserEntity;
import com.user_aggregator.service.DataAggregatorService;
import com.user_aggregator.service.UserCreationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final DataAggregatorService aggregatorService;
    private final UserCreationService userCreationService;

    @GetMapping
    public List<UserEntity> getUsers() {
        return aggregatorService.fetchAllUsers()
                .collectList()
                .block();
    }

    @PostMapping("/create")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<String> createUser(@RequestBody UserCreationRequest request) {
        String successMessage = userCreationService.createUser(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(successMessage);
    }
}

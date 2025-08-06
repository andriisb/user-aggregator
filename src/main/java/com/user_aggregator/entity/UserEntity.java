package com.user_aggregator.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserEntity {
    private String id;
    private String username;
    private String name;
    private String surname;
}

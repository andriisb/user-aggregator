package com.user_aggregator.config;

import lombok.Data;

import java.util.Map;

@Data
public class DataSourceProperties {
    private String name;
    private String strategy;
    private String url;
    private String user;
    private String password;
    private String table;
    private Map<String, String> mapping;
}
